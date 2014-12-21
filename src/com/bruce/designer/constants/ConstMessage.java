package com.bruce.designer.constants;


public interface ConstMessage {


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

	
}
