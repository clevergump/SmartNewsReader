package com.clevergump.newsreader.netease_news.presenter;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/2/25 0:22
 * @projectName SmartNewsReader
 */
public interface IPresenter {
    /**
     * 进行相关资源的关闭或清理 (例如: 若该接口的某个实现类持有了Handler引用, 需要在实现类的该方法中调用
     * Handler.removeCallbacksAndMessages(null)方法).
     */
    void clear();
}