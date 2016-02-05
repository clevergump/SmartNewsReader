package com.clevergump.newsreader.netease_news.dao.table.news_detail.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail.ImageDetail;
import com.clevergump.newsreader.netease_news.dao.db.DbInfoImpl;
import com.clevergump.newsreader.netease_news.dao.helper.NeteaseNewsDbHelper;
import com.clevergump.newsreader.netease_news.dao.table.NewsDetailTableManager;
import com.clevergump.newsreader.netease_news.dao.table.news_detail.INeteaseNewsDetailDao;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/21 15:57
 * @projectName NewsReader
 */
public class NeteaseNewsDetailDaoImpl implements INeteaseNewsDetailDao {
    private final String TAG = getClass().getSimpleName();


    private static NeteaseNewsDetailDaoImpl sInstance;
    private final NeteaseNewsDbHelper mDbHelper;

    private Context mContext;

    private NeteaseNewsDetailDaoImpl() {
        this.mDbHelper = NeteaseNewsDbHelper.getInstance(DbInfoImpl.getInstance());
    }

    public static NeteaseNewsDetailDaoImpl getInstance() {
        if (sInstance == null) {
            synchronized (NeteaseNewsDetailDaoImpl.class) {
                if (sInstance == null) {
                    sInstance = new NeteaseNewsDetailDaoImpl ();
                }
            }
        }
        return sInstance;
    }

    /**
     * 插入一条新闻详情数据到数据库中
     * @param neteaseNewsDetail 给定的新闻详情对象
     */
    @Override
    public void insertDistinctly(NeteaseNewsDetail neteaseNewsDetail) {
        if (neteaseNewsDetail == null) {
            throw new IllegalArgumentException(neteaseNewsDetail + " == null");
        }
//        // 如果已经包含了该条目, 则直接退出.
//        if (contains(neteaseNewsDetail)) {
//            return;
//        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues(6);
            values.put(NewsDetailTableManager.FIELD_DOCID, neteaseNewsDetail.docid);
            values.put(NewsDetailTableManager.FIELD_TITLE, neteaseNewsDetail.title);
            values.put(NewsDetailTableManager.FIELD_BODY, neteaseNewsDetail.body);
            values.put(NewsDetailTableManager.FIELD_SOURCE, neteaseNewsDetail.source);
            values.put(NewsDetailTableManager.FIELD_PTIME, neteaseNewsDetail.ptime);
            // 如果有图片(不论是只有一张还是有多张), 则只存入第一张图片的url地址.
            if (neteaseNewsDetail.img != null && neteaseNewsDetail.img.size()>0
                    && neteaseNewsDetail.img.get(0) != null) {
                values.put(NewsDetailTableManager.FIELD_IMG_SRC, neteaseNewsDetail.img.get(0).src);
            }

            db.insert(NewsDetailTableManager.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 查询缓存中是否包含与给定的docid对应的新闻详情
     * @param docid 给定的docid
     * @return
     */
    @Override
    public boolean contains(String docid) {
        if (TextUtils.isEmpty(docid)) {
            throw new IllegalArgumentException(docid + " == null or contains no characters");
        }
        Cursor cursor = null;
//        try {
            cursor = getCursor(docid);
            if (cursor != null && cursor.getCount() > 0) {
                return true;
            }
            return false;
//        }
//        finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
    }

    /**
     * 查询缓存中是否包含给定的新闻详情
     * @param neteaseNewsDetail 给定的新闻详情对象
     * @return
     */
    @Override
    public boolean contains(NeteaseNewsDetail neteaseNewsDetail) {
        if (neteaseNewsDetail == null) {
            throw new IllegalArgumentException(neteaseNewsDetail + " == null");
        }
        return contains(neteaseNewsDetail.docid);
    }

    @Override
    public NeteaseNewsDetail getNewsDetail(String docid) {
        if (TextUtils.isEmpty(docid)) {
            throw new IllegalArgumentException(docid + " == null or contains no characters");
        }
        Cursor cursor = null;
        try {
            cursor = getCursor(docid);
            if (cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                NeteaseNewsDetail detail = new NeteaseNewsDetail();
                detail.docid = docid;
                detail.title = cursor.getString(cursor.getColumnIndex(NewsDetailTableManager.FIELD_TITLE));
                detail.source = cursor.getString(cursor.getColumnIndex(NewsDetailTableManager.FIELD_SOURCE));
                detail.ptime = cursor.getString(cursor.getColumnIndex(NewsDetailTableManager.FIELD_PTIME));
                detail.body = cursor.getString(cursor.getColumnIndex(NewsDetailTableManager.FIELD_BODY));
                String imgSrc = cursor.getString(cursor.getColumnIndex(NewsDetailTableManager.FIELD_IMG_SRC));
                detail.img.add(new ImageDetail(imgSrc));

                return detail;
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 获取数据库查询相关的Cursor对象
     * @param docid 给定的docid
     * @return
     */
    private Cursor getCursor(String docid) {
        if (TextUtils.isEmpty(docid)) {
            throw new IllegalArgumentException(docid + " == null or contains no characters");
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(true, NewsDetailTableManager.TABLE_NAME, null,
                NewsDetailTableManager.FIELD_DOCID + "=?", new String[]{docid},
                null, null, null, null);
        return cursor;
    }
}