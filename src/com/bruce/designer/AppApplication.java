package com.bruce.designer;

import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

public class AppApplication extends Application {

	private static String versionName;
	private static int versionCode;
	
//	private static float screendensity;
//	private static int screenHeight;
//	private static int screenWidth;

	/**
	 * 其他全局量
	 */
	private static AppApplication application;
	private static Handler uiHandler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		init();
		//Universal ImageLoader init
		ImageLoader.getInstance().init(UniversalImageUtil.buildUniversalImageConfig(application));//全局初始化配置  
	}

	public static void init() {
		//获取packageVersion
		String packageName = application.getPackageName();
		PackageManager pm = application.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(packageName, 0);
			versionName = info.versionName;
			versionCode = info.versionCode;
		} catch (NameNotFoundException ignored) {
			versionName = "";
		}
	}

	public static String getVersionName() {
		return versionName;
	}
	
	public static int getVersionCode() {
		return versionCode;
	}

	public static AppApplication getApplication() {
		return application;
	}

	public static Handler getUiHandler() {
		return uiHandler;
	}

//	public static float getScreendensity() {
//		return screendensity;
//	}
//
//	public static int getScreenHeight() {
//		return screenHeight;
//	}
//
//	public static int getScreenWidth() {
//		return screenWidth;
//	}

	
//	public static WindowManager.LayoutParams getwmParams() {
//		return wmParams;
//	}

}
