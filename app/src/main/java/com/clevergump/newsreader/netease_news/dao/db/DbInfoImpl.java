package com.clevergump.newsreader.netease_news.dao.db;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/18 16:26
 * @projectName NewsReader
 */
public class DbInfoImpl implements IDbInfo {

    // 数据库的名称
    private final String DB_NAME = "netease_news.db";

    // 数据库版本号
    private final int DB_VERSION = 1;

    private DbInfoImpl(){
    }

    public static DbInfoImpl getInstance(){
        return DbInfoImplHolder.INSTANCE;
    }

    private static class DbInfoImplHolder {
        private static final DbInfoImpl INSTANCE = new DbInfoImpl();
    }

    @Override
    public String getDbName() {
        return DB_NAME;
    }

    @Override
    public int getDbVersion() {
        return DB_VERSION;
    }
}