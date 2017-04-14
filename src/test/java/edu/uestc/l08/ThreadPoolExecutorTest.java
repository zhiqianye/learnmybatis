package edu.uestc.l08;

import org.junit.Test;

/**
 * Created by zhiqianye on 2017/4/14.
 */
public class ThreadPoolExecutorTest {

	private static final int COUNT_BITS = Integer.SIZE - 3;
	private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

	private static final int RUNNING    = -1 << COUNT_BITS;//11100000000000000000000000000000
	private static final int SHUTDOWN   =  0 << COUNT_BITS;//0
	private static final int STOP       =  1 << COUNT_BITS;//00100000000000000000000000000000
	private static final int TIDYING    =  2 << COUNT_BITS;//01000000000000000000000000000000
	private static final int TERMINATED =  3 << COUNT_BITS;//01100000000000000000000000000000

	@Test
	public void testRunState() throws Exception {
		System.out.println(Integer.toBinaryString(RUNNING));
		System.out.println(Integer.toBinaryString(SHUTDOWN));
		System.out.println(Integer.toBinaryString(STOP));
		System.out.println(Integer.toBinaryString(TIDYING));
		System.out.println(Integer.toBinaryString(TERMINATED));
		System.out.println(CAPACITY);
	}

	private int ctlOf(int rs, int wc) { return rs | wc; }
}
