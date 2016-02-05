package com.clevergump.newsreader.netease_news.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间的工具类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/25 15:09
 * @projectName NewsReader
 */
public class DateTimeUtils {

    private static SimpleDateFormat formatter;

    /**
     * 将给定的long型时间转换为使用 "年-月-日 时:分:秒"格式的时间
     * @param timeMillis
     * @return
     */
    public static String getFormattedTime(long timeMillis) {
        if (timeMillis <= 0) {
            throw new IllegalArgumentException("非法时间: "+timeMillis);
        }
        if (formatter == null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        String formattedDateTime = formatter.format(new Date(timeMillis));
        return formattedDateTime;
    }

    /**
     * 获取"年-月-日 时:分:秒"格式的当前时间
     * @return
     */
    public static String getFormattedCurrentTime() {
        return getFormattedTime(System.currentTimeMillis());
    }

    /**
     * 获取新闻页面中下拉刷新时显示出来的上次更新时间的文字描述语句
     *
     * @param lastUpdateTimeMillis 上次更新时间(单位: 毫秒)
     * @return 新闻页面下拉刷新时显示出来的上次更新时间的文字描述语句
     * @throws IllegalArgumentException 当提供的上次更新时间不在合理的取值范围内时, 会抛出该异常
     */
    public static String getLastUpdateTimeDesc(long lastUpdateTimeMillis) {
        if (lastUpdateTimeMillis < 0) {
            throw new IllegalArgumentException("上次更新时间不能为负数");
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (lastUpdateTimeMillis > currentTimeMillis) {
            throw new IllegalArgumentException("上次更新时间不能是未来的时间点");
        }
        // 1分钟内更新. 1000 * 60---1分钟转换为毫秒的数
        if (currentTimeMillis - lastUpdateTimeMillis < 1000 * 60) {
            int secondsAgo = (int) (currentTimeMillis - lastUpdateTimeMillis) / 1000;
            return secondsAgo + "秒前更新";
        }
        // 1小时内更新. 1000 * 60 * 60---1小时转换为毫秒的数
        if (currentTimeMillis - lastUpdateTimeMillis < 1000 * 60 * 60) {
            // 1000 * 60---1分钟转换为毫秒的数
            int minutesAgo = (int) (currentTimeMillis - lastUpdateTimeMillis) / (1000 * 60);
            return minutesAgo + "分钟前更新";
        }
        // 1天内更新. 1000 * 60 * 60 * 24---1天转换为毫秒的数.
        if (currentTimeMillis - lastUpdateTimeMillis < 1000 * 60 * 60 * 24) {
            // 1000 * 60 * 60---1小时转换为毫秒的数
            int hoursAgo = (int) (currentTimeMillis - lastUpdateTimeMillis) / (1000 * 60 * 60);
            return hoursAgo + "小时前更新";
        }
        // 上次更新时间已超过1天.
        else {
            // 1000 * 60 * 60 * 24---1天转换为毫秒的数.
            int daysAgo = (int) (currentTimeMillis - lastUpdateTimeMillis) / (1000 * 60 * 60 * 24);
            return daysAgo + "天前更新";
        }
    }
}