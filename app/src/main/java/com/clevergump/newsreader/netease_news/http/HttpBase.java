package com.clevergump.newsreader.netease_news.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 17:55
 * @projectName NewsReader
 */
public abstract class HttpBase {
    protected final Context bContext;

    public HttpBase(Context bContext) {
        this.bContext = bContext;
    }

    public void request(){
        RequestQueue queue = Volley.newRequestQueue(bContext);
        Request request = getRequest();
        queue.add(request);
    }

    protected abstract Request getRequest();
}