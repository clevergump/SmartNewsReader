package com.clevergump.newsreader.netease_news.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.clevergump.newsreader.MyApplication;

/**
 * http工具类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 19:57
 * @projectName NewsReader
 */
public class NetworkUtils {

    /**
     * 检查手机是否联网
     * @return
     */
    public static boolean hasNetworkConnection () {
        Context appContext = MyApplication.getAppContext();
        ConnectivityManager connectivity = (ConnectivityManager) appContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}