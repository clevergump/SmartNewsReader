package com.clevergump.newsreader.netease_news.event.impl;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsDetailEvent;

/**
 * 当获取到缓存的新闻详情的事件
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/1 17:33
 * @projectName NewsReader
 */
public class OnGetNewsDetailEvent extends BaseNewsDetailEvent {

    public final NeteaseNewsDetail newsDetail;

    public OnGetNewsDetailEvent(String docid, NeteaseNewsDetail newsDetail) {
        super(docid);
        if (newsDetail == null) {
            throw new IllegalArgumentException(newsDetail + " == null");
        }
        this.newsDetail = newsDetail;
    }
}