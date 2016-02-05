package com.clevergump.newsreader.netease_news.presenter;

import android.os.Handler;

import com.clevergump.newsreader.netease_news.model.IPageNewsModel;
import com.clevergump.newsreader.netease_news.utils.ToastUtils;
import com.clevergump.newsreader.netease_news.view.IPageNewsView;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 20:04
 * @projectName NewsReader
 */
public class PageNewsListPresenter {

    private IPageNewsView iView;
    private IPageNewsModel iModel;

    // 延迟加载新闻标签内容的Handler
    private static Handler sLazyLoadHandler;

    public PageNewsListPresenter(IPageNewsView iView, IPageNewsModel iModel) {
        this.iView = iView;
        this.iModel = iModel;
        if (sLazyLoadHandler == null) {
            sLazyLoadHandler = new Handler();
        }
    }

    /**
     * 请求特定类别的新闻在特定页的数据.
     * @param newsTabName 请求数据所属的新闻版块名称
     */
    public void requestPageNews(final String newsTabName, final boolean isPullToRequest,
                                final boolean shouldLoadCache) {
        // 先显示一个加载进度条, 然后数据的加载需要延迟一小段时间再去执行, 这是为了降低
        // fragment之间切换时可能出现的卡顿现象.
        iView.showLoadingView();

        // 因为如果是点击某个fragment标签来切换fragment, 为了避免切换过程的卡顿, 可以将fragment的内容
        // 进行延迟加载, 这样可以降低fragment点击切换后, 在切换过程中出现的卡顿现象.
        sLazyLoadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lazyLoad(newsTabName, isPullToRequest, shouldLoadCache);
            }
        }, 300);
    }

    /**
     * 延迟加载新闻标签的内容
     * @param newsTabName
     * @param isPullToRequest
     * @param shouldLoadCache
     */
    private void lazyLoad(String newsTabName, boolean isPullToRequest,
                          boolean shouldLoadCache) {
        if (isPullToRequest) {
            iView.initPullingState();
        }
        if (shouldLoadCache) {
            iView.initCacheQueryingState();
        }
        // 是否需要刷新整个页面的数据.
        boolean shouldRefreshWholePageData = iModel.shouldRefreshWholePageData(newsTabName, isPullToRequest);
        int currPageNumber = iView.setCurrAndNextPageNumber(shouldRefreshWholePageData);
        iView.setNextDataIncomingType(shouldRefreshWholePageData);
        String tabNewsUrl = iModel.getTabNewsUrl(newsTabName, currPageNumber);
        String newsTypeId = iModel.getNewsTypeId(newsTabName);
        ToastUtils.showDebug("要加载的页码 == " + currPageNumber);
        iModel.requestPageNews(tabNewsUrl, currPageNumber, newsTypeId, shouldLoadCache);
    }

    /**
     * 获取给定新闻类别的新闻的上一次刷新时间
     * @param newsTabName
     * @return 该新闻标签上一次刷新的时间. 如果返回-1, 表示先前没有记录过该标签的上次刷新时间.
     */
    public long getLastRefreshTimeMillis(String newsTabName) {
        return iModel.getLastRefreshTimeMillis(newsTabName);
    }

    /**
     * 更新给定新闻类别的新闻的上一次刷新时间
     * @param newsTabName
     */
    public void updateLastRefreshTime(String newsTabName) {
        iModel.updateLastRefreshTime(newsTabName);
    }
}