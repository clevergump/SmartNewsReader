package com.clevergump.newsreader.netease_news.event.impl;

import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/22 16:26
 * @projectName NewsReader
 */
public class TestNewsListEvent extends BaseNewsListEvent {
    public TestNewsListEvent(String newsTypeId) {
        super(newsTypeId);
    }
}