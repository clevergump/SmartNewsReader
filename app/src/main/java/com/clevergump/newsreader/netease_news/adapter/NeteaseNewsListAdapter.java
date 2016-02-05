package com.clevergump.newsreader.netease_news.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clevergump.newsreader.R;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsBase;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.dao.table.news_list.impl.NeteaseNewsListDaoImpl;
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

    private Context mContext;
    private LayoutInflater mInflater;
    // 新闻数据的list
    private List<NeteaseNewsItem> mNeteaseNewsItems;
//    // 被点击的那个新闻条目的docid.
//    private String mClickedItemDocId;

    // 各个已被成功查看的新闻条目的docId所组成的set
    private Set<String> mClickedItemDocIds = new TreeSet<String>();

    public NeteaseNewsListAdapter(Context context, LayoutInflater inflater, List<NeteaseNewsItem> neteaseNewsItems) {
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
    private void setNewsItemTitleTextColor(NeteaseNewsItem neteaseNewsItem, ViewHolder holder) {
        if (mClickedItemDocIds.contains(neteaseNewsItem.docid)) {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.color_netease_news_list_item_title_has_read));
        } else {
            new GetNewsItemReadStateTask(neteaseNewsItem.docid, holder).execute();
        }
    }

    /**
     * 处理新闻图片显示的业务逻辑.
     * @param iv
     * @param imgUrl
     */
    private void displayImage(ImageView iv, String imgUrl) {
        ImageLoaderUtils.displayImage(imgUrl, iv, null, null);
//        ImageLoaderUtils.displayImage(imgUrl, iv, null, null);
    }

    public void onNewsItemHasBeenRead(String clickedItemDocId) {
//        mHasNewsItemBeenRead = true;
        mClickedItemDocIds.add(clickedItemDocId);
        notifyDataSetChanged();
        new UpdateNewsItemToHasReadStateTask(clickedItemDocId).execute();
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

    /**
     * 将某条新闻条目的是否已读状态设置为"已读"的任务.
     */
    private class UpdateNewsItemToHasReadStateTask extends AsyncTask<Void, Void, Void> {
        private String mItemBeenReadDocId;

        public UpdateNewsItemToHasReadStateTask(String itemBeenReadDocId) {
            this.mItemBeenReadDocId = itemBeenReadDocId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            NeteaseNewsListDaoImpl.getInstance().updateNewsItemToHasReadState(mItemBeenReadDocId);
            return null;
        }
    }

    /**
     * 获取某个新闻条目的阅读状态的任务
     */
    private class GetNewsItemReadStateTask extends AsyncTask<Void, Void, Integer> {
        private String mItemDocId;
        private ViewHolder mHolder;

        public GetNewsItemReadStateTask(String itemDocId, ViewHolder holder) {
            this.mItemDocId = itemDocId;
            this.mHolder = holder;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int hasRead = NeteaseNewsListDaoImpl.getInstance().queryNewsItemReadState(mItemDocId);
            return hasRead;
        }

        @Override
        protected void onPostExecute(Integer hasRead) {
            if (hasRead == 1) {
                mHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.color_netease_news_list_item_title_has_read));
            } else {
                mHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.color_netease_news_list_item_title_not_read));
            }
        }
    }
}