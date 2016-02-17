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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.IOException;
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
            ToastUtils.showDebug((CharSequence) msg.obj);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
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
        String imageDiskCachePath = ImageLoaderUtils.getDiskCachePath(getAppContext(), IMAGE_CACHE_DIR_NAME);
        LogUtils.i("图片在手机上的缓存路径: " + imageDiskCachePath);

        File imageDiskCacheDir = ImageLoaderUtils.makeAndGetImageDiskCacheDir(imageDiskCachePath);

        ImageLoaderConfiguration.Builder configBuilder = null;
        try {
            configBuilder = new ImageLoaderConfiguration.Builder(this)
                    // .memoryCacheExtraOptions(480, 800) // max width, max
                    // height，即保存的每个缓存文件的最大长宽
                    // .discCacheExtraOptions(480, 800, CompressFormat.JPEG,
                    // 75, null) // Can slow ImageLoader, use it carefully
                    // (Better don't use it)设置缓存的详细信息，最好不要设置这个
                    .threadPoolSize(3)// 线程池内加载的数量
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()

              /* memoryCache() and memoryCacheSize() calls overlap each other */
                    // 内存缓存容量2MB
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))

              /* diskCache() and diskCacheFileNameGenerator() calls overlap each other */
              /* diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other */
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    // 硬盘缓存容量50MB
                    .diskCache(new LruDiskCache(imageDiskCacheDir, new Md5FileNameGenerator(),
                            1L * 50 * 1024 * 1024))
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    // 连接超时时间5s, 读取超时时间30s
                    .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000));
                    // .writeDebugLogs() // Remove for release app
                    // .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 配置是否打开UIL的调试开关
        if (Constant.DEBUG) {
            configBuilder.writeDebugLogs();
        }
        ImageLoaderConfiguration config = configBuilder.build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
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