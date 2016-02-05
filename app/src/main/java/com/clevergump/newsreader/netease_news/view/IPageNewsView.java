package com.clevergump.newsreader.netease_news.view;

import android.view.View;

import com.clevergump.newsreader.netease_news.presenter.PageNewsListPresenter;

/**
 * 分页加载新闻数据的 UI视图接口类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 20:02
 * @projectName NewsReader
 */
public interface IPageNewsView {


    void setPresenter(PageNewsListPresenter presenter);
    /**
     * 获取整个内容视图
     * @return
     */
    View getContentView();

    /**
     * 进入给定标签名称对应的新闻页面后, 数据正在加载时的View展示.
     */
    void showLoadingView();

    void removeLoadingView();

    /**
     * 数据加载完毕后, 页面视图的显示
     */
    void showViewOnDataLoaded();

//    /**
//     * 当发现没有更多数据可以加载的时候, 需要显示一个特定的UI效果来提示用户
//     */
//    void showTipsViewNoMoreDataToLoad();

    /**
     * 设置当前将要加载的页码和下一次加载要加载的页码. 并返回当前要加载的页码
     * @return 当前要加载的页码
     */
    int setCurrAndNextPageNumber(boolean shouldRefreshWholePageData);

    void onCreate();

    void onDestroy();

    void initPullingState();

    void initCacheQueryingState();

//    /**
//     * 是否需要刷新重新显示整个页面
//     * @param shouldRefreshWholePageData
//     */
//    void setShouldRefreshWholePageData(boolean shouldRefreshWholePageData);

    void setNextDataIncomingType(boolean shouldRefreshWholePageData);

    void onNewsItemHasBeenRead();

//    void setCurrAndNextPageNumber();
}