package com.clevergump.newsreader.netease_news.activity;

import android.os.Bundle;

import com.clevergump.newsreader.Constant;
import com.clevergump.newsreader.netease_news.utils.ToastUtils;

/**
 * BaseActivity的代理类.
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/3/31 20:55
 * @projectName SmartNewsReader
 */
public abstract class SimpleProxyActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constant.DEBUG) {
            ToastUtils.show(getClass().getSimpleName() + " onCreate");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constant.DEBUG) {
            ToastUtils.show(getClass().getSimpleName() + " onDestroy");
        }
    }
}
