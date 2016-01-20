package cn.dengx.cousyncdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.dengx.cousyncdb.Exceptions.CouSyncDbException;
import cn.dengx.cousyncdb.util.LogUtil;

public class CouSyncDb {
    public static final String LOG_HEADER = "[CouSyncDb] >>> ";
    public static final String TAG = "CouSyncDb";

    private static CouSyncDb instance;

    private Context appContext;

    private CouSyncDbOpenHelper openHelper;

//    private CountDownLatch countDownLatch;

    private final BlockingQueue<OperateTask> tasks;

    /**
     * 数据库名
     */
    private String dbName;

    /**
     * 版本号
     */
    private int versionDb = 1;

    OperateThread thread;

    private OnUpgradeListener onUpgradeListener;

    private CouSyncDb(Context context) {
        appContext = context;
//        countDownLatch = new CountDownLatch(1);
        tasks = new LinkedBlockingQueue<>();
    }

    public static CouSyncDb getInstance(Context appContext) {
        if (instance == null) {
            synchronized (CouSyncDb.class) {
                if (instance == null) {
                    instance = new CouSyncDb(appContext);
                }
            }
        }
        return instance;
    }

    public void setDbName(String name) {
        if (openHelper != null) {
            throw new CouSyncDbException("method setDbName must invoke before init()");
        }
        this.dbName = name;
    }

    public String getDbName() {
        return TextUtils.isEmpty(dbName) ? TAG : dbName;
    }


    public void init() {
        if (openHelper != null) {
            throw new CouSyncDbException("method init() only invoke once");
        }
        thread = new OperateThread(appContext, tasks);
        thread.start();
    }

    public SQLiteDatabase getSQLiteDatabase() {
        if (openHelper == null)
            throw new CouSyncDbException("method setDbName must invoke after init()");
//        try {
//            countDownLatch.wait();
//        } catch (InterruptedException e) {
//        }
        return openHelper.getWritableDatabase();
    }

    void setOpenHelper(CouSyncDbOpenHelper openHelper) {
        this.openHelper = openHelper;
//        countDownLatch.countDown();
    }

    CouSyncDbOpenHelper getOpenHelper() {
        return openHelper;
    }


    public int getVersionDb() {
        return versionDb;
    }

    public void setVersionDb(int versionDb) {
        if (openHelper != null)
            throw new CouSyncDbException("method setVersionDb must invoke before init()");
        this.versionDb = versionDb;
    }

    public void close() {
        if (thread != null) {
            thread.exit();
            thread = null;
        }
    }

    public OnUpgradeListener getOnUpgradeListener() {
        return onUpgradeListener;
    }

    public void setOnUpgradeListener(OnUpgradeListener onUpgradeListener) {
        this.onUpgradeListener = onUpgradeListener;
    }

    //sql operate--------------start

    /**
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     */
    public int delete(@NonNull String table, String whereClause, String[] whereArgs) {
        return (int) new Delete(table, whereClause, whereArgs).runTask(getSQLiteDatabase());
    }

    /**
     * 根据主键值相等delete
     * T class 必须设置primaryKey
     *
     * @param t
     * @param <T>
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     */
    public <T> int delete(@NonNull T t) {
        return (int) new Delete(t).runTask(getSQLiteDatabase());
    }

    /**
     * @param table
     * @param nullColumnHack
     * @param values
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insert(@NonNull String table, String nullColumnHack, ContentValues values) {
        return (long) new Insert(table, nullColumnHack, values).runTask(getSQLiteDatabase());
    }

    /**
     * @param t
     * @param <T>
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public <T> long insert(@NonNull T t) {
        return (long) new Insert(t).runTask(getSQLiteDatabase());
    }

    /**
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @param clazz
     * @return a cursor don't invoke close() method if clazz is null,or a List<clazz> had invoke close() inside;
     */
    public Object query(@NonNull String table, String[] columns, String selection, String[] selectionArgs,
                        String groupBy, String having, String orderBy, String limit, Class clazz) {
        return query(false, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, clazz);
    }

    /**
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param clazz
     * @return a cursor don't invoke close() method if clazz is null,or a List<clazz> had invoke close() inside;
     */
    public Object query(@NonNull String table, String[] columns, String selection, String[] selectionArgs,
                        String groupBy, String having, String orderBy, Class clazz) {
        return query(false, table, columns, selection, selectionArgs, groupBy, having, orderBy, null, clazz);
    }

    /**
     * @param distinct
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @param clazz
     * @return a cursor don't invoke close() method if clazz is null,or a List<clazz> had invoke close() inside;
     */
    public Object query(boolean distinct, @NonNull String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having, String orderBy,
                        String limit, Class clazz) {
        return new Qurey(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, clazz)
                .runTask(getSQLiteDatabase());
    }

    /**
     * @param sql
     * @param selectionArgs
     * @return a cursor don't invoke close() method if clazz is null,or a List<clazz> had invoke close() inside;
     */
    public Object rawQuery(@NonNull String sql, String[] selectionArgs, Class clazz) {
        return new Qurey(sql, selectionArgs, clazz).runTask(getSQLiteDatabase());
    }

    /**
     * @param table
     * @param nullColumnHack
     * @param initialValues
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long replace(@NonNull String table, String nullColumnHack, ContentValues initialValues) {
        return (long) new Replace(table, nullColumnHack, initialValues).runTask(getSQLiteDatabase());
    }

    /**
     * @param t
     * @param <T>
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public <T> long replace(@NonNull T t) {
        return (long) new Replace(t).runTask(getSQLiteDatabase());
    }

    /**
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return the number of rows affected
     */
    public int update(@NonNull String table, ContentValues values, String whereClause, String[] whereArgs) {
        return (int) new Update(table, values, whereClause, whereArgs).runTask(getSQLiteDatabase());
    }

    /**
     * 根据主键值相等update
     * T class 必须设置primaryKey
     *
     * @param t
     * @param <T>
     * @return the number of rows affected
     */
    public <T> int update(@NonNull T t) {
        return (int) new Update(t).runTask(getSQLiteDatabase());
    }

    public void deleteSync(@NonNull String table, String whereClause, String[] whereArgs, OnOperateFinish l) {
        Delete delete = new Delete(table, whereClause, whereArgs);
        delete.setOnOperateFinish(l);
        tasks.add(delete);
    }

    /**
     * 根据主键值相等delete
     * T class 必须设置primaryKey
     *
     * @param t
     * @param <T>
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     */
    public <T> void deleteSync(@NonNull T t, OnOperateFinish l) {
        Delete delete = new Delete(t);
        delete.setOnOperateFinish(l);
        tasks.add(delete);
    }

    public void insertSync(@NonNull String table, String nullColumnHack, ContentValues values, OnOperateFinish l) {
        Insert insert = new Insert(table, nullColumnHack, values);
        insert.setOnOperateFinish(l);
        tasks.add(insert);
    }

    public <T> void insertSync(@NonNull T t, OnOperateFinish l) {
        Insert insert = new Insert(t);
        insert.setOnOperateFinish(l);
        tasks.add(insert);
    }


    public void replaceSync(@NonNull String table, String nullColumnHack, ContentValues initialValues, OnOperateFinish l) {
        Replace replace = new Replace(table, nullColumnHack, initialValues);
        replace.setOnOperateFinish(l);
        tasks.add(replace);
    }

    /**
     */
    public <T> void replaceSync(@NonNull T t, OnOperateFinish l) {
        Replace replace = new Replace(t);
        replace.setOnOperateFinish(l);
        tasks.add(replace);
    }

    public void updateSync(@NonNull String table, ContentValues values, String whereClause,
                           String[] whereArgs, OnOperateFinish l) {
        Update update = new Update(table, values, whereClause, whereArgs);
        update.setOnOperateFinish(l);
        tasks.add(update);
    }

    /**
     * 根据主键值相等update
     * T class 必须设置primaryKey
     *
     * @param t
     * @param l
     */
    public <T> void updateSync(@NonNull T t, OnOperateFinish l) {
        Update update = new Update(t);
        update.setOnOperateFinish(l);
        tasks.add(update);
    }


    public void querySync(@NonNull String table, String[] columns, String selection, String[] selectionArgs,
                          String groupBy, String having, String orderBy, Class clazz, OnOperateFinish l) {
        querySync(false, table, columns, selection, selectionArgs, groupBy, having, orderBy, null, clazz, l);
    }

    public void querySync(@NonNull String table, String[] columns, String selection, String[] selectionArgs,
                          String groupBy, String having, String orderBy, String limit, Class clazz, OnOperateFinish l) {
        querySync(false, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, clazz, l);
    }

    public void querySync(boolean distinct, @NonNull String table, String[] columns, String selection,
                          String[] selectionArgs, String groupBy, String having, String orderBy,
                          String limit, Class clazz, OnOperateFinish l) {
        Qurey qurey = new Qurey(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, clazz);
        qurey.setOnOperateFinish(l);
        tasks.add(qurey);
    }


    public void rawQuerySync(@NonNull String sql, String[] selectionArgs, Class clazz, OnOperateFinish l) {
        Qurey qurey = new Qurey(sql, selectionArgs, clazz);
        qurey.setOnOperateFinish(l);
        tasks.add(qurey);
    }

    public void addTask(@NonNull OperateTask task) {
        tasks.add(task);
    }

    //sql operate--------------end


    public static final class Config {

        private static boolean checkPrimaryKey = true;

        private static boolean scanClassWhenCreate = true;

        private static boolean dropDbWhenNoUpgradeL = false;

        /**
         * check bean has primary field
         *
         * @return true if bean has no primary field will throw NoPrimaryKeyException
         */
        public static boolean isCheckPrimaryKey() {
            return checkPrimaryKey;
        }

        /**
         * check bean has primary field
         *
         * @param check true if bean has no primary field will throw NoPrimaryKeyException
         */
        public static void setCheckPrimaryKey(boolean check) {
            checkPrimaryKey = check;
        }

        public static boolean isDebug() {
            return LogUtil.isDebugable();
        }

        public static void setDEBUG(boolean debug) {
            LogUtil.setDebugable(debug);
        }

        /**
         * scan bean class it has PrimaryKey annotation when database creating,then create tables
         *
         * @return true scan
         */
        public static boolean isScanClassWhenCreate() {
            return scanClassWhenCreate;
        }

        /**
         * scan bean class it has PrimaryKey annotation when database creating,then create tables
         *
         * @param scanClassWhenCreate scan or not;
         */
        public static void setScanClassWhenCreate(boolean scanClassWhenCreate) {
            Config.scanClassWhenCreate = scanClassWhenCreate;
        }

        /**
         * drop all tables onUpgrade in SQLiteOpenHelper when has no OnUpgradeListener
         *
         * @return true drop
         */
        public static boolean isDropDbWhenNoUpgradeL() {
            return dropDbWhenNoUpgradeL;
        }

        /**
         * drop all tables onUpgrade in SQLiteOpenHelper when has no OnUpgradeListener
         *
         * @param dropDbWhenNoUpgradeL drop or not;
         */
        public static void setDropDbWhenNoUpgradeL(boolean dropDbWhenNoUpgradeL) {
            Config.dropDbWhenNoUpgradeL = dropDbWhenNoUpgradeL;
        }
    }

    public interface OnUpgradeListener {
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }


}


