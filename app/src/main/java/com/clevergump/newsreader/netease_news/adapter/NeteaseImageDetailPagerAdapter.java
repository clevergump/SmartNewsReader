package com.clevergump.newsreader.netease_news.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clevergump.newsreader.R;
import com.clevergump.newsreader.netease_news.utils.ImageLoaderUtils;
import com.clevergump.newsreader.netease_news.utils.LogUtils;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * 图片详情页面中ViewPager的adapter
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/11 16:39
 * @projectName NewsReader
 */
public class NeteaseImageDetailPagerAdapter extends PagerAdapter {

    /*************************** View *********************************/
    // ViewPager的每一页中包含的整个根控件
    private View mVgPageRootView;


    /*************************** 变量 *********************************/
    private Context mContext;
    // 图片url组成的list
    private List<String> mImageUrls;
//    // UIL框架进行图片加载时的一个配置选项.
//    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListener mImageLoadingListener;
    // 用于缓存ViewPager中各个页面View的容器. 设置只存储3个View对象, 也就是ViewPager中3个页面的View.
    private SparseArray<View> mPageViewCache = new SparseArray<View>(3);
    // 用于记录ViewPager中的各个页面是否是第一次加载的一个容器. key--int页码, value--boolean是否是第一次加载该页面.
    private SparseBooleanArray mMapOfEachPageIsFirstLoad = new SparseBooleanArray(3);
    // 当前显示的那一页的页码(也是位置position). 从0开始.
    private int mCurrentPage;


    public NeteaseImageDetailPagerAdapter(Context context, List<String> imageUrls) {
        if (context == null) {
            throw new IllegalArgumentException(context + " == null");
        }
        if (imageUrls == null || imageUrls.size() <= 0) {
            throw new IllegalArgumentException(imageUrls + " == null or contains no elements");
        }
        this.mContext = context;
        this.mImageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 实例化ViewPager的item. 这里的参数 container其实就是 ViewPager
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // 由于每次inflate都相当于使用new的方式创建View对象, 而我们这里只有3张图片, 也就是整个ViewPager
        // 里面只有3个子View对象, 所以可以将这些子View缓存起来, 避免每次切换图片后都要重新new View对象.
        // 缓存容器可以使用Map, key--图片所在的位置, value--图片对象, 由于key是int值, 所以可以使用
        // SparseArray代替Map以提升性能.
        PageViewHolder holder = null;
        if (mPageViewCache.get(position) == null) {
            // inflate 出来的 View其实就是xml布局文件中根节点对应的View, 每一次inflate其实就相当于使用new来创建View对象.
            mVgPageRootView = View.inflate(mContext, R.layout.pager_item_netease_image_detail, null);
            // 将当前页的 View存入缓存容器.
            mPageViewCache.put(position, mVgPageRootView);
            // 只在该子View未曾添加到ViewPager的时候, 才进行添加. 如果先前添加过, 就不再重复添加.
            container.addView(mVgPageRootView);

            holder = new PageViewHolder();
            holder.progressBar = (ProgressBar) mVgPageRootView.findViewById(R.id.progress_bar_loading);
            holder.vgPageLoadedView = (ViewGroup) mVgPageRootView.findViewById(R.id.vg_image_detail_content);
            holder.tvImageNumber = (TextView) mVgPageRootView.findViewById(R.id.tv_image_number);
            holder.ivImage = (ImageView) mVgPageRootView.findViewById(R.id.iv_image);
            mVgPageRootView.setTag(holder);

            // 初始化数据, 初始化赋值等.
            initData();
        } else {
            mVgPageRootView = mPageViewCache.get(position);
        }

    // position = 0, mVgPageRootView = android.widget.RelativeLayout{529ee184 V.E..... ......ID 0,0-0,0 #7f0b0032 app:id/vg_image_pager_root}
    // position = 1, mVgPageRootView = android.widget.RelativeLayout{52b949f0 V.E..... ......ID 0,0-0,0 #7f0b0032 app:id/vg_image_pager_root}
    // position = 2, mVgPageRootView = android.widget.RelativeLayout{5299de28 V.E..... ......ID 0,0-0,0 #7f0b0032 app:id/vg_image_pager_root}
        // 根据上述log可知, 每一页所对应的整个内容的View对象其实是不同的对象, 也就是, 每一次inflate相同的
        // 布局文件, 其实就是一次new View的过程. 所以必须考虑View对象的复用, 因此就有了 mPageViewCache的设计.
        LogUtils.i("position = " + position + ", mVgPageRootView = " + mVgPageRootView);

        return mVgPageRootView;
    }

    /**
     * ViewPager中每一页中所包含的所有子View的Holder容器类
     */
    private class PageViewHolder {
        // 正在加载过程中显示的环形进度条
        ProgressBar progressBar;
        // 加载完成后要显示的整个View
        ViewGroup vgPageLoadedView;
        // vgPageLoadedView中的子View----图片左下角用于指示当前图片是第几张的那个编号数字
        TextView tvImageNumber;
        // vgPageLoadedView中的子View----图片
        ImageView ivImage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 由于图片数量较少(只有3张), 所以没必要清理
//        container.removeView((View) object);
    }

    private void initData() {
//        if (mDisplayImageOptions == null) {
//            mDisplayImageOptions = new DisplayImageOptions.Builder()
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true)
//                // RGB565: 图片的每1px所占用的存储空间大小是 5+6+5=16bit=2字节
//                    .bitmapConfig(Bitmap.Config.ALPHA_8)
//                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//                    .displayer(new FadeInBitmapDisplayer(100))// 淡入
//                    .build();
//        }

        if (mImageLoadingListener == null) {
            mImageLoadingListener = new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    View pageRootView = mPageViewCache.get(mCurrentPage);
                    PageViewHolder holder = (PageViewHolder) pageRootView.getTag();
                    // 加载完成后, 设置进度条隐藏, 图片内容和文字角标显示.
                    if (holder.progressBar.getVisibility() == View.VISIBLE) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                    if (holder.vgPageLoadedView.getVisibility() == View.GONE) {
                        holder.vgPageLoadedView.setVisibility(View.VISIBLE);
                    }
                }
            };
        }
    }

    /**
     * 根据ViewPager的currentItem的位置, 来设置该位置对应页面上的数据.
     */
    private void setDatas(int position) {
        View vgPageRootView = mPageViewCache.get(position);
        PageViewHolder holder = (PageViewHolder) vgPageRootView.getTag();
        holder.tvImageNumber.setText((position + 1) + "/" + getCount());
    }

    /**
     * 开始进行图片的加载. 由于图片框架设计了缓存机制, 所以如果先前已经加载过该位置所对应页面上的图片,
     * 则会直接从缓存中调用该图片.
     * @param position
     */
    private void startLoadingImage(int position) {
        View vgPageRootView = mPageViewCache.get(position);
        PageViewHolder holder = (PageViewHolder) vgPageRootView.getTag();
        String currentImageUrl = mImageUrls.get(position);
        ImageLoaderUtils.displayImage(currentImageUrl, holder.ivImage, mImageLoadingListener, null);
    }

    /**
     * 当ViewPager切换页面时的回调方法
     * @param position
     */
    public void onPageChanged(int position) {
        // 记录下当前正在显示的页码, 便于后续在图片加载完成后, 从ViewCache中取出对应页的相关控件时所用. 会
        // 取出一个ImageView用于显示该页的图片, 取出一个TextView作为图片左下角的角标用于显示当前图片是第几张
        mCurrentPage = position;

        // 设置该页相关的数据, 比如: 用于指示当前显示的是第几张图片的角标文字等.
        setDatas(position);
        // 设置开始加载这一页的图片.
        startLoadingImage(position);
        View pageRootView = mPageViewCache.get(position);
        PageViewHolder holder = (PageViewHolder) pageRootView.getTag();

        // 设置当加载某一页的内容时, 只有该页在第一次加载时才会显示加载进度条. 第二次及后续次再来加载该页时,
        // 由于该页的图片在第一次加载后已经被存入了缓存, 所以就不需要再显示加载进度条了
        boolean isSpecifiedPageFirstLoad = mMapOfEachPageIsFirstLoad.get(position);
        if (isSpecifiedPageFirstLoad) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.vgPageLoadedView.setVisibility(View.GONE);
            mMapOfEachPageIsFirstLoad.put(position, false);
        }

//        notifyDataSetChanged();
    }
}