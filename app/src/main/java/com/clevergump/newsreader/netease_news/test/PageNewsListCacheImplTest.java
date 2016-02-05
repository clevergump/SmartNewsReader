package com.clevergump.newsreader.netease_news.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ../cache/impl/PageNewsListCacheImpl.java类的java测试类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/10 14:52
 * @projectName NewsReader
 */
public class PageNewsListCacheImplTest {
    private static final int PAGE_NEWS_ITEM_COUNT = 10;
    private static final String KEY_A = "a";
    private static final String KEY_B = "b";
    private static final String KEY_C = "c";

    private static Map<String, List<Integer>> mMemCache = new HashMap<String, List<Integer>>();

    public static void main(String[] args) {
//		subListElementCountTest();
//		testGetFromCache();
        testPutToCache();
    }

    private static void testPutToCache() {
        initCache();
        System.out.println(mMemCache.get(KEY_B));
        System.out.println(getFromMemCache(KEY_B, 2));

        List<Integer> newData = new ArrayList<Integer>();
        for (int i = 100; i < 110; i++) {
            newData.add(i);
        }
        putToMemCache(KEY_B, newData);
        System.out.println(mMemCache.get(KEY_B));
        System.out.println(getFromMemCache(KEY_B, 2));
    }

    private static void testGetFromCache() {
        initCache();
        List<Integer> memCache = getFromMemCache(KEY_B, 2);
        System.out.println(memCache == null ? "null" : memCache);
    }

    private static void subListElementCountTest() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        List<Integer> newList = new ArrayList<>();
        newList.addAll(list.subList(10, 20));
        System.out.println(newList.size());
    }

    private static void initCache() {
        List<Integer> listA = new ArrayList<Integer>();
        List<Integer> listB = new ArrayList<Integer>();
        List<Integer> listC = new ArrayList<Integer>();

        for (int i = 0; i < 100; i++) {
            listA.add(i);
        }
        for (int i = 0; i < 25; i++) {
            listB.add(i);
        }
        for (int i = 0; i < 32; i++) {
            listC.add(i);
        }

        mMemCache.put(KEY_A, listA);
        mMemCache.put(KEY_B, listB);
        mMemCache.put(KEY_C, listC);
    }

    private static List<Integer> getFromMemCache(String key, int pageNumber) {
        List<Integer> allMemCache = mMemCache.get(key);
        int memCacheSize = allMemCache.size();
        int requestedLastIndex = (pageNumber + 1) * PAGE_NEWS_ITEM_COUNT - 1;

        // 如果内存缓存中新闻条目数量不足, 则直接认为内存缓存中没有该页的条目.
        if (requestedLastIndex + 1 > memCacheSize) {
            return null;
        }
        List<Integer> result = new ArrayList<Integer>(PAGE_NEWS_ITEM_COUNT);
        int startIndexIncluded = pageNumber * PAGE_NEWS_ITEM_COUNT;
        int endIndexExcluded = (pageNumber + 1) * PAGE_NEWS_ITEM_COUNT;
        List<Integer> specificPageCacheList =
                allMemCache.subList(startIndexIncluded, endIndexExcluded);
        result.addAll(specificPageCacheList);
        return result;
    }

    private static void putToMemCache(String key, List<Integer> newsItemList) {
        List<Integer> newCache = newsItemList;
        List<Integer> previousCache = mMemCache.get(key);
        // 如果缓存中先前已存储了该类别的新闻, 那么需要对将要存入的缓存值进行一定的处理．
        if (previousCache != null) {
            // 将新元素list 续接(append)到先前已存储的缓存元素list中
            previousCache.addAll(newsItemList);
            newCache = previousCache;
        }
        mMemCache.put(key, newCache);
    }
}