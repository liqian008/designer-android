package com.bruce.designer.constants;

import com.bruce.designer.AppApplication;

public class Config {
	
	public static final boolean DEBUG = true;
	
	public static final int CLIENT_TYPE = 2;
	
	/*游客身份的uid*/
	public static int GUEST_USER_ID = 100000;
	
	/*客户端versionName*/
	public static final String APP_VERSION_NAME = AppApplication.getVersionName();
	/*客户端versionCode*/
	public static final int APP_VERSION_CODE = AppApplication.getVersionCode();
	/*客户端ID*/
	public static final String APP_ID = "1";//jinwanr_android
	/*客户端SECRET KEY*/
	public static final String APP_SECRET_KEY = "1qaz2wsx";
	
	
	//online website	
	public static final String JINWAN_WEB_DOMAIN = "http://www.jinwanr.com";
	//online mcap api	
	public static final String JINWAN_MOBILE_DOMAIN = "http://mobile.jinwanr.com";
	//test mcap api
//	public static final String JINWAN_MOBILE_DOMAIN = "http://172.168.1.88:8080/designer-mcap";

	
	
	public static final String JINWAN_API_PREFIX = JINWAN_MOBILE_DOMAIN + "/api";
	
	
//	public static final String JINWAN_API_ALBUMS = JINWAN_API_PREFIX + "/moreAlbums.json";
	
	/*SP的默认Config*/
	public static final String SP_CONFIG_DEFAULT = "CONFIG";
	/*SP的账户Config*/
	public static final String SP_CONFIG_ACCOUNT = "ACCOUNT";
	/*SP中userPassport的key*/
	public static final String SP_KEY_USERPASSPORT = "USER_PASSPORT";
	/*SP中userInfo的key*/
	public static final String SP_KEY_USERINFO = "USER_INFO";
	/*SP中push的key*/
	public static final String SP_KEY_BAIDU_PUSH = "BAIDU_PUSH";
	
	
	

}
