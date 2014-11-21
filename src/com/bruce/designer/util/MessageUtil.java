package com.bruce.designer.util;

import android.widget.ImageView;

import com.bruce.designer.R;
import com.bruce.designer.constants.Config;
import com.bruce.designer.constants.ConstantDesigner;
import com.bruce.designer.model.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MessageUtil {

	/**
	 * 构造消息title
	 * 
	 * @param messageType
	 * @return
	 */
	public static String buildMessageTitle(int messageType, String nickname) {
		String result = null;
		if (messageType == ConstantDesigner.MESSAGE_TYPE_SYSTEM) {
			result = "系统消息";
		} else if (messageType == ConstantDesigner.MESSAGE_TYPE_FOLLOW) {
			result = "关注消息";
		} else if (messageType == ConstantDesigner.MESSAGE_TYPE_COMMENT) {
			result = "评论消息";
		} else if (messageType == ConstantDesigner.MESSAGE_TYPE_LIKE) {
			result = "赞消息";
		} else if (messageType == ConstantDesigner.MESSAGE_TYPE_FAVORITIES) {
			result = "收藏消息";
		} else if (messageType == ConstantDesigner.MESSAGE_TYPE_AT) {
			result = "@消息";
		} else if (isChatMessage(messageType)) {
			if (nickname == null) {
				result = "私信消息";
			} else {
				result = "私信 - " + nickname;
			}
		}
		return result;
	}

	public static void displayAvatarView(ImageView msgAvatrView, Message message) {
		switch (message.getMessageType()) {
			case ConstantDesigner.MESSAGE_TYPE_SYSTEM: {
				msgAvatrView.setImageResource(R.drawable.icon_msgbox_sys);
				break;
			}
			case ConstantDesigner.MESSAGE_TYPE_FOLLOW: {
				msgAvatrView.setImageResource(R.drawable.icon_msgbox_follow);
				break;
			}
			case ConstantDesigner.MESSAGE_TYPE_COMMENT: {
				msgAvatrView.setImageResource(R.drawable.icon_msgbox_comment);
				break;
			}
			case ConstantDesigner.MESSAGE_TYPE_LIKE: {
				msgAvatrView.setImageResource(R.drawable.icon_msgbox_like);
				break;
			}
			case ConstantDesigner.MESSAGE_TYPE_FAVORITIES: {
				msgAvatrView.setImageResource(R.drawable.icon_msgbox_favorite);
				break;
			}
			case ConstantDesigner.MESSAGE_TYPE_AT: {
				msgAvatrView.setImageResource(R.drawable.icon_msgbox_at);
				break;
			}
			default: {
				//私信消息
				if(message.getChatUser()!=null){
					ImageLoader.getInstance().displayImage(message.getChatUser().getHeadImg(), msgAvatrView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				}
				break;
			}
		}
	}
	
	
	public static boolean isChatMessage(int messageType) {
		return messageType >= Config.GUEST_USER_ID;
	}
	
	/**
	 * 判断是否是系统广播
	 * @param messageType
	 * @return
	 */
	public static boolean isBroadcastMessage(int messageType) {
		return messageType == ConstantDesigner.MESSAGE_TYPE_SYSTEM;
	}

	
	/**
	 * 判断是否是交互的消息（交互类消息有源，通常是Album）
	 * @param messageType
	 * @return
	 */
	public static boolean isInteractiveMessage(int messageType) {
		return messageType == ConstantDesigner.MESSAGE_TYPE_COMMENT || messageType == ConstantDesigner.MESSAGE_TYPE_LIKE || messageType == ConstantDesigner.MESSAGE_TYPE_FAVORITIES;
	}

}
