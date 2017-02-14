/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cache.decorators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * The 2nd level cache transactional buffer.
 *
 * This class holds all cache entries that are to be added to the 2nd level cache during a Session.
 * Entries are sent to the cache when commit is called or discarded if the Session is rolled back.
 * Blocking cache support has been added. Therefore any get() that returns a cache miss
 * will be followed by a put() so any lock associated with the key can be released.
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
/**
 * 具有事务功能的缓存
 * 该类持有回话期间需要添加到二级缓存的所有缓存条目。当调用commit时插入缓存；当调用rollback时丢弃缓存。
 * 具有阻塞功能？
 * 一次性存入多个缓存，移除多个缓存
 *
 */
public class TransactionalCache implements Cache {
    //日志工具
    private static final Log log = LogFactory.getLog(TransactionalCache.class);
    //缓存代理
    private Cache delegate;
    //是否在commit后清除缓存
    private boolean clearOnCommit;
    //delegate的缓存，即提交到delegate之前的临时存储
    private Map<Object, Object> entriesToAddOnCommit;
    //缓存未命中时，保存条目key
    private Set<Object> entriesMissedInCache;

    public TransactionalCache(Cache delegate) {
        this.delegate = delegate;
        this.clearOnCommit = false;
        this.entriesToAddOnCommit = new HashMap<Object, Object>();
        this.entriesMissedInCache = new HashSet<Object>();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public Object getObject(Object key) {
        // issue #116
        Object object = delegate.getObject(key);
        if (object == null) {
            //如果未命中，则保存下该key
            entriesMissedInCache.add(key);
        }
        // issue #146
        if (clearOnCommit) {
            //并非清理缓存，而是在get时返回null
            return null;
        } else {
            return object;
        }
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

    @Override
    public void putObject(Object key, Object object) {
        //并非直接插入到delegate中，而是插入delegate的缓存
        entriesToAddOnCommit.put(key, object);
    }

    @Override
    public Object removeObject(Object key) {
        //不支持移除？
        return null;
    }

    @Override
    public void clear() {
        //clear方法含义有变化，是针对delegate的缓存，而不是delegate本身
        //清楚操作针对标记为和临时内存，而真正的执行体现在commit和rollback中
        clearOnCommit = true;
        //清除delegate的缓存
        entriesToAddOnCommit.clear();
    }

    //多了commit方法，提供事务功能
    public void commit() {
        //此处先clear后flush，保留了clear与新增的顺序
        if (clearOnCommit) {
            delegate.clear();
        }
        flushPendingEntries();
        reset();
    }

    //多了rollback方法，提供事务功能
    public void rollback() {
        unlockMissedEntries();
        reset();
    }

    //重置操作
    private void reset() {
        clearOnCommit = false;
        entriesToAddOnCommit.clear();
        entriesMissedInCache.clear();
    }

    private void flushPendingEntries() {
        for (Map.Entry<Object, Object> entry : entriesToAddOnCommit.entrySet()) {
            //保存delegate的缓存中的条目到delegate中
            delegate.putObject(entry.getKey(), entry.getValue());
        }
        for (Object entry : entriesMissedInCache) {
            if (!entriesToAddOnCommit.containsKey(entry)) {
                //将未命中条目加入到delegate中
                delegate.putObject(entry, null);
            }
        }
    }

    private void unlockMissedEntries() {
        //TODO 没明白
        for (Object entry : entriesMissedInCache) {
            try {
                delegate.removeObject(entry);
            } catch (Exception e) {
                log.warn("Unexpected exception while notifiying a rollback to the cache adapter."
                        + "Consider upgrading your cache adapter to the latest version.  Cause: " + e);
            }
        }
    }

}
