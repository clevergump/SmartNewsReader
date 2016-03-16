package com.clevergump.newsreader.netease_news.http;

import com.android.volley.Request;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 17:55
 * @projectName NewsReader
 */
public abstract class HttpBase {
//    protected final WeakReference<Context> mContextWeakRef;
//
//    public HttpBase(Context context) {
//        mContextWeakRef = new WeakReference<Context>(context);
//    }

    public void request() {
//        if (mContextWeakRef != null) {
            Request request = getRequest();
            VolleyUtils.addToRequestQueue(request);
//        }
    }

    protected abstract Request getRequest();
}