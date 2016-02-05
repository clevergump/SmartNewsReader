package com.clevergump.newsreader.netease_news.utils;

import android.content.Context;
import android.widget.Toast;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.MyApplication;

/**
 * toast工具类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/6 19:51
 * @projectName NewsReader
 */
public class ToastUtils {

    /**
     * 简化版的toast
     * @param msg 要显示的消息内容
     */
    public static void show(CharSequence msg) {
        Toast.makeText(MyApplication.getAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 简化版的toast
     * @param stringResId 要显示的消息内容所对应的资源id
     */
    public static void show(int stringResId){
        Context appContext = MyApplication.getAppContext();
        show(appContext.getText(stringResId));
    }

    /**
     * 调试专用的toast
     * @param msg 要显示的消息内容
     */
    public static void showDebug(CharSequence msg) {
        if(Constant.DEBUG){
            show(msg);
        }
    }

    /**
     * 调试专用的toast
     * @param stringResId 要显示的消息内容所对应的资源id
     */
    public static void showDebug(int stringResId) {
        if(Constant.DEBUG){
            show(stringResId);
        }
    }
}