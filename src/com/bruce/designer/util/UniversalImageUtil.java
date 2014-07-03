package com.bruce.designer.util;

import android.content.Context;

import com.bruce.designer.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class UniversalImageUtil {
	
	public static final DisplayImageOptions DEFAULT_DISPLAY_OPTION = buildDisplayOption(R.drawable.logo_splash);
	
	public static ImageLoaderConfiguration buildUniversalImageConfig(
			Context context) {
		// File cacheDir = StorageUtils.getOwnCacheDirectory(context,
		// "imageloader/Cache");

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// max width, max height，即保存的每个缓存文件的最大长宽
				.memoryCacheExtraOptions(480, 800)
				// 线程池内加载的数量
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				// 将保存的时候的URI名称用MD5 加密
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// 缓存的文件数量
				.discCacheFileCount(100)
				// 自定义缓存路径
				// .discCache(new UnlimitedDiscCache(cacheDir))
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(context, 5 * 1000, 30 * 1000))// connectTimeout (5s), readTimeout(30s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();// 开始构建
		return config;
	}

	public static DisplayImageOptions buildDisplayOption(int defaultImageId) {
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
//				.showStubImage(defaultImageId)
//				.showImageForEmptyUri(defaultImageId)
//				.showImageOnFail(defaultImageId)
				.cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading().build();
		return displayImageOptions;
	}

}
