package com.clevergump.newsreader.netease_news.event.impl.base;

import android.text.TextUtils;

import com.clevergump.newsreader.netease_news.event.IEvent;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/1 17:31
 * @projectName NewsReader
 */
public class BaseNewsDetailEvent implements IEvent {
    protected String docid;

    public BaseNewsDetailEvent(String docid) {
        if (TextUtils.isEmpty(docid)) {
            throw new IllegalArgumentException(docid + " == null or contains no characters");
        }
        this.docid = docid;
    }
}