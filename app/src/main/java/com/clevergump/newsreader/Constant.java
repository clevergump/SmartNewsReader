package com.clevergump.newsreader;

/**
 * 存储所有公共常量的接口
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/6 19:41
 * @projectName NewsReader
 */
public interface Constant {
    // 调试开关
    boolean DEBUG = true;

    // 是否是只调试 ptrListView的下拉功能及数据显示功能, 即: 忽略http联网加载的功能.
    boolean isTestPtrListViewOnly = false;

    // 新闻分页加载, 每一页的新闻条目数
    int PAGE_NEWS_ITEM_COUNT = 20;

    // 下拉刷新时需要进行网络请求的最小时间间隔(毫秒数)
    long PULL_TO_REFRESH_REQUEST_INTERVAL = 2 * 60 * 1000;

    // 以startActivityForResult()方式启动 NewsDetailActivity 时的请求码
    int REQUEST_CODE_OPEN_NEWS_DETAIL_ACTIVITY = 0;

    // 以startActivityForResult()方式启动 ImageDetailActivity 时的请求码
    int REQUEST_CODE_OPEN_IMAGE_DETAIL_ACTIVITY = 1;

    // 从新闻列表页面点击某一个特定新闻条目, 打开该条目的详情页面时要传递的Intent中携带的参数docid对应的key.
    String KEY_NETEASE_NEWS_DETAIL_DOCID = "key_netease_news_detail_docid";
    // 从新闻列表页面点击某一个特定新闻条目, 打开该条目的详情页面时要传递的Intent中携带的参数title对应的key.
    String KEY_NETEASE_NEWS_DETAIL_TITLE = "key_netease_news_detail_title";
    // 从新闻列表页面点击某一个特定新闻条目, 打开该条目的详情页面时要传递的Intent中携带的该条目中包含的所有
    // 图片的url地址组成的list, 所对应的key.
    String KEY_NETEASE_NEWS_DETAIL_IMAGE_URLS = "key_netease_news_detail_image_urls";
}