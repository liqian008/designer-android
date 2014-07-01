package com.bruce.designer.constants;

public interface ConstantOAuth {
	
	/*Weibo的OAuth参数*/
	
	public static final String APP_KEY = "753177599";
	// 应用的APP_KEY
	public static final String REDIRECT_URL = "http://www.jinwanr.com.cn/wbCallback";// 应用的回调页
	// 应用申请的高级权限
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog,invitation_write";
}
