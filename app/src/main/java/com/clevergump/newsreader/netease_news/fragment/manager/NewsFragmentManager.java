package com.clevergump.newsreader.netease_news.fragment.manager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.clevergump.newsreader.netease_news.fragment.BaseNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.EntertainmentNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.FinanceNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.HealthNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.InternationalFootballNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.MilitaryNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.SocialNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.SportsNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.TechnologyNewsFragment;
import com.clevergump.newsreader.netease_news.utils.LogUtils;

import java.lang.ref.WeakReference;

/**
 * 各个新闻fragment的统一管理类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/10 10:32
 * @projectName NewsReader
 */
public class NewsFragmentManager {

    private static final String TAG = NewsFragmentManager.class.getSimpleName();
    public static final String LOG_ACTIVITY_HAS_BEEN_RECYCLED = "Activity has been recycled";
    private final int NEWS_INT_TYPE_ENTERTAINMENT = 0;
    private final int NEWS_INT_TYPE_FINANCE = 1;
    private final int NEWS_INT_TYPE_HEALTH = 2;
    private final int NEWS_INT_TYPE_INTERNATION_FOOTBALL = 3;
    private final int NEWS_INT_TYPE_MILITARY = 4;
    private final int NEWS_INT_TYPE_SOCIAL = 5;
    private final int NEWS_INT_TYPE_SPORTS = 6;
    private final int NEWS_INT_TYPE_TECHNOLOGY = 7;

    private String mCurrentShowingFragmentTag;
//    private FragmentManager mFragmentManager;
    private WeakReference<FragmentManager> mFragmentMgrWeakRef;

    private NewsFragmentManager(){
    }

    // 单例.
    public static NewsFragmentManager getInstance() {
//        return new NewsFragmentManager();
        return NewsFragmentManagerHolder.INSTANCE;
    }

    private static class NewsFragmentManagerHolder {
        private static final NewsFragmentManager INSTANCE = new NewsFragmentManager();
    }

    /**
     * 切换fragment的显示, 并获取当前要显示的BaseNewsFragment对象.
     */
    public BaseNewsFragment switchNewsFragment(FragmentActivity activity, String newsTabName){
        setFragmentManager(activity);
        FragmentManager fragmentManager = mFragmentMgrWeakRef.get();
        if (fragmentManager == null) {
            Log.w(TAG, LOG_ACTIVITY_HAS_BEEN_RECYCLED);
            return new BaseNewsFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        BaseNewsFragment f = transactFragment(newsTabName, transaction);
        transaction.commit();
//      transaction.commitAllowingStateLoss();
        LogUtils.i(f.getTabName() + "------ transaction commit");
        return f;
    }

    /**
     * 根据新闻标签名称获取到对应的BaseNewsFragment子类对象.
     *
     * @param activity 包含多个新闻标签(多个新闻fragment)的Activity对象
     * @param newsTabName 对应的新闻标签名称
     * @param <T>
     * @return
     */
    public <T extends FragmentActivity> Fragment findFragmentByTabName(T activity, String newsTabName) {
        String tag = getNewsFragmentTag(newsTabName);
        Fragment f = activity.getSupportFragmentManager().findFragmentByTag(tag);
        return f;
    }

    /**
     * 根据新闻标签名称获取对应的新闻fragment的tag. 如果查询不到与该新闻标签名称对应的fragment tag,
     * 则表示系统中不存在给定的新闻标签名称.
     *
     * @param newsTabName 新闻标签名称
     * @return 与给定的新闻标签名称对应的新闻fragment的tag
     */
    public String getNewsFragmentTag(String newsTabName) {
        return getNewsTypeId(newsTabName);
    }

    /**
     * 根据新闻标签名称获取对应的新闻类别id. 如果查询不到与该新闻标签名称对应的typeId,
     * 则表示系统中不存在给定的新闻标签名称.
     *
     * 返回的新闻类别id有以下几个用途:
     * 1. 用于拼接对应新闻类别的json形式的url.
     *    例如: 体育fragment的 typeId就可以用 "T1348649079062" 来表示.
     * 2. 作为识别新闻fragment的tag, 用于新闻fragment之间进行切换时, 作为识别相应fragment的唯一依据.
     *    此功能等同于 getFragmentTag().
     *
     * @param newsTabName 新闻标签名称
     * @return 与给定的新闻标签名称对应的新闻类别id
     */
    public String getNewsTypeId(String newsTabName) {
        switch (newsTabName) {
            case "娱乐":
                return "T1348648517839";
            // http://c.3g.163.com/nc/article/list/T1348648756099/0-20.html
            case "财经":
                return "T1348648756099";
            case "健康":
                return "T1414389941036";
            case "国际足球":
                return "T1348649176279";
            case "军事":
                return "T1348648141035";
            case "社会":
                return "T1348648037603";
            case "体育":
                return "T1348649079062";
            case "科技":
                return "T1348649580692";
            default:
                throw new IllegalArgumentException("不存在与"+newsTabName+"对应的新闻标签/fragment tag");
        }
    }

    /**
     * 仅供测试
     * @param newsTypeId
     * @return
     */
    public String getNewsTabName(String newsTypeId) {
        switch (newsTypeId) {
            case "T1348648517839":
                return "娱乐";
            // http://c.3g.163.com/nc/article/list/T1348648756099/0-20.html
            case "T1348648756099":
                return "财经";
            case "T1414389941036":
                return "健康";
            case "T1348649176279":
                return "国际足球";
            case "T1348648141035":
                return "军事";
            case "T1348648037603":
                return "社会";
            case "T1348649079062":
                return "体育";
            case "T1348649580692":
                return "科技";
            default:
                throw new IllegalArgumentException("不存在与"+newsTypeId+"对应的新闻标签/fragment tag");
        }
    }

    /**
     * 新建与给定新闻标签名称对应的新闻fragment实例.
     *
     * @param newsTabName 给定的新闻标签名称
     * @param <T>
     * @return 与给定新闻标签名称对应的新闻fragment实例
     */
    public <T extends BaseNewsFragment> T createNewsFragment(String newsTabName) {
        T fragment = null;
        switch (newsTabName) {
            case "娱乐":
                fragment = (T) new EntertainmentNewsFragment();
                break;
            // http://c.3g.163.com/nc/article/list/T1348648756099/0-20.html
            case "财经":
                fragment = (T) new FinanceNewsFragment();
                break;
            case "健康":
                fragment = (T) new HealthNewsFragment();
                break;
            case "国际足球":
                fragment = (T) new InternationalFootballNewsFragment();
                break;
            case "军事":
                fragment = (T) new MilitaryNewsFragment();
                break;
            case "社会":
                fragment = (T) new SocialNewsFragment();
                break;
            case "体育":
                fragment = (T) new SportsNewsFragment();
                break;
            case "科技":
                fragment = (T) new TechnologyNewsFragment();
                break;
            default:
                throw new IllegalArgumentException("不存在与"+newsTabName+"对应的新闻标签/fragment tag");
        }
        Bundle bundle = new Bundle();
        bundle.putString(BaseNewsFragment.KEY_NEWS_TAB_NAME, newsTabName);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 获取代表新闻类别的int型代号, 主要用于作为 SparseArray的 key.
     * @param newsTabName 新闻标签的名称
     * @return 新闻标签对应的int型id.
     */
    public int getNewsIntType(String newsTabName) {
        int newsIntType = 0;
        switch (newsTabName) {
            default:
            case "娱乐":
                newsIntType = NEWS_INT_TYPE_ENTERTAINMENT;
                break;
            case "财经":
                newsIntType = NEWS_INT_TYPE_FINANCE;
                break;
            case "健康":
                newsIntType = NEWS_INT_TYPE_HEALTH;
                break;
            case "国际足球":
                newsIntType = NEWS_INT_TYPE_INTERNATION_FOOTBALL;
                break;
            case "军事":
                newsIntType = NEWS_INT_TYPE_MILITARY;
                break;
            case "社会":
                newsIntType = NEWS_INT_TYPE_SOCIAL;
                break;
            case "体育":
                newsIntType = NEWS_INT_TYPE_SPORTS;
                break;
            case "科技":
                newsIntType = NEWS_INT_TYPE_TECHNOLOGY;
                break;
        }
        return newsIntType;
    }


    /************************************************************/
    /*******************  private方法  **************************/
    /************************************************************/


    /**
     * FragmentTransaction (各个fragment之间相互切换显示)的逻辑处理方法
     *
     * @param nextNewsTabName 接下来将要显示的新闻标签的名称
     * @param transaction 用来实现 fragment切换的 FragmentTransaction对象
     * @return 接下来要显示的那个fragment对象
     */
    private BaseNewsFragment transactFragment(String nextNewsTabName, FragmentTransaction transaction) {

        if(nextNewsTabName == null) {
            throw new IllegalArgumentException(nextNewsTabName + " == null");
        }

        String nextToShowFragmentTag = getNewsFragmentTag(nextNewsTabName);
        BaseNewsFragment currentShowingFragment = null;
        BaseNewsFragment nextToShowFragment = null;

        // 接下来要显示的fragment的tag不为null的前提下. 其实如果查询不到tag的话, 会在先前就已经抛出异常了.
        if (nextToShowFragmentTag != null) {
            FragmentManager fragmentManager = mFragmentMgrWeakRef.get();
            if (fragmentManager == null) {
                Log.w(TAG, LOG_ACTIVITY_HAS_BEEN_RECYCLED);
                return new BaseNewsFragment();
            }

            /****************** 当前正在显示的 fragment *****************************/
            // 如果记录表明, 曾经有fragment显示过 (但此时可能不一定正在显示).
            if (mCurrentShowingFragmentTag != null) {
                currentShowingFragment = (BaseNewsFragment) fragmentManager.findFragmentByTag(mCurrentShowingFragmentTag);
                // 如果transaction的fragment栈中存在fragment(当前不一定显示)
                if (currentShowingFragment != null) {
                    // 如果接下来要显示的fragment就是后台任务栈中最近一次添加进去的那个fragment
                    // (比如:重复点击当前正在显示的那个fragment的标签)，就要让这上一个fragment显示出来．
                    if (mCurrentShowingFragmentTag.equals(nextToShowFragmentTag)) {
                        transaction.show(currentShowingFragment);
                        return currentShowingFragment;
                    }
                    // 如果接下来想要显示的fragment, 与上一个曾经显示(可能当前也正在显示)的fragment不同,
                    // 就要隐藏掉上一个fragment. 而接下来要显示的fragment在后续代码中处理.
                    transaction.hide(currentShowingFragment);
                }
            }

            /****************** 接下来要显示的 fragment *********************/
            nextToShowFragment = (BaseNewsFragment) fragmentManager.findFragmentByTag(nextToShowFragmentTag);
            // 如果接下来要显示的fragment, 在transaction的fragment栈中不存在, 那么需要添加进去.
            if(nextToShowFragment == null){
                nextToShowFragment = createNewsFragment(nextNewsTabName);

                // 由于我们使用的fragment是嵌套在ViewPager中的, 而ViewPager的 FragmentPagerAdapter
                // 的 instantiateItem(..)方法会自动帮我们添加了 FragmentTransaction.add(..)的代码,
                // FragmentTransaction 是抽象类, 在实际执行时, 系统是调用了其子类
                // android.support.v4.app.BackStackRecord 的 add(..)方法, 而该方法的众多重载方法最终都会
                // doAddOp(..)方法, 在该方法中就会检查是否对同一个fragment重复添加了tag. 代码如下. 如果
                // 发现重复添加, 就会抛异常"Can't change tag of fragment ..."

                /*
                <pre>
                if (fragment.mTag != null && !tag.equals(fragment.mTag)) {
                     throw new IllegalStateException("Can't change tag of fragment "
                     + fragment + ": was " + fragment.mTag
                     + " now " + tag);
                }
                </pre>
                 */

                // 所以我们就不能再次添加. 所以下面这句 FragmentTransaction.add(..) 代码需要删除.
                // 另外, 从 BackStackRecord 类的名字中也可以看出, 系统对fragment的切换的底层实现机制,
                // 确实是在后台存在一个栈(Stack)来保存先前已创建的各个fragment的, 该栈起到缓存的作用,
                // 所以我们就无需自己创建类似功能的 fragment缓存容器了.
    //                transaction.add(nextToShowFragment, nextToShowFragmentTag);
            }
            else {
                transaction.show(nextToShowFragment);
            }

            /******************* 更新 tag 记录 ********************/
            mCurrentShowingFragmentTag = nextToShowFragmentTag;
        }
        return nextToShowFragment;
    }

    private void setFragmentManager(FragmentActivity activity) {
        if(mFragmentMgrWeakRef == null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            mFragmentMgrWeakRef = new WeakReference<FragmentManager>(fragmentManager);
        }
    }
}