package com.clevergump.newsreader.netease_news.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 17:55
 * @projectName NewsReader
 */
public abstract class HttpBase {
    protected final WeakReference<Context> mContextWeakRef;

    public HttpBase(Context context) {
        mContextWeakRef = new WeakReference<Context>(context);
    }

    public void request() {
        if (mContextWeakRef != null) {
            Context context = mContextWeakRef.get();
            RequestQueue queue = Volley.newRequestQueue(context);
            Request request = getRequest();
            queue.add(request);
        }
    }

    protected abstract Request getRequest();
}