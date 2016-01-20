package cn.dengx.cousyncdb;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/19,15:39.
 */
public interface OnOperateFinish {
    /**
     * @param operateType OperateTask.OPERATE_TYPE_QUERY;
     *                    OperateTask.OPERATE_TYPE_INSERT;
     *                    OperateTask.OPERATE_TYPE_UPDATE;
     *                    OperateTask.OPERATE_TYPE_DELETE;
     *                    OperateTask.OPERATE_TYPE_REPLACE;
     * @param result      参考CouSyncDb数据库操作return
     */
    void onFinish(int operateType, Object result);
}
