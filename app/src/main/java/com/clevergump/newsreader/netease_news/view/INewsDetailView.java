package com.clevergump.newsreader.netease_news.view;

/**
 * 新闻详情页面展示的接口
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 20:03
 * @projectName NewsReader
 */
public interface INewsDetailView {
    /**
     * 显示"正在加载中..."的界面
     */
    void showLoadingUI();

    /**
     * 隐藏"正在加载中..."的界面
     */
    void hideLoadingUI();

    /**
     * 显示新闻详情数据相关的界面
     */
    void showContentUI();

    /**
     * 隐藏新闻详情数据相关的界面
     */
    void hideContentUI();
    /**
     * 显示网络加载失败时的界面
     */
    void showLoadFailsUI();

    /**
     * 隐藏网络加载失败时的界面
     */
    void hideLoadFailsUI();
}