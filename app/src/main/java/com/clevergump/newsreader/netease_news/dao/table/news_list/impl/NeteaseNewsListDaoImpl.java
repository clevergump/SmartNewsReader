package com.clevergump.newsreader.netease_news.dao.table.news_list.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.dao.db.DbInfoImpl;
import com.clevergump.newsreader.netease_news.dao.helper.NeteaseNewsDbHelper;
import com.clevergump.newsreader.netease_news.dao.table.NewsListTableManager;
import com.clevergump.newsreader.netease_news.dao.table.news_list.INeteaseNewsListDao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 网易新闻数据库的增删改查等Dao操作的实现类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/18 19:08
 * @projectName NewsReader
 *
 *
 * SQLite数据库事务操作的步骤:
 *
 *   db.beginTransaction();
 *   try {
 *     ...
 *     db.setTransactionSuccessful();
 *   } finally {
 *     db.endTransaction();
 *   }
 */
public class NeteaseNewsListDaoImpl implements INeteaseNewsListDao {

    private final String TAG = getClass().getSimpleName();

    private Context mContext;
    private final NeteaseNewsDbHelper mDbHelper;
    private static NeteaseNewsListDaoImpl sInstance;

    private List<String> newsItemImageUrls = new ArrayList<String>();

    private NeteaseNewsListDaoImpl() {
        this.mDbHelper = NeteaseNewsDbHelper.getInstance(DbInfoImpl.getInstance());
    }

    public static NeteaseNewsListDaoImpl getInstance() {
        if (sInstance == null) {
            synchronized (NeteaseNewsListDaoImpl.class) {
                if (sInstance == null) {
                    sInstance = new NeteaseNewsListDaoImpl();
                }
            }
        }
        return sInstance;
    }

    /**
     * 插入一条新闻记录
     * @param neteaseNewsItem 一个新闻条目的对象
     */
    @Override
    public void insertDistinctly(NeteaseNewsItem neteaseNewsItem) {
        if (neteaseNewsItem == null) {
            throw new IllegalArgumentException(neteaseNewsItem + " == null");
        }
        // 如果已经包含了该条目, 则直接退出.
        if (contains(neteaseNewsItem)) {
            return;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            insertOneRecord(neteaseNewsItem, db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 插入多条新闻记录
     * @param neteaseNewsItems 多个新闻条目的对象的 list
     */
    @Override
    public void insertDistinctly(List<NeteaseNewsItem> neteaseNewsItems) {
        if (neteaseNewsItems == null) {
            throw new IllegalArgumentException(neteaseNewsItems + " == null");
        }
        if (neteaseNewsItems.size() <= 0) {
            throw new IllegalArgumentException(neteaseNewsItems + "包含的新闻条目个数不是正数");
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (NeteaseNewsItem item : neteaseNewsItems) {
                if (!contains(item)) {
                    insertOneRecord(item, db);
                }
                else {
//                    LogUtils.i(TAG, "重复条目:" + item);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 在指定的每页新闻数量的前提下, 获取给定类型给定页码的最新新闻数据.
     *
     * @param newsTypeId 新闻类别的id
     * @param cachePageNumber 符合筛选条件要求的一系列新闻条目进行分页后的特定页码(页码从0开始).
     *                   通俗地讲, 就是对缓存数据经过分页后, 要获取第几页的数据.
     * @param pageItemCount 分页时每页包含的新闻条目的数量
     * @return
     *
     * select distinct * from table_netease_news order by newsTypeId asc limit 1,2
     */
    @Override
    public List<NeteaseNewsItem> queryRecentPageNews(String newsTypeId, int cachePageNumber, int pageItemCount) {
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no characters.");
        }
        if (cachePageNumber < 0) {
            throw new IllegalArgumentException("页码" + cachePageNumber + "不能是负数");
        }
        if (pageItemCount <= 0) {
            throw new IllegalArgumentException("要查询的条数"+pageItemCount+"不是正数");
        }
        List<NeteaseNewsItem> neteaseNewsItems = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int pageStartIndex = cachePageNumber * pageItemCount;
        Cursor cursor = null;
        try {
            String selection = NewsListTableManager.FIELD_NEWS_TYPE_ID + "=?";
            String[] selectionArgs = {newsTypeId};
            String orderBy = NewsListTableManager.FIELD_ID + " asc";
            String limit = pageStartIndex + ", " + pageItemCount;
            cursor = db.query(true, NewsListTableManager.TABLE_NAME, null, selection, selectionArgs, null, null, orderBy, limit);
            neteaseNewsItems = getNewsItems(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return neteaseNewsItems;

//        // TODO: 取缓存.
//        NewsItem item = new NewsItem.Builder()
//                .setTitle("标题")
//                .setDigest("摘要")
//                .setImageSrcUrl("imageSrcUrl")
//                .build();
//        List<NewsItem> newsItems = new LinkedList<NewsItem>();
//        newsItems.add(item);
//        return newsItems;
    }

    @Override
    public boolean contains(NeteaseNewsItem item) {
        if (item == null) {
            throw new IllegalArgumentException(item + " == null ");
        }
        int rowCount;
        Cursor cursor = null;
        try {
            String docid = item.docid;
            cursor = query(new String[]{NewsListTableManager.FIELD_DOC_ID},
                    NewsListTableManager.FIELD_DOC_ID + "=?", new String[]{docid});
            rowCount = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return rowCount > 0;
    }

    @Override
    public Cursor query(String[] columns, String whereColumns, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(true, NewsListTableManager.TABLE_NAME, columns, whereColumns, whereArgs,
                null, null, null, null);
        return cursor;
    }

    /**
     * 根据新闻条目的docid, 查询与该docid所对应的那条新闻条目的所有图片url地址组成的list. 如果数据库中没有存储
     * 与该docid相关的记录或者查询失败, 则会返回null.
     * @param docid
     * @return
     */
    @Override
    public List<String> queryImageUrls(String docid) {
        if (TextUtils.isEmpty(docid)) {
            throw new IllegalArgumentException(docid + " == null or contains no characters.");
        }

        Cursor cursor = query(new String[]{NewsListTableManager.FIELD_IMG_SRC,
                        NewsListTableManager.FIELD_IMG_EXTRA_SRC0,
                        NewsListTableManager.FIELD_IMG_EXTRA_SRC1},
                NewsListTableManager.FIELD_DOC_ID + "=?", new String[]{docid});
        if (cursor != null && cursor.moveToFirst()) {
            String imgSrc = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_IMG_SRC));
            String imgExtraSrc0 = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_IMG_EXTRA_SRC0));
            String imgExtraSrc1 = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_IMG_EXTRA_SRC1));

            addNonNullUrlToContainer(newsItemImageUrls, imgSrc);
            addNonNullUrlToContainer(newsItemImageUrls, imgExtraSrc0);
            addNonNullUrlToContainer(newsItemImageUrls, imgExtraSrc1);

            return newsItemImageUrls;
        }
        return null;
    }

    /**
     * 将非null的url地址添加到url容器中
     * @param imageNonNullUrlsContainer
     * @param imgsrc
     */
    private void addNonNullUrlToContainer(List<String> imageNonNullUrlsContainer, String imgsrc) {
        if (!TextUtils.isEmpty(imgsrc)) {
            imageNonNullUrlsContainer.add(imgsrc);
        }
    }

    private List<NeteaseNewsItem> getNewsItems(Cursor cursor) {
        List<NeteaseNewsItem> neteaseNewsItems = new LinkedList<NeteaseNewsItem>();
        while (cursor.moveToNext()) {
            NeteaseNewsItem item = new NeteaseNewsItem.Builder().build();
            item.hasRead = cursor.getInt(cursor.getColumnIndex(
                    NewsListTableManager.FIELD_HAS_READ)) == 1 ? true : false;
            item.newsItemViewType = cursor.getInt(cursor.getColumnIndex(
                    NewsListTableManager.FIELD_LIST_ITEM_VIEW_TYPE));
            item.docid = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_DOC_ID));
            item.title = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_TITLE));
            item.digest = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_DIGEST));
            item.imgsrc = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_IMG_SRC));
            item.imgExtraSrc0 = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_IMG_EXTRA_SRC0));
            item.imgExtraSrc1 = cursor.getString(cursor.getColumnIndex(NewsListTableManager.FIELD_IMG_EXTRA_SRC1));
            neteaseNewsItems.add(item);
        }
        return neteaseNewsItems;
    }

    /**
     * 插入一行新闻数据的记录
     * @param neteaseNewsItem 新闻数据
     * @param db 数据库
     */
    private void insertOneRecord(NeteaseNewsItem neteaseNewsItem, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(NewsListTableManager.FIELD_NEWS_TYPE_ID, neteaseNewsItem.newsTypeId);
        values.put(NewsListTableManager.FIELD_NEWS_TAB_NAME, neteaseNewsItem.newsTabName);
        values.put(NewsListTableManager.FIELD_HAS_READ, neteaseNewsItem.hasRead);
        values.put(NewsListTableManager.FIELD_LIST_ITEM_VIEW_TYPE, neteaseNewsItem.newsItemViewType);
        values.put(NewsListTableManager.FIELD_DOC_ID, neteaseNewsItem.docid);
        values.put(NewsListTableManager.FIELD_TITLE, neteaseNewsItem.title);
        values.put(NewsListTableManager.FIELD_DIGEST, neteaseNewsItem.digest);
        values.put(NewsListTableManager.FIELD_IMG_SRC, neteaseNewsItem.imgsrc);
        values.put(NewsListTableManager.FIELD_IMG_EXTRA_SRC0, neteaseNewsItem.imgExtraSrc0);
        values.put(NewsListTableManager.FIELD_IMG_EXTRA_SRC1, neteaseNewsItem.imgExtraSrc1);

        db.insert(NewsListTableManager.TABLE_NAME, null, values);
    }

    /**
     * 设置给定docId对应的新闻条目的是否已读的状态为"已读"
     * @param beenReadNewsItemDocId
     */
    public void updateNewsItemToHasReadState(String beenReadNewsItemDocId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            // 1表示已读, 0表示未读
            values.put(NewsListTableManager.FIELD_HAS_READ, 1);
            db.update(NewsListTableManager.TABLE_NAME, values, NewsListTableManager.FIELD_DOC_ID + "=?",
                    new String[]{beenReadNewsItemDocId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 查询给定的docid所对应的那个新闻条目是否已读的状态. 如果已读, 则返回1; 未读则返回0.
     * @param docId
     * @return
     */
    public int queryNewsItemReadState(String docId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = null;
        int hasRead = 0;
        try {
            cursor = db.query(true, NewsListTableManager.TABLE_NAME,
                    new String[]{NewsListTableManager.FIELD_HAS_READ},
                    NewsListTableManager.FIELD_DOC_ID + "=?", new String[]{docId},
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                hasRead = cursor.getInt(cursor.getColumnIndex(NewsListTableManager.FIELD_HAS_READ));
            }
            return hasRead;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}