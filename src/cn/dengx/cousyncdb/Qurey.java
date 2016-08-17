package cn.dengx.cousyncdb;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import cn.dengx.cousyncdb.util.BeanUtil;
import cn.dengx.cousyncdb.util.LogUtil;
import cn.dengx.cousyncdb.util.SqlUtil;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/16,13:41.
 */
public class Qurey implements OperateTask {
    private Class tClass;

    private boolean rawQuery;
    private String sql;

    private boolean distinct;
    private String table;
    private String[] columns;
    private String selection;
    private String[] selectionArgs;
    private String groupBy;
    private String having;
    private String orderBy;
    private String limit;

    private OnOperateFinish l;

    public Qurey(@NonNull String sql, String[] selectionArgs, Class tClass) {
        rawQuery = true;
        this.sql = sql;
        this.selectionArgs = selectionArgs;
        this.tClass = tClass;
    }

    public Qurey(@NonNull String table, String[] columns, String selection, String[] selectionArgs,
                 String groupBy, String having, String orderBy, Class tClass) {
        this(table, columns, selection, selectionArgs, groupBy, having, orderBy, null, tClass);
    }

    public Qurey(@NonNull String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                 String having, String orderBy, String limit, Class tClass) {
        this(false, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, tClass);
    }

    public Qurey(boolean distinct, @NonNull String table, String[] columns, String selection, String[] selectionArgs,
                 String groupBy, String having, String orderBy, String limit, Class tClass) {
        this.distinct = distinct;
        this.table = table;
        this.columns = columns;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.limit = limit;
        this.tClass = tClass;
    }


    @Override
    public int getOperateType() {
        return OPERATE_TYPE_QUERY;
    }

    @Override
    public Object runTask(@NonNull SQLiteDatabase db) {
        boolean check;
        if (tClass == null) {
            check = SqlUtil.checkTableExists(table, db);
        } else {
            SqlUtil.checkTableExists(tClass, db);
            check = true;
        }
        Cursor cursor = null;
        if (check) {
            if (rawQuery) {
                cursor = db.rawQuery(sql, selectionArgs);
            } else {
                cursor = db.queryWithFactory(null, distinct, table, columns, selection, selectionArgs,
                        groupBy, having, orderBy, limit);
            }
        }
        if (cursor != null && CouSyncDb.Config.isDebug()) {
            if (rawQuery)
                LogUtil.d(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "raw query sql: " + sql);
            LogUtil.i(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "query result print cursor " +
                    DatabaseUtils.dumpCursorToString(cursor));
        }
        if (cursor != null && tClass != null) {
            try {
                if (rawQuery){
                    if(cursor.moveToFirst())
                        return BeanUtil.convertCursorToClass(cursor, tClass);
                    else return null;
                }
                else return BeanUtil.convertCursorToClasses(cursor, tClass);
            } finally {
                cursor.close();
            }
        }
        return cursor;
    }

    @Override
    public OnOperateFinish getL() {
        return l;
    }

    public void setOnOperateFinish(OnOperateFinish l) {
        this.l = l;
    }

}
