package com.clevergump.newsreader.netease_news.presenter;

import android.content.Context;

import com.clevergump.newsreader.netease_news.model.INewsDetailModel;
import com.clevergump.newsreader.netease_news.view.INewsDetailView;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 20:04
 * @projectName NewsReader
 */
public class NewsDetailPresenter {
    private INewsDetailView iView;
    private INewsDetailModel iModel;

    public NewsDetailPresenter(INewsDetailView iView, INewsDetailModel iModel) {
        if (iView == null) {
            throw new IllegalArgumentException(iView + " == null");
        }
        if (iModel == null) {
            throw new IllegalArgumentException(iModel + " == null");
        }
        this.iView = iView;
        this.iModel = iModel;
    }

    /**
     * 加载新闻详情页的过程
     * @param docid
     */
    public void requestNewsDetail(Context context, String docid) {
        iView.hideContentUI();
        iView.hideLoadFailsUI();
        // 显示加载进度条
        iView.showLoadingUI();
        // 获取与给定的docid对应的新闻详情数据. 如果有缓存, 就直接用缓存里的数据;
        // 如果没有缓存, 才去加载网络上的数据.
        iModel.requestNewsDetail(context, docid);
    }

    /**
     * 当新闻详情数据加载成功(不论是成功加载了缓存数据, 还是没有缓存而成功加载了服务器上的数据)后, 对UI的处理
     */
    public void handleUIWhenLoadingSucceeds() {
        iView.hideLoadingUI();
        iView.showContentUI();
    }

    /**
     * 当新闻详情数据加载失败时, 对UI的处理
     */
    public void handleUIWhenLoadingFails() {
        iView.hideLoadingUI();
        iView.showLoadFailsUI();
    }
}
