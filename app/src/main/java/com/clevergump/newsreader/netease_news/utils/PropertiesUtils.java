package com.clevergump.newsreader.netease_news.utils;

import android.content.res.AssetManager;

import com.clevergump.newsreader.MyApplication;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 配置文件的工具类
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/10 11:57
 * @projectName NewsReader
 */
public class PropertiesUtils {
    private static final String KEY_NEWS_TAB_NAMES = "newsTabNames";
    private static Map<String, String> map;

    private PropertiesUtils() {
    }

    public static PropertiesUtils getInstance() {
        return PropertiesUtilsHolder.INSTANCE;
    }

    private static class PropertiesUtilsHolder {
        private static final PropertiesUtils INSTANCE = new PropertiesUtils();
    }

    static {
        BufferedReader bufferedReader = null;
        try {
            AssetManager assetManager = MyApplication.getAppContext().getAssets();
            InputStream inputStream = assetManager.open("ProjectProperties.json");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            map = GsonFactory.getGson().fromJson(bufferedReader,
                    new TypeToken<Map<String, String>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取新闻标签名称的list.
     * @return 新闻标签名称的list
     */
    public List<String> getNewsTabNames() {
        List<String> tabNamesList = new ArrayList<String>();

        String newsTabNamesStr = map.get(KEY_NEWS_TAB_NAMES);
        String[] newsTabNamesArr = newsTabNamesStr.split(",");

        // 只有下面这种写法的trim()后的结果才会真正赋值到相应的元素.
        for(int i = 0; i < newsTabNamesArr.length; i++){
            newsTabNamesArr[i] = newsTabNamesArr[i].trim();
        }
        // 这种写法的trim()后赋值操作无效. 而且软件也会提示该赋值式无效, 因为不存在str变量.
//        for(String str : newsTabNamesArr){
//         str = str.trim();
//        }
        tabNamesList.addAll(Arrays.asList(newsTabNamesArr));

        return tabNamesList;
    }
}