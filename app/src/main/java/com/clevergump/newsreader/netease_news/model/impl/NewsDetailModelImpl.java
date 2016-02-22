package com.clevergump.newsreader.netease_news.model.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;
import com.clevergump.newsreader.netease_news.dao.table.news_detail.impl.NeteaseNewsDetailDaoImpl;
import com.clevergump.newsreader.netease_news.event.impl.NetworkFailsNewsDetailEvent;
import com.clevergump.newsreader.netease_news.event.impl.OnGetNewsDetailEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsDetailEvent;
import com.clevergump.newsreader.netease_news.http.HttpGetNewsDetail;
import com.clevergump.newsreader.netease_news.model.INewsDetailModel;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.NetworkUtils;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/1 17:20
 * @projectName NewsReader
 */
public class NewsDetailModelImpl implements INewsDetailModel {

    @Override
    public void requestNewsDetail(Context context, String docid) {
        // 使用AsyncTask自带的线程池.
        new GetNewsDetailTask(context, docid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 根据给定的docid, 查询缓存中与该docid相对应的那篇新闻文章的详情实体对象.
     * 需在子线程中调用
     * @param context
     * @param docid 给定的新闻详情的docid
     */
    private NeteaseNewsDetail requestNewsDetailFromCache(Context context, String docid) {
        NeteaseNewsDetail cachedNewsDetail = NeteaseNewsDetailDaoImpl.getInstance().getNewsDetail(docid);
        return cachedNewsDetail;
    }

    private void requestNewsDetailFromServer(Context context, String docid) {
        // 在发起http请求之前, 要先判断网络连接状况是否正常. 如果网络连接异常, 则无需发起http请求,
        // 可以直接发送一个网络异常的事件. 如果网络连接正常, 才会发起http请求.
        if (NetworkUtils.hasNetworkConnection()) {
            new HttpGetNewsDetail(context, docid).request();
        } else {
            BaseNewsDetailEvent event = new NetworkFailsNewsDetailEvent(docid);
            EventBusUtils.post(event);
        }
    }

    private class GetNewsDetailTask extends AsyncTask<Void, Void, NeteaseNewsDetail> {
        private Context mContext;
        private String mDocid;

        public GetNewsDetailTask(Context context, String docid) {
            if (context == null) {
                throw new IllegalArgumentException(context + " == null");
            }
            if (TextUtils.isEmpty(docid)) {
                throw new IllegalArgumentException(docid + " == null or contains no characters");
            }
            this.mContext = context;
            this.mDocid = docid;
        }

        @Override
        protected NeteaseNewsDetail doInBackground(Void... params) {
            NeteaseNewsDetail cachedNewsDetail = requestNewsDetailFromCache(mContext, mDocid);
            return cachedNewsDetail;
        }

        @Override
        protected void onPostExecute(NeteaseNewsDetail cachedNewsDetail) {
            // 如果缓存中有该新闻详情, 则直接使用该缓存的内容, 就不再请求网络.
            if (cachedNewsDetail != null) {
                BaseNewsDetailEvent event = new OnGetNewsDetailEvent(mDocid, cachedNewsDetail);
                EventBusUtils.post(event);
            }
            // 如果缓存中没有该新闻详情, 则请求服务器.
            else {
                requestNewsDetailFromServer(mContext, mDocid);
            }
        }
    }
}