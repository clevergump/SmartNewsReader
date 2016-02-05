//package com.clevergump.newsreader.netease_news.view.impl;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewStub;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//
//import com.clevergump.newsreader.Constant;
//import com.clevergump.newsreader.R;
//import com.clevergump.newsreader.netease_news.adapter.NeteaseNewsListAdapter;
//import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
//import com.clevergump.newsreader.netease_news.event.IEvent;
//import com.clevergump.newsreader.netease_news.event.impl.OnGetCachedNewsDataEvent;
//import com.clevergump.newsreader.netease_news.event.impl.OnGetLatestNewsDataEvent;
//import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;
//import com.clevergump.newsreader.netease_news.presenter.PageNewsPresenter;
//import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
//import com.clevergump.newsreader.netease_news.view.impl.base.PageNewsPtrBaseView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import de.greenrobot.event.EventBus;
//
///**
// * @author zhangzhiyi
// * @version 1.0
// * @createTime 2015/11/11 16:47
// * @projectName NewsReader
// */
//public class PageNewsPtrListView extends PageNewsPtrBaseView implements OnScrollListener {
//
//    /******************** View **************************/
//    private View mContentView;
//    private ListView mLvNews;
//    private ProgressBar mProgressBarLoading;
//    private ViewStub mViewStubLoadFail;
//
//
//    /******************** 变量 **************************/
//    private static PageNewsPtrListView sInstance;
//    private PageNewsPresenter mPageNewsPresenter;
//    private Activity mActivity;
//    private LayoutInflater mLayoutInflater;
//    private BaseAdapter mNewsListAdapter;
//    private List<NeteaseNewsItem> mNeteaseNewsItems = new ArrayList<NeteaseNewsItem>(Constant.PAGE_NEWS_COUNT);
//    private NextDataComingType mNextDataComingType;
//    // 当前所在的新闻标签
//    private String mCurrNewsTabName;
//    private String mCurrNewsTypeId;
//    private boolean mHasGetNewestNewsData = false;
//
//
//    /******************** 常量 *************************/
//    protected final int MAX_PAGE_NUMBER = 22;   // 最大页码数, 不是总页数.
//    protected final int LAYOUT_RES_ID_NEWS_FRAGMENT = R.layout.layout_news_fragment_listview;
//    protected final int RES_ID_NEWS_LISTVIEW = R.id.lv_news;
//    protected final int RES_ID_PROGRESSBAR_LOADING = R.id.progress_bar_loading;
//    protected final int RES_ID_VIEWSTUB_LOAD_FAIL = R.id.viewstub_load_fail;
//
//
//
//    public PageNewsPtrListView(Activity activity, LayoutInflater inflater, String newsTabName) {
//        if (activity == null) {
//            throw new IllegalArgumentException(activity + " == null");
//        }
//        if (inflater == null) {
//            throw new IllegalArgumentException(inflater + " == null");
//        }
//        if (newsTabName == null) {
//            throw new IllegalArgumentException(newsTabName + " == null");
//        }
//        this.mActivity = activity;
//        this.mLayoutInflater = inflater;
//        this.mCurrNewsTabName = newsTabName;
//        mCurrNewsTypeId = NewsFragmentManager.getInstance().getNewsTypeId(newsTabName);
//    }
//
//    // 单例
////    public static PageNewsView getInstance(LayoutInflater inflater, String newsTabName) {
////        if (sInstance == null) {
////            synchronized (PageNewsView.class) {
////                if (sInstance == null) {
////                    sInstance = new PageNewsView(inflater, newsTabName);
////                }
////            }
////        }
////        return sInstance;
////    }
//
//    @Override
//    public void setPresenter(PageNewsPresenter presenter) {
//        this.mPageNewsPresenter = presenter;
//    }
//
//    @Override
//    public View getContentView() {
//        mContentView = mLayoutInflater.inflate(LAYOUT_RES_ID_NEWS_FRAGMENT, null);
//        initView();
//        initData();
//        setListenerAndAdapter();
//        return mContentView;
//    }
//
//    public void initView() {
//        mLvNews = (ListView) mContentView.findViewById(RES_ID_NEWS_LISTVIEW);
//        mProgressBarLoading = (ProgressBar) mContentView.findViewById(RES_ID_PROGRESSBAR_LOADING);
//        mViewStubLoadFail = (ViewStub) mContentView.findViewById(RES_ID_VIEWSTUB_LOAD_FAIL);
//    }
//
//    private void initData() {
//        mNewsListAdapter = new NeteaseNewsListAdapter(mLayoutInflater, mNeteaseNewsItems);
//    }
//
//    private void setListenerAndAdapter() {
//        mLvNews.setAdapter(mNewsListAdapter);
//    }
//
//    @Override
//    public void showLoadingView() {
//        mNextDataComingType = NextDataComingType.REPLACE;
//        mProgressBarLoading.setVisibility(View.VISIBLE);
//        mLvNews.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void removeLoadingView() {
//
//    }
//
//    public void onEventMainThread(IEvent e) {
//        if (e instanceof OnGetCachedNewsDataEvent) {
//            OnGetCachedNewsDataEvent event = (OnGetCachedNewsDataEvent) e;
//            handleReceivingCacheNewsEvent(event);
//        }
//        else if (e instanceof OnGetLatestNewsDataEvent) {
//            OnGetLatestNewsDataEvent event = (OnGetLatestNewsDataEvent) e;
//            handleReceivingNewestNewsEvent(event);
//        }
//    }
//
//    private void handleReceivingNewestNewsEvent(OnGetLatestNewsDataEvent event) {
//        if(event != null) {
//            String newsTypeId = event.getNewsType();
//            if(mCurrNewsTypeId.equals(newsTypeId)) {
//                List<NeteaseNewsItem> newsCache = event.getNewsList();
//                if (newsCache != null && newsCache.size() > 0) {
//                    // TODO:
//                    String newsTabName = NewsFragmentManager.getInstance()
//                            .getNewsTabName(newsCache.get(0).newsTypeId);
////                    ToastUtils.showDebug("EventBus 接收到最新的" + newsTabName + "数据");
//                    mNeteaseNewsItems.clear();
//                    mNeteaseNewsItems.addAll(newsCache);
//                    mNewsListAdapter.notifyDataSetChanged();
//                    showViewOnDataLoaded();
//                }
////            mHasGetNewestNewsData = true;
//            }
//        }
//    }
//
//    private void handleReceivingCacheNewsEvent(OnGetCachedNewsDataEvent event) {
//        if(event != null) {
//            String newsTypeId = event.getNewsType();
//            if(mCurrNewsTypeId.equals(newsTypeId)) {
//                List<NeteaseNewsItem> newsCache = event.getNewsList();
//                if (newsCache != null && newsCache.size() > 0) {
//                    // TODO:
//                    mNeteaseNewsItems.clear();
//                    mNeteaseNewsItems.addAll(newsCache);
//                    mNewsListAdapter.notifyDataSetChanged();
//                    showViewOnDataLoaded();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void showViewOnDataLoaded() {
//        if (mProgressBarLoading.getVisibility() == View.VISIBLE) {
//            mProgressBarLoading.setVisibility(View.GONE);
//        }
//        if (mLvNews.getVisibility() == View.GONE) {
//            mLvNews.setVisibility(View.VISIBLE);
//        }
//    }
//
////    @Override
////    public void showTipsViewNoMoreDataToLoad() {
////
////    }
//
//    @Override
//    public void setCurrAndNextPageNumber() {
//
//    }
//
//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//    }
//
//    private enum NextDataComingType {
//        APPEND, REPLACE
//    }
//
//    @Override
//    public void onCreate() {
//        // 保险起见, 多加一句判断.
//        if(!EventBus.getDefault().isRegistered(this)) {
//            EventBusUtils.registerEventBus(this);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        EventBusUtils.unregisterEventBus(this);
//    }
//
//    @Override
//    public void initPullingState() {
//
//    }
//
//    @Override
//    public void initCacheQueryingState() {
//
//    }
//
//    @Override
//    public void setShouldRefreshWholePageData(boolean shouldRefreshWholePageData) {
//
//    }
//
//}