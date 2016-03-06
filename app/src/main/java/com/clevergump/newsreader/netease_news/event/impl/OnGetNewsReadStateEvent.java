package com.clevergump.newsreader.netease_news.event.impl;

import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/3/7 3:46
 * @projectName SmartNewsReader
 */
public class OnGetNewsReadStateEvent extends BaseNewsListEvent {

    // 要查询阅读状态的那个新闻条目的 docId
    private String mDocId;
    // 新闻条目是否已读过. 读过: Constant.NEWS_ITEM_HAS_READ. 未读: Constant.NEWS_ITEM_NOT_READ
    private int mReadState;


    public OnGetNewsReadStateEvent(String newsTypeId, String docId, int readState) {
        super(newsTypeId);
        this.mReadState = readState;
    }

    /**
     * 获取该事件中所携带的阅读状态.
     * 如果为 Constant.NEWS_ITEM_HAS_READ, 表示已读过.
     * 如果为 Constant.NEWS_ITEM_NOT_READ, 表示未读过.
     * @return
     */
    public int getReadState() {
        return mReadState;
    }

    /**
     * 获取要查询阅读状态的那个新闻条目的 docId
     * @return
     */
    public String getDocId () {
        return mDocId;
    }
}