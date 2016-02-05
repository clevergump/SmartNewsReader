package com.clevergump.newsreader.netease_news.dao.table.news_list;

import android.database.Cursor;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;

import java.util.List;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/12 15:54
 * @projectName NewsReader
 */
public interface INeteaseNewsListDao {

    /**
     * 插入给定的一条新闻条目数据到数据库
     * @param neteaseNewsItem
     */
    void insertDistinctly(NeteaseNewsItem neteaseNewsItem);

    /**
     * 插入给定的新闻条目list数据到数据库
     * @param items
     */
    void insertDistinctly(List<NeteaseNewsItem> items);

    /**
     * 从数据库中获取新闻数据
     * @param newsTypeId 新闻类别
     * @param cachePageNumber 对缓存的新闻数据进行分页后的第几页
     * @param pageItemCount 缓存新闻数据中每一页所包含的新闻条目的数量
     * @return
     */
    List<NeteaseNewsItem> queryRecentPageNews(String newsTypeId, int cachePageNumber, int pageItemCount);

    /**
     * 查询数据库表中是否已经包含了(先前是否已经插入过)给定的新闻条目.
     * @param item
     * @return
     */
    boolean contains(NeteaseNewsItem item);

    Cursor query(String[] columns, String whereColumns, String[] whereArgs);

    List<String> queryImageUrls(String docid);
}