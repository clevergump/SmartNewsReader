package com.clevergump.newsreader.netease_news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.R;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsBase;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.presenter.impl.PageNewsListPresenter;
import com.clevergump.newsreader.netease_news.utils.ImageLoaderUtils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 网易新闻数据 ListView的 adapter
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/11 17:31
 * @projectName NewsReader
 */
public class NeteaseNewsListAdapter extends BaseAdapter {

    private static final int ITEM_LAYOUT_RES_FOR_ONE_IMAGE = R.layout.list_item_news_one_image;
    private static final int ITEM_LAYOUT_RES_FOR_THREE_IMAGES = R.layout.list_item_news_three_images;

    private PageNewsListPresenter mPresenter;
    private Context mContext;
    private LayoutInflater mInflater;
    // 新闻数据的list
    private List<NeteaseNewsItem> mNeteaseNewsItems;

    // 各个已被成功查看的新闻条目的docId所组成的set
    private Set<String> mClickedItemDocIds = new TreeSet<String>();

    public NeteaseNewsListAdapter(Context context,
                                  LayoutInflater inflater, List<NeteaseNewsItem> neteaseNewsItems) {

        if (context == null) {
            throw new IllegalArgumentException(context + " == null");
        }
        if (inflater == null) {
            throw new IllegalArgumentException(inflater + " == null");
        }
        if (neteaseNewsItems == null || neteaseNewsItems.size() < 0) {
            throw new IllegalArgumentException(neteaseNewsItems + " == null or size < 0");
        }

        this.mContext = context;
        this.mInflater = inflater;
        this.mNeteaseNewsItems = neteaseNewsItems;
    }

    @Override
    public int getCount() {
        return mNeteaseNewsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNeteaseNewsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NeteaseNewsItem neteaseNewsItem = (NeteaseNewsItem) getItem(position);
        if (neteaseNewsItem == null) {
            return null;
        }
        ViewHolder holder = null;
        switch (getItemViewType(position)){
            case NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_ONE_IMAGE:
                if (convertView == null) {
                    convertView = mInflater.inflate(ITEM_LAYOUT_RES_FOR_ONE_IMAGE, null);
                    holder = new ViewHolder();
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_news_item_title);
                    holder.tvDigest = (TextView) convertView.findViewById(R.id.tv_news_item_digest);
                    holder.ivImageForOneImageItem = (ImageView) convertView.findViewById(R.id.iv_news_list_item_one_image);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.tvTitle.setText(neteaseNewsItem.title);
                // 设置新闻标题文字的颜色
                setNewsItemTitleTextColor(neteaseNewsItem, holder);

                /**
                 *  1. 这里将标题的TextView设置为 convertView的tag, 目的是为了处理新闻条目阅读后需要根据
                 *  这里设定的tag找到被点击条目的标题TextView, 然后才能设置其颜色为灰色(表示已读过).
                 *
                 *  2. View.setTag(int key, Object tag)方法的参数key, 只能是该APP内的资源id, 不能是自己
                 *  随意设定的一个int值, 否则会报异常. 这样设计的目的是为了保证key的唯一性.
                 */
                convertView.setTag(R.id.tv_news_item_title, holder.tvTitle);
                holder.tvDigest.setText(neteaseNewsItem.digest);
                displayImage(holder.ivImageForOneImageItem, neteaseNewsItem.imgsrc);
                break;
            case NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_THREE_IMAGE:
                if (convertView == null) {
                    convertView = mInflater.inflate(ITEM_LAYOUT_RES_FOR_THREE_IMAGES, null);
                    holder = new ViewHolder();
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_news_item_title);
                    holder.ivLeft = (ImageView) convertView.findViewById(R.id.iv_news_list_item_left_image);
                    holder.ivMiddle = (ImageView) convertView.findViewById(R.id.iv_news_list_item_middle_image);
                    holder.ivRight = (ImageView) convertView.findViewById(R.id.iv_news_list_item_right_image);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.tvTitle.setText(neteaseNewsItem.title);
                // 设置新闻标题文字的颜色
                setNewsItemTitleTextColor(neteaseNewsItem, holder);

                /**
                 *  1. 这里将标题的TextView设置为 convertView的tag, 目的是为了处理新闻条目阅读后需要根据
                 *  这里设定的tag找到被点击条目的标题TextView, 然后才能设置其颜色为灰色(表示已读过).
                 *
                 *  2. View.setTag(int key, Object tag)方法的参数key, 只能是该APP内的资源id, 不能是自己
                 *  随意设定的一个int值, 否则会报异常. 这样设计的目的是为了保证key的唯一性.
                 */
                convertView.setTag(R.id.tv_news_item_title, holder.tvTitle);
                displayImage(holder.ivLeft, neteaseNewsItem.imgsrc);
                displayImage(holder.ivMiddle, neteaseNewsItem.imgExtraSrc0);
                displayImage(holder.ivRight, neteaseNewsItem.imgExtraSrc1);
                break;
        }
        return convertView;
    }

    /**
     * 设置新闻条目的标题文字的颜色是黑色还是灰色. 黑色表示未读过, 灰色表示已读过.
     * @param neteaseNewsItem
     * @param holder
     */
    private void setNewsItemTitleTextColor(NeteaseNewsItem neteaseNewsItem, final ViewHolder holder) {
        if (mClickedItemDocIds.contains(neteaseNewsItem.docid)) {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.color_netease_news_list_item_title_has_read));
        } else if (mPresenter != null) {
            mPresenter.getNewsItemReadState(neteaseNewsItem.docid, new OnGetNewsItemReadStateListener() {
                @Override
                public void onGetNewsItemReadState(int readState) {
                    if (readState == Constant.NEWS_ITEM_HAS_READ) {
                        holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.color_netease_news_list_item_title_has_read));
                    } else {
                        holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.color_netease_news_list_item_title_not_read));
                    }
                }
            });
        }
    }

    /**
     * 设置 PageNewsListPresenter 的引用.
     * @param presenter
     */
    public void setPresenter(PageNewsListPresenter presenter) {
        if (presenter == null) {
            throw new IllegalArgumentException(presenter + " == null");
        }
        this.mPresenter = presenter;
    }

    /**
     * 当获取到某条新闻是否已读的状态之后的回调接口
     */
    public interface OnGetNewsItemReadStateListener {
        void onGetNewsItemReadState(int readState);
    }

    /**
     * 处理新闻图片显示的业务逻辑.
     * @param iv
     * @param imgUrl
     */
    private void displayImage(ImageView iv, String imgUrl) {
        ImageLoaderUtils.displayImage(imgUrl, iv, null, null);
    }

    /**
     * 将与给定docId所对应的那条新闻条目在底层(通常是数据库)存储的阅读状态更改为"已读".
     * @param clickedItemDocId
     */
    public void updataNewsItemToHasReadState(String clickedItemDocId) {
        mClickedItemDocIds.add(clickedItemDocId);

        /**
         * 如果这里使用 notifyDataSetChanged(), 那么效果将是所有可见的条目都将被重新加载一次, 那么看到的现象
         * 将是: 当关闭某个新闻详情页回到新闻ListView页面时, 会发现ListView的每一个item的图片都将闪烁一次,
         * 这样的效果显然不好, 其实只需要设置被阅读的那个item的标题TextView的颜色为灰色即可.
         */
//        notifyDataSetChanged();
        if (mPresenter != null) {
            mPresenter.updateNewsItemToHasReadState(clickedItemDocId);
        }
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvDigest;
        ImageView ivImageForOneImageItem;
        ImageView ivLeft;
        ImageView ivMiddle;
        ImageView ivRight;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    // ListView包含多种item view布局类别时, 对应的 Adapter的 getItemViewType(..)方法的返回值不能
    // 随意取值. 必须从0开始取值, 并且必须小于getViewTypeCount()方法返回的 item view type总数.
    // 否则会报数组角标越界异常.
    // java.lang.ArrayIndexOutOfBoundsException: length=2; index=2
    @Override
    public int getItemViewType(int position) {
        NeteaseNewsItem neteaseNewsItem = (NeteaseNewsItem) getItem(position);
        return neteaseNewsItem.getNewsItemType(neteaseNewsItem.imgextra);
    }
}