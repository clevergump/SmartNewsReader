package com.clevergump.newsreader.netease_news.utils;

import android.util.Log;

import com.clevergump.newsreader.Constant;

/**
 * log工具类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/6 19:44
 * @projectName NewsReader
 */
public class LogUtils {

    private static final String TAG_APP = "APP";

    public static void e(String tag, String msg) {
        if (Constant.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (Constant.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Constant.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Constant.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Constant.DEBUG) {
            Log.v(tag, msg);
        }
    }


    /*************** APP的全局log *******************/

    public static void e(String appMsg) {
        e(TAG_APP, appMsg);
    }

    public static void w(String appMsg) {
        w(TAG_APP, appMsg);
    }

    public static void i(String appMsg) {
        i(TAG_APP, appMsg);
    }

    public static void d(String appMsg) {
        d(TAG_APP, appMsg);
    }

    public static void v(String appMsg) {
        v(TAG_APP, appMsg);
    }
}