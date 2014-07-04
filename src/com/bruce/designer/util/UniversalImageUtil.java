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
	
	private static final int LOADING_IMAGE_ID = R.drawable.tab_icon_main;
	private static final int LOADING_FAIL_IMAGE_ID = R.drawable.tab_icon_main;
	
	private static final int LOADING_AVATAR_IMAGE_ID = R.drawable.default_avatar;
	private static final int LOADING_FAIL_AVATAR_IMAGE_ID = R.drawable.default_avatar;
	
	public static final DisplayImageOptions DEFAULT_DISPLAY_OPTION = buildDisplayOption(LOADING_IMAGE_ID, LOADING_FAIL_IMAGE_ID);
	public static final DisplayImageOptions DEFAULT_AVATAR_DISPLAY_OPTION = buildDisplayOption(LOADING_AVATAR_IMAGE_ID, LOADING_FAIL_AVATAR_IMAGE_ID);
	
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

	public static DisplayImageOptions buildDisplayOption(int loadingImgId, int failImgId) {
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(loadingImgId)
				.showImageForEmptyUri(loadingImgId)
				.showImageOnFail(loadingImgId)
				.cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true).build();
		return displayImageOptions;
	}

}
