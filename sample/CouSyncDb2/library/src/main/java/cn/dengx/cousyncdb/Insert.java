package cn.dengx.cousyncdb;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import cn.dengx.cousyncdb.util.BeanUtil;
import cn.dengx.cousyncdb.util.LogUtil;
import cn.dengx.cousyncdb.util.SqlUtil;


/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/17,16:07.
 */
public class Insert<T> implements OperateTask {
    private T t;
    private boolean fromT;

    private String table, nullColumnHack;
    private ContentValues values;
    private Class aClass;

    private OnOperateFinish l;

    public Insert(@NonNull T t) {
        this.t = t;
        fromT = true;
    }

    public Insert(@NonNull String table, String nullColumnHack, ContentValues values) {
        this.table = table;
        this.nullColumnHack = nullColumnHack;
        this.values = values;
    }


    private void parseT() {
        table = CreateTableSQLBuilder.getTableName(t);
        values = BeanUtil.convertClassToValues(t);
        aClass = t.getClass();
    }


    @Override
    public int getOperateType() {
        return OPERATE_TYPE_INSERT;
    }

    @Override
    public Object runTask(@NonNull SQLiteDatabase db) {
        if (fromT)
            parseT();

        boolean check;
        if (aClass == null) {
            check = SqlUtil.checkTableExists(table, db);
        } else {
            SqlUtil.checkTableExists(aClass, db);
            check = true;
        }
        if (check) {
            try {
                return db.insertOrThrow(table, nullColumnHack, values);
            } catch (SQLException e) {
                LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "insertOrThrow error table=" + table + " values=" + values, e);
            }
        }
        return -1L;
    }

    @Override
    public OnOperateFinish getL() {
        return l;
    }

    public void setOnOperateFinish(OnOperateFinish l) {
        this.l = l;
    }

}
