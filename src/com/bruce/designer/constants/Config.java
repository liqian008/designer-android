package com.bruce.designer.constants;

import com.bruce.designer.AppApplication;

public class Config {
	
	public static final boolean DEBUG = true;
	
	public static final String GUEST_TOAST_TEXT = "游客无法进行该操作，请先进行登录";
	
	public static final String NETWORK_UNAVAILABLE = "当前网络不可用";
	
	public static final String RESPONSE_ERROR = "网络连接不稳定，请重试";
	
	
	/*客户端类型，1为ios，2为android*/
	public static final int CLIENT_TYPE = 2;
	/*用于web来路统计的参数名*/
	public static final String CHANNEL_FLAG = "r";
	
	/*游客身份的uid*/
	public static int GUEST_USER_ID = 100000;
	
	/*客户端versionName*/
	public static final String APP_VERSION_NAME = AppApplication.getVersionName();
	/*客户端versionCode*/
	public static final int APP_VERSION_CODE = AppApplication.getVersionCode();
	/*客户端ID*/
	public static final String APP_ID = "2";//jinwanr_android
	/*客户端SECRET KEY*/
	public static final String APP_SECRET_KEY = "1qaz2wsx";
	
	
	//online website	
	public static final String JINWAN_WEB_DOMAIN = "http://www.jinwanr.com";
	//online mcap api	
	public static final String JINWAN_MOBILE_DOMAIN = "http://mobile.jinwanr.com";
	//测试服务器的 mcap api	
//	public static final String JINWAN_MOBILE_DOMAIN = "http://mtest.jinwanr.com";
	//本机测试的 mcap api
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
	
	/*SP中是否是第一次进入*/
	public static final String SP_KEY_APP_FIRST_OPEN = "APP_FIRST_OPEN";
	
	/*SP中pushMask的key*/
	public static final String SP_KEY_BAIDU_PUSH_MASK = "BAIDU_PUSH";
	
	/*SP中baidu push对象中userId的key*/
	public static final String SP_KEY_BAIDU_PUSH_USER_ID = "BAIDU_PUSH_USER_ID";
	/*SP中baidu push对象中channelId的key*/
	public static final String SP_KEY_BAIDU_PUSH_CHANNEL_ID = "BAIDU_PUSH_CHANNEL_ID";

	/*SP中微信公众帐号二维码图片的key*/
	public static final String SP_KEY_WEIXINMP_QRCODE_URL = "WEIXINMP_QRCODE_URL";

	
	
	
	
	

}
