package com.clevergump.newsreader.netease_news.event.impl;

import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;

/**
 * 新闻标签ListView页面上网络状况异常的事件
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/18 10:07
 * @projectName NewsReader
 */
public class NetworkFailsNewsListEvent extends BaseNewsListEvent {

    public NetworkFailsNewsListEvent(String newsTypeId) {
        super(newsTypeId);
    }
}