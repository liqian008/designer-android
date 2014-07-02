package com.bruce.designer.util;

import java.io.File;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class UniversalImageUtil {

	public static ImageLoaderConfiguration buildUniversalImageConfig(Context context) {
//		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		// max width, max height，即保存的每个缓存文件的最大长宽
				.memoryCacheExtraOptions(480, 800)
				// 线程池内加载的数量
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				// You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				// 将保存的时候的URI名称用MD5 加密
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// 缓存的文件数量
				.discCacheFileCount(100)
				// 自定义缓存路径
//				.discCache(new UnlimitedDiscCache(cacheDir))
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))// connectTimeout (5 s), readTimeout (30 s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();// 开始构建
		return config;
	}
}
