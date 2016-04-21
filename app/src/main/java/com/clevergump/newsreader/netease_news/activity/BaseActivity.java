package com.clevergump.newsreader.netease_news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        setListenerOrAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * findViewById(int ResId)的简化版
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T findView(int viewId) {
        return (T) findViewById(viewId);
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void setListenerOrAdapter();

    protected <T extends BaseActivity> void startActivity(Class<T> destActivityClazz) {
        Intent intent = new Intent(this, destActivityClazz);
        startActivity(intent);
    }

//    protected <T extends BaseActivity> void startActivityForResult(Class<T> destActivityClazz,
//               int requestCode) {
//        Intent intent = new Intent(this, destActivityClazz);
//        startActivityForResult(intent, requestCode);
//    }
}