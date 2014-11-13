package com.bruce.designer.util;

import com.bruce.designer.AppApplication;
import com.bruce.designer.constants.Config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference工具类
 * 
 * @author liqian
 */
public class SharedPreferenceUtil {

	/**
	 * 取出config中name字段对应的string类型的值
	 * 
	 * @param mContext
	 *            上下文，来区别哪一个activity调用的
	 * @param name
	 *            SharedPreferences的哪一个字段
	 * @return
	 */
	public static String getSharePreStr(Context mContext, String name,
			String defaultVal) {
		return getSharePreStr(mContext, Config.SP_CONFIG_DEFAULT, name, defaultVal);
	}

	/**
	 * 取出config中name字段对应的string类型的值
	 * 
	 * @param mContext 上下文，来区别哪一个activity调用的
	 * @param config 使用的SharedPreferences的名字
	 * @param name SharedPreferences的哪一个字段
	 * @return
	 */
	public static String getSharePreStr(Context mContext, String config,
			String name, String defaultVal) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(config, Context.MODE_PRIVATE);
		String s = sp.getString(name, defaultVal);// 如果该字段没对应值，则取出默认字符串
		return s;
	}

	/**
	 * 取出config中name字段对应的int类型的值
	 * 
	 * @param mContext
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static int getSharePreInt(Context mContext, String name, int defaultVal) {
		return getSharePreInt(mContext, Config.SP_CONFIG_DEFAULT, name, defaultVal);
	}

	/**
	 * 取出config中name字段对应的int类型的值
	 * 
	 * @param mContext
	 * @param config
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static int getSharePreInt(Context mContext, String config, String name, int defaultVal) {
		SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(config, Context.MODE_PRIVATE);
		int value = sp.getInt(name, defaultVal);// 如果该字段没对应值，则取出defaultVal
		return value;
	}
	
	
	/**
	 * 取出config中name字段对应的long类型的值
	 * 
	 * @param mContext
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static long getSharePreLong(Context mContext, String name, long defaultVal) {
		return getSharePreLong(mContext, Config.SP_CONFIG_DEFAULT, name, defaultVal);
	}

	/**
	 * 取出config中name字段对应的long类型的值
	 * 
	 * @param mContext
	 * @param config
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static long getSharePreLong(Context mContext, String config, String name, long defaultVal) {
		SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(config, Context.MODE_PRIVATE);
		long value = sp.getLong(name, defaultVal);// 如果该字段没对应值，则取出defaultVal
		return value;
	}
	
	/**
	 * 取出config中name字段对应的boolean类型的值
	 * 
	 * @param mContext
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static boolean getSharePreBoolean(Context mContext, String name, boolean defaultVal) {
		return getSharePreBoolean(mContext, Config.SP_CONFIG_DEFAULT, name, defaultVal);
	}

	/**
	 * 取出config中name字段对应的boolean类型的值
	 * 
	 * @param mContext
	 * @param config
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static boolean getSharePreBoolean(Context mContext, String config, String name, boolean defaultVal) {
		SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(config, Context.MODE_PRIVATE);
		boolean value = sp.getBoolean(name, defaultVal);// 如果该字段没对应值，则取出defaultVal
		return value;
	}

	/**
	 * 保存string类型的value到config中的name字段
	 * 
	 * @param mContext
	 * @param name
	 * @param value
	 */
	public static void putSharePre(Context mContext, String name, String value) {
		putSharePre(mContext, Config.SP_CONFIG_DEFAULT, name, value);
	}

	/**
	 * 保存string类型的value到config中的name字段
	 * 
	 * @param mContext
	 * @param config
	 * @param name
	 * @param value
	 */
	public static void putSharePre(Context mContext, String config,
			String name, String value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(config, Context.MODE_PRIVATE);
		sp.edit().putString(name, value).commit();
	}

	/**
	 * 保存int类型的value到config中的name字段
	 * 
	 * @param mContext
	 * @param name
	 * @param value
	 */
	public static void putSharePre(Context mContext, String name, int value) {
		putSharePre(mContext, Config.SP_CONFIG_DEFAULT, name, value);
	}

	/**
	 * 保存int类型的value到config中的name字段
	 * 
	 * @param mContext
	 * @param config
	 * @param name
	 * @param value
	 */
	public static void putSharePre(Context mContext, String config, String name, int value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(config, Context.MODE_PRIVATE);
		sp.edit().putInt(name, value).commit();
	}

	/**
	 * 保存long类型的value到config中的name字段
	 * 
	 * @param mContext
	 * @param name
	 * @param value
	 */
	public static void putSharePre(Context mContext, String name, long value) {
		putSharePre(mContext, Config.SP_CONFIG_DEFAULT, name, value);
	}

	/**
	 * 保存long类型的value到config中的name字段
	 * 
	 * @param mContext
	 * @param config
	 * @param name
	 * @param value
	 */
	public static void putSharePre(Context mContext, String config,
			String name, long value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(config, Context.MODE_PRIVATE);
		sp.edit().putLong(name, value).commit();
	}
	
	/**
	 * 保存int类型的value到config中的name字段
	 * 
	 * @param mContext
	 * @param name
	 * @param value
	 */
	public static void putSharePre(Context mContext, String name, boolean value) {
		putSharePre(mContext, Config.SP_CONFIG_DEFAULT, name, value);
	}

	/**
	 * 保存int类型的value到config中的name字段
	 * 
	 * @param mContext
	 * @param config
	 * @param name
	 * @param value
	 */
	public static void putSharePre(Context mContext, String config, String name, boolean value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(config, Context.MODE_PRIVATE);
		sp.edit().putBoolean(name, value).commit();
	}
	
	
	

	/**
	 * 清除sp
	 * 
	 * @param mContext
	 * @param name
	 */
	public static void removeSharePre(Context mContext, String name) {
		removeSharePre(mContext, Config.SP_CONFIG_DEFAULT, name);
	}

	/**
	 * 清除sp
	 * 
	 * @param mContext
	 * @param config
	 * @param name
	 */
	public static void removeSharePre(Context mContext, String config,
			String name) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(config, Context.MODE_PRIVATE);
		sp.edit().remove(name).commit();
	}

	
	/**
	 * 从sp中取出对象
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static synchronized <T> T readObjectFromSp(Class<T> clazz,
			String name) {
		return readObjectFromSp(clazz, Config.SP_CONFIG_DEFAULT, name);
	}

	/**
	 * 从sp中取出对象
	 * @param clazz
	 * @param config
	 * @param name
	 * @return
	 */
	public static synchronized <T> T readObjectFromSp(Class<T> clazz,
			String config, String name) {
		String value = getSharePreStr(AppApplication.getApplication(), config,
				name, null);
		if (value != null) {
			try {
				return JsonUtil.gson.fromJson(value, clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 将对象保存至sp
	 * @param data
	 * @param name
	 * @return
	 */
	public static synchronized boolean writeObjectToSp(Object data, String name) {
		return writeObjectToSp(data, Config.SP_CONFIG_DEFAULT, name);
	}
	
	/**
	 * 将对象保存至sp
	 * @param data
	 * @param config
	 * @param name
	 * @return
	 */
	public static synchronized boolean writeObjectToSp(Object data,
			String config, String name) {
		try {
			String value = JsonUtil.gson.toJson(data);
			putSharePre(AppApplication.getApplication(), config, name, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
