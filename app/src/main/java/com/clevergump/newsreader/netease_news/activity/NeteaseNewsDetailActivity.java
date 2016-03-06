package com.clevergump.newsreader.netease_news.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.R;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;
import com.clevergump.newsreader.netease_news.event.impl.NetworkFailsNewsDetailEvent;
import com.clevergump.newsreader.netease_news.event.impl.OnGetNewsDetailEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsDetailEvent;
import com.clevergump.newsreader.netease_news.model.impl.NewsDetailModelImpl;
import com.clevergump.newsreader.netease_news.presenter.impl.NewsDetailPresenter;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.ImageLoaderUtils;
import com.clevergump.newsreader.netease_news.utils.ToastUtils;
import com.clevergump.newsreader.netease_news.view.INewsDetailView;
import com.clevergump.newsreader.netease_news.widget.htmltextview.HtmlTextView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 网易新闻的新闻详情页面
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/30 22:45
 * @projectName NewsReader
 */
public class NeteaseNewsDetailActivity extends BaseActivity implements View.OnClickListener, INewsDetailView, ImageLoadingProgressListener {

    /**************************** 常量 *********************************/
    private final String TAG = getClass().getSimpleName();


    /**************************** View *********************************/
    // 左上角的返回键
    private ImageView mIvBack;
    // 环形进度条, 显示在屏幕中心处用于表示内容正在加载中
    private ProgressBar mProgressBarLoading;

    // 新闻内容的整体的ScrollView
    private ScrollView mScrollViewContent;
    // 新闻标题
    private TextView mTvNewsDetailTitle;
    // 新闻来源
    private TextView mTvNewsDetailSource;
    // 新闻时间
    private TextView mTvNewsDetailPtime;
    // 新闻图片
    private ImageView mIvNewsDetailImage;
    // 新闻内容
    private HtmlTextView mHtvNewsDetailBody;

    // 加载失败时的ViewStub
    private ViewStub mViewStubLoadFail;
    // 加载失败时的ViewStub所对应的布局View.
    private View mLoadFailContentView;

    /**************************** 变量 *********************************/
    private NewsDetailPresenter mNewsDetailPresenter;
    // 当前该页面显示的新闻详情的docid
    private String mDocid;
    // 图片加载的监听器
    private SimpleImageLoadingListener mImageLoadingListener;


    /*******************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_netease_news_detail);
        super.onCreate(savedInstanceState);
        EventBusUtils.registerEventBus(this);
    }

    @Override
    protected void onDestroy() {
        clear();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNewsDetailPresenter.requestNewsDetail(this, mDocid);
    }

    @Override
    public void clear() {
        EventBusUtils.unregisterEventBus(this);
        if (mNewsDetailPresenter != null) {
            mNewsDetailPresenter.clear();
        }
    }

    @Override
    protected void initView() {
        /************************** 标题栏 *****************************/
        mIvBack = findView(R.id.iv_back);

        /************************** 内容区域 *****************************/
        mProgressBarLoading = findView(R.id.progress_bar_loading);

        /*********** ScrollView中的控件 ************/
        mScrollViewContent = findView(R.id.sv_netease_news_detail);
        mTvNewsDetailTitle = findView(R.id.tv_netease_news_detail_title);
        mTvNewsDetailSource = findView(R.id.tv_netease_news_detail_source);
        mTvNewsDetailPtime = findView(R.id.tv_netease_news_detail_ptime);
        mIvNewsDetailImage = findView(R.id.iv_netease_news_detail_image);
        mHtvNewsDetailBody = findView(R.id.htv_netease_news_detail_body);

        /*********** 加载失败时的 ViewStub界面 ************/
        mViewStubLoadFail = findView(R.id.viewstub_load_fail);

        mIvNewsDetailImage.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        mDocid = getIntent().getStringExtra(Constant.KEY_NETEASE_NEWS_DETAIL_DOCID);
//        ToastUtils.showDebug("接收到的docid = " + (mDocid == null ? "null" : mDocid));
        mNewsDetailPresenter = new NewsDetailPresenter(this, new NewsDetailModelImpl());
        mImageLoadingListener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
//                ToastUtils.showDebug("开始加载图片");
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                ToastUtils.showDebug("图片加载失败");
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
//                ToastUtils.showDebug("取消图片加载");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                ToastUtils.showDebug("图片加载完毕");

//                if (view != null && view instanceof ImageView) {
//                    ((ImageView) view).setImageBitmap(loadedImage);
//                }
//                mIvNewsDetailImage.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    protected void setListenerOrAdapter() {
        mIvBack.setOnClickListener(this);
    }

    public void onEventMainThread(BaseNewsDetailEvent event) {
        if (event instanceof OnGetNewsDetailEvent) {
            NeteaseNewsDetail newsDetail = ((OnGetNewsDetailEvent)event).newsDetail;
            handleOnGetNewsDetail(newsDetail);
        }
        // 网络异常的事件
        else if (event instanceof NetworkFailsNewsDetailEvent) {
            setResult(RESULT_CANCELED);
            mNewsDetailPresenter.handleUIWhenLoadingFails();
        }
    }

    /**
     * 在获取到NewsDetail(新闻详情)数据后, 要进行的处理
     * @param newsDetail
     */
    private void handleOnGetNewsDetail(NeteaseNewsDetail newsDetail) {
        if (newsDetail != null) {
            setContentDatas(newsDetail);
            setResult(RESULT_OK);
            mNewsDetailPresenter.handleUIWhenLoadingSucceeds();
            mNewsDetailPresenter.putToCache(newsDetail);
        }
        // 如果获取到的数据为null, 则也可以认为是网络异常, 所以需要显示网络异常的界面.
        else {
            setResult(RESULT_CANCELED);
            mNewsDetailPresenter.handleUIWhenLoadingFails();
        }
    }

    /**
     * 设置该页面上各个控件的具体内容
     * @param newsDetail 获取到的新闻详情的对象
     */
    private void setContentDatas(NeteaseNewsDetail newsDetail) {
        if (newsDetail == null) {
            throw new IllegalArgumentException(newsDetail + " == null");
        }
        mTvNewsDetailTitle.setText(newsDetail.title);
        mTvNewsDetailSource.setText(newsDetail.source);
        mTvNewsDetailPtime.setText(newsDetail.ptime);

        // 只有当图片的url非null并且是个有长度的String时, 才会设置该ImageView可见. 如果文章没有图片, 网易
        // 的json数据传递的图片url不是null, 而是一个空字符串"". 所以一定要用TextUtils.isEmpty(imgUrl)
        // 来做判断, 而不能仅仅判别是否非null.  例如: 下面这些文章就是没有图片的文章
        // http://c.3g.163.com/nc/article/BAQOH2EM000915BE/full.html
        // http://c.3g.163.com/nc/article/BAQTM27L00094O5H/full.html
        // 一定要保证这些文章不论是第一次加载服务器数据, 还是后来加载缓存数据, 都不能让一个显示着默认图片
        // 的空白ImageView控件出现.
        if (newsDetail.img != null && newsDetail.img.size() > 0 && newsDetail.img.get(0) != null
                && !TextUtils.isEmpty(newsDetail.img.get(0).src)) {
            mIvNewsDetailImage.setVisibility(View.VISIBLE);
            setNewsDetailImage(mIvNewsDetailImage, newsDetail.img.get(0).src);
        }
        setNewsDetailBody(mHtvNewsDetailBody, newsDetail.body);
    }

    /**
     * 加载新闻详情中的图片
     * @param iv
     * @param imageUrl
     */
    private void setNewsDetailImage(ImageView iv, String imageUrl) {
        ImageLoaderUtils.displayImage(imageUrl, iv, mImageLoadingListener, this);
//        ImageLoaderUtils.displayImage(imageUrl, iv, null, null);
    }

    /**
     * 显示html格式的新闻详情的文字内容, 删除多余的html标签
     * @param htmlTextView
     * @param htmlFormattedText
     */
    private void setNewsDetailBody(HtmlTextView htmlTextView, String htmlFormattedText) {
        htmlFormattedText = htmlFormattedText.replace("<!--VIDEO#1--></p><p>", "");
        htmlFormattedText = htmlFormattedText.replace("<!--VIDEO#2--></p><p>", "");
        htmlFormattedText = htmlFormattedText.replace("<!--VIDEO#3--></p><p>", "");
        htmlFormattedText = htmlFormattedText.replace("<!--VIDEO#4--></p><p>", "");
        htmlFormattedText = htmlFormattedText.replace("<!--REWARD#0--></p><p>", "");
        // HtmlTextView的显示。
        htmlTextView.setHtmlFromString(htmlFormattedText, new HtmlTextView.LocalImageGetter());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 显示"正在加载中..."的UI界面
     */
    @Override
    public void showLoadingUI() {
        mProgressBarLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏"正在加载中..."的UI界面
     */
    @Override
    public void hideLoadingUI() {
        mProgressBarLoading.setVisibility(View.GONE);
    }

    /**
     * 显示新闻详情内容的UI界面
     */
    @Override
    public void showContentUI() {
        mScrollViewContent.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏新闻详情内容的UI界面(一般都是因为新闻详情的数据暂时还没有加载出来, 所以必须将相关控件暂时先隐藏起来)
     */
    @Override
    public void hideContentUI() {
        mScrollViewContent.setVisibility(View.GONE);
    }

    /**
     * 显示加载失败时的UI界面
     */
    @Override
    public void showLoadFailsUI() {
        ToastUtils.show(R.string.fail_network_connection);

        if (mLoadFailContentView == null) {
            mLoadFailContentView = mViewStubLoadFail.inflate();
            TextView tvReload = (TextView) mLoadFailContentView.findViewById(R.id.tv_reload);
            tvReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 尝试再次请求数据.
                    mNewsDetailPresenter.requestNewsDetail(NeteaseNewsDetailActivity.this, mDocid);
                }
            });
        }
        else {
            mLoadFailContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏加载失败时的UI界面 (如果有的话)
     */
    @Override
    public void hideLoadFailsUI() {
        if (mLoadFailContentView != null) {
            mLoadFailContentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProgressUpdate(String imageUri, View view, int current, int total) {
//        ToastUtils.showDebug("图片加载进度:" + (current * 100 / total) + "%");
//        if (total == current) {
//            ToastUtils.showDebug("图片加载进度100%");
//        }
    }
}