package com.clevergump.newsreader.netease_news.event.impl;

import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;

import java.util.List;

/**
 * 当获取到最新的新闻数据的事件
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/11 18:50
 * @projectName NewsReader
 */
public class OnGetLatestNewsListEvent<T> extends BaseNewsListEvent {

    private int mPageNumber;
    private List<T> mNewsItems;

    public OnGetLatestNewsListEvent(String newsTypeId, int pageNumber, List<T> mNewsItems) {
        super(newsTypeId);
        if (pageNumber < 0) {
            throw new IllegalArgumentException(pageNumber + " < 0");
        }
        if (mNewsItems == null) {
            throw new IllegalArgumentException(newsTypeId + " == null");
        }
        this.mPageNumber = pageNumber;
        this.mNewsItems = mNewsItems;
    }

    public String getNewsType() {
        return bNewsTypeId;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    public List<T> getNewsList() {
        return mNewsItems;
    }
}