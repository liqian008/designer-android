package com.bruce.designer.constants;

/**
 * 百度统计事件key，在mtj中的自定义事件中配置
 * 
 * @author liqian
 * 
 */
public interface ConstantsStatEvent {

	// 建议更新事件
	public static final String EVENT_UPDATE_SUGGEST = "event_update_suggest";
	// 强制更新事件
	public static final String EVENT_UPDATE_FORCE = "event_update_force";

	// 登录事件
	public static final String EVENT_LOGIN = "event_login";
	// 主屏tab点击事件
	public static final String EVENT_MAIN_TAB = "event_main_tab";
	// 主屏上方tab点击事件
	public static final String EVENT_MAIN_TAB_SUBTAB = "event_main_tab_subtab";
	// 主屏刷新事件
	public static final String EVENT_MAIN_TAB_REFRESH = "event_main_tab_refresh";
	
	// 热门上方tab点击事件
	public static final String EVENT_HOT_ALBUMS_TAB_SUBTAB = "event_hot_albums_tab_subtab";
	// 热门刷新事件
	public static final String EVENT_HOT_ALBUMS_TAB_REFRESH = "event_hot_albums_tab_refresh";
	

	// 查看个人主页点击事件
	public static final String EVENT_VIEW_HOME = "event_view_home";
	// 查看个人资料点击事件
	public static final String EVENT_VIEW_PROFILE = "event_view_profile";
	// 查看作品详情点击事件
	public static final String EVENT_VIEW_ALBUM = "event_view_album";
	// 查看作品图片点击事件
	public static final String EVENT_VIEW_ALBUM_SLIDE = "event_view_album_slide";
	// 查看关注事件
	public static final String EVENT_VIEW_FOLLOWS = "event_view_follows";
	// 查看粉丝事件
	public static final String EVENT_VIEW_FANS = "event_view_fans";
	// 查看收藏事件
	public static final String EVENT_VIEW_FAVORITES = "event_view_favorites";
	// 查看设置事件
	public static final String EVENT_VIEW_SETTINGS = "event_view_settings";
	// 查看私信事件
	public static final String EVENT_VIEW_CHAT = "event_view_chat";
	// 查看消息列表事件
	public static final String EVENT_VIEW_MSGLIST = "event_view_msglist";

	
	
	// 评论点击事件
	public static final String EVENT_COMMENT = "event_comment";
	// 赞点击事件
	public static final String EVENT_LIKE = "event_like";
	// 取消点击事件
	public static final String EVENT_UNLIKE = "event_unlike";
	// 收藏点击事件
	public static final String EVENT_FAVORITE = "event_favorite";
	// 取消收藏点击事件
	public static final String EVENT_UNFAVORITE = "event_unfavorite";
	// 点击分享事件
	public static final String EVENT_SHARE = "event_share";
	// 选择分享目标事件
	public static final String EVENT_SHARE_TARGET = "event_share_target";
	// 发表评论点击事件
	public static final String EVENT_SEND_COMMENT = "event_send_comment";

	// 发送私信点击事件
	public static final String EVENT_SEND_CHAT = "event_send_chat";

	// 关注点击事件
	public static final String EVENT_FOLLOW = "event_follow";
	// 取消关注点击事件
	public static final String EVENT_UNFOLLOW = "event_unfollow";

	// 点击设置项
	public static final String EVENT_SETTINGS_OPTION = "event_settings_option";
	// 切换PUSH项
	public static final String EVENT_UPDATE_PUSH_SETTING = "event_update_push_settings";

	// 点击修改头像事件
	public static final String EVENT_CHANGE_AVATAR = "event_change_avatar";

}
