package com.clevergump.newsreader.netease_news.bean;

import com.clevergump.newsreader.netease_news.utils.GsonFactory;

import java.util.List;
import java.util.Map;

/**
 * 新闻条目的实体类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/9 9:57
 * @projectName NewsReader
 */
public class NeteaseNewsItem {

    /**
     * 是否已阅读过这条新闻.
     */
    public boolean hasRead;

    /**
     * 所属的新闻类别
     */
    public String newsTypeId;

    /**
     * 所属的标签名称
     */
    public String newsTabName;

    /**
     * 新闻详情页的唯一识别符
     */
    public String docid;

    /**
     * 标题
     */
    public String title;

    /**
     * 摘要
     */
    public String digest;

    /**
     * 图片的url
     */
    public String imgsrc;

    /**
     * 其他图片的url. 如果是单张图片的条目, 则该字段为null;
     * 如果是三张图片的条目, 则该字段不为null, 且size() == 2
     */
    public List<Map<String, String>> imgextra;

    /**
     * 图片额外的url0
     */
    public String imgExtraSrc0;

    /**
     * 图片额外的url1
     */
    public String imgExtraSrc1;

    /**
     * 新闻条目在ListView中显示时的view种类. 默认值必须取一个负数, 因为0已经是一种有效的type类型了.
     * 所以取一个负数作为默认值可以避免掉一些因疏忽导致的错误, 也便于调试.
     */
    public int newsItemViewType = -1;

    private NeteaseNewsItem() {
    }

    /**
     * 设置新闻条目的类别. 用于新闻条目的ListView在显示时作为区分条目类别的依据.
     */
    public void setNewsItemType(List<Map<String, String>> imgextra) {
        if (imgextra != null && imgextra.size() >= 2) {
            newsItemViewType = NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_THREE_IMAGE;
        }
        else {
            newsItemViewType = NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_ONE_IMAGE;
        }
    }

    public int getNewsItemType(List<Map<String, String>> imgextra) {
        // 该字段未赋值时, 是默认值-1. 如果发现该字段的值不是默认值-1(即: 是正数或0),
        // 表明该字段已经赋过值了, 所以直接返回该值即可.
        if (newsItemViewType >= 0) {
            return newsItemViewType;
        }
        // 如果newsItemViewType == 在定义该字段时设置的默认负数值-1, 就表示该字段未赋过值, 因此需要进行计算.
        int itemType;
        if (imgextra != null && imgextra.size() >= 2) {
            itemType = NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_THREE_IMAGE;
        }
        else {
            itemType = NeteaseNewsBase.NEWS_ITEM_VIEW_TYPE_ONE_IMAGE;
        }
        return itemType;
    }

    /**
     * 设置字段 imgExtraSrc0 和 imgExtraSrc1的值
     */
    public void setImageExtraSrcs(List<Map<String, String>> imgextra) {
        if (imgextra != null && imgextra.size() >= 2) {
            imgExtraSrc0 = imgextra.get(0).get("imgsrc");
            imgExtraSrc1 = imgextra.get(1).get("imgsrc");
        }
    }

    @Override
    public String toString() {
        return GsonFactory.getGson().toJson(this);
    }

    public static class Builder {
        private NeteaseNewsItem neteaseNewsItem;

        public Builder() {
            neteaseNewsItem = new NeteaseNewsItem();
        }

        public Builder setTitle(String title) {
            neteaseNewsItem.title = title;
            return this;
        }

        public Builder setDigest(String digest) {
            neteaseNewsItem.digest = digest;
            return this;
        }

        public Builder setImageSrcUrl (String imageSrcUrl) {
            neteaseNewsItem.imgsrc = imageSrcUrl;
            return this;
        }

        public Builder setImageExtraSrc0Url (String imgExtraSrc0Url) {
            neteaseNewsItem.imgExtraSrc0 = imgExtraSrc0Url;
            return this;
        }

        public Builder setImageExtraSrc1Url (String imgExtraSrc1Url) {
            neteaseNewsItem.imgExtraSrc1 = imgExtraSrc1Url;
            return this;
        }

        public Builder setNewsTypeId(String newsTypeId) {
            neteaseNewsItem.newsTypeId = newsTypeId;
            return this;
        }

        public Builder setNewsTabName(String newsTabName) {
            neteaseNewsItem.newsTabName = newsTabName;
            return this;
        }

        public NeteaseNewsItem build() {
            return neteaseNewsItem;
        }
    }
}