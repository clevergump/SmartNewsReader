package com.clevergump.newsreader.netease_news.cache;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;

import java.util.List;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/10 9:39
 * @projectName NewsReader
 */
public interface IPageNewsListCache {

    /**
     * 从缓存中获取
     * @param newsTypeId
     * @param pageNumber
     * @return
     */
    List<NeteaseNewsItem> getNewsListFromCache(String newsTypeId, int pageNumber);

    /**
     * 存入缓存
     * @param newsTypeId
     * @param newsItemList
     */
    void putNewsListToCache(String newsTypeId, List<NeteaseNewsItem> newsItemList);


    /*********** 主要用于加载 NeteaseImageDetailActivity 时使用 **********************/

    /**
     * 从缓存中获取与给定docid对应的新闻条目的图片url组成的list
     * @param docid
     * @return
     */
    List<String> getNewsItemImageUrlsFromCache(String docid);

    /**
     * 将给定新闻条目的 "docid-图片url的list" 组成的键值对存入缓存.
     * @param newsItem
     */
    void putNewsItemImageUrlsToCache(NeteaseNewsItem newsItem);
}
