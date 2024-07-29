package com.ashenone.dfp.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflactUtil {
    public static Object invoke(Object arg2, String arg3) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return ReflactUtil.invoke(arg2, ReflactUtil.getMethed(arg2.getClass(), arg3, null), new Object[0]);
    }

    public static Object invoke(Object arg1, String arg2, Class[] arg3, Object[] arg4) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return ReflactUtil.invoke(arg1, ReflactUtil.getMethed(arg1.getClass(), arg2, arg3), arg4);
    }

    public static Object getValue(Object arg1, Field arg2) throws IllegalAccessException {
        arg2.setAccessible(true);
        return arg2.get(arg1);
    }

    public static Object invoke(Object arg1, Method arg2, Object[] arg3) throws InvocationTargetException, IllegalAccessException {
        arg2.setAccessible(true);
        return arg2.invoke(arg1, arg3);
    }

    public static Object invoke(String className, String methodName) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return ReflactUtil.invoke(null, ReflactUtil.getMethed(Class.forName(className), methodName, null), new Object[0]);
    }

    public static Object invoke(String arg2, String arg3, Class[] arg4, Object[] arg5) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return ReflactUtil.invoke(null, ReflactUtil.getMethed(Class.forName(arg2), arg3, arg4), arg5);
    }

    public static Field getField(Class arg2, String arg3) throws NoSuchFieldException {
        try {
            return arg2.getField(arg3);
        }
        catch(NoSuchFieldException v0) {
            try {
                return arg2.getDeclaredField(arg3);
            }
            catch(NoSuchFieldException v0_1) {
                if(arg2.getSuperclass() != null) {
                    return ReflactUtil.getField(arg2.getSuperclass(), arg3);
                }

                throw v0_1;
            }
        }
    }

    public static Method getMethed(Class cls, String methodName, Class[] argTypes) throws NoSuchMethodException {
        try {
            return cls.getMethod(methodName, argTypes);
        }
        catch(NoSuchMethodException v0) {
            try {
                return cls.getDeclaredMethod(methodName, argTypes);
            }
            catch(NoSuchMethodException v0_1) {
                if(cls.getSuperclass() != null) {
                    return ReflactUtil.getMethed(cls.getSuperclass(), methodName, argTypes);
                }

                throw v0_1;
            }
        }
    }

    public static Field[] getDeclaredFields(Class arg1) {
        return arg1.getDeclaredFields();
    }

    public static Field[] getDeclaredFieldsByClassName(String arg1) throws ClassNotFoundException {
        return ReflactUtil.getDeclaredFields(Class.forName(arg1));
    }

    public static Object getValue(Object arg1, String arg2) throws NoSuchFieldException, IllegalAccessException {
        return ReflactUtil.getValue(arg1, ReflactUtil.getField(arg1.getClass(), arg2));
    }
}
