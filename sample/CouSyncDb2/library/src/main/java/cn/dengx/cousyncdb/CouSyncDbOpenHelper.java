package cn.dengx.cousyncdb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.text.TextUtils;

import java.util.List;

import cn.dengx.cousyncdb.util.BeanUtil;
import cn.dengx.cousyncdb.util.LogUtil;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/13,18:18.
 */
public class CouSyncDbOpenHelper extends SQLiteOpenHelper {

    private final Context mContext;

    public CouSyncDbOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (CouSyncDb.Config.isScanClassWhenCreate()) {
            scanClass(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CouSyncDb.OnUpgradeListener listener = CouSyncDb.getInstance(mContext).getOnUpgradeListener();
        if (listener == null) {
            LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "do not set CouSyncDb.OnUpgradeListener " +
                    "interface then jump it onUpgrade oldVersion=" + oldVersion + " newVersion" + newVersion);
            if (CouSyncDb.Config.isDropDbWhenNoUpgradeL()) {
                dropDb(db);
            }
        } else
            listener.onUpgrade(db, oldVersion, newVersion);
    }

    private void scanClass(SQLiteDatabase db) {
        long timeStart = SystemClock.uptimeMillis();

        List<Class> classes = BeanUtil.getClassWithTable(mContext);
        for (Class clazz : classes) {
            Container container = ClassParser.getContainer(clazz);
            if (container != null) {
                String createSql = CreateTableSQLBuilder.builder(container);
                if (!TextUtils.isEmpty(createSql)) {
                    db.execSQL(createSql);
                    LogUtil.i(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "Sql : " + createSql);
                }
            } else {
                LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "sql create table error do not " +
                        "convert to container with class=" + clazz.getName());
            }
        }

        long timeEnd = SystemClock.uptimeMillis();
        long time = timeEnd - timeStart;
        LogUtil.i(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "CouSyncDbOpenHelper onCreate spend time " + time + "ms");
    }

    /**
     * 删除所有数据表
     */
    public void dropDb(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type ='table' AND name != 'sqlite_sequence'", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                db.execSQL("DROP TABLE " + cursor.getString(0));
                LogUtil.d(CouSyncDb.TAG,CouSyncDb.LOG_HEADER+"drop table "+cursor.getString(0));
            }
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

}
