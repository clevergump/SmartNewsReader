package com.clevergump.newsreader.netease_news.dao.helper;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.clevergump.newsreader.MyApplication;
import com.clevergump.newsreader.netease_news.dao.db.IDbInfo;
import com.clevergump.newsreader.netease_news.dao.table.NewsDetailTableManager;
import com.clevergump.newsreader.netease_news.dao.table.NewsListTableManager;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/17 19:10
 * @projectName NewsReader
 */
public class NeteaseNewsDbHelper extends SQLiteOpenHelper {

    private static volatile NeteaseNewsDbHelper sInstance;

    private NeteaseNewsDbHelper(IDbInfo dbInfo) {
        super(MyApplication.getAppContext(), dbInfo.getDbName(), null, dbInfo.getDbVersion());
    }

    public static NeteaseNewsDbHelper getInstance(IDbInfo dbInfo) {
        if (sInstance == null) {
            synchronized (NeteaseNewsDbHelper.class) {
                if (sInstance == null) {
                    sInstance = new NeteaseNewsDbHelper(dbInfo);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NewsListTableManager.SQL_CREATE_TABLE);
        db.execSQL(NewsDetailTableManager.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}