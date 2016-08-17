package cn.dengx.cousyncdb.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dengx.cousyncdb.ClassParser;
import cn.dengx.cousyncdb.Container;
import cn.dengx.cousyncdb.CouSyncDb;
import cn.dengx.cousyncdb.Exceptions.NoFieldException;
import cn.dengx.cousyncdb.Exceptions.NoSetMethodException;
import cn.dengx.cousyncdb.FieldContainer;
import cn.dengx.cousyncdb.annotations.Ignore;
import cn.dengx.cousyncdb.annotations.PrimaryKey;
import cn.dengx.cousyncdb.annotations.Table;
import dalvik.system.DexFile;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,15:01.
 */
public class BeanUtil {

    private static final String JAVA = "java.lang.";

    public static final String BYTE = "byte";
    public static final String BOOLEAN = "boolean";
    public static final String SHORT = "short";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";

    public static final String JAVA_BYTE = JAVA + "Byte";
    public static final String JAVA_BOOLEAN = JAVA + "Boolean";
    public static final String JAVA_SHORT = JAVA + "Short";
    public static final String JAVA_INT = JAVA + "Integer";
    public static final String JAVA_LONG = JAVA + "Long";
    public static final String JAVA_FLOAT = JAVA + "Float";
    public static final String JAVA_DOUBLE = JAVA + "Double";
    public static final String JAVA_STRING = JAVA + "String";

    public static final String[] BASE_TYPES = {BYTE, BOOLEAN, SHORT, INT, LONG, FLOAT, DOUBLE, JAVA_BYTE,
            JAVA_BOOLEAN, JAVA_SHORT, JAVA_INT, JAVA_LONG, JAVA_FLOAT, JAVA_DOUBLE, JAVA_STRING};


    private static final String SET = "set";
    private static final String IS = "is";

    private BeanUtil() {
    }

    public static FieldContainer create(@NonNull Field field, @NonNull Method[] methods) {
        FieldContainer container = new FieldContainer();
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        if (primaryKey != null) {
            container.setIsPrimaryKey(true);
            container.setIsAuto(primaryKey.autoIncrement());
        }
        Ignore ignore = field.getAnnotation(Ignore.class);
        if (ignore != null)
            container.setIgnore(true);
        ArrayList<Method> ms = new ArrayList<>(2);
        container.setField(field);
        for (Method m : methods) {
            String methodName = m.getName();
            if (TextUtils.equals(methodName.substring(0, 2), IS)) {
                methodName = methodName.substring(2);
            } else {
                methodName = methodName.substring(3);
            }
            if (methodName.equalsIgnoreCase(field.getName())) {
                ms.add(m);
            }
            if (ms.size() >= 2)
                break;
        }
        for (Method m : ms) {
            if (m.getName().contains(SET)) {
                container.setMethodSet(m);
            } else {
                container.setMethodGet(m);
            }
        }
        return container;
    }

    /**
     * 扫描含有table注解的class
     *
     * @param context
     * @return a list fill with class that has table annotation or a empty list
     */
    public static ArrayList<Class> getClassWithTable(@NonNull Context context) {
        ArrayList<Class> classes = new ArrayList<>();
        try {
            String packageCodePath = context.getPackageCodePath();
            String pacName = context.getPackageName();
            DexFile df = new DexFile(packageCodePath);
            Enumeration<String> iter = df.entries();
            while (iter.hasMoreElements()) {
                String className = iter.nextElement();
                if (TextUtils.isEmpty(className))
                    continue;
                if (!className.contains(pacName))
                    continue;
                Class c = getClassWithTable(className);
                if (c != null)
                    classes.add(c);
            }
        } catch (IOException e) {
            LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "dex file io exception", e);
        }
        return classes;
    }

    /**
     * 获得含有table注解的class
     *
     * @param className
     * @return class has table annotation or null
     */
    private static Class getClassWithTable(@NonNull String className) {
        Class clazz = null;
        try {
            Class c = Class.forName(className);
            if (c.getAnnotation(Table.class) != null) {
                clazz = c;
            }
        } catch (ClassNotFoundException e) {
            LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "getClassWithTable error when class name is"
                    + className, e);
        }
        return clazz;
    }

    public static ContentValues convertClassToValues(@NonNull Object object) {
        Class clazz = object.getClass();
        Container container = ClassParser.getContainer(clazz);
        HashMap<String, FieldContainer> fieldContainers = container.getFieldContainers();
        ContentValues values = new ContentValues();
        if (fieldContainers == null || fieldContainers.size() < 1) {
            throw new NoFieldException(clazz.getName() + " has no field");
        } else {
            for (Map.Entry<String, FieldContainer> entry : fieldContainers.entrySet()) {
                FieldContainer fieldContainer = entry.getValue();
                if (fieldContainer.isIgnore())
                    continue;
                String typeName = fieldContainer.getField().getType().getName();
                if (TextUtils.isEmpty(typeName) || !BeanUtil.baseType(typeName)) {//基本类型检测
                    LogUtil.d(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "field is not base type on " +
                            "convertClassToValues fieldContainer=" + fieldContainer);
                    continue;
                }
                putValues(fieldContainer.getField().getName(), values, object, fieldContainer.getMethodGet());
            }
        }
        return values;
    }

    private static void putValues(@NonNull String key, @NonNull ContentValues values,
                                  @NonNull Object object, Method get) {
        if (get == null)
            return;
        try {
            Object o = get.invoke(object);
            if (o != null) {
                put(values, key, o);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * put object to contentValues
     *
     * @param values
     * @param key
     * @param o
     */
    public static void put(@NonNull ContentValues values, @NonNull String key, @NonNull Object o) {
        try {
            if (o instanceof String)
                values.put(key, o.toString());
            else if (o instanceof Byte)
                values.put(key, Byte.parseByte(o.toString()));
            else if (o instanceof Short)
                values.put(key, Short.parseShort(o.toString()));
            else if (o instanceof Integer)
                values.put(key, Integer.parseInt(o.toString()));
            else if (o instanceof Long)
                values.put(key, Long.parseLong(o.toString()));
            else if (o instanceof Float)
                values.put(key, Float.parseFloat(o.toString()));
            else if (o instanceof Double)
                values.put(key, Double.parseDouble(o.toString()));
            else if (o instanceof Boolean)
                values.put(key, Boolean.valueOf(o.toString()));
            else
                LogUtil.d(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "ContentValues put key=" + key + " value="
                        + o + o.getClass() + " error");
        } catch (NumberFormatException e) {
            LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "ContentValues put key=" + key +
                    " value=" + o.toString(), e);
        }
    }

    /**
     * 读取cursor 对象集合
     * 该方法不会调用cursor.close()
     *
     * @param cursor
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> convertCursorToClasses(@NonNull Cursor cursor, @NonNull Class<T> tClass) {
        int count = cursor.getCount();
        ArrayList<T> os = new ArrayList<>(count);
        while (cursor.moveToNext()) {
            T t = convertCursorToClass(cursor, tClass);
            if (t != null)
                os.add(t);
            else {
                LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "lose data when cursor at position=" +
                        cursor.getPosition() + "  " + cursor);
            }
        }
        return os;
    }

    /**
     * 调用该方法以前，已经使用cursor.moveToNext();
     * 方法里不会调用cursor.close()
     *
     * @param cursor
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T convertCursorToClass(@NonNull Cursor cursor, @NonNull Class<T> tClass) {
        Container container = ClassParser.getContainer(tClass);
        HashMap<String, FieldContainer> fieldContainers = container.getFieldContainers();
        T t = null;
        if (fieldContainers == null || fieldContainers.size() < 1) {
            throw new NoFieldException(tClass.getName() + " has no field");
        } else {
            try {
                t = tClass.newInstance();
                int columnCount = cursor.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    fillObject(cursor, i, t, fieldContainers);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    static void fillObject(@NonNull Cursor cursor, int columnIndex, @NonNull Object o,
                           @NonNull HashMap<String, FieldContainer> fieldContainers) {
        if (cursor.isNull(columnIndex)) return;//过滤null
        String key = cursor.getColumnName(columnIndex);
        FieldContainer fieldContainer = fieldContainers.get(key);
        Method set = fieldContainer.getMethodSet();
        if (set == null) {
//            LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "FieldContainer" + fieldContainer +
//                    " has no set method when cursor get key=" + key);
//            return;
            throw new NoSetMethodException("FieldContainer" + fieldContainer.toString() + "has no set method ");
        }
        Object value = null;
        Class type = fieldContainer.getField().getType();
        if (type.equals(String.class)) {
            value = cursor.getString(columnIndex);
        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
            value = (byte) cursor.getShort(columnIndex);
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            value = cursor.getShort(columnIndex);
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            value = cursor.getInt(columnIndex);
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            value = cursor.getLong(columnIndex);
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            value = cursor.getFloat(columnIndex);
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            value = cursor.getDouble(columnIndex);
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            value = cursor.getShort(columnIndex) > 0;
        } else if (type.equals(byte[].class) || type.equals(Byte[].class)) {
            value = cursor.getBlob(columnIndex);
        }
        if (value != null) {
            try {
                set.invoke(o, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "cursor key=" + key +
                    " do not get value when fieldContainer field=" + type);
        }
    }

    /**
     * is java base type
     *
     * @param typeName 完整类名
     * @return
     */
    public static boolean baseType(@NonNull String typeName) {
        if (typeName.equals(BYTE) || typeName.equals(BOOLEAN) || typeName.equals(SHORT) || typeName.equals(INT)
                || typeName.equals(LONG) || typeName.equals(FLOAT) || typeName.equals(DOUBLE)
                || typeName.equals(JAVA_BYTE) || typeName.equals(JAVA_BOOLEAN) || typeName.equals(JAVA_SHORT)
                || typeName.equals(JAVA_INT) || typeName.equals(JAVA_LONG) || typeName.equals(JAVA_FLOAT)
                || typeName.equals(JAVA_DOUBLE) || typeName.equals(JAVA_STRING)) {
            return true;
        } else
            return false;
//
//        int index = Arrays.binarySearch(BASE_TYPES, typeName);
//        return index >= 0;
    }

}
