package cn.dengx.cousyncdb;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/15,16:41.
 */
public interface OperateTask {
    int OPERATE_TYPE_QUERY = 1;
    int OPERATE_TYPE_INSERT = 2;
    int OPERATE_TYPE_UPDATE = 3;
    int OPERATE_TYPE_DELETE = 4;
    int OPERATE_TYPE_REPLACE = 5;

    /**
     * 当前是哪种操作
     *
     * @return
     */
    int getOperateType();

    Object runTask(@NonNull SQLiteDatabase db);

    OnOperateFinish getL();

}
