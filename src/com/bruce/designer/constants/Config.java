package com.bruce.designer.constants;

import com.bruce.designer.AppApplication;

public class Config {
	/*客户端versionName*/
	public static final String APP_VERSION_NAME = AppApplication.getVersionName();
	/*客户端versionCode*/
	public static final int APP_VERSION_CODE = AppApplication.getVersionCode();
	/*客户端ID*/
	public static final String APP_ID = "1";//jinwanr_android
	/*客户端SECRET KEY*/
	public static final String APP_SECRET_KEY = "1qaz2wsx";
	
	
//	public static final String JINWAN_DOMAIN = "http://mobile.jinwanr.com.cn";
	//test mcap api
	public static final String JINWAN_DOMAIN = "http://172.168.1.88:8080/designer-mcap";
	
	public static final String JINWAN_API_PREFIX = JINWAN_DOMAIN + "/api";
	
	
	public static final String JINWAN_API_ALBUMS = JINWAN_API_PREFIX + "/moreAlbums.json";
	
	/*SP的默认Config*/
	public static final String SP_CONFIG_DEFAULT = "CONFIG";
	/*SP的账户Config*/
	public static final String SP_CONFIG_ACCOUNT = "ACCOUNT";
	/*SP的key*/
	public static final String SP_KEY_USERPASSPORT = "USER_PASSPORT";
	
	
	

}
