package com.clevergump.newsreader.netease_news.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.clevergump.newsreader.netease_news.fragment.BaseNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;

import java.util.List;

/**
 * 网易新闻由每个新闻页所组成的 ViewPager的 adapter
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/7 12:59
 * @projectName NewsReader
 */
public class NeteaseNewsPagerAdapter extends FragmentPagerAdapter {

    // TODO: 待完善

    private List<String> mNewsTabNames;
    private FragmentActivity mActivity;

    public NeteaseNewsPagerAdapter(FragmentActivity activity, FragmentManager fm, List<String> newsTabNames) {
        super(fm);
        this.mActivity = activity;
        this.mNewsTabNames = newsTabNames;
    }

    @Override
    public Fragment getItem(int position) {
        String newsTabName = mNewsTabNames.get(position);
        BaseNewsFragment f = NewsFragmentManager.getInstance().switchNewsFragment(mActivity, newsTabName);
        return f;
    }

    @Override
    public int getCount() {
        if (mNewsTabNames != null) {
            return mNewsTabNames.size();
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mNewsTabNames.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        ToastUtils.showDebug("PagerAdapter instantiateItem");
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ToastUtils.showDebug("PagerAdapter destroyItem");

        // 将super.destroyItem(..)屏蔽掉, 可避免频繁地删除fragment的动作, 从而可以降低点击切换fragment
        // 过程中的卡顿现象. 但缺点是fragment都存放在内存中, 容易OOM. 所以降低卡顿现象还可以通过延迟加载
        // fragment内容的方式来实现.
        // 参见: http://www.cnblogs.com/spring87/p/4308247.html
        super.destroyItem(container, position, object);
    }
}