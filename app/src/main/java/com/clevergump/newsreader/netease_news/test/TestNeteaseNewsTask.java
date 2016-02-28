package com.clevergump.newsreader.netease_news.test;

import android.os.AsyncTask;

import com.clevergump.newsreader.netease_news.event.impl.TestNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/22 19:07
 * @projectName NewsReader
 */
public class TestNeteaseNewsTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            String newsTypeId = NewsFragmentManager.getInstance().getNewsTypeId("科技");
            BaseNewsListEvent event = new TestNewsListEvent(newsTypeId);
            EventBusUtils.post(event);
        }
        return null;
    }
}