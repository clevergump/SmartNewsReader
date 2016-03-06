package com.clevergump.newsreader.netease_news.model;

import android.content.Context;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 20:03
 * @projectName NewsReader
 */
public interface INewsDetailModel extends IModel {

    /**
     * 获取新闻详情的数据. 如果缓存中有, 就直接从缓存中获取. 如果缓存中没有, 才去从服务器上获取.
     * @param context
     * @param docid
     */
    void getNewsDetail(Context context, String docid);

    /**
     * 将从服务器上获取的新闻详情数据存入缓存
     * @param newsDetail
     */
    void putToCache(NeteaseNewsDetail newsDetail);
}