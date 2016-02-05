package com.clevergump.newsreader.netease_news.http;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clevergump.newsreader.netease_news.bean.NeteaseNewsItem;
import com.clevergump.newsreader.netease_news.event.impl.NetworkFailsNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.OnGetLatestNewsListEvent;
import com.clevergump.newsreader.netease_news.event.impl.base.BaseNewsListEvent;
import com.clevergump.newsreader.netease_news.fragment.BaseNewsFragment;
import com.clevergump.newsreader.netease_news.fragment.manager.NewsFragmentManager;
import com.clevergump.newsreader.netease_news.utils.EventBusUtils;
import com.clevergump.newsreader.netease_news.utils.GsonFactory;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 请求获取新闻数据的 http通信类.
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 16:01
 * @projectName NewsReade
 */
public class HttpGetNewsList<T extends BaseNewsFragment> extends HttpBase {
    private static final String TAG = "HttpGetNewsList";

    private int mPageNumber;
    private String mNewsTabUrl;
    private String mNewsTypeId;

    public HttpGetNewsList(Context context, int pageNumber, String newsTabUrl, String newsTypeId) {
        super(context);
        if (pageNumber < 0) {
            throw new IllegalArgumentException(pageNumber + " < 0");
        }
        if (TextUtils.isEmpty(newsTabUrl)) {
            throw new IllegalArgumentException(newsTabUrl + " == null or contains no content");
        }
        if (TextUtils.isEmpty(newsTypeId)) {
            throw new IllegalArgumentException(newsTypeId + " == null or contains no content");
        }
        this.mPageNumber = pageNumber;
        this.mNewsTabUrl = newsTabUrl;
        this.mNewsTypeId = newsTypeId;
    }

    @Override
    protected Request getRequest() {
        Request request = new JsonObjectRequest(Request.Method.GET,
                mNewsTabUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                // 解析获取到的最新的json数据, 转换为新闻数据list对象
                List<NeteaseNewsItem> neteaseNewsItems = parseResponseData(jsonObject, mNewsTypeId);
//                if (neteaseNewsItems != null && neteaseNewsItems.size() > 0) {
//
//                    // 将从服务器获取到的最新的新闻数据list对象存入数据库.
//                    new NewsListDbInsertTask(bContext, neteaseNewsItems).execute();
//                }
                // 发送包含了本次获取到的最新新闻数据的相应事件给相关的接收者, 一般是UI线程来接收, 然后用于更新UI的显示.
                // 同时还要缓存类来接收, 将该数据存入缓存.
                BaseNewsListEvent event = new OnGetLatestNewsListEvent(mNewsTypeId, mPageNumber, neteaseNewsItems);
//                ToastUtils.showDebug("EventBus 发送最新的" + NewsFragmentManager.getInstance().getNewsTabName(mNewsTypeId) + "数据");
                EventBusUtils.post(event);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // 发送网络异常的事件. 一般是UI线程接收后用于更新UI的显示(例如: 取消加载进度条的显示, 并toast提示用户网络异常).
                BaseNewsListEvent event = new NetworkFailsNewsListEvent(mNewsTypeId);
                EventBusUtils.post(event);
            }
        });
        return request;
    }

    /**
     * 解析json数据, 获取给定页的新闻对象数据.
     * @param jsonObject
     * @return
     */
    private List<NeteaseNewsItem> parseResponseData(JSONObject jsonObject, String newsTypeId) {
        Map<String, List<NeteaseNewsItem>> dataMap = GsonFactory.getGson().fromJson(jsonObject.toString(),
                new TypeToken<Map<String, List<NeteaseNewsItem>>>() {}.getType());
        List<NeteaseNewsItem> items = dataMap.get(mNewsTypeId);
        for(NeteaseNewsItem item : items) {
            item.newsTypeId = newsTypeId;
            item.newsTabName = NewsFragmentManager.getInstance().getNewsTabName(newsTypeId);
            item.setImageExtraSrcs(item.imgextra);
            item.setNewsItemType(item.imgextra);
        }
        return items;
    }
}