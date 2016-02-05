package com.clevergump.newsreader.netease_news.dao.table;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/4 0:23
 * @projectName NewsReader
 */
public class NewsDetailTableManager {

    // 记录http请求和响应日志的表名称
    public static final String TABLE_NAME = "table_netease_news_detail";

    // 主键docid.
    public static final String FIELD_DOCID = "docid";
    // 新闻详情的标题
    public static final String FIELD_TITLE = "title";
    // 新闻详情的文字内容部分
    public static final String FIELD_BODY = "body";
    // 新闻详情中第一张图片的url地址
    public static final String FIELD_IMG_SRC = "imgSrc";
    // 该新闻的来源
    public static final String FIELD_SOURCE = "source";
    // 该新闻的发布时间
    public static final String FIELD_PTIME = "ptime";

    // 数据库的建表语句
    public static final String SQL_CREATE_TABLE = "create table " + TABLE_NAME +"("
            + FIELD_DOCID + " text primary key, "
            + FIELD_TITLE + " text, "
            + FIELD_BODY + " text, "
            + FIELD_IMG_SRC + " text, "
            + FIELD_SOURCE + " text, "
            + FIELD_PTIME + " text"
            + ")";
}