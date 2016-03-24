package com.clevergump.newsreader.netease_news.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.clevergump.newsreader.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.File;

/**
 * UIL图片加载框架的工具类.
 * 也可参考 QuickNews-tiger开源项目中的 src/.../utils/Options类对UIL的配置.
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2015/11/24 22:07
 * @projectName NewsReader
 */
public class ImageLoaderUtils {
    public static ImageLoader sImageLoader = ImageLoader.getInstance();
    public static DisplayImageOptions sOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageOnLoading(R.mipmap.bg_image_loading)
            .showImageOnFail(R.mipmap.bg_image_loading)
            .showImageForEmptyUri(R.mipmap.bg_image_loading)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            // RGB565: 图片的每1px所占用的存储空间大小是 5+6+5=16bit=2字节
            .bitmapConfig(Bitmap.Config.ALPHA_8)
            // 类似于scaleType="fitCenter". inSampleSize就是对图片宽高的压缩, 当然也顺带对应着像素点的减少从而降低了图片的大小(字节数)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new FadeInBitmapDisplayer(100))// 淡入
            .build();

    public static void displayImage(String uri, ImageView iv, ImageLoadingListener loadingListener,
                                    ImageLoadingProgressListener progressListener) {
        sImageLoader.displayImage(uri, iv, sOptions, loadingListener, progressListener);
    }

    /**
     * 根据传入的缓存文件夹的名称 (cacheDirName) 获取硬盘上缓存文件夹的路径.
     * @param context 上下文
     * @param cacheDirName 缓存文件夹的名称
     */
    public static String getDiskCachePath(Context context, String cacheDirName) {
        if (context == null) {
            throw new IllegalArgumentException(context + " == null");
        }
        if (TextUtils.isEmpty(cacheDirName)) {
            throw new IllegalArgumentException(cacheDirName + " == null or contains no characters");
        }
        String cacheParentPath = null;

        // 如果有SD卡, 就将缓存文件夹设置为SD卡. 如果没有SD卡, 就将缓存文件夹设置为手机自身的空间.
//        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) && context.getExternalCacheDir() != null) {
//            cacheParentPath = context.getExternalCacheDir().getPath();
//        } else if (context.getCacheDir() != null) {
//            cacheParentPath = context.getCacheDir().getPath();
//        }

        // 由于手机不稳定, 有时能识别到SD卡, 有时又不能识别到, 所以就强制要求缓存全部存放在手机空间中.
        if (context.getCacheDir() != null) {
            cacheParentPath = context.getCacheDir().getPath();
        }
        String cachePath = cacheParentPath + File.separator + cacheDirName;
        return cachePath;
    }

    /**
     * 根据传入的 cacheDirSimpleName 创建并获取图片在硬盘上的缓存文件夹对象.
     * @param context 上下文
     * @param cacheDirSimpleName 图片在硬盘缓存路径中最内层文件夹的名称
     * @return 图片在硬盘上的缓存文件夹对象
     */
    public static File makeAndGetImageDiskCacheDir(Context context, String cacheDirSimpleName) {
        String cachePath = getDiskCachePath(context, cacheDirSimpleName);
        File cacheDir = new File(cachePath);
        if ( !(cacheDir != null && cacheDir.exists() && cacheDir.isDirectory()) ) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 根据传入的硬盘缓存路径创建对应的硬盘缓存文件夹对象, 并返回该对象.
     * @param cachePath 硬盘缓存路径
     * @return
     */
    public static File makeAndGetImageDiskCacheDir(String cachePath) {
        if (TextUtils.isEmpty(cachePath)) {
            throw new IllegalArgumentException(cachePath + " == null or contains no characters");
        }
        File cacheDir = new File(cachePath);
        if ( !(cacheDir != null && cacheDir.exists() && cacheDir.isDirectory()) ) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }
}