package com.clevergump.newsreader.netease_news.event.impl;

import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsDetailEvent;

/**
 * 通用的网络状况异常的事件
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/1 17:35
 * @projectName NewsReader
 */
public class NetworkFailsNewsDetailEvent extends BaseNewsDetailEvent {

    public NetworkFailsNewsDetailEvent(String docid) {
        super(docid);
    }
}