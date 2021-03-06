package com.clevergump.newsreader.netease_news.model.impl;

import com.clevergump.newsreader.netease_news.adapter.NeteaseNewsListAdapter;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.event.impl.OnGetLatestNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;
import com.clevergump.newsreader.netease_news.model.IPageNewsModel;
import com.clevergump.newsreader.netease_news.model.MyAsyncTask;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 专用于测试 PtrListView的刷新功能以及数据显示功能是否正常的model类.
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/17 14:50
 * @projectName NewsReader
 */
public class TestPtrListViewPageNewsModelImpl implements IPageNewsModel {
    private String mNewsTypeId;
    private MyAsyncTask<Object, Void, Void> mTask;


    @Override
    public String getTabNewsUrl(String newsTabName, int pageNumber) {
        return "http://...";
    }

    @Override
    public void requestPageNews(String tabNewsUrl, int pageNumber, String newsTypeId,
                                boolean shouldLoadCache) {
        this.mNewsTypeId = newsTypeId;
        String newsTabName = NewsFragmentManager.getInstance().getNewsTabName(newsTypeId);
        // 使用固定线程容量的并发线程池.
        if (mTask == null) {
            mTask = new TestRefreshingTask().
                    executeOnExecutor(MyAsyncTask.CACHED_THREAD_POOL_FOR_GETTING_FROM_SERVER, newsTabName, pageNumber);
        }
    }

    @Override
    public String getNewsTypeId(String newsTabName) {
        return NewsFragmentManager.getInstance().getNewsTypeId(newsTabName);
    }

    @Override
    public boolean shouldRefreshWholePageData(String newsTabName, boolean isPullToRefresh) {
        return false;
    }

    @Override
    public long getLastRefreshTimeMillis(String newsTabName) {
        return 0;
    }

    @Override
    public void updateLastRefreshTime(String newsTabName) {

    }

    @Override
    public void getNewsItemReadState(String docid, NeteaseNewsListAdapter.OnGetNewsItemReadStateListener listener) {
        // TODO
    }

    @Override
    public void updateNewsItemToHasReadState(String clickedItemDocId) {
        // TODO
    }

    @Override
    public void putToCache(String newsTypeId, List<NeteaseNewsItem> latestNewsList) {

    }

    @Override
    public void clear() {
        if (mTask != null && mTask.isCancelable()) {
            mTask.cancel(true);
        }
    }

    private class TestRefreshingTask extends MyAsyncTask<Object, Void, Void> {
        @Override
        public boolean isCancelable() {
            return true;
        }

        @Override
        protected Void doInBackground(Object... params) {
            String newsTabName = (String) params[0];
            int pageNumber = (int) params[1];
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                List<String> listData = new ArrayList<String>();
                // 当前页的起始 index
                int startIndex = pageNumber * 20;
                // 当前页的终止 index
                int endIndex = (pageNumber + 1) * 20 - 1;
                for (int index = startIndex; index <= endIndex; index ++) {
                    listData.add(newsTabName + ": item " + String.valueOf(index));
                }
                BaseNewsListEvent event = new OnGetLatestNewsListEvent<String>(mNewsTypeId, 0, listData);
                EventBusUtils.post(event);
            }
            return null;
        }
    }
}