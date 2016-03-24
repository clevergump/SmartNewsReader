package com.clevergump.newsreader.netease_news.activity;

import android.app.Activity;
import android.os.Bundle;

import com.clevergump.newsreader.R;
import com.clevergump.newsreader.netease_news.utils.ToastUtils;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/3/16 23:31
 * @projectName SmartNewsReader
 */
public class TestActivity extends Activity {
    private static final String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ToastUtils.show(TAG);
    }
}