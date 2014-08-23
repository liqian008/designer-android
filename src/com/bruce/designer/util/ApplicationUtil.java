package com.bruce.designer.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class ApplicationUtil {

	/**
	 * 获取application中的meta-data
	 * 
	 * @param context
	 * @param string
	 * @return
	 */
	public static String getMetaValue(Context context, String metaKey) {
		PackageManager packageManager = context.getPackageManager();
		try {
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
					context.getPackageName(), packageManager.GET_META_DATA);
			if(applicationInfo!=null){
				Bundle bundle = applicationInfo.metaData;
				if(bundle!=null){
					return bundle.getString(metaKey);
				}
			}
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
