package com.clevergump.newsreader.netease_news.utils;

/**
 * EventBus 的订阅者需要实现该接口
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/3/9 22:41
 * @projectName SmartNewsReader
 */
public interface IEventBusSubscriber {

    /**
     * 注册 EventBus
     */
    void registerEventBus();

    /**
     * 取消注册 EventBus
     */
    void unregisterEventBus();
}
