package com.bruce.designer.constants;


public interface ConstantDesigner {

	public static final short ALBUM_DELETE_STATUS = 0;

	public static final short ALBUM_OPEN_STATUS = 1;

	public static final short ALBUM_PRIVATE_STATUS = 2;

	// ////////////////////用户状态常量////////////////////////////////
	/* 用户正常状态 */
	public static final short USER_STATUS_OPEN = 1;
	/* 用户被封禁状态 */
	public static final short USER_STATUS_FORBIDDEN = 2;
	
	/* 设计师原始状态 */
	public static final short DESIGNER_APPLY_NONE = 0;
	/* 设计师申请已发送 */
	public static final short DESIGNER_APPLY_SENT = 1;
	/* 设计师审核通过 */
	public static final short DESIGNER_APPLY_APPROVED = 2;
	/* 设计师审核拒绝 */
	public static final short DESIGNER_APPLY_DENIED = 3;

	// ////////////////////用户状态常量////////////////////////////////
	/* 未选定cover的状态 */
	public static final short ALBUM_SLIDE_ISNOT_COVER = 0;
	/* 选定cover的状态 */
	public static final short ALBUM_SLIDE_IS_COVER = 1;

	// ////////////////////消息常量////////////////////////////////

	/* 系统类型消息 */
	public static final int MESSAGE_TYPE_SYSTEM = 1;
	/* 评论类型消息 */
	public static final int MESSAGE_TYPE_COMMENT = 2;
	/* 关注类型消息 */
	public static final int MESSAGE_TYPE_FOLLOW = 3;
	/* like类型消息 */
	public static final int MESSAGE_TYPE_LIKE = 4;
	/* 收藏类型消息 */
	public static final int MESSAGE_TYPE_FAVORITIES = 5;
	/* @类型消息 */
	public static final int MESSAGE_TYPE_AT = 6;
	/* 聊天类型消息 */
	// public static final short MESSAGE_TYPE_CHAT = 100;

	/* 系统广播专用的fromId */
	public static final int MESSAGE_DELIVER_ID_BROADCAST = MESSAGE_TYPE_SYSTEM;
	/* 评论类型消息 */
	public static final int MESSAGE_DELIVER_ID_COMMENT = MESSAGE_TYPE_COMMENT;
	/* 关注类型消息 */
	public static final int MESSAGE_DELIVER_ID_FOLLOW = MESSAGE_TYPE_FOLLOW; 
	/* like类型消息 */
	public static final int MESSAGE_DELIVER_ID_LIKE = MESSAGE_TYPE_LIKE;
	/* 收藏类型消息 */
	public static final int MESSAGE_DELIVER_ID_FAVORITES = MESSAGE_TYPE_FAVORITIES;
	/* @类型消息 */
	public static final int MESSAGE_DELIVER_ID_AT = MESSAGE_TYPE_AT;

	/* 消息可读状态 */
	public static final short MESSAGE_READ = 0;
	/* 消息可读状态 */
	public static final short MESSAGE_UNREAD = 1;
	
	
	
}
