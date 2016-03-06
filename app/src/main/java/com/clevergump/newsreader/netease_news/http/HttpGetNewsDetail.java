package com.clevergump.newsreader.netease_news.http;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsDetail;
import com.clevergump.newsreader.netease_news.event.impl.NetworkFailsNewsDetailEvent;
import com.clevergump.newsreader.netease_news.event.impl.OnGetNewsDetailEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsDetailEvent;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.GsonFactory;
import com.clevergump.newsreader.netease_news.utils.LogUtils;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/12/3 12:47
 * @projectName NewsReader
 */
public class HttpGetNewsDetail extends HttpBase {

    private static final String TAG = HttpBase.class.getSimpleName();

    private static String mDocid;
    private String mNewsDetailUrl;

    public HttpGetNewsDetail(Context context, String docid) {
        super(context);
        if (TextUtils.isEmpty(docid)) {
            throw new IllegalArgumentException(docid + " == null or contains no characters");
        }
        this.mDocid = docid;
        this.mNewsDetailUrl = NeteaseNewsUrlUtils.getNewsDetailUrl(docid);
    }

    @Override
    protected Request getRequest() {
        if (mContextWeakRef == null) {
            throw new IllegalStateException("mContextWeakRef has not been initialized.");
        }
        Request request = new JsonObjectRequest(Request.Method.GET, mNewsDetailUrl, null,
            new GetNewsDetailResponseListener(mContextWeakRef.get()), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // 发送网络异常的事件.
                BaseNewsDetailEvent event = new NetworkFailsNewsDetailEvent(mDocid);
                EventBusUtils.post(event);
            }
        });
        return request;
    }

    /**
     * Volley的 Response.Listener的实现类. 设计成静态内部类, 并结合WeakReference, 可避免内存泄露(具体来说,
     * 就是可以避免因隐式地持有外部类HttpGetNewsDetail的引用, 外部类 HttpGetNewsDetail又持有 Context
     * (这里就是指 NeteaseNewsDetailActivity)的引用, 导致在 NeteaseNewsDetailActivity 被打开又迅速关闭后,
     * 由于Volley的网络请求仍在进行中而导致 NeteaseNewsDetailActivity 的对象被泄露.
     */
    private static class GetNewsDetailResponseListener implements Response.Listener<JSONObject> {

        private WeakReference<Context> contextWeakRef;

        public GetNewsDetailResponseListener(Context context) {
            contextWeakRef = new WeakReference<Context>(context);
        }

        @Override
        public void onResponse(JSONObject jsonObject) {
            LogUtils.i(TAG, jsonObject.toString());
            NeteaseNewsDetail newsDetail = parseResponseData(jsonObject, mDocid);
            BaseNewsDetailEvent event = null;
            // 如果从服务器获取到的最新的新闻详情数据不为null
            if (newsDetail != null) {
                Context context = contextWeakRef.get();
                // 因为弱引用所引用的对象, 存在被GC的可能性, 所以每次获取到该引用所引用的对象后,
                // 都应该做非null判断. 只有在非null(即: 该对象还未被GC)时才能执行后续操作.
                if (context != null) {
                    event = new OnGetNewsDetailEvent(mDocid, newsDetail);
                }
            }
            // 如果从服务器获取到的最新的新闻详情数据为null, 则发送网络异常的事件.
            else {
                event = new NetworkFailsNewsDetailEvent(mDocid);
            }

            if (event != null) {
                EventBusUtils.post(event);
            }
        }
    }

    /**
     * 解析获取到的json形式的响应数据
     * @param jsonObject
     * @param docid
     * @return
     */
    private static NeteaseNewsDetail parseResponseData(JSONObject jsonObject, String docid) {
        Map<String, NeteaseNewsDetail> map = GsonFactory.getGson().fromJson(jsonObject.toString(),
                new TypeToken<Map<String, NeteaseNewsDetail>>() {
                }.getType());
        if (map != null) {
            NeteaseNewsDetail newsDetail = map.get(docid);
            return newsDetail;
        }
        return null;
    }
}