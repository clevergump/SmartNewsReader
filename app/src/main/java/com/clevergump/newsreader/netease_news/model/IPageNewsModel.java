package com.clevergump.newsreader.netease_news.model;

import com.clevergump.newsreader.netease_news.adapter.NeteaseNewsListAdapter;

/**
 * 分页加载新闻的业务逻辑处理接口
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 20:02
 * @projectName NewsReader
 */
public interface IPageNewsModel extends IModel {

    /**
     * 根据给定的新闻标签名称, 得到该标签对应的url地址.
     * @param newsTabName 给定的新闻标签名称
     * @return 与给定的新闻标签名称对应的url地址.
     */
    String getTabNewsUrl(String newsTabName, int pageNumber);

    /**
     * 请求获取某一页的新闻数据.
     * @param tabNewsUrl
     * @param newsTypeId
     */
    void requestPageNews(String tabNewsUrl, int pageNumber, String newsTypeId, boolean shouldLoadCache);

    String getNewsTypeId(String newsTabName);

    /**
     * 判断是否需要刷新整个页面的数据. 一般在上一次刷新的时间节点距离当前时间已经很久远时, 需要刷新整个页面的数据.
     * @return
     */
    boolean shouldRefreshWholePageData(String newsTabName, boolean isPullToRefresh);

    long getLastRefreshTimeMillis(String newsTabName);

    /**
     * 更新上一次刷新时间
     */
    void updateLastRefreshTime(String newsTabName);

    /**
     * 获取某个新闻条目的"是否已读过"的状态
     * @param docid
     * @param listener
     */
    void getNewsItemReadState(String docid, NeteaseNewsListAdapter.OnGetNewsItemReadStateListener listener);

    void updateNewsItemToHasReadState(String clickedItemDocId);
}