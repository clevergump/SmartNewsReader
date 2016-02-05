package com.clevergump.newsreader.netease_news.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 13:54
 * @projectName NewsReader
 */
public class NeteaseNewsDetail {
    /**
     * 该新闻实体对象的唯一识别id
     */
    public String docid;

    /**
     * 标题
     */
    public String title;

    /**
     * 内容的文字部分
     */
    public String body;

    /**
     * 内容中嵌入的图片的list.
     */
    public List<ImageDetail> img = new ArrayList<ImageDetail>();

//    /**
//     * 为便于处理图片的存取, 特地增加了该字段, 该字段不在服务器传来的json字符串中.
//     */
//    public String imageSrc0;

    /**
     * 新闻来源
     */
    public String source;

    /**
     * 新闻发布的时间
     */
    public String ptime;

    public static class ImageDetail {
        public String src;

        public ImageDetail(String src) {
            this.src = src;
        }
    }
}