package edu.uestc.l08;

import org.junit.Test;

import java.lang.reflect.*;

public class TypeTest {

    @Test
    public void testParameterizedType() throws NoSuchFieldException, SecurityException {

        Type mapGenericType = GenericTest.class.getDeclaredField("map").getGenericType();  //ParameterizedType
        if (mapGenericType instanceof ParameterizedType) {
            ParameterizedType mapParameterizedType = (ParameterizedType) mapGenericType;
            //原始类型
            Type basicType = mapParameterizedType.getRawType();
            System.out.println("基本类型为：" + basicType);  //Map
            // 获取泛型类型的泛型参数, 分别为T1, class java.lang.Integer
            Type[] types = mapParameterizedType.getActualTypeArguments();
            for (int i = 0; i < types.length; i++) {
                System.out.println("第" + (i + 1) + "个泛型类型是：" + types[i]);
            }
            System.out.println(mapParameterizedType.getOwnerType());  //null
        }
    }

    @Test
    public void testTypeVariable() throws NoSuchFieldException, SecurityException {

        Type mapGenericType = GenericTest.class.getDeclaredField("map").getGenericType();  //ParameterizedType
        if (mapGenericType instanceof ParameterizedType) {
            // 获取泛型类型的泛型参数, 分别为 T1, class java.lang.Integer
            Type[] types = ((ParameterizedType) mapGenericType).getActualTypeArguments();
            for (int i = 0; i < types.length; i++) {
                if (types[i] instanceof TypeVariable) {
                    // T1 is TypeVariable, and Integer is not.
                    System.out.println("the " + (i + 1) + "th is TypeVariable");
                } else {
                    System.out.println("the " + (i + 1) + "th is not TypeVariable");
                }
            }

        }

        System.out.println("GenericTest TypeVariable");

        TypeVariable<Class<GenericTest>>[] typeVariables = GenericTest.class.getTypeParameters();

        // console print  T1, T2
        for (TypeVariable tmp : typeVariables) {
            System.out.println("" + tmp);
            Type[] bounds = tmp.getBounds(); //return upper bounds
            if (bounds.length > 0) {
                //T2 has upper bounds which is class java.lang.Number,
                //T1's upper bounds is Object which is default.
                System.out.println("bounds[0] is: " + bounds[0]);
            }

            System.out.println("name is: " + tmp.getName());  //  T1, T2
            System.out.println("GenericDeclaration is equals GenericTest.class: " + (tmp.getGenericDeclaration() == GenericTest.class)); //GenericTest
        }
    }

    @Test
    public void testWildcardType() throws NoSuchFieldException, SecurityException{
        Type mapGenericType = GenericTest.class.getDeclaredField("map1").getGenericType();
        TypeVariable<Class<GenericTest>>[] typeVariables = GenericTest.class.getTypeParameters();
        Type[] types = ((ParameterizedType) mapGenericType).getActualTypeArguments();
        for(Type t : types){
            if(t instanceof WildcardType){
                System.out.println("wildcardType");

                if( ((WildcardType) t).getLowerBounds().length > 0 )
                    System.out.println((((WildcardType) t).getLowerBounds())[0]); //print java.lang.Integer

                if( ((WildcardType) t).getUpperBounds().length > 0 )
                    System.out.println((((WildcardType) t).getUpperBounds())[0]); //print java.lang.Number, Object

            }
        }
    }

    @Test
    public void testGenericArrayType() throws NoSuchFieldException, SecurityException{
        Type tArrayGenericType = GenericTest.class.getDeclaredField("tArray").getGenericType();
        if(tArrayGenericType instanceof GenericArrayType){
            System.out.println("is GenericArrayType");  //
            Type t1 = ((GenericArrayType) tArrayGenericType).getGenericComponentType();
            System.out.println(t1);   // print T1
            if( t1 instanceof TypeVariable){
                System.out.println("t1 is TypeVariable");
                System.out.println(((TypeVariable) t1).getGenericDeclaration());
            }
        }
    }

}
