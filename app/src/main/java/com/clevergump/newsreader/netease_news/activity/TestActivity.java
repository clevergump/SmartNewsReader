package com.clevergump.newsreader.netease_news.activity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.clevergump.newsreader.MyApplication;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/3/16 23:31
 * @projectName SmartNewsReader
 */
public class TestActivity extends Activity {
    private static final String TAG = "TestActivity";

    @Override
    protected void onResume() {
        super.onResume();
        Context applicationContext = getApplicationContext();
        Context appContext = MyApplication.getAppContext();
        Log.i(TAG, "applicationContext = " + applicationContext);
        Log.i(TAG, "appContext         = " + appContext);
    }
}