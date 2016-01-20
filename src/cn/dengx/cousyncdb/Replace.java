package cn.dengx.cousyncdb;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import cn.dengx.cousyncdb.util.BeanUtil;
import cn.dengx.cousyncdb.util.SqlUtil;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/17,16:09.
 */
public class Replace<T> implements OperateTask {
    private T t;
    private boolean fromT;

    private String table, nullColumnHack;
    private ContentValues values;
    private Class aClass;

    private OnOperateFinish l;

    public Replace(@NonNull T t) {
        this.t = t;
        fromT = true;
    }

    public Replace(String table, String nullColumnHack, ContentValues initialValues) {
        this.table = table;
        this.nullColumnHack = nullColumnHack;
        this.values = initialValues;
    }

    private void parseT() {
        table = CreateTableSQLBuilder.getTableName(t);
        values = BeanUtil.convertClassToValues(t);
        aClass = t.getClass();
    }


    @Override
    public int getOperateType() {
        return OPERATE_TYPE_REPLACE;
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
            return db.replace(table, nullColumnHack, values);
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
