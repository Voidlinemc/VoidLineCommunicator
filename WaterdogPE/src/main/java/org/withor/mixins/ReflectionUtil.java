package org.withor.mixins;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class ReflectionUtil {
    @SneakyThrows
    public static Object getPrivateField(Object instance, String fieldName, Class<?> clazz) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    @SneakyThrows
    public static void setPrivateField(Object instance, String fieldName, Object value, Class<?> clazz) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static Object getPrivateField(Object instance, String fieldName) {
        return getPrivateField(instance, fieldName, instance.getClass());
    }

    public static void setPrivateField(Object instance, String fieldName, Object value) {
        setPrivateField(instance, fieldName, value, instance.getClass());
    }
}