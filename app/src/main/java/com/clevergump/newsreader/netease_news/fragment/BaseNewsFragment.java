package com.clevergump.newsreader.netease_news.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.netease_news.activity.NeteaseNewsListActivity;
import com.clevergump.newsreader.netease_news.cache.impl.PageNewsListCache;
import com.clevergump.newsreader.netease_news.dao.table.news_list.impl.NeteaseNewsListDaoImpl;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;
import com.clevergump.newsreader.netease_news.model.IPageNewsModel;
import com.clevergump.newsreader.netease_news.model.impl.PageNewsModelImpl;
import com.clevergump.newsreader.netease_news.presenter.impl.PageNewsListPresenter;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.IEventBusSubscriber;
import com.clevergump.newsreader.netease_news.utils.LogUtils;
import com.clevergump.newsreader.netease_news.view.IPageNewsView;
import com.clevergump.newsreader.netease_news.view.impl.PageNewsPullToRefreshListView;

import java.util.HashSet;
import java.util.Set;

/**
 * 新闻fragment的基类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/6 17:45
 * @projectName NewsReader
 */
public class BaseNewsFragment extends Fragment {

    public static final String KEY_NEWS_TAB_NAME = "key_news_tab_name";
    private final int FIRST_PAGE_NUMBER = 0;

    protected NeteaseNewsListActivity bActivity;
    protected IPageNewsView bPageNewsView;
    protected PageNewsListPresenter bPageNewsListPresenter;
    protected String bNewsTabName;
    protected String bNewsTypeId;

    private Set<IEventBusSubscriber> bEventBusSubscribers = new HashSet<IEventBusSubscriber>();

    protected int bCurrentPageNumber = FIRST_PAGE_NUMBER;

    public String getTabName() {
        String fragmentName = getClass().getSimpleName();
        switch (fragmentName) {
            case "EntertainmentNewsFragment":
                return "娱乐 ";
            case "FinanceNewsFragment":
                return "财经 ";
            case "HealthNewsFragment":
                return "健康 ";
            case "InternationalFootballNewsFragment":
                return "国际足球 ";
            case "MilitaryNewsFragment":
                return "军事 ";
            case "SocialNewsFragment":
                return "社会 ";
            case "SportsNewsFragment":
                return "体育 ";
            case "TechnologyNewsFragment":
                return "科技 ";
            default:
                return "默认的fragment ";
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.i(getTabName() + "onCreate");
        super.onCreate(savedInstanceState);
        bActivity = (NeteaseNewsListActivity) getActivity();
//        EventBusUtils.registerEventBus(this);
//        ToastUtils.showDebug(getClass().getSimpleName()+" onCreate");
    }

    /**
     * 创建各个类别新闻的 fragment View的默认方法, 默认显示的是可下拉刷新的新闻条目的 ListView.
     * 如果子类有其他的View样式需要显示 (例如: 显示的是图片瀑布流, 或者天气预报等), 则可以重写该方法.
     * 另外，也可以重写该方法然后方法体调用 setTabNameAsContentView()方法, 让fragment仅显示一个TextView,
     * 用于debug.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i(getTabName() + "onCreateView");
        bNewsTabName = getArguments().getString(KEY_NEWS_TAB_NAME);
        bNewsTypeId = NewsFragmentManager.getInstance().getNewsTypeId(bNewsTabName);
        bPageNewsView = getPageNewsView(bActivity, inflater, bNewsTabName);
        bEventBusSubscribers.add(bPageNewsView);

        // 每个Fragment实例都会执行一次生命周期方法. 所以每次执行时都会调用下面这句代码.
        // 而下面这句代码中包含了EventBus注册监听器的方法, 而EventBus的监听器是不允许重复注册的,
        // 所以需要对其注册进行预先的判断, 如果已经注册过, 就不再重复注册.
        // 重复注册EventBus监听器会报异常: eventbus Subscriber... already registered to event...
        bPageNewsView.onCreate();
        return bPageNewsView.getContentView();

//        return setTabNameAsContentView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.i(getTabName() + "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        initData();

        // 一进入该fragment页面, 就要请求加载新闻数据并显示.
        bPageNewsListPresenter.requestPageNews(bNewsTabName, true, true);
    }

    @Override
    public void onStart() {
//        ToastUtils.showDebug(getTabName() + " onStart, 网络请求");
        LogUtils.i(getTabName() + "onStart");
        super.onStart();

        // 因为为了节省资源, 我们在 Activity或 Fragment的 onStop()方法中取消对EventBus的注册, 所以当
        // 一个页面从后台再回到前台, 就需要再次注册 EventBus, 所以对 EventBus的注册过程应该放在 Activity
        // 或 Fragment的 onStart() 方法中执行, 而不是 onCreate()方法中执行.
        for (IEventBusSubscriber subscriber : bEventBusSubscribers) {
            if (!EventBusUtils.isRegistered(subscriber)) {
                EventBusUtils.registerEventBus(subscriber);
            }
        }
    }

    @Override
    public void onResume() {
        LogUtils.i(getTabName() + "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.i(getTabName() + "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtils.i(getTabName() + "onStop");
        super.onStop();
        for (IEventBusSubscriber subscriber : bEventBusSubscribers) {
            if (EventBusUtils.isRegistered(subscriber)) {
                EventBusUtils.unregisterEventBus(subscriber);
            }
        }
    }

    @Override
    public void onDestroyView() {
        LogUtils.i(getTabName() + "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        LogUtils.i(getTabName() + "clear");
        super.onDestroy();

        // 这个onDestroy()的调用很有必要. 因为在该方法中可以执行"取消监听器的注册", 清理Handler发出
        // 的所有 callbacks和messages, 取消所有正在执行的异步任务(如: AsyncTask), 关闭所有的
        // Cursor/IO流/HttpURLConnection等.
        bPageNewsView.clear();
    }

    @Override
    public void onDetach() {
        LogUtils.i(getTabName() + "onDetach");
        super.onDetach();
    }

    /**
     * 不同视图样式的新闻 fragment可以通过重写此方法来提供自己的视图样式.
     * @param inflater
     * @return
     */
    protected IPageNewsView getPageNewsView(NeteaseNewsListActivity activity, LayoutInflater inflater, String newsTabName) {
        return new PageNewsPullToRefreshListView(activity, inflater, newsTabName);
//        return new PageNewsListView(activity, inflater, newsTabName);
    }

    private void initData() {
        IPageNewsModel pageNewsModel;
        if (Constant.isTestPtrListViewOnly) {
//            pageNewsModel = new TestPtrListViewPageNewsModelImpl();
        } else {
            PageNewsListCache pageNewsListCache = PageNewsListCache.getInstance();
            pageNewsModel = new PageNewsModelImpl(bActivity, bNewsTypeId,
                    NeteaseNewsListDaoImpl.getInstance(), pageNewsListCache);
            bEventBusSubscribers.add(pageNewsListCache);
        }

        bPageNewsListPresenter = new PageNewsListPresenter(bPageNewsView, pageNewsModel);
        bPageNewsView.setPresenter(bPageNewsListPresenter);
    }

    /**
     * 将fragment标签的名称构成的TextView作为整个fragment的内容View.
     * 该方法可用于测试.
     * @return
     */
    @NonNull
    protected View setTabNameAsContentView() {
        TextView tv = new TextView(getActivity());

        // onMeasure, onLayout 的过程
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // onDraw 的过程
        tv.setText(bNewsTabName);
        tv.setTextColor(Color.parseColor("#0000ff"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30.0f);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}