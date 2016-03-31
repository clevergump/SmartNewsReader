package com.clevergump.newsreader.netease_news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.R;
import com.clevergump.newsreader.netease_news.adapter.NeteaseImageDetailPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 网易新闻的图片详情页面
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/30 23:50
 * @projectName NewsReader
 */
public class NeteaseImageDetailActivity extends SimpleProxyActivity implements View.OnClickListener {
    /******************** 常量 ****************************/


    /******************** View ****************************/
    private ImageView mIvBack;
    private TextView mTvTitle;
    private ViewPager mViewPagerImages;


    /******************** 变量 ****************************/
    // 图片ViewPager的adapter
    private NeteaseImageDetailPagerAdapter mImagePagerAdapter;
    // 标题
    private String mImageDetailTitle;
    // 图片URL组成的 list
    private List<String> mImageUrls;
    // 图片ViewPager当前的滚动状态
    private int mImagePagerCurrScrollState;



    /*******************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_netease_image_detail);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mIvBack = findView(R.id.iv_back);
        mTvTitle = findView(R.id.tv_netease_image_detail_title);
        mViewPagerImages = findView(R.id.viewpager_netease_image_detail_imgs);
    }

    @Override
    protected void initData() {
        initIntentParams();

        mImagePagerAdapter = new NeteaseImageDetailPagerAdapter(this, mImageUrls);
        mTvTitle.setText(mImageDetailTitle);
        // 等到图片加载出来之后再设置为 RESULT_OK.
        setResult(RESULT_OK);
    }

    private void initIntentParams() {
        Intent intent = getIntent();
        mImageDetailTitle = intent.getStringExtra(Constant.KEY_NETEASE_NEWS_DETAIL_TITLE);
        Serializable serializableImgUrls = intent.getSerializableExtra(Constant.KEY_NETEASE_NEWS_DETAIL_IMAGE_URLS);
        if (serializableImgUrls instanceof ArrayList) {
            mImageUrls = (ArrayList<String>) serializableImgUrls;
        }
    }

    @Override
    protected void setListenerOrAdapter() {
        mIvBack.setOnClickListener(this);
        mViewPagerImages.setAdapter(mImagePagerAdapter);
//        mViewPagerImages.setCurrentItem(0, false);
        mViewPagerImages.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                String scrollStateStr = null;
//                switch (mImagePagerCurrScrollState) {
//                    case ViewPager.SCROLL_STATE_DRAGGING:
//                        scrollStateStr = "dragging";
//                        break;
//                    case ViewPager.SCROLL_STATE_SETTLING:
//                        scrollStateStr = "settling";
//                        break;
//                    case ViewPager.SCROLL_STATE_IDLE:
//                        scrollStateStr = "idle";
//                        break;
//                }
//                LogUtils.i("position = " + position + ", scrollState = " + scrollStateStr);
                if (mImagePagerCurrScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    mImagePagerAdapter.onPageChanged(position);
                    mViewPagerImages.setCurrentItem(position, false);
//                    ToastUtils.showDebug("setCurrentItem = " + position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mImagePagerAdapter.onPageChanged(position);
                mViewPagerImages.setCurrentItem(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mImagePagerCurrScrollState = state;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }
}