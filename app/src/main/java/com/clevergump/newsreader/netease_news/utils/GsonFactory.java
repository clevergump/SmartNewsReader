package com.clevergump.newsreader.netease_news.utils;

import com.google.gson.Gson;

/**
 * Gson工厂, 用于产生单例的 Gson对象.
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 14:35
 * @projectName NewsReader
 */
public class GsonFactory {

    private GsonFactory() {
    }

    public static Gson getGson() {
        return GsonHolder.INSTANCE;
    }

    private static class GsonHolder {
        private static final Gson INSTANCE = new Gson();
    }
}