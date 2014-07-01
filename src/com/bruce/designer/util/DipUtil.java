package com.bruce.designer.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * @author dingwei.chen
 * */
public class DipUtil {

	public static int getScreenWidth(Activity activity) {
		int fullWidth = getDisplayMetrics(activity).widthPixels;
		LogUtil.d("==========fullWidth========"+fullWidth);
		return fullWidth;
	}

	public static int getScreentHeight(Activity activity) {
		int heightPixels = getDisplayMetrics(activity).heightPixels;
		LogUtil.d("==========heightPixels========"+heightPixels);
		return heightPixels;
	}

	public static int calcFromDip(Activity activity, int number) {
		float f = getDisplayMetrics(activity).density;
		LogUtil.d("==========f========"+f);
		int result =  (int) (number * f + 0.5);
		LogUtil.d("==========result========"+result);
		return result;
	}

	static DisplayMetrics sDisplay = null;

	public static DisplayMetrics getDisplayMetrics(Activity activity) {
		if (sDisplay == null) {
			sDisplay = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(sDisplay);
		}
		return sDisplay;
	}

	public static float getDensity(Activity activity) {
		float density = getDisplayMetrics(activity).density;
		int densityDpi = getDisplayMetrics(activity).densityDpi;
		LogUtil.d("=========density=========="+density);
		LogUtil.d("=========densityDpi=========="+densityDpi);
		return density;
	}
}
