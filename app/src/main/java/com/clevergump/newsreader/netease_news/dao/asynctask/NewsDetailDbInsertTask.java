package com.clevergump.newsreader.netease_news.dao.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;
import com.clevergump.newsreader.netease_news.dao.table.news_detail.impl.NeteaseNewsDetailDaoImpl;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/3 20:58
 * @projectName NewsReader
 */
public class NewsDetailDbInsertTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private NeteaseNewsDetail mNewsDetail;

    public NewsDetailDbInsertTask(Context context, NeteaseNewsDetail newsDetail) {
        if (context == null) {
            throw new IllegalArgumentException(context + " == null");
        }
        if (newsDetail == null ) {
            throw new IllegalArgumentException(newsDetail + " == null or contains no elements");
        }
        this.mContext = context;
        this.mNewsDetail = newsDetail;
    }

    @Override
    protected Void doInBackground(Void... params) {
        NeteaseNewsDetailDaoImpl.getInstance().insertDistinctly(mNewsDetail);
        return null;
    }
}