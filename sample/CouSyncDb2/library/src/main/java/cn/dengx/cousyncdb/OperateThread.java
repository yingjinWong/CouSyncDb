package cn.dengx.cousyncdb;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.BlockingQueue;

import cn.dengx.cousyncdb.util.LogUtil;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/14,22:56.
 */
public class OperateThread extends Thread {

    private final Context mContext;
    private final BlockingQueue<OperateTask> tasks;
    private final MyHandler myHandler;

    private boolean exit;

    public OperateThread(Context context, BlockingQueue<OperateTask> tasks) {
        super();
        mContext = context;
        this.tasks = tasks;
        setName("CouSyncDb_operateThread");
        setDaemon(true);
        myHandler = new MyHandler();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        init();

        while (true) {
            OperateTask task;
            try {
                task = tasks.take();
            } catch (InterruptedException e) {
                if(exit){
                    break;
                }else
                    continue;
            }
            try {
                final Object result = task.runTask(CouSyncDb.getInstance(mContext).getSQLiteDatabase());
                final int type = task.getOperateType();
                final OnOperateFinish l = task.getL();
                if (l != null)
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            l.onFinish(type, result);
                        }
                    });
            } catch (Exception e) {
                LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "run task error task=" + task.toString(), e);
            }
        }

        CouSyncDbOpenHelper helper =CouSyncDb.getInstance(mContext).getOpenHelper();
        if(helper!=null)
            helper.close();
    }

    void exit(){
        exit =true;
        interrupt();
    }

    /**
     * CouSyncDb initiate in background thread in fact
     */
    private void init() {
        CouSyncDb couSyncDb = CouSyncDb.getInstance(mContext);
        CouSyncDbOpenHelper openHelper = new CouSyncDbOpenHelper(mContext, couSyncDb.getDbName(),
                couSyncDb.getVersionDb());
        couSyncDb.setOpenHelper(openHelper);
        openHelper.getWritableDatabase();
    }

    static class MyHandler extends Handler {
        MyHandler() {
            super(Looper.getMainLooper());
        }
    }
}
