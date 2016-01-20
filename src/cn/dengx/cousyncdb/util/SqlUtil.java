package cn.dengx.cousyncdb.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.dengx.cousyncdb.ClassParser;
import cn.dengx.cousyncdb.Container;
import cn.dengx.cousyncdb.CreateTableSQLBuilder;
import cn.dengx.cousyncdb.Statement;
import cn.dengx.cousyncdb.annotations.Table;


/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/13,12:03.
 */
public class SqlUtil {

    private static final String IF_TABLE = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='";

    /**
     * Returns data type of the given object's value.
     *
     * @param obj the object whose value type is to be returned
     * @return object value type
     */
    public static String getTypeOfObject(Object obj) {
        if (obj == null) {
//            return Statement.NULL;
            return null;
        } else if (obj instanceof byte[]) {
            return Statement.BLOB;
        } else if (obj instanceof Float || obj instanceof Double) {
            return Statement.FLOAT;
        } else if (obj instanceof Long || obj instanceof Integer
                || obj instanceof Short || obj instanceof Byte) {
            return Statement.INT;
        } else if (obj instanceof String || obj instanceof Character) {
            return Statement.TEXT;
        } else
            return null;
    }

    public static String getTypeOfClass(Class c) {
        if (c == null)
            return null;
        else if (c.equals(Byte[].class))
            return Statement.BLOB;
        else if (c.equals(Float.class) || c.equals(Double.class)
                || c.equals(float.class) || c.equals(double.class))
            return Statement.FLOAT;
        else if (c.equals(Long.class) || c.equals(Integer.class)
                || c.equals(Short.class) || c.equals(Byte.class)
                || c.equals(Boolean.class) || c.equals(long.class)
                || c.equals(int.class) || c.equals(short.class)
                || c.equals(boolean.class) || c.equals(byte.class))
            return Statement.INT;
        else if (c.equals(String.class) || c.equals(Character.class))
            return Statement.TEXT;
        else
            return null;
    }

    /**
     * 获得表名字
     *
     * @param container
     * @return
     */
    public static String getTableName(@NonNull Container container) {
        Table table = container.getTable();
        String name;
        if (table == null || TextUtils.isEmpty(table.Name())) {
            name = container.getClass().getSimpleName();
        } else {
            name = table.Name();
        }
        return name;
    }

    /**
     * check table exists with create table name if not exists sql String
     *
     * @param clazz
     * @param db
     */
    public static void checkTableExists(@NonNull Class clazz, @NonNull SQLiteDatabase db) {
        Container container = ClassParser.getContainer(clazz);
        if (!container.isTableChecked()) {
            String sql = CreateTableSQLBuilder.builder(container);
            if (!TextUtils.isEmpty(sql)) {
                db.execSQL(sql);
                container.setTableChecked(true);
            }
        }
    }

    public static boolean checkTableExists(@NonNull String table, @NonNull SQLiteDatabase db) {
        String sql = IF_TABLE + table + "';";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                return true;
            } else
                return false;
        } finally {
            if (cursor != null)
                cursor.close();
        }

    }
}
