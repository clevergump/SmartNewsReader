package com.clevergump.newsreader.netease_news.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;
import com.clevergump.newsreader.netease_news.dao.table.news_detail.impl.NeteaseNewsDetailDaoImpl;
import com.clevergump.newsreader.netease_news.event.impl.NetworkFailsNewsDetailEvent;
import com.clevergump.newsreader.netease_news.event.impl.OnGetNewsDetailEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsDetailEvent;
import com.clevergump.newsreader.netease_news.http.HttpGetNewsDetail;
import com.clevergump.newsreader.netease_news.model.INewsDetailModel;
import com.clevergump.newsreader.netease_news.model.MyAsyncTask;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.NetworkUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/1 17:20
 * @projectName NewsReader
 */
public class NewsDetailModelImpl implements INewsDetailModel {

    List<MyAsyncTask> mTasks = new LinkedList<MyAsyncTask>();

    @Override
    public void getNewsDetail(Context context, String docid) {
        MyAsyncTask<Void, Void, NeteaseNewsDetail> getFromCacheTask = new GetNewsDetailTask(context, docid)
                .executeOnExecutor(MyAsyncTask.CACHED_THREAD_POOL_FOR_GETTING_FROM_CACHE);
        mTasks.add(getFromCacheTask);
    }

    @Override
    public void putToCache(NeteaseNewsDetail newsDetail) {
        // 将从服务器获取到的最新的新闻详情数据插入到数据库中
        MyAsyncTask<Void, Void, Void> putToCacheTask = new NewsDetailPutToCacheTask(newsDetail)
                .executeOnExecutor(MyAsyncTask.CACHED_THREAD_POOL_FOR_PUTTING_TO_CACHE);
        mTasks.add(putToCacheTask);
    }

    /**
     * 执行相关清理工作.
     */
    @Override
    public void clear() {
        for (MyAsyncTask task : mTasks) {
            if (task.isCancelable()) {
                task.cancel(true);
            }
        }
    }

    /**
     * 将新闻详情数据存入缓存的异步任务
     */
    private class NewsDetailPutToCacheTask extends MyAsyncTask<Void, Void, Void> {
        private NeteaseNewsDetail mNewsDetail;

        public NewsDetailPutToCacheTask(NeteaseNewsDetail newsDetail) {
            if (newsDetail == null ) {
                throw new IllegalArgumentException(newsDetail + " == null or contains no elements");
            }
            this.mNewsDetail = newsDetail;
        }

        @Override
        public boolean isCancelable() {
            // 不允许取消
            return false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            NeteaseNewsDetailDaoImpl.getInstance().insertDistinctly(mNewsDetail);
            return null;
        }
    }

    /**
     * 获取新闻详情数据的异步任务.
     */
    private class GetNewsDetailTask extends MyAsyncTask<Void, Void, NeteaseNewsDetail> {
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
            // 从缓存中获取与 mDocid相对应的那篇新闻文章的详情实体对象.
            NeteaseNewsDetail cachedNewsDetail = NeteaseNewsDetailDaoImpl.getInstance().getNewsDetail(mDocid);
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

        /**
         * 从服务器获取新闻详情的数据 (只有在缓存中没有这条新闻的详情时, 才会执行该方法. 也即: "缓存查询"
         * 和"从服务器获取"这两个动作是串行执行的, 而非并行执行, 并且"从服务器获取"这个动作有可能不执行.
         * 因为一条新闻详情被发表出来以后, 它的内容就永远不变了, 所以只要曾经加载过该新闻详情并且缓存中还
         * 存在, 那么就可以直接从缓存中去获取, 而不用再从服务器上获取了. 这与首页的新闻列表不同, 新闻列表的
         * 数据是每隔一段时间要刷新的, 是会不断变化的, 所以"缓存查询"和"从服务器获取"这两个动作需要并行执行)
         *
         * @param context
         * @param docid
         */
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

        /**
         * 是否允许取消该任务
         * @return
         */
        @Override
        public boolean isCancelable() {
            // 允许取消.
            return true;
        }
    }
}