package com.clevergump.newsreader.netease_news.dao.table;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/4 0:02
 * @projectName NewsReader
 */
public class NewsListTableManager {

    // 记录http请求和响应日志的表名称
    public static final String TABLE_NAME = "table_netease_news_list";

    // 主键自增的id.
    public static final String FIELD_ID = "_id";
    // 所属的新闻类别
    public static final String FIELD_NEWS_TYPE_ID = "newsTypeId";
    // 所属的标签名称
    public static final String FIELD_NEWS_TAB_NAME = "newsTabName";
    // 是否已阅读过这条新闻. 该字段1表示已读, 0表示未读.
    public static final String FIELD_HAS_READ = "hasRead";
    // 该新闻对象在ListView的显示中, 所对应的View的种类.
    public static final String FIELD_LIST_ITEM_VIEW_TYPE = "listItemViewType";
    // 新闻详情页的唯一识别符
    public static final String FIELD_DOC_ID = "docid";
    // 新闻标题
    public static final String FIELD_TITLE = "title";
    // 新闻摘要
    public static final String FIELD_DIGEST = "digest";
    // 图片url
    public static final String FIELD_IMG_SRC = "imgsrc";
    // 图片额外的url0
    public static final String FIELD_IMG_EXTRA_SRC0 = "imgExtraSrc0";
    // 图片额外的url1
    public static final String FIELD_IMG_EXTRA_SRC1 = "imgExtraSrc1";

    // 数据库的建表语句
    public static final String SQL_CREATE_TABLE = "create table "
            + TABLE_NAME +"("
            + FIELD_ID + " integer primary key autoincrement, "
            + FIELD_NEWS_TYPE_ID + " text, "
            + FIELD_NEWS_TAB_NAME + " text, "
            + FIELD_HAS_READ + " integer, "
            + FIELD_LIST_ITEM_VIEW_TYPE + " integer, "
            + FIELD_DOC_ID + " text, "
            + FIELD_TITLE + " text, "
            + FIELD_DIGEST + " text, "
            + FIELD_IMG_SRC + " text, "
            + FIELD_IMG_EXTRA_SRC0 + " text, "
            + FIELD_IMG_EXTRA_SRC1 + " text)";
}