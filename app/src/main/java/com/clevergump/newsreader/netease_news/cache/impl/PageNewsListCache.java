package com.clevergump.newsreader.netease_news.cache.impl;

import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.MyApplication;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsBase;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.dao.table.news_list.impl.NeteaseNewsListDaoImpl;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 新闻list页面的缓存处理类
 * 该类的测试类见 ../../test/PageNewsListCacheImpl.java
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/10 9:41
 * @projectName NewsReader
 */
public class PageNewsListCache extends PageNewsListCacheBase {

    // 新闻条目的内存缓存
    // key: 新闻条目的typeId, value: 属于key类型的一系列新闻条目.
    private LruCache<String, List<NeteaseNewsItem>> mNewsListMemCache;

    // 存放3张图片的新闻条目的图片URL地址的容器. 主要用于从新闻条目List页面点击了包含3张图片的那些条目
    // 进入图片详情页面时, 作为图片url的内存缓存容器.
    // key: docid
    // value: 3张图片那些新闻条目的图片URL地址(不是md5值, 因为图片缓存的调用者不需要知道缓存框架在存储图片
    // 时对该图片url的处理, 调用者只需要知道传入图片的原始URL地址, 就能得到缓存容器中已存放的该图片即可)
    private LruCache<String, List<String>> mThreeImageNewsItemImageUrlsContainer;


    /******************************* 单例 *****************************************/
    private PageNewsListCache() {
        initMemCache();
        initImageUrlContainer();
    }

    public static PageNewsListCache getInstance () {
        return PageNewsListCacheImplHolder.INSTANCE;
    }

    private static class PageNewsListCacheImplHolder {
        private static final PageNewsListCache INSTANCE = new PageNewsListCache();
    }


    /******************************* public方法 ******************************************/

    /**
     * 从缓存中获取
     * @param newsTypeId
     * @param pageNumber
     * @return
     */
    @Override
    public List<NeteaseNewsItem> getNewsListFromCache(String newsTypeId, int pageNumber) {
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no characters.");
        }
        if (pageNumber < 0) {
            throw new IllegalArgumentException("页码" + pageNumber + "不能是负数");
        }

        List<NeteaseNewsItem> memCache = getNewsListFromMemCache(newsTypeId, pageNumber);
        if (memCache != null) {
            return memCache;
        }
        List<NeteaseNewsItem> diskCache = getNewsListFromDiskCache(newsTypeId, pageNumber);
        if (diskCache != null && diskCache.size() > 0) {
            putNewsListToMemCache(newsTypeId, diskCache);
        }
        return diskCache;
    }

    /**
     * 存入缓存
     * @param newsTypeId
     * @param newsItemList
     */
    @Override
    public void putNewsListToCache(String newsTypeId, List<NeteaseNewsItem> newsItemList) {
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no characters.");
        }
        if (newsItemList == null || newsItemList.size() <= 0) {
            throw new IllegalArgumentException(newsItemList + " == null or contains no elements");
        }
        putNewsListToMemCache(newsTypeId, newsItemList);
        putNewsListToDiskCache(newsTypeId, newsItemList);
    }

    /**
     * 从新闻条目的图片url缓存中获取与给定的docid所对应的图片url list. 如果返回null, 表示缓存中不存在
     * 与给定的docid所对应的缓存.
     *
     * @param docid 给定的新闻条目docid, 也是新闻条目中图片url缓存容器的key.
     * @return URL缓存中与给定的docid所对应的新闻条目图片的url list. 如果返回null, 则表示缓存中
     *      没有与给定docid对应的缓存.
     */
    @Override
    public List<String> getNewsItemImageUrlsFromCache(String docid) {
        if (TextUtils.isEmpty(docid)) {
            throw new IllegalArgumentException(docid + " == null or contains no characters");
        }
        List<String> memCachedImageUrls = getNewsItemImageUrlsFromMemCache(docid);
        if (memCachedImageUrls != null && memCachedImageUrls.size() > 0) {
            return memCachedImageUrls;
        }
        // 如果内存缓存中没有要查询的图片url list, 则去查找硬盘缓存.
        List<String> diskCachedImageUrls = getNewsItemImageUrlsFromDiskCache(docid);
        if (diskCachedImageUrls != null) {
            putNewsItemImageUrlsToMemCache(docid, diskCachedImageUrls);
        }
        return diskCachedImageUrls;
    }

    @Override
    public void putNewsItemImageUrlsToCache(NeteaseNewsItem threeImageNewsItem) {
        if (threeImageNewsItem == null) {
            throw new IllegalArgumentException(threeImageNewsItem + " == null");
        }
        // 如果该新闻条目不是3张图片的类型, 则直接忽略.
        if (threeImageNewsItem.newsItemViewType != NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_THREE_IMAGE) {
            return;
        }

        String docid = threeImageNewsItem.docid;
        List<String> imageUrls = new LinkedList<String>();
        imageUrls.add(threeImageNewsItem.imgsrc);
        imageUrls.add(threeImageNewsItem.imgExtraSrc0);
        imageUrls.add(threeImageNewsItem.imgExtraSrc1);

        mThreeImageNewsItemImageUrlsContainer.put(docid, imageUrls);
    }

    /*************************** private方法 **********************************/

    /******************** 初始化 ********************/
    /**
     * 初始化内存缓存
     */
    private void initMemCache() {
        if (mNewsListMemCache == null) {
            // 设置新闻条目的内容缓存容量为 500KB.
            this.mNewsListMemCache = new LruCache<String, List<NeteaseNewsItem>>(500 * 1024) {
                @Override
                protected int sizeOf(String key, List<NeteaseNewsItem> newsItemList) {
                    if (newsItemList != null && newsItemList.size() > 0) {
                        // 一个新闻条目的字节数
                        return newsItemList.get(0).toString().getBytes().length;
                    }
                    // 默认情况下认为在新闻ListView中的一个新闻条目的字节数为300字节 (不是新闻详情).
                    return 300;
                }
            };
        }
    }

    /**
     * 初始化图片原始URL的容器
     */
    private void initImageUrlContainer() {
        if (mThreeImageNewsItemImageUrlsContainer == null) {
            // 一条3张图片的新闻条目的3个图片url组成的list的大小约为300字节.
            // 这里设置缓存的3张图片的新闻条目数是 500 条新闻. 缓存总容量小于 300字节 * 500 ≈ 150KB,
            // 缓存总容量也就相当于一张普通图片的大小.
            mThreeImageNewsItemImageUrlsContainer = new LruCache<String, List<String>>(500);
        }
    }

    /******************** NewsList 内存缓存+硬盘缓存 方法 ********************/
    /**
     * 将给定的新闻类别以及该类别下的新闻条目list组成的键值对存入内存缓存.
     * @param newsTypeId
     * @param newsItemList
     */
    private void putNewsListToMemCache(String newsTypeId, List<NeteaseNewsItem> newsItemList) {
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no characters.");
        }
        if (newsItemList == null || newsItemList.size() <= 0) {
            throw new IllegalArgumentException(newsItemList + " == null or contains no elements");
        }

        List<NeteaseNewsItem> newCache = newsItemList;
        List<NeteaseNewsItem> previousCache = mNewsListMemCache.get(newsTypeId);
        // 如果缓存中先前已存储了该类别的新闻, 那么需要对将要存入的缓存值进行一定的处理．
        if (previousCache != null) {
            // 将新元素list 续接(append)到先前已存储的缓存元素list中
            previousCache.addAll(newsItemList);
            newCache = previousCache;
        }
        mNewsListMemCache.put(newsTypeId, newCache);

        String newsTabName = NewsFragmentManager.getInstance().getNewsTabName(newsTypeId);
        String msgContent = newsTabName + "新闻list存入内存缓存";
        Message msg = MyApplication.mToastDebugHandler.obtainMessage(0, msgContent);
        MyApplication.mToastDebugHandler.sendMessage(msg);
    }

    /**
     * 从内存缓存中获取与特定类别的新闻在特定页的新闻条目list.
     * 如果缓存中没有, 则会返回 null.
     * @param newsTypeId 新闻类别
     * @param pageNumber 页码
     * @return
     */
    private List<NeteaseNewsItem> getNewsListFromMemCache(String newsTypeId, int pageNumber) {
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no characters.");
        }
        if (pageNumber < 0) {
            throw new IllegalArgumentException("页码" + pageNumber + "不能是负数");
        }

        List<NeteaseNewsItem> allMemCache = mNewsListMemCache.get(newsTypeId);
        // 如果内存缓存中没有该类型的新闻, 则直接返回null表示内存缓存中没有.
        if (allMemCache == null) {
            return null;
        }
        int memCacheSize = allMemCache.size();
        int requestedLastIndex = (pageNumber + 1) * Constant.PAGE_NEWS_ITEM_COUNT - 1;

        // 如果内存缓存中新闻条目数量不足, 则直接认为内存缓存中没有该页的条目.
        if (requestedLastIndex + 1 > memCacheSize) {
            return null;
        }
        List<NeteaseNewsItem> result = new ArrayList<NeteaseNewsItem>(Constant.PAGE_NEWS_ITEM_COUNT);
        int startIndexIncluded = pageNumber * Constant.PAGE_NEWS_ITEM_COUNT;
        int endIndexExcluded = (pageNumber + 1) * Constant.PAGE_NEWS_ITEM_COUNT;
        List<NeteaseNewsItem> specificPageCacheList = allMemCache.subList(startIndexIncluded, endIndexExcluded);
        result.addAll(specificPageCacheList);
        return result;
    }

    /**
     * 将新闻条目list对象存入硬盘缓存
     * @param newsTypeId
     * @param newsItemList
     */
    private void putNewsListToDiskCache(String newsTypeId, List<NeteaseNewsItem> newsItemList) {
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no characters.");
        }
        if (newsItemList == null || newsItemList.size() <= 0) {
            throw new IllegalArgumentException(newsItemList + " == null or contains no elements");
        }

        NeteaseNewsListDaoImpl.getInstance().insertDistinctly(newsItemList);

        String newsTabName = NewsFragmentManager.getInstance().getNewsTabName(newsTypeId);
        String msgContent = newsTabName + "新闻list存入数据库";
        Message msg = MyApplication.mToastDebugHandler.obtainMessage(0, msgContent);
        MyApplication.mToastDebugHandler.sendMessage(msg);
    }

    /**
     * 从硬盘缓存中获取
     * @param newsTypeId
     * @param pageNumber
     * @return
     */
    private List<NeteaseNewsItem> getNewsListFromDiskCache(String newsTypeId, int pageNumber) {
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no characters.");
        }
        if (pageNumber < 0) {
            throw new IllegalArgumentException("页码" + pageNumber + "不能是负数");
        }

        List<NeteaseNewsItem> items = NeteaseNewsListDaoImpl.getInstance()
                .queryRecentPageNews(newsTypeId, pageNumber, Constant.PAGE_NEWS_ITEM_COUNT);
//        BaseNewsListEvent event = new OnGetCachedNewsListEvent(newsTypeId, items);
//        EventBusUtils.post(event);
//        return null;
        return items;
    }

    /******************** ThreeImageNewsItemImageUrls 内存缓存+硬盘缓存 方法 ********************/

    private List<String> getNewsItemImageUrlsFromMemCache(String docid) {
        return mThreeImageNewsItemImageUrlsContainer.get(docid);
    }

    /**
     * 从硬盘缓存中查询与给定docid对应的新闻条目中包含的所有图片的url地址所组成的list. 如果硬盘缓存中没有存储
     * 相关内容或者查询失败, 则返回null.
     * @param docid
     * @return
     */
    private List<String> getNewsItemImageUrlsFromDiskCache(String docid) {
        List<String> imageUrls = NeteaseNewsListDaoImpl.getInstance().queryImageUrls(docid);
        return imageUrls;
    }

    private void putNewsItemImageUrlsToMemCache(String docid, List<String> newsItemImageUrls) {
        mThreeImageNewsItemImageUrlsContainer.put(docid, newsItemImageUrls);
    }

    // putNewsItemImageUrlsToDiskCache 的操作直接在存储新闻list到数据库时就已经完成了.
}