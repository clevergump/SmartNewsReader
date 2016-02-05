package com.clevergump.newsreader.netease_news.view.impl.base;

import com.clevergump.newsreader.netease_news.view.IPageNewsView;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/21 11:39
 * @projectName NewsReader
 */
public abstract class PageNewsPtrBaseView implements IPageNewsView {
    // 下拉刷新的状态
    protected PullingState bPullingState;
    // 新闻缓存查询的状态
    protected NewsCacheQueryState bNewsCacheQueryState;

    /**
     * 下拉刷新过程的几种状态
     */
    protected enum PullingState {
        /**
         * 下拉还未开始时的一种初始复位状态. 下拉结束后应该将相应变量置为该状态, 表示本次下拉结束,
         * 下一次下拉还未开始.
         */
        RESET,
        /**
         * 已下拉但下拉的结果还要待确认的状态. 一般是在执行了下拉刷新方法后, 就要立即将相应变量置为该状态,
         * 表示目前正处于下拉请求ing的过程中, 但还未收到响应, 所以暂时无法确认本次下拉请求是否能成功.
         */
        PENDING,
        /**
         * 已收到下拉结果, 并且数据刷新请求成功的状态.
         */
        REQUEST_SUCCEED,
        /**
         * 已收到下拉结果, 但请求失败的状态.
         */
        REQUEST_FAIL
    }

    /**
     * 缓存查询过程的几种状态
     */
    protected enum NewsCacheQueryState {
        /**
         * 还未执行缓存查询动作时的一种初始复位状态. 缓存查询的过程结束后, 不论是否查询结果如何,
         * 都应该将相应的变量置为该状态, 表示本次查询结束, 下一次查询还未开始.
         */
         RESET,
        /**
         * 正在执行查询缓存的过程中ing, 此时暂时还无法确认查询结果的一种待确认状态. 一般是在开始
         * 执行缓存查询的动作后, 就要立即将相应的变量置为该状态.
         */
        PENDING,
        /**
         * 本次查询后发现有缓存.
         */
        HAS_CACHE,
        /**
         * 本次查询后发现无缓存.
         */
        NO_CACHE
    }
}