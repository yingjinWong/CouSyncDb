package cn.dengx.cousyncdb;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.dengx.cousyncdb.Exceptions.NoGetMethodException;
import cn.dengx.cousyncdb.Exceptions.NoPrimaryKeyException;
import cn.dengx.cousyncdb.util.SqlUtil;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/17,16:08.
 */
public class Delete<T> implements OperateTask {
    private T t;
    private boolean fromT;


    private Class aClass;
    private String table, whereClause;
    private String[] whereArgs;

    private OnOperateFinish l;

    public Delete(@NonNull T t) {
        this.t = t;
        fromT = true;
    }

    public Delete(@NonNull String table, String whereClause, String[] whereArgs) {
        this.table = table;
        this.whereClause = whereClause;
        this.whereArgs = whereArgs;
    }

    @Override
    public int getOperateType() {
        return OPERATE_TYPE_DELETE;
    }

    @Override
    public Object runTask(@NonNull SQLiteDatabase db) {
        if (fromT) {
            parseT();
        }

        boolean check;
        if (aClass == null) {
            check = SqlUtil.checkTableExists(table, db);
        } else {
            check = true;
            SqlUtil.checkTableExists(aClass, db);
        }
        if (check) {
            return db.delete(table, whereClause, whereArgs);
        }
        return 0;
    }

    private void parseT() {
        aClass = t.getClass();
        Container container = ClassParser.getContainer(aClass);
        if (container != null) {
            table = CreateTableSQLBuilder.getTableName(container);
            FieldContainer primaryKey = container.getPrimaryField();
            if (primaryKey != null) {
                init(t, primaryKey);
            } else {
                throw new NoPrimaryKeyException("Delete(@NonNull T t) T class must have primary key field");
            }
        }
    }

    private void init(@NonNull T t, FieldContainer primaryKey) {
        Field f = primaryKey.getField();
        if(f!=null)
            whereClause = f.getName()+"=?";
        Method get = primaryKey.getMethodGet();
        if (get != null) {
            try {
                Object result = get.invoke(t);
                whereArgs = new String[]{result.toString()};
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            throw new NoGetMethodException(t.getClass().getName() + " field=" + primaryKey.getField() + " has no get method");
        }
    }

    @Override
    public OnOperateFinish getL() {
        return l;
    }

    public void setOnOperateFinish(OnOperateFinish l) {
        this.l = l;
    }

}
