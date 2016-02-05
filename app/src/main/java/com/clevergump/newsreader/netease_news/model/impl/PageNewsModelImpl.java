package com.clevergump.newsreader.netease_news.model.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.clevergump.newsreader.MyApplication;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.cache.impl.PageNewsListCacheBase;
import com.clevergump.newsreader.netease_news.dao.table.news_detail.INeteaseNewsDetailDao;
import com.clevergump.newsreader.netease_news.dao.table.news_list.INeteaseNewsListDao;
import com.clevergump.newsreader.netease_news.event.impl.NetworkFailsNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.OnGetCachedNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;
import com.clevergump.newsreader.netease_news.http.HttpGetNewsList;
import com.clevergump.newsreader.netease_news.http.NeteaseNewsUrlUtils;
import com.clevergump.newsreader.netease_news.model.IPageNewsModel;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.NetworkUtils;

import java.util.List;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/11 19:45
 * @projectName NewsReader
 */
public class PageNewsModelImpl implements IPageNewsModel {
    private final String TAG = getClass().getSimpleName();

    // 用毫秒数表示的一天
    private static final long TIME_MILLIS_ONE_DAY = 24 * 60 * 60 * 1000;
    // 上次刷新时间的初始值, 是个负数-1, 表示该新闻标签还从来没有刷新过. 因为只要刷新过, 该变量就一定是正数.
    private final long LAST_UPDATE_TIME_INIT_VALUE = -1;

    // 是否为-1来判断该标签的新闻是否加载过.
    private long mLastRefreshTimeMillis = LAST_UPDATE_TIME_INIT_VALUE;


    private Context mContext;
    private String mNewsTypeId;
    private INeteaseNewsListDao mNewsListDao;
    private INeteaseNewsDetailDao mNewsDatailDao;
    private PageNewsListCacheBase mPageNewsListCache;

    public PageNewsModelImpl(Context context, String newsTypeId, INeteaseNewsListDao newsListDao,
                             PageNewsListCacheBase pageNewsListCache) {
        if (context == null) {
            throw new IllegalArgumentException(context + " == null");
        }
        if (newsTypeId == null) {
            throw new IllegalArgumentException(newsTypeId + " == null");
        }
        if (newsListDao == null) {
            throw new IllegalArgumentException(newsListDao + " == null");
        }
        if (pageNewsListCache == null) {
            throw new IllegalArgumentException(pageNewsListCache + " == null");
        }
        this.mContext = context;
        this.mNewsTypeId = newsTypeId;
        this.mNewsListDao = newsListDao;
        this.mPageNewsListCache = pageNewsListCache;
    }

    /**
     * 根据给定的新闻标签名称, 得到该标签对应的url地址.
     * @param newsTabName 给定的新闻标签名称
     * @return 与给定的新闻标签名称对应的url地址.
     */
    public String getTabNewsUrl(String newsTabName, int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("页码"+ pageNumber + "不能为负数.");
        }
        String url = NeteaseNewsUrlUtils.getPageNewsListUrl(newsTabName, pageNumber);
        return url;
    }

    @Override
    public String getNewsTypeId(String newsTabName) {
        String newsTypeId = NewsFragmentManager.getInstance().getNewsTypeId(newsTabName);
        return newsTypeId;
    }

    /**
     * 获取上一次下拉刷新的时间点
     * @return 上一次刷新的时间(毫秒数). 返回-1表示内存中先前没有记录过该标签的刷新时间.
     */
    @Override
    public long getLastRefreshTimeMillis(String newsTabName) {
        // 从内存中获取
        Long lastRefreshTime = MyApplication.lastUpdateTimeMillis.get(newsTabName);
        if (lastRefreshTime != null && lastRefreshTime.longValue() > 0) {
            return lastRefreshTime.longValue();
        }
        return LAST_UPDATE_TIME_INIT_VALUE;
    }

    @Override
    public void updateLastRefreshTime(String newsTabName) {
        /**
         * 存入内存. 如果该HashMap先前已经存储过该key相关的键值对, 那么将会用现在这个value
         * (也就是上次刷新时间)替换掉旧的value(也就是先前保存的"上次刷新时间").
         *
         * If the map previously contained a mapping for the key, the old
         * value is replaced.
         */
        MyApplication.lastUpdateTimeMillis.put(newsTabName, System.currentTimeMillis());
        Long lastUpdateTimeMillis = MyApplication.lastUpdateTimeMillis.get(newsTabName);
//        Log.i(TAG, newsTabName+"上次更新时间: " +lastUpdateTimeMillis + "---"
//                + DateTimeUtils.getFormattedTime(lastUpdateTimeMillis));
        // 存入硬盘
        // TODO: 存入硬盘
    }

    @Override
    public boolean shouldRefreshWholePageData(String newsTabName, boolean isPullToRefresh) {
        if (isPullToRefresh) {
            return true;
        }
        // 如果上一次刷新时间已经在24小时前, 那么需要刷新当前的整个页面
        if (System.currentTimeMillis() - getLastRefreshTimeMillis(newsTabName) > TIME_MILLIS_ONE_DAY) {
            return true;
        }
        return false;
    }

    /**
     * 请求获取某一页的新闻数据.
     * @param tabNewsUrl
     */
    @Override
    public void requestPageNews(String tabNewsUrl, int pageNumber, String newsTypeId, boolean shouldLoadCache) {
        // 只有需要加载缓存数据时, 才会去获取缓存数据, 页码为0不代表就一定要加载缓存, 因为也可能是使用了下拉刷新
        // 导致数据需要完全重新加载而请求第0页的数据, 但这时是不需要加载缓存的.
        if (shouldLoadCache) {
            new GetNewsListFromCacheTask(newsTypeId, pageNumber).execute();
        }

        // 如果网络连接异常, 就不发起http请求, 而直接发出网络连接失败的事件.
        if (!NetworkUtils.hasNetworkConnection()) {
            BaseNewsListEvent event = new NetworkFailsNewsListEvent(newsTypeId);
            EventBusUtils.post(event);
        } else {
            // 联网请求服务器数据
            new HttpGetNewsList(mContext, pageNumber, tabNewsUrl, newsTypeId).request();
        }
    }

    private class GetNewsListFromCacheTask extends AsyncTask<Void, Void, Void> {
        private String newsTypeId;
        private int pageNumber;

        public GetNewsListFromCacheTask(String newsTypeId, int pageNumber) {
            if (TextUtils.isEmpty(newsTypeId)) {
                throw new IllegalArgumentException(newsTypeId + " == null or contains no characters.");
            }
            if (pageNumber < 0) {
                throw new IllegalArgumentException("页码" + pageNumber + "不能是负数");
            }
            this.newsTypeId = newsTypeId;
            this.pageNumber = pageNumber;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NeteaseNewsItem> cachedNewsItemList = mPageNewsListCache.getNewsListFromCache(newsTypeId, pageNumber);
            BaseNewsListEvent event = new OnGetCachedNewsListEvent(mNewsTypeId, cachedNewsItemList);
            EventBusUtils.post(event);
            return null;
        }
    }
}