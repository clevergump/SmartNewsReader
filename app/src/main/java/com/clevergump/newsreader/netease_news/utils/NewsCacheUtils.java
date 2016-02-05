package com.clevergump.newsreader.netease_news.utils;

import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/12 15:03
 * @projectName NewsReader
 */
public class NewsCacheUtils {

    public static boolean hasCache(String newsTabName) {
        String newsTypeId = NewsFragmentManager.getInstance().getNewsTypeId(newsTabName);
        // TODO:
        // 查询数据库该类别的新闻是否有20条数据, 如果有, 就返回true. 否则返回false.
        return false;
    }
}
