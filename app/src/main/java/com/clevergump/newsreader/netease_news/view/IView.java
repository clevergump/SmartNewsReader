package com.clevergump.newsreader.netease_news.view;

import com.clevergump.newsreader.netease_news.utils.IEventBusSubscriber;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/2/25 0:56
 * @projectName SmartNewsReader
 */
public interface IView extends IEventBusSubscriber {
    /**
     * 执行相关的资源清理或关闭工作. 例如: 取消监听器/EventBus等的注册, 清理 Handler发出的所有
     * callbacks和messages, 取消所有正在执行的异步任务(如: AsyncTask), 关闭所有的
     * Cursor/IO流/HttpURLConnection等.
     *
     * 通常来说, 该接口的实现类在该方法中需要调用他所引用的 Presenter的 clear()方法, 因为Presenter
     * 内部可能也需要执行一些清理工作.
     */
    void clear();
}