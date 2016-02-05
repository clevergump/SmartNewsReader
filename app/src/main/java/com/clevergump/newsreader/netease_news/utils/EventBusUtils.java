package com.clevergump.newsreader.netease_news.utils;

import com.clevergump.newsreader.netease_news.event.IEvent;

import de.greenrobot.event.EventBus;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/11 18:48
 * @projectName NewsReader
 */
public class EventBusUtils {
    private static final String TAG = EventBusUtils.class.getSimpleName();

    public static <T> void registerEventBus(T subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static <T> void unregisterEventBus(T subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static <T> boolean isRegistered(T subscriber) {
        return EventBus.getDefault().isRegistered(subscriber);
    }

    /**
     * 发布事件 (尽量是IEvent的实现类)
     *
     * @param event
     * @param <T>
     */
    public static <T> void post(T event) {
        EventBus.getDefault().post(event);
    }

    /**
     * 发布事件
     *
     * @param event
     */
    public static void post(IEvent event) {
        EventBus.getDefault().post(event);
    }
}