package com.clevergump.newsreader.netease_news.dao.table.news_detail;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/21 15:57
 * @projectName NewsReader
 */
public interface INeteaseNewsDetailDao {

    /**
     * 插入给定的一条新闻详情到数据库
     * @param neteaseNewsDetail 给定的新闻详情对象
     */
    void insertDistinctly(NeteaseNewsDetail neteaseNewsDetail);

    /**
     * 查询数据库表中是否已经包含了(先前是否已经插入过)给定的新闻详情.
     * @param docid 给定的新闻详情的docid
     * @return
     */
    boolean contains(String docid);

    /**
     * 查询数据库表中是否已经包含了(先前是否已经插入过)给定的新闻详情.
     * @param neteaseNewsDetail 给定的新闻详情对象
     * @return
     */
    boolean contains(NeteaseNewsDetail neteaseNewsDetail);

    /**
     * 根据给定的docid, 查询缓存中与该docid相对应的那篇新闻文章的详情实体对象.
     * @param docid 给定的新闻详情的docid
     */
    NeteaseNewsDetail getNewsDetail(String docid);
}