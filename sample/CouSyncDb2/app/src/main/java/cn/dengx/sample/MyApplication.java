package cn.dengx.sample;

import android.app.Application;

import cn.dengx.cousyncdb.CouSyncDb;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/20,13:43.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CouSyncDb.Config.setDEBUG(true);
//        CouSyncDb.Config.setCheckPrimaryKey(false);
        CouSyncDb.getInstance(getApplicationContext()).init();
    }
}
