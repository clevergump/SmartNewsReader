package com.clevergump.newsreader.netease_news.model;

import android.content.Context;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 20:03
 * @projectName NewsReader
 */
public interface INewsDetailModel {

    void requestNewsDetail(Context context, String docid);
}
