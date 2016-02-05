package com.clevergump.newsreader.netease_news.dao.db;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/18 16:25
 * @projectName NewsReader
 */
public interface IDbInfo {
    // 获取数据库名称
    String getDbName();

    // 获取数据库的版本号
    int getDbVersion();
}
