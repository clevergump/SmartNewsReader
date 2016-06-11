package com.clevergump.newsreader;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.clevergump.newsreader.netease_news.utils.ImageLoaderUtils;
import com.clevergump.newsreader.netease_news.utils.LogUtils;
import com.clevergump.newsreader.netease_news.utils.ToastUtils;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UpdateConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/6 19:53
 * @projectName NewsReader
 */
public class MyApplication extends Application {

    private static final String IMAGE_CACHE_DIR_NAME = "imageCache";
    private static Context mAppContext;
    public static Map<String, Long> lastUpdateTimeMillis = new HashMap<String, Long>();
    // 用于观察记录内存泄露.
    private RefWatcher mRefWatcher;
    public static Handler mToastDebugHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Constant.DEBUG) {
                ToastUtils.showDebug((CharSequence) msg.obj);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // 使用友盟集成测试模式
        MobclickAgent.setDebugMode(Constant.DEBUG);
        // 友盟更新log输出
        UpdateConfig.setDebug(Constant.DEBUG);
        mAppContext = this;
        // 初始化图片加载框架.
        initImageLoader();
        // 注册LeakCanary内存泄露检测器.
        mRefWatcher = LeakCanary.install(this);
    }

    /**
     * 初始化 UIL图片加载框架
     */
    private void initImageLoader() {
        // 缓存路径:
        // /data/data/com.clevergump.newsreader/cache/imageCache
        // /storage/emulated/0/Android/data/com.clevergump.newsreader/cache/imageCache
        // /storage/sdcard0/Android/data/com.clevergump.newsreader/cache/imageCache
        String imageDiskCachePath = ImageLoaderUtils.getDiskCachePath(getApplicationContext(), IMAGE_CACHE_DIR_NAME);
        if (Constant.DEBUG) {
            LogUtils.d("图片在手机上的缓存路径: " + imageDiskCachePath);
        }

        File imageDiskCacheDir = ImageLoaderUtils.makeAndGetImageDiskCacheDir(imageDiskCachePath);

        try {
            ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(this)
                    .threadPoolSize(5)// 线程池内加载的数量
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    // 同一张图片的多个尺寸的图只存储一个, 不会全部存储
                    .denyCacheImageMultipleSizesInMemory()

                    /* memoryCache() and memoryCacheSize() calls overlap(重复) each other */
                    // 以下几个方法不能同时设置: memoryCache() 和 memoryCacheSize()
                    // 内存缓存容量2MB
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    // 设置线程池中的图片队列为 LIFO队列, 即: 优先加载最后打开或者最后呈现出来的图片
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    /* diskCache() and diskCacheFileNameGenerator() calls overlap each other */
                    /* diskCache(), diskCacheSize() and diskCacheFileCount() calls overlap each other */
                    // 以下几个方法不能同时设置: diskCache(), diskCacheSize() and diskCacheFileCount(), diskCacheFileNameGenerator()
                    // 硬盘缓存容量50MB
                    .diskCache(new LruDiskCache(imageDiskCacheDir, new Md5FileNameGenerator(),
                            1L * 50 * 1024 * 1024))
                    // 连接超时时间5s, 读取超时时间30s
                    .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000));

            // 只有在DEBUG模式下, 才会开启图片加载框架 UIL的调试log开关.
            if (Constant.DEBUG) {
                configBuilder.writeDebugLogs();
            }
            ImageLoaderConfiguration config = configBuilder.build();
            // 初始化图片加载框架 UIL
            ImageLoader.getInstance().init(config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取整个应用程序的Context上下文.
     * @return
     */
    public static Context getAppContext() {
        return mAppContext;
    }

    /**
     * 获取用于检测特定上下文环境中发生内存泄露的检测器
     * @param context 要检测内存泄露发生地所在的上下文环境.
     *                如果要检测 Activity中的内存泄露, 就用 Activity调用该方法并将该参数赋值为 this;
     *                如果要检测 Fragment中的内存泄露, 就用 Fragment调用该方法并将该参数赋值为 getActivity().
     * @return 内存泄露的检测器
     */
    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }
}