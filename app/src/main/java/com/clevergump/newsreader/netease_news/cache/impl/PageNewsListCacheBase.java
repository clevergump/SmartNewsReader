package com.clevergump.newsreader.netease_news.cache.impl;

import android.os.Message;

import com.clevergump.newsreader.MyApplication;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.cache.IPageNewsListCache;
import com.clevergump.newsreader.netease_news.event.impl.OnGetLatestNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.LogUtils;

import java.util.List;

/**
 * 新闻list缓存的抽象父类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/10 15:53
 * @projectName NewsReader
 */
public abstract class PageNewsListCacheBase implements IPageNewsListCache {

    public PageNewsListCacheBase() {
        EventBusUtils.registerEventBus(this);
    }

    /**
     * 异步子线程中接收并执行的事件
     * @param event
     */
    public void onEventAsync(BaseNewsListEvent event) {
        // http响应并解析的数据, 存入缓存.
        if (event instanceof OnGetLatestNewsListEvent) {
            OnGetLatestNewsListEvent e = ((OnGetLatestNewsListEvent)event);
            // 线程名称: pool-3-thread-1
//            Thread.currentThread().getName();
            List<NeteaseNewsItem> newsList = e.getNewsList();
            if (newsList != null && newsList.size() > 0) {
                String newsTypeId = e.getNewsType();

                String newsTabName = NewsFragmentManager.getInstance().getNewsTabName(newsTypeId);
                String msgContent = newsTabName + "新闻存入缓存, 线程name ==" + Thread.currentThread().getName();
                Message msg = MyApplication.mToastDebugHandler.obtainMessage(0, msgContent);
                MyApplication.mToastDebugHandler.sendMessage(msg);
                LogUtils.i("存入缓存, 线程name ==" + Thread.currentThread().getName());
                putNewsListToCache(newsTypeId, newsList);
            }
        }
    }
}