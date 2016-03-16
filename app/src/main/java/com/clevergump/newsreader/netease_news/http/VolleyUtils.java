package com.clevergump.newsreader.netease_news.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.clevergump.newsreader.MyApplication;

/**
 * Volley网络请求的工具类.
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/3/16 23:15
 * @projectName SmartNewsReader
 */
public class VolleyUtils {
    // 整个应用程序范围内单例的 Volley网络请求的请求队列.
    private static RequestQueue sRequestQueue;

    private VolleyUtils() {
    }

    /**
     * 将给定的请求对象添加到请求队列中.
     * @param request
     */
    public static void addToRequestQueue(Request request) {
        RequestQueue requestQueue = getRequestQueue();
        requestQueue.add(request);
    }

    /**
     * 获取请求队列.
     * @return
     */
    private static RequestQueue getRequestQueue() {
        if (sRequestQueue == null) {
            synchronized (VolleyUtils.class) {
                if (sRequestQueue == null) {
                    Context applicationContext = MyApplication.getAppContext();
                    sRequestQueue = Volley.newRequestQueue(applicationContext);
                }
            }
        }
        return sRequestQueue;
    }
}