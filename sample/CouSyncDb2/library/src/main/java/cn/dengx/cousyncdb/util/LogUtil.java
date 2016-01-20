package cn.dengx.cousyncdb.util;

import android.util.Log;

/**
 * Current project:MyScan.
 * Created by dengx on 15/12/12,11:13.
 */
public class LogUtil {

    private static boolean debugable;

    private LogUtil() {
    }

    public static void i(String tag, String msg) {
        if (debugable)
            Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (debugable)
            Log.i(tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        if (debugable)
            Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (debugable)
            Log.d(tag, msg, tr);
    }


    public static void e(String tag, String msg) {
        if (debugable)
            Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (debugable)
            Log.e(tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        if (debugable)
            Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (debugable)
            Log.w(tag, msg, tr);
    }

    public static boolean isLoggable(String s, int i) {
        return Log.isLoggable(s, i);
    }

    public static boolean isDebugable() {
        return debugable;
    }

    public static void setDebugable(boolean debugable) {
        LogUtil.debugable = debugable;
    }
}
