package com.clevergump.newsreader.netease_news.view.impl;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.R;
import com.clevergump.newsreader.netease_news.activity.NeteaseNewsListActivity;
import com.clevergump.newsreader.netease_news.adapter.NeteaseNewsListAdapter;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsBase;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.event.impl.NetworkFailsNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.OnGetCachedNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.OnGetLatestNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.TestNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;
import com.clevergump.newsreader.netease_news.presenter.impl.PageNewsListPresenter;
import com.clevergump.newsreader.netease_news.utils.DateTimeUtils;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.IEventBusSubscriber;
import com.clevergump.newsreader.netease_news.utils.LogUtils;
import com.clevergump.newsreader.netease_news.utils.NetworkUtils;
import com.clevergump.newsreader.netease_news.utils.ToastUtils;
import com.clevergump.newsreader.netease_news.view.impl.base.PageNewsPtrBaseView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/11 16:47
 * @projectName NewsReader
 */
public class PageNewsPullToRefreshListView extends PageNewsPtrBaseView
        implements OnScrollListener, PullToRefreshBase.OnRefreshListener<ListView>,
        AdapterView.OnItemClickListener, Runnable, IEventBusSubscriber {

    // 表示没有条目被点击, 或者点击的条目的标题都已经更新为灰色了.
    private static final int ITEM_CLICK_RESETED_POSITION = -1;
    /******************** 常量 *************************/
    private final String TAG = getClass().getSimpleName();
    // 最大页码数, 不是总页数.
    private final int MAX_PAGE_NUMBER = 22;
    // 上次刷新时间的初始值, 是个负数-1, 表示该新闻标签还从来没有刷新过. 因为只要刷新过, 该变量就一定是正数.
    private final long LAST_UPDATE_TIME_INIT_VALUE = -1;
    private final int LAYOUT_RES_ID_NEWS_FRAGMENT = R.layout.layout_news_fragment_ptrlistview;
    private final int RES_ID_NEWS_VIEWGROUP_FOOTER_LOADING_BAR = R.id.vg_footer_loading_view;
    private final int RES_ID_NEWS_LISTVIEW = R.id.lv_news;
    private final int RES_ID_PROGRESSBAR_LOADING = R.id.progress_bar_loading;
    private final int RES_ID_VIEWSTUB_LOAD_FAIL = R.id.viewstub_load_fail;


    /******************** View **************************/
    private View mContentView;
    private PullToRefreshListView mPtrLvNews;
    private ListView mLvNewsInternal;

    // ListView的footerView的容器
    private View mVgFooterViewContainer;
    // ListView的footerView----环形进度条 + "正在加载中..."
    private View mVgFooterLoadingNextPage;
    // ListView的footerView----"没有更多数据了"
    private TextView mTvFooterNoMoreToLoad;
    // ListView的footerView----"加载失败, 请点击重试"
    private TextView mTvFooterLoadFail;

    // 页面上没有任何数据时, 需要在页面中部显示的一个环形加载进度条.
    private ProgressBar mProgressBarLoading;
    private ViewStub mViewStubLoadFail;
    // 既没有缓存又加载失败时的ViewStub的layout布局所对应的整个View
    private View mLoadFailPageContentView;


    /******************** 变量 **************************/
    private NeteaseNewsListActivity mActivity;
    private LayoutInflater mLayoutInflater;
    private NeteaseNewsListAdapter mNewsListAdapter;
    private ArrayAdapter mTestArrayAdapter;
    private List<NeteaseNewsItem> mNeteaseNewsItems = new ArrayList<NeteaseNewsItem>(Constant.PAGE_NEWS_ITEM_COUNT);
    private List<String> mTestListData = new ArrayList<String>(10);
    private NextDataIncomingType mNextDataIncomingType = NextDataIncomingType.REPLACE;
    // 当前所在的新闻标签
    private String mCurrNewsTabName;
    private String mCurrNewsTypeId;
    // 是否是下拉刷新导致的数据请求. 区别于翻页请求.
    private boolean mIsPullToRefreshRequest = true;
    private boolean mShouldRefreshWholePageData = true;

    // 是否有新闻数据显示在屏幕上. 如果是第一次创建页面, 则为false, 其他时候都为true.
    // 在获取并显示数据(不论数据来自缓存还是服务器)后就应该将该字段置为true.
    // 该字段主要用于识别在进行网络请求的过程中UI到底需要显示什么样的加载效果。
    // 如果该字段为false, 表示页面上没有任何数据, 则是在屏幕中部显示一个环形进度条.
    // 如果该字段为true，表示在加载时页面上已经有数据了, 这时进行网络请求时的UI加载效果
    // 要么是下拉刷新时的效果, 要么是在ListView翻页时为其添加一个FooterView用于显示加载效果.
    private boolean mHasNewsDataShownOnScreen = false;

    // 当前正在加载中的页面, 现在这个页面
    private int mCurrPageNumber = 0;
    // 接下来要加载的页面, 下一个页面
    private int mNextPageNumber = 1;
    private int mTempCurrPageNumber;

    // 新闻ListView中显示出来的条目总数.
    private int mLvNewsTotalItemCount;
    // 新闻ListView中显示出来的最后一个条目的index.
    private int mLvNewsLastVisibleItem;
    private PullToRefreshBase.State mPtrLvNewsState;
    // 上次刷新时间(单位: 毫秒数). 初始值为-1表示该新闻标签还未记录过上次刷新时间. 所以可以通过判断该变量的值

    // 下拉刷新时用于显示一系列文字描述以及刷新logo的layout
    private ILoadingLayout mPtrHeaderLoadingLayout;
    // 当前被点击的那个新闻条目的docid
    private String mClickedItemDocid;

    /**
     * 被点击的那个item的索引. 每当点击某个新闻item打开了该item对应的新闻详情页, 就需要设置该变量为
     * 被点击的那个item的位置. 当关闭了新闻详情页回到新闻 ListView页面时, 如果 onActivityResult()方法
     * 返回的 resultCode为 RESULT_OK, 则将该变量对应的那个item的 View的标题颜色更新为灰色, 然后再将
     * 该变量复位到 ITEM_CLICK_RESETED_POSITION 值; 如果 onActivityResult()方法返回的 resultCode为
     * RESULT_CANCEL, 则直接将该变量复位到 ITEM_CLICK_RESETED_POSITION 值, 而不更新那个item的标题颜色.
     */
    private int mClickedItemPosition = ITEM_CLICK_RESETED_POSITION;

    /**
     * 被阅读过的新闻item的标题颜色(这里使用的是灰色). 定义该变量主要是为了避免多次从colors.xml
     * 文件中重复获取这一灰色的数值, 属于性能优化的操作.
     */
    private int mBeenReadItemTitleColor = -1;


    /****************************************************/


    public PageNewsPullToRefreshListView(NeteaseNewsListActivity activity, LayoutInflater inflater, String newsTabName) {
        if (activity == null) {
            throw new IllegalArgumentException(activity + " == null");
        }
        if (inflater == null) {
            throw new IllegalArgumentException(inflater + " == null");
        }
        if (newsTabName == null) {
            throw new IllegalArgumentException(newsTabName + " == null");
        }
        this.mActivity = activity;
        this.mLayoutInflater = inflater;
        this.mCurrNewsTabName = newsTabName;
        mCurrNewsTypeId = NewsFragmentManager.getInstance().getNewsTypeId(newsTabName);
        onCreate();
    }

    @Override
    public void setPresenter(PageNewsListPresenter presenter) {
        this.mPageNewsListPresenter = presenter;
        mNewsListAdapter.setPresenter(presenter);
    }

    @Override
    public View getContentView() {
        mContentView = mLayoutInflater.inflate(LAYOUT_RES_ID_NEWS_FRAGMENT, null);
        initView();
        initData();
        initSetting();
        setListenerAndAdapter();
        return mContentView;
    }

    public void initView() {
        mPtrLvNews = (PullToRefreshListView) mContentView.findViewById(RES_ID_NEWS_LISTVIEW);
        mProgressBarLoading = (ProgressBar) mContentView.findViewById(RES_ID_PROGRESSBAR_LOADING);
        mViewStubLoadFail = (ViewStub) mContentView.findViewById(RES_ID_VIEWSTUB_LOAD_FAIL);

        /******************** 衍生的View ************************/
        // 下拉刷新功能的ListView内嵌的那个ListView.
        mLvNewsInternal = mPtrLvNews.getRefreshableView();

        /******************** ListView底部的 Footer View ***********************/
        mVgFooterViewContainer = View.inflate(mActivity, R.layout.list_item_footer, null);
        mVgFooterLoadingNextPage = mVgFooterViewContainer.findViewById(R.id.vg_footer_loading_view);
        mTvFooterNoMoreToLoad = (TextView) mVgFooterViewContainer.findViewById(R.id.tv_footer_no_more_to_load);
        mTvFooterLoadFail = (TextView) mVgFooterViewContainer.findViewById(R.id.tv_footer_load_fail);
    }

    private void initData() {
        mNewsListAdapter = new NeteaseNewsListAdapter(mActivity, mLayoutInflater, mNeteaseNewsItems);
        mTestArrayAdapter = new ArrayAdapter<String>(
                mActivity, android.R.layout.simple_list_item_1, mTestListData);

        // 进入页面后那次请求(是刷新请求)的请求时间.
//        mLastRefreshTimeMillis = MyApplication.lastRefreshTimeMillis.get(mCurrNewsTabName);
    }

    private void initSetting() {
        // 设置ListView的item点击时不显示默认的黄色背景色。
        mLvNewsInternal.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mPtrLvNews.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        // 如果设置为true, 即显示indicator, 那么将会在PtrListView的第0个item的右上角显示IndicatorLayout
        // 所代表的View.
        mPtrLvNews.setShowIndicator(false);
        mPtrHeaderLoadingLayout = mPtrLvNews.getLoadingLayoutProxy(true, false);
        mPtrHeaderLoadingLayout.setLoadingDrawable(mActivity.getResources().getDrawable(R.mipmap.android));
        mPtrHeaderLoadingLayout.setPullLabel("下拉可以刷新");
        mPtrHeaderLoadingLayout.setReleaseLabel("释放立即刷新");
        mPtrHeaderLoadingLayout.setRefreshingLabel("正在刷新中...");
        // 设置上次更新时间
//        setPtrHeaderLastUpdateTimeDesc();
    }

    /**
     * 设置下拉刷新时的HeaderView中"上次更新时间"的文字显示
     */
    private void setPtrHeaderLastUpdateTimeDesc() {
        // 设置在下拉刷新的 HeaderView 中显示该新闻标签上次更新时间的文字描述语句.
        long lastRefreshTimeMillis = mPageNewsListPresenter.getLastRefreshTimeMillis(mCurrNewsTabName);
        if (lastRefreshTimeMillis != LAST_UPDATE_TIME_INIT_VALUE) {
            String lastUpdateTimeDesc = DateTimeUtils.getLastUpdateTimeDesc(lastRefreshTimeMillis);
            mPtrHeaderLoadingLayout.setLastUpdatedLabel(lastUpdateTimeDesc);
            LogUtils.i(mCurrNewsTabName + "---上次更新时间:" + DateTimeUtils.getFormattedTime(lastRefreshTimeMillis)
                    + ", 毫秒: " + lastRefreshTimeMillis + ", " + lastUpdateTimeDesc);
        } else {
            mPtrHeaderLoadingLayout.setLastUpdatedLabel("先前未曾更新过");
        }
    }

    private void setListenerAndAdapter() {
        mPtrLvNews.setOnItemClickListener(this);
        /**
         * When first introduced, the method ListView.addFooterView(...) could only be called
         * before setting the adapter with {@link #setAdapter(ListAdapter)}. Starting with
         * {@link android.os.Build.VERSION_CODES#KITKAT----Android 4.4 }, this method may be
         * called at any time.
         */
        mLvNewsInternal.addFooterView(mVgFooterViewContainer);
        if (Constant.isTestPtrListViewOnly) {
            mPtrLvNews.setAdapter(mTestArrayAdapter);
        } else {
            mPtrLvNews.setAdapter(mNewsListAdapter);
        }
        mLvNewsInternal.removeFooterView(mVgFooterViewContainer);

        mLvNewsInternal.setOnScrollListener(this);
        mPtrLvNews.setOnRefreshListener(this);

        // 只要有了一点点的下拉动作或趋势, 即使只下拉了很小的距离, 也会触发 OnPullEventListener接口的
        // 回调执行, 而 OnRefreshListener接口只有在下拉的距离达到一定的距离后以至于"释放立即刷新"能够
        // 显示出来的时候才会去执行他的回调方法. 所以将重新计算并设置"上次更新时间"的操作放在
        // onPullEvent()方法中会比放在onRefresh()方法中更合适.
        mPtrLvNews.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {
            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView,
                                    PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
                // 设置上次更新时间的文本提示．
                setPtrHeaderLastUpdateTimeDesc();
            }
        });
        mTvFooterLoadFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 只有在有网络的情况下, 才会再次发起请求.
                if (NetworkUtils.hasNetworkConnection()) {
                    // 隐藏FooterView中的该"加载失败, 请点击重试"按钮, 然后重新显示"加载中..."
                    mTvFooterLoadFail.setVisibility(View.GONE);
                    mVgFooterLoadingNextPage.setVisibility(View.VISIBLE);
                    // 再次发起请求
                    mPageNewsListPresenter.requestPageNews(mCurrNewsTabName, false, false);
                }
            }
        });
    //        mPtrLvNews.setRefreshing();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ToastUtils.showDebug("position = " + position + ", id = " + id);

//        ToastUtils.showDebug("mNewsListAdapter.getCount() = " + mNewsListAdapter.getCount());

        // 如果点击的是最底部的"没有更多数据了", 则不会响应点击事件.
        // position从0开始计算. 点击的新闻条目的position都比adapter.getCount()多1, 但此处需要加2,
        // 是因为在ListView的底部栏同时添加了两个Footer, 一个是"加载中...", 另一个是"没有更多数据了",
        // 而我们点击的是"没有更多数据了", 所以我们点击的这个item的position 应该在adapter.getCount()的基础上加2.
        if (position == mNewsListAdapter.getCount() + 2) {
            return;
        }

        // TODO: 待思考, 为什么是 position-1 呢? position-1 其实和参数 id的数值是相同的, 所以 id的含义是什么 ?
        int itemViewType = mNewsListAdapter.getItemViewType(position - 1);
        mClickedItemPosition = position;
        mClickedItemDocid = ((NeteaseNewsItem) mNewsListAdapter.getItem(position - 1)).docid;
//        ToastUtils.showDebug("点击的新闻条目的 docid = " + (mCurrClickedItemDocid == null ? "null" : mCurrClickedItemDocid));
        switch (itemViewType) {
            case NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_ONE_IMAGE:
                mActivity.launchNewsDetailActivity(this, mClickedItemDocid);
                break;
            case NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_THREE_IMAGE:
                String title = ((NeteaseNewsItem) mNewsListAdapter.getItem(position - 1)).title;
                String imgsrc = ((NeteaseNewsItem) mNewsListAdapter.getItem(position - 1)).imgsrc;
                String imgExtraSrc0 = ((NeteaseNewsItem) mNewsListAdapter.getItem(position - 1)).imgExtraSrc0;
                String imgExtraSrc1 = ((NeteaseNewsItem) mNewsListAdapter.getItem(position - 1)).imgExtraSrc1;
                ArrayList<String> imageUrls = new ArrayList<String>();
                imageUrls.add(imgsrc);
                imageUrls.add(imgExtraSrc0);
                imageUrls.add(imgExtraSrc1);
                // TODO: 启动Activity的参数是否合适?
                mActivity.launchImageDetailActivity(this, mClickedItemDocid, title, imageUrls);
                break;
        }
    }

    @Override
    public void registerEventBus() {
        EventBusUtils.registerEventBus(this);
    }

    @Override
    public void unregisterEventBus() {
        EventBusUtils.unregisterEventBus(this);
    }

    /**
     * 其实主要是想提供一个单例 Handler的获取方法, 因为短时间内频繁执行下拉刷新动作时往往都不会去
     * 请求服务器, 而是直接取消下拉后刷新的UI界面效果, 这样会经常调用 Handler.postDelayed()方法,
     * 使用单例Handler可以避免频繁地 new Handler对象.
     */
    private static class MyHandler extends Handler {
        private MyHandler() {
        }

        public static MyHandler getInstance() {
            return MyHandlerHolder.INSTANCE;
        }

        private static class MyHandlerHolder {
            private static final MyHandler INSTANCE = new MyHandler();
        }
    }

    @Override
    public void run() {
        mPtrLvNews.onRefreshComplete();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        // 如果下拉刷新的动作太频繁, 则实际上是不会发起http请求的. 两次http请求的最小间隔是2分钟. 在2分钟内
        // 连续下拉, 其实只是执行了一个空的下拉动作而已, 实际上在后台是不会执行任何操作的.
        long lastRefreshTimeMillis = mPageNewsListPresenter.getLastRefreshTimeMillis(mCurrNewsTabName);
        if (System.currentTimeMillis() - lastRefreshTimeMillis < Constant.PULL_TO_REFRESH_REQUEST_INTERVAL) {
            doResetStates();
            MyHandler.getInstance().postDelayed(this, 500);
            return;
        }
        // 如果执行下拉的动作时发现没有网络连接, 则需要取消刷新.
        if (!NetworkUtils.hasNetworkConnection()) {
            doResetStates();
            /**
             * 下拉刷新后手离开屏幕, ListView的HeaderView却长时间一直显示"正在刷新..."而不收回的bug
             * 产生的根本原因是:
             *
             * 虽然我们也确实调用了 onRefreshComplete()方法, 但调用得过早, 早于系统其他初始化设置的一系列
             * 方法, 导致系统在执行完这一系列初始化后该方法早已结束了执行, 系统没有捕获到该方法的执行, 所以
             * 就仍然一直显示"正在刷新..."而不收回. 修复此bug的思路是, 要想办法让 onRefreshComplete()方法
             * 延迟执行, 具体实施细节上, 既可以开启一个异步任务, 也可以使用 Handler.postDelayed().但不能
             * 直接在此处调用onRefreshComplete()方法, 也不能在此处直接发送一个网络异常的事件, 因为这两种
             * 处理方式都耗时太短, onRefreshComplete()方法很快就能被执行, 所以这两种处理方式都会出现此bug.
             */
            MyHandler.getInstance().postDelayed(this, 500);
    //            mPtrLvNews.onRefreshComplete();
            return;
        }
        mPullingState = PullingState.PENDING;
        mPageNewsListPresenter.requestPageNews(mCurrNewsTabName, true, false);

        // 调试代码1. 在断网状态下, 调试下拉刷新ListView的HeaderView将一直显示"刷新中...", 即使
        // onRefreshComplete()方法已经被调用, 该HeaderView也仍不收回的bug(该bug只在断网时会出现,
        // 有网时不出现)时, 直接改为调用下面两行代码或更下边的一行代码, 都能使该HeaderView正常收回.
    //        String tabNewsUrl = NewsFragmentManager.getInstance().getPageNewsListUrl(mCurrNewsTabName, 0);
    //        new HttpGetNewsList(mActivity, 0, tabNewsUrl, mCurrNewsTypeId).request();

        // 调试代码2. 见上边调试代码1.
    //        new TestNeteaseNewsTask().execute();
    }

    public void onEventMainThread(BaseNewsListEvent e) {
        // 判断是否是这个新闻标签的事件. 如果不是, 则直接不处理。
        if (e == null || e.getNewsTypeId() == null || !e.getNewsTypeId().equals(mCurrNewsTypeId)) {
            return;
        }
//        ToastUtils.showDebug("onRefreshComplete");
        mPtrLvNews.onRefreshComplete();

        if (e instanceof OnGetCachedNewsListEvent) {
//            ToastUtils.showDebug("缓存数据");
            OnGetCachedNewsListEvent event = (OnGetCachedNewsListEvent) e;
            handleReceivingCacheNewsEvent(event);
        }
        // 获取到最新新闻数据的事件
        else if (e instanceof OnGetLatestNewsListEvent) {
//            ToastUtils.showDebug("最新数据");
            OnGetLatestNewsListEvent event = (OnGetLatestNewsListEvent) e;
            handleReceivingLatestNewsListEvent(event);
        }
        // 网络异常的事件
        else if (e instanceof NetworkFailsNewsListEvent) {
            ToastUtils.show(R.string.fail_network_connection);
            mNextPageNumber = mCurrPageNumber;
            mCurrPageNumber = mTempCurrPageNumber;
            mPullingState = PullingState.REQUEST_FAIL;
            if (mNewsCacheQueryState == NewsCacheQueryState.NO_CACHE) {
                doResetStates();
                doShowViewStubNetworkFail();
            }

            // 如果是翻页加载失败, 则将ListView的 FooterView 由"加载中..."变为"加载失败, 请点击重试".
            if (mVgFooterLoadingNextPage.getVisibility() == View.VISIBLE) {
                mVgFooterLoadingNextPage.setVisibility(View.GONE);
                mTvFooterLoadFail.setVisibility(View.VISIBLE);
            }
        }

        else if (e instanceof TestNewsListEvent) {
            ToastUtils.showDebug("测试数据");
        }
    }

    /**
     * 显示既无缓存, 又没有获取到服务器数据时的Stub页面相关的UI处理
     */
    private void doShowViewStubNetworkFail() {
        // 取消当前正在显示的加载效果. 其实也就是取消屏幕中央的环形进度条的显示
        removeLoadingView();
        // 显示ViewStub
        showLoadFailViewStub();
    }

    @Override
    public void initPullingState() {
        mPullingState = PullingState.PENDING;
    }

    @Override
    public void initCacheQueryingState() {
        mNewsCacheQueryState = NewsCacheQueryState.PENDING;
    }

    @Override
    public void setNextDataIncomingType(boolean shouldRefreshWholePageData) {
        // 下拉刷新, 以及翻页加载时发现先前页的数据已经太久远了, 这些情况都需要完全替换掉现有数据.
        mNextDataIncomingType = shouldRefreshWholePageData ?
                NextDataIncomingType.REPLACE : NextDataIncomingType.APPEND;
    }

    @Override
    public void onNewsItemHasBeenRead() {
        // 更新数据库中"是否已读"的标志位, 便于下次加载时直接显示为灰色.
        mNewsListAdapter.updataNewsItemToHasReadState(mClickedItemDocid);

        // 更新当前被点击的item的标题颜色为灰色.

        if (mClickedItemPosition == ITEM_CLICK_RESETED_POSITION) {
            return;
        }

        int firstVisiblePosition = mLvNewsInternal.getFirstVisiblePosition();
        int lastVisiblePosition = mLvNewsInternal.getLastVisiblePosition();
        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            Object item = mNewsListAdapter.getItem(i);
            if (item instanceof NeteaseNewsItem && mClickedItemDocid.equals(((NeteaseNewsItem)item).docid)) {
                // 从ListView中获取被点击的那个item的View
                View newsItemView = mLvNewsInternal.getChildAt(i - firstVisiblePosition + 1);

                /**
                 * 下面两种获取标题TextView的方法都是错误的:
                 *   TextView tvTitle = (TextView) newsItemView.findViewById(R.id.tv_news_item_title);
                 *   TextView tvTitle = (TextView) newsItemView.getTag(Constant.KEY_NEWS_LIST_ITEM_TITLE);
                 *
                 * 第一种: 不能用findViewById(), 因为没有指定布局文件, 所以得到的TextView为null.
                 * 第二种: setTag(int key, Object tag) 是个好主意. 但是参数key只能是系统内部的相关资源id,
                 *        而不能随意取值. 否则会报异常:
                 *        "IllegalArgumentException: The key must be an application-specific resource id."
                 *        其实系统这样设计是为了保证key的唯一性, 所以就根据该异常的提示使用R.id.XXX作为key.
                 *
                 * 正确的方法应该是:
                 *   TextView tvTitle = (TextView) newsItemView.getTag(R.id.tv_news_item_title);
                 * 其中, setTag(int key, Object tag)和 getTag(int key) 方法的参数key要求必须是该APP
                 * 内定义的资源id, 不能自由设定, 这样是为了保证 key的唯一性.
                 */
//                    TextView tvTitle = (TextView) newsItemView.findViewById(R.id.tv_news_item_title);
//                    TextView tvTitle = (TextView) newsItemView.getTag(Constant.KEY_NEWS_LIST_ITEM_TITLE);

                TextView tvTitle = (TextView) newsItemView.getTag(R.id.tv_news_item_title);

                /**
                 * 只需获取一次该颜色值. 后续如果再遇到阅读过的item需要让其标题变为灰色的情况时,
                 * 直接将该变量拿来用即可, 而无需再次从colors.xml中获取该颜色值.
                 */
                if (mBeenReadItemTitleColor == -1) {
                    mBeenReadItemTitleColor = mActivity.getResources().getColor(R.color.color_netease_news_list_item_title_has_read);
                }
                // setTextColor()方法的源码内部已经包含了invalidate()的步骤了, 所以我们无需手动调用invalidate()方法.
                tvTitle.setTextColor(mBeenReadItemTitleColor);
                // 将被点击的item位置 mClickedItemPosition 进行复位, 以避免对接下来点击其他item造成干扰.
                resetClickedItemPosition();
                break;
            }
        }

    }

    /**
     * 将被点击的item位置 mClickedItemPosition 进行复位
     */
    private void resetClickedItemPosition() {
        mClickedItemPosition = ITEM_CLICK_RESETED_POSITION;
    }

    @Override
    public void showLoadingView() {
        // 当首次创建某个页面, 屏幕上没有数据, 这时要显示的UI加载效果.
        if (!mHasNewsDataShownOnScreen) {
            showLoadingProgressBar();
        }
        // 当页面已经存在, 进行下拉刷新或翻页等http请求, 并且屏幕上已经有数据时, 需要显示的UI效果.
        else {
            showLoadingViewOnListViewBottomReached();
        }
    }

    @Override
    public void removeLoadingView() {
        if (mProgressBarLoading.getVisibility() == View.VISIBLE) {
            mProgressBarLoading.setVisibility(View.GONE);
        } else {
            mLvNewsInternal.removeFooterView(mVgFooterViewContainer);
        }
    }

    private void showLoadingProgressBar() {
        mProgressBarLoading.setVisibility(View.VISIBLE);
        mPtrLvNews.setVisibility(View.GONE);
    }

    private void showLoadingViewOnListViewBottomReached() {
        if (NetworkUtils.hasNetworkConnection()) {
            mTvFooterLoadFail.setVisibility(View.GONE);
            mVgFooterLoadingNextPage.setVisibility(View.VISIBLE);
        } else {
            mTvFooterLoadFail.setVisibility(View.VISIBLE);
        }
        mLvNewsInternal.addFooterView(mVgFooterViewContainer);
    }

    /**
     * 当既没有缓存可以调用, 又加载失败的时候, 需要显示的一个ViewStub页面.
     * 该页面上通常会包含一个"加载失败"的文本提示, 以及一个"点击再次尝试加载"的按钮.
     */
    private void showLoadFailViewStub() {
        if (mLoadFailPageContentView == null) {
            mLoadFailPageContentView = mViewStubLoadFail.inflate();
            TextView tvReload = (TextView) mLoadFailPageContentView.findViewById(R.id.tv_reload);
            tvReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 隐藏该按钮所在的 Container View, 以使得在再次请求时只显示环形进度条.
                    mLoadFailPageContentView.setVisibility(View.GONE);
                    // 尝试再次请求数据.
                    mPageNewsListPresenter.requestPageNews(mCurrNewsTabName, true, true);
                }
            });
        } else {
            mLoadFailPageContentView.setVisibility(View.VISIBLE);
        }
    }

    private void showViewOnNoMoreDataToLoad() {
        if (mVgFooterLoadingNextPage.getVisibility() == View.VISIBLE) {
            mVgFooterLoadingNextPage.setVisibility(View.GONE);
        }
        if (mTvFooterLoadFail.getVisibility() == View.VISIBLE) {
            mTvFooterLoadFail.setVisibility(View.GONE);
        }
        mTvFooterNoMoreToLoad.setVisibility(View.VISIBLE);
    }

    /**
     * 处理收到的最新的新闻 ListView的数据.
     * @param event
     */
    private void handleReceivingLatestNewsListEvent(OnGetLatestNewsListEvent event) {
//        if (mPtrLvNews.isRefreshing()) {
//            mPtrLvNews.onRefreshComplete();
//            // 复位本次下拉的状态, 表示本次下拉已结束.
//            bPullingState = PullingState.RESET;
//        }
        if (event == null || !mCurrNewsTypeId.equals(event.getNewsTypeId())) {
            return;
        }
        if (!Constant.isTestPtrListViewOnly) {
            List<NeteaseNewsItem> latestNewsList = event.getNewsList();
            mPageNewsListPresenter.putToCache(event.getNewsType(), latestNewsList);
            if (latestNewsList != null && latestNewsList.size() > 0) {
                if (NextDataIncomingType.REPLACE.equals(mNextDataIncomingType)) {
                    mNeteaseNewsItems.clear();
                }
                mNeteaseNewsItems.addAll(latestNewsList);
                mNewsListAdapter.notifyDataSetChanged();
                // 更新上一次刷新时间(当前这次的刷新时间, 就是下一次刷新时的上次刷新时间).
                // 只有http请求成功得到所需数据时才会更新该时间(该时间就是当前时间).
                mPageNewsListPresenter.updateLastRefreshTime(mCurrNewsTabName);
                mHasNewsDataShownOnScreen = true;

                synchronized (mPullingState) {
                    // 如果是因为下拉导致的刷新操作, 那么就将下拉的状态置为请求成功.
                    if (mPullingState == PullingState.PENDING) {
                        mPullingState = PullingState.REQUEST_SUCCEED;

                        // 如果已经结束了缓存查询, 就重置2个状态. 如果还在查询中, 则等到缓存查询结束后,
                        // 再一起重置二者的状态.
                        if (mNewsCacheQueryState != NewsCacheQueryState.PENDING) {
                            doResetStates();
                        }
                    }
                }
                showViewOnDataLoaded();
            }
            // 数据为null或size == 0时, 表示没有更多数据了.
            else {
                // TODO: 数据为null时, 表示没有更多数据了.
//              ToastUtils.showDebug("没有更多数据了, 当前页码 == " + mCurrPageNumber);
                synchronized (mPullingState) {
                    // 如果是因为下拉导致的刷新操作, 那么就将下拉的状态置为请求失败.
                    if (mPullingState == PullingState.PENDING) {
                        mPullingState = PullingState.REQUEST_FAIL;
                        // 如果已经结束了缓存查询, 就重置2个状态. 如果还在查询中, 则等到缓存查询结束后,
                        // 再一起重置二者的状态.
                        if (mNewsCacheQueryState != NewsCacheQueryState.PENDING) {
                            doResetStates();
                        }
                    }
                }
                showViewOnNoMoreDataToLoad();
            }
        }
    }

    private void handleReceivingCacheNewsEvent(OnGetCachedNewsListEvent event) {
//        if (bNewsCacheQueryState != NewsCacheQueryState.PENDING) {
//            throw new IllegalStateException("未初始化缓存查询的状态bNewsCacheQueryState, " +
//                    "请在执行查询前先将该变量置为 PENDING 状态");
//        }
        if (event == null || !mCurrNewsTypeId.equals(event.getNewsTypeId())) {
            return;
        }
        List<NeteaseNewsItem> newsCache = event.getNewsList();
        // 如果能查询到缓存数据
        if (newsCache != null && newsCache.size() > 0) {
            mNewsCacheQueryState = NewsCacheQueryState.HAS_CACHE;
            switch (mPullingState) {
                // 如果发现先前刷新请求服务器最新数据已经成功, 那么就不显示缓存的数据, 并重置两个状态.
                case REQUEST_SUCCEED:
                    doResetStates();
                    break;
                // 如果先前刷新请求服务器最新数据失败, 就显示缓存的数据, 并重置两个状态.
                case REQUEST_FAIL:
                    mNeteaseNewsItems.addAll(newsCache);
                    mNewsListAdapter.notifyDataSetChanged();
                    mHasNewsDataShownOnScreen = true;
                    showViewOnDataLoaded();
                    doResetStates();
                    break;
                // 如果先前刷新请求服务器最新数据仍在请求中, 就先显示缓存的数据
                case PENDING:
                    mNeteaseNewsItems.addAll(newsCache);
                    mNewsListAdapter.notifyDataSetChanged();
                    mHasNewsDataShownOnScreen = true;
                    showViewOnDataLoaded();
                    break;
            }
        }
        // 如果查询不到缓存数据
        else {
            mNewsCacheQueryState = NewsCacheQueryState.NO_CACHE;
            // 如果没有缓存, 但发现先前刷新请求服务器最新数据已经成功, 那么就直接重置两个状态.
            if (mPullingState == PullingState.REQUEST_SUCCEED) {
                doResetStates();
                return;
            }
            // 如果既没有缓存, 又不能从服务器上得到最新数据, 那么只能显示一个"加载失败"的空白页面
            else if (mPullingState == PullingState.REQUEST_FAIL) {
                mCurrPageNumber = 0;
                mNextPageNumber = 1;
                doResetStates();
                doShowViewStubNetworkFail();
            }
            // TODO: 数据为null时, 表示没有更多数据了.
//                    ToastUtils.showDebug("没有更多数据了, 当前页码 == " + mCurrPageNumber);
//            showViewOnNoMoreDataToLoad();
        }
    }

    /**
     * 执行复位缓存查询的状态和下拉刷新请求的状态
     */
    private void doResetStates() {
        // 缓存查询的状态复位.
        mNewsCacheQueryState = NewsCacheQueryState.RESET;
        // 下拉刷新请求的状态复位.
        mPullingState = PullingState.RESET;
    }

    @Override
    public void showViewOnDataLoaded() {
        removeLoadingView();
        mPtrLvNews.setVisibility(View.VISIBLE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!Constant.isTestPtrListViewOnly) {
            // PtrListView的visible item需要减去顶部和底部这两个刷新用的item之后剩余的才是新闻条目的item.
            if (mLvNewsTotalItemCount > 0 && (mLvNewsLastVisibleItem - 2) == mNewsListAdapter.getCount()
                    && scrollState == SCROLL_STATE_IDLE) {
                // 不是下拉导致的请求, 而是翻页导致的请求.
                mIsPullToRefreshRequest = false;
                mPageNewsListPresenter.requestPageNews(mCurrNewsTabName, false, false);
            }
        }
    }

    @Override
    public int setCurrAndNextPageNumber(boolean shouldRefreshWholePageData) {
        if (shouldRefreshWholePageData) {
            mCurrPageNumber = 0;
            mNextPageNumber = 1;
        } else {
            // 如果本次刷新失败, 则页码再从该临时变量里取回来.
            mTempCurrPageNumber = mCurrPageNumber;
            mCurrPageNumber = mNextPageNumber;
            mNextPageNumber++;
        }
        return mCurrPageNumber;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mLvNewsTotalItemCount = totalItemCount;
        mLvNewsLastVisibleItem = firstVisibleItem + visibleItemCount;
        LogUtils.i(TAG, "lastVisibleItem = " + mLvNewsLastVisibleItem + ", visibleItemCount = " + mLvNewsTotalItemCount);
    }

    @Override
    public void onCreate() {
        // 该方法应该在该类所在的 Activity或 Fragment的 onStart()方法中执行, 因为为了节省资源, 我们在
        // 该类所在的 Activity或 Fragment的 onStop()方法中取消对EventBus的注册, 所以当一个页面从后台再
        // 回到前台, 就需要再次注册 EventBus, 所以对 EventBus的注册过程应该放在 Activity或 Fragment的
        // onStart() 方法中进行.

//        if (!EventBusUtils.isRegistered(this)) {
//            EventBusUtils.registerEventBus(this);
//        }
    }

    @Override
    public void clear() {
        // 这句话不能少. 因为在这句话中要执行 Presenter的清理工作, 而Presenter的清理工作包括Model的清理
        // 以及Presenter内部定义的其他资源(例如: Handler的实例)的清理.
        super.clear();

        // 这句话应该放在该类所在的 Activity或 Fragment的 onStop()方法中执行, 而不是在他们的 onDestroy()中执行.
        // 因为当一个页面到后台后, 就应该取消 EventBus的注册以节省系统资源.
//        EventBusUtils.unregisterEventBus(this);

        // 清理 MyHandler实例发送的所有的callbacks和messages.
        MyHandler.getInstance().removeCallbacksAndMessages(null);
    }

    private enum NextDataIncomingType {
        APPEND, REPLACE
    }
}