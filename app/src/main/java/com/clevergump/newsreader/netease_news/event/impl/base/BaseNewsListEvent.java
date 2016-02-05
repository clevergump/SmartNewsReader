package com.clevergump.newsreader.netease_news.event.impl.base;

import android.text.TextUtils;

import com.clevergump.newsreader.netease_news.event.IEvent;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/22 16:32
 * @projectName NewsReader
 */
public class BaseNewsListEvent implements IEvent {
    protected final String bNewsTypeId;

    public BaseNewsListEvent(String newsTypeId) {
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no content");
        }
        this.bNewsTypeId = newsTypeId;
    }

    public String getNewsTypeId() {
        return bNewsTypeId;
    }
}