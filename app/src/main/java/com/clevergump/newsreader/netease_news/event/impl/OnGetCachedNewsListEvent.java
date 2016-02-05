package com.clevergump.newsreader.netease_news.event.impl;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;

import java.util.List;

/**
 * 当获取到缓存的新闻数据的事件
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/12 16:07
 * @projectName NewsReader
 */
public class OnGetCachedNewsListEvent extends BaseNewsListEvent {

    private List<NeteaseNewsItem> mNeteaseNewsItems;

    public OnGetCachedNewsListEvent(String newsTypeId, List<NeteaseNewsItem> mNeteaseNewsItems) {
        super(newsTypeId);
        this.mNeteaseNewsItems = mNeteaseNewsItems;
    }

    public String getNewsType() {
        return bNewsTypeId;
    }

    public List<NeteaseNewsItem> getNewsList() {
        return mNeteaseNewsItems;
    }
}
