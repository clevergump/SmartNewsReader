package com.clevergump.newsreader.netease_news.http;

import android.text.TextUtils;

import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;

/**
 * 用于获取网易新闻相关数据的 URL工具类.
 * 使用fiddler配置手机抓包获取的.  http://www.trinea.cn/android/android-network-sniffer/
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/6 15:47
 * @projectName NewsReader
 */
public class NeteaseNewsUrlUtils {
    // 主机.
    private static final String HOST = "http://c.3g.163.com";

    // 新闻列表页面的URL地址的后半部分. %s需根据具体的新闻类别和翻页的页数来共同决定.
    private static final String PAGE_NEWS_URL_SUFFIX = "/nc/article/list/%s-20.html";

    // 新闻详情页面的URL地址的后半部分. %s需替换为某条具体的新闻的docid.
    private static final String NEWS_DETAIL_URL_SUFFIX = "/nc/article/%s/full.html";

    // 新闻列表页面的URL格式
    // http://c.3g.163.com/nc/article/list/%s-20.html
    public static final String PAGE_NEWS_LIST_URL_FORMAT = HOST + PAGE_NEWS_URL_SUFFIX;

    // 新闻详情页面的URL格式
    // http://c.3g.163.com/nc/article/%s/full.html
    // http://c.3g.163.com/nc/article/docid/full.html
    public static final String NEWS_DETAIL_URL_FORMAT = HOST + NEWS_DETAIL_URL_SUFFIX;

    // 科技：
    // http://c.3g.163.com/nc/article/list/T1348649580692/0-20.html
    // 体育:
    // http://c.3g.163.com/nc/article/list/T1348649079062/0-20.html


    /**
     * 分页加载新闻条目时, 用于获取给定新闻类别(例如: 体育, 军事, 科技...)在给定页(如: 第0页, 第1页...)的
     * json数据的 URL地址. 每页最多能得到20条新闻数据. 注意: 页码从0开始, 不能为负数.
     *
     * 例如: 体育新闻第0页的url:
     * http://c.3g.163.com/nc/article/list/T1348649079062/0-20.html
     *
     * @param newsTabName 要加载的新闻的标签名称
     * @param pageNumber 当前要加载的页码 (页码从0开始).
     * @return 给定新闻类别在给定页的json数据的 URL地址.
     */
    public static String getPageNewsListUrl(String newsTabName, int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("页码不能为负数.");
        }
        String newsTypeId = NewsFragmentManager.getInstance().getNewsTypeId(newsTabName);
        // 每页最多加载20条数据.
        String dynamicParams = newsTypeId + "/" + (pageNumber * 20);
        return String.format(NeteaseNewsUrlUtils.PAGE_NEWS_LIST_URL_FORMAT, dynamicParams);
    }

    /**
     * 获取与给定的docid对应的那篇新闻详情的url地址
     * @param docid 要查询url地址的新闻详情的docid
     * @return 与给定的docid对应的那篇新闻详情的url地址
     */
    public static String getNewsDetailUrl(String docid) {
        if (TextUtils.isEmpty(docid)) {
            throw new IllegalArgumentException(docid + " == null or contains no characters");
        }
        return String.format(NeteaseNewsUrlUtils.NEWS_DETAIL_URL_FORMAT, docid);
    }
}