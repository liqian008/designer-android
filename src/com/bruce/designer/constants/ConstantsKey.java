package com.bruce.designer.constants;

public interface ConstantsKey {
	
	public static final String BUNDLE_ALBUM_INFO_ID = "bundle_album_info_id";
	
	public static final String BUNDLE_ALBUM_INFO = "bundle_album_info";
	
	public static final String BUNDLE_ALBUM_AUTHOR_INFO = "bundle_album_author_info";
	
	public static final String BUNDLE_ALBUM_GOTO_COMMENT = "bundle_album_goto_comment";

	/*用户ID*/
	public static final String BUNDLE_USER_INFO_ID = "bundle_user_info_id";
	/*用户昵称*/
	public static final String BUNDLE_USER_INFO_NICKNAME = "bundle_user_info_nickname";
	/*用户头像*/
	public static final String BUNDLE_USER_INFO_AVATAR = "bundle_user_info_avatar";
	/*用户是否是设计师*/
	public static final String BUNDLE_USER_INFO_ISDESIGNER = "bundle_user_info_isdesigner";
	/*用户的关注状态*/
	public static final String BUNDLE_USER_INFO_HASFOLLOWED = "bundle_user_info_hasfollowed";
	
	/* 广播的action*/
//	public static final String BROADCAST_ACTION = "com.bruce.designer.action";
	/* 广播的key*/
	public static final String BUNDLE_BROADCAST_KEY = "bundle_broadcast_key";
	/* album被操作的key*/
	public static final String BUNDLE_BROADCAST_KEY_OPERATED_ALBUMID = "bundle_broadcast_key_operated_albumid";
	
	
	/* 微信登录广播的action */
	public static final String BROADCAST_ACTION_WEIXIN_LOGIN = "com.bruce.designer.action.weixin.login";
	
	public static final int BROADCAST_NETWORK_INVALID = 1;
	
	public static final int BROADCAST_GUEST_DENIED = 11;
	public static final int BROADCAST_BACK_TO_LOGIN = 12;
	
	public static final int BROADCAST_ALBUM_OPERATED = 100;
	public static final int BROADCAST_MESSAGE_READ_CHANGED = 110;
	
	public static final String LAST_REFRESH_TIME_MAIN_PREFIX = "last_refresh_main_prefix_";
	public static final String LAST_REFRESH_TIME_HOTALBUM_PREFIX = "last_refresh_hotalbum_prefix_";
	public static final String LAST_REFRESH_TIME_HOTDESIGNER_PREFIX = "last_refresh_hotdesigner_prefix_";
	
	public static final String LAST_REFRESH_TIME_MSGBOX_PREFIX = "last_refresh_msgbox_prefix";
	public static final String LAST_REFRESH_TIME_MYHOME_PREFIX = "last_refresh_myhome_prefix";
	
	/*广播action的enum*/
	public static enum BroadcastActionEnum{
		SYSTEM("com.bruce.designer.action.system"),
		ALBUM_OPERATED("com.bruce.designer.action.albumOperated"),
		MESSAGE_STATUS_CHANGED("com.bruce.designer.action.messageStatusChanged");
		
		private String action;
		BroadcastActionEnum(String action){
			this.action = action;
		}
		public String getAction() {
			return action;
		}
		
		@Override
		public String toString() {
			return action;
		}
		
	}
}


