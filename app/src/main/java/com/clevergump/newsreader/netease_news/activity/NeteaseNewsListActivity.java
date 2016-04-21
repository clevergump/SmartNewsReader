package com.clevergump.newsreader.netease_news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.R;
import com.clevergump.newsreader.netease_news.adapter.NeteaseNewsPagerAdapter;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.LogUtils;
import com.clevergump.newsreader.netease_news.utils.PropertiesUtils;
import com.clevergump.newsreader.netease_news.utils.ToastUtils;
import com.clevergump.newsreader.netease_news.view.IPageNewsView;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

import java.io.Serializable;
import java.util.List;


/**
 * 网易新闻的页面
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/7 11:00
 * @projectName NewsReader
 */
public class NeteaseNewsListActivity extends SimpleProxyActivity {


    /***************************** 常量 ****************************************/
    private final String TAG = getClass().getSimpleName();



    /******************* View *********************/
    private TabPageIndicator mTabPageIndicator;
    private ViewPager mNewsPager;

    private IPageNewsView mPageNewsView;

    /******************* ViewStub *********************/

    private View mVgLoadFail;
    private TextView mTvReload;


    /******************** Data *************************/
    private List<String> mNewsTabNames = null;
    // 上一次按返回键的时刻(毫秒数)
    private long mPrevPressTimeMillis;
    // 本次按返回键的时刻(毫秒数)
    private long mCurrPressTimeMillis;
    // 连续两次按返回键能成功退出App的按键时间间隔（毫秒数）
    private final long EXIT_APP_BACK_PRESSED_INTERVAL = 1500;

    /**
     * 获取该Activity的 simpleName
     * @return
     */
    private String getName() {
        return getClass().getSimpleName() + " ";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Constant.DEBUG) {
            LogUtils.d(getName() + "onCreate");
        }
        setContentView(R.layout.activity_netease_news_list);
        super.onCreate(savedInstanceState);
        // 设置友盟统计以加密的方式来传输日志
        AnalyticsConfig.enableEncrypt(true);
    }

    @Override
    protected void onStart() {
        if (Constant.DEBUG) {
            LogUtils.d(getName() + "onStart");
        }
        super.onStart();
        if (mPageNewsView != null && !EventBusUtils.isRegistered(mPageNewsView)) {
            mPageNewsView.registerEventBus();
        }
    }

    @Override
    protected void onResume() {
        if (Constant.DEBUG) {
            LogUtils.d(getName() + "onResume");
        }
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (Constant.DEBUG) {
            LogUtils.d(getName() + "onSaveInstanceState");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        if (Constant.DEBUG) {
            LogUtils.d(getName() + "onPause");
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (Constant.DEBUG) {
            LogUtils.d(getName() + "onStop");
        }
        super.onStop();

        if (mPageNewsView != null && EventBusUtils.isRegistered(mPageNewsView)) {
            mPageNewsView.unregisterEventBus();
        }
    }

    @Override
    protected void onRestart() {
        if (Constant.DEBUG) {
            LogUtils.d(getName() + "onRestart");
        }
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        if (Constant.DEBUG) {
            LogUtils.d(getName() + "onDestroy");
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mCurrPressTimeMillis = System.currentTimeMillis();
        if(mCurrPressTimeMillis - mPrevPressTimeMillis < EXIT_APP_BACK_PRESSED_INTERVAL) {
            super.onBackPressed();
            finish();

            MobclickAgent.onKillProcess(this);
            // 销毁当前应用所在的进程。如果不执行这句代码, 那么只是该Activity结束了，但该
            // APP所属的进程仍然在后台运行，该APP所持有的静态变量依然在内存中不能得到释放，
            // 就有可能在下次运行该APP时造成异常 IllegalStateException: Can not perform
            // this action after onSaveInstanceState。例如：再次运行APP时运行到
            // FragmentTransaction.commit() 或 commitAllowingStateLoss()时就会报异常。
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            mPrevPressTimeMillis = mCurrPressTimeMillis;
            ToastUtils.show("再按一次退出软件");
        }
    }

    @Override
    protected void initView() {
        mTabPageIndicator = findView(R.id.tab_page_indicator);
        mNewsPager = findView(R.id.viewpager_news);
    }

    @Override
    protected void initData() {
        if (mNewsTabNames == null) {
            mNewsTabNames = PropertiesUtils.getInstance().getNewsTabNames();
        }
    }

    @Override
    protected void setListenerOrAdapter() {
        NeteaseNewsPagerAdapter pagerAdapter = new NeteaseNewsPagerAdapter(
                this, getSupportFragmentManager(), mNewsTabNames);
        mNewsPager.setAdapter(pagerAdapter);
        OnPageChangeListenerImpl pageChangeListenerImpl = new OnPageChangeListenerImpl();
        mNewsPager.addOnPageChangeListener(pageChangeListenerImpl);
        mTabPageIndicator.setViewPager(mNewsPager);
//        mTabPageIndicator.setOnTabReselectedListener(this);
    }

    /**
     * 打开新闻详情的页面
     */
    public void launchNewsDetailActivity(IPageNewsView pageNewsView, String docid) {
        mPageNewsView = pageNewsView;
        Intent intent = new Intent(NeteaseNewsListActivity.this, NeteaseNewsDetailActivity.class);
        intent.putExtra(Constant.KEY_NETEASE_NEWS_DETAIL_DOCID, docid);
        startActivityForResult(intent, Constant.REQUEST_CODE_OPEN_NEWS_DETAIL_ACTIVITY);
    }

    /**
     * 打开图片详情的页面
     */
    public void launchImageDetailActivity(IPageNewsView pageNewsView, String docid, String title,
                                          Serializable imageUrls) {
        mPageNewsView = pageNewsView;
        Intent intent = new Intent(this, NeteaseImageDetailActivity.class);
        // docid. 主要用于在关闭详情页面后, 该条新闻在新闻list中的标题是否会设置其变为灰色, 表示已经读过这条详情.
        intent.putExtra(Constant.KEY_NETEASE_NEWS_DETAIL_DOCID, docid);
        // 标题. 主要用于在图片详情页面中显示.
        intent.putExtra(Constant.KEY_NETEASE_NEWS_DETAIL_TITLE, title);
        // 图片的url地址. 主要用于在图片详情页面中加载图片.
        intent.putExtra(Constant.KEY_NETEASE_NEWS_DETAIL_IMAGE_URLS, imageUrls);
        startActivityForResult(intent, Constant.REQUEST_CODE_OPEN_IMAGE_DETAIL_ACTIVITY);
    }

    // TODO: 将新闻条目相关的文字变为灰色
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case Constant.REQUEST_CODE_OPEN_NEWS_DETAIL_ACTIVITY:
//                if (resultCode == RESULT_OK && mPageNewsView != null) {
//                    // 设置新闻条目的字体变为灰色, 表示已经读过这篇文章或看过这个图片集
//                    mPageNewsView.updataNewsItemToHasReadState();
//                }
//                break;
//            case Constant.REQUEST_CODE_OPEN_IMAGE_DETAIL_ACTIVITY:
//
//                break;
//        }
        if (resultCode == RESULT_OK && mPageNewsView != null) {
            // 设置新闻条目的字体变为灰色, 表示已经读过这篇文章或看过这个图片集
            mPageNewsView.onNewsItemHasBeenRead();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void onTabReselected(int position) {
//        mNewsPager.setCurrentItem(position, false);
//    }

    /**
     * ViewPager 页面切换的监听器实现类
     */
    private class OnPageChangeListenerImpl extends ViewPager.SimpleOnPageChangeListener {

        private int currState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (currState == ViewPager.SCROLL_STATE_IDLE) {
                mNewsPager.setCurrentItem(position, false);
            }
        }

        @Override
        public void onPageSelected(int position) {
            mNewsPager.setCurrentItem(position, false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            currState = state;
        }
    }
}