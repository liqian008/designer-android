package com.bruce.designer.activity;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.activity.fragment.Fragment_Msgbox;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.message.MessageListApi;
import com.bruce.designer.constants.ConstantDesigner;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Message;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 普通消息界面
 * @author liqian
 */
public class Activity_MessageList extends BaseActivity {
	
	private View titlebarView;
	private TextView titleView;
	
	private MessageListAdapter messageListAdapter;

	private int messageType;

//	private int userId;
	
	public static void show(Context context, int messageType){
		Intent intent = new Intent(context, Activity_MessageList.class);
		intent.putExtra("messageType", messageType);
		context.startActivity(intent);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_dialog);
		
		Intent intent = getIntent();
		//获取messageType
		messageType =  intent.getIntExtra("messageType", 0); 
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText(Fragment_Msgbox.buildMessageTitle(messageType, null, -1));
		
		ListView messageListView = (ListView)findViewById(R.id.msgDialog);
		messageListAdapter = new MessageListAdapter(context, null);
		messageListView.setAdapter(messageListAdapter);
		
		//获取消息列表
		getMessageList(0);
		//TODO 需要增加下拉刷新
	}
	
	
	class MessageListAdapter extends BaseAdapter {

		private List<Message> messageList;
		private Context context;
		
		public MessageListAdapter(Context context, List<Message> messageList) {
			this.context = context;
			this.messageList = messageList;
		}
		
		public List<Message> getMessageList() {
			return messageList;
		}

		public void setMessageList(List<Message> messageList) {
			this.messageList = messageList;
		}

		@Override
		public int getCount() {
			if (messageList != null) {
				return messageList.size();
			}
			return 0;
		}

		@Override
		public Message getItem(int position) {
			if (messageList != null) {
				return messageList.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//TODO 暂未使用convertView
			final Message message = getItem(position);
			if(message!=null){
				View itemView = LayoutInflater.from(context).inflate(R.layout.item_msgbox_view, null);
				
				TextView msgTitleView = (TextView) itemView.findViewById(R.id.msgTitle);
				
				if(Fragment_Msgbox.isBroadcastMessage(message.getMessageType())){//系统消息title不变
					msgTitleView.setText("系统消息");
				}else if(!Fragment_Msgbox.isChatMessage(position)){//赞，评论，收藏等消息的title需要是fromUser
					msgTitleView.setText(message.getFromUser().getNickname());
				}else{//私信消息
					//此界面不处理
				}
				
				TextView msgContentView = (TextView) itemView.findViewById(R.id.msgContent);
				msgContentView.setText(message.getMessage());
				
				TextView msgPubTimeView = (TextView) itemView.findViewById(R.id.msgPubTime);
				msgPubTimeView.setText(TimeUtil.displayTime(message.getCreateTime()));
				
				//头像
				ImageView msgAvatrView = (ImageView) itemView.findViewById(R.id.msgAvatar);
				displayAvatarView(msgAvatrView, message);
				
				//头像点击事件
				msgAvatrView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						//跳转个人主页
						Activity_UserHome.show(context, message.getFromId(), message.getFromUser().getNickname(), message.getFromUser().getHeadImg(), false, false);
					}
				});
				
				return itemView;
			}
			return null;
		}
	}
	
	/**
	 * 获取关注列表
	 * @param fansTailId
	 */
	private void getMessageList(final int fansTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				android.os.Message message;
				
				MessageListApi api = new MessageListApi(messageType, 1);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(0);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				case 0:
					Map<String, Object> messagesDataMap = (Map<String, Object>) msg.obj;
					if(messagesDataMap!=null){
						List<Message> messageList = (List<Message>)  messagesDataMap.get("messageList");
						if(messageList!=null&&messageList.size()>0){
							messageListAdapter.setMessageList(messageList);
							messageListAdapter.notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
			}
		}
	};
	

	private OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.titlebar_return:
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	
	/**
	 * 构造消息头像
	 * @param msgAvatrView
	 * @param message
	 */
	public static void displayAvatarView(ImageView msgAvatrView, Message message) {
		switch (message.getMessageType()) {
			case ConstantDesigner.MESSAGE_TYPE_SYSTEM: {
				msgAvatrView.setImageResource(R.drawable.icon_msgbox_sys);//系统消息
				break;
			}
			case ConstantDesigner.MESSAGE_TYPE_LIKE:
			case ConstantDesigner.MESSAGE_TYPE_FAVORITIES:
			case ConstantDesigner.MESSAGE_TYPE_FOLLOW:
			case ConstantDesigner.MESSAGE_TYPE_COMMENT:
			case ConstantDesigner.MESSAGE_TYPE_AT: {//sns 交互类消息
				if(message.getFromUser()!=null){
					ImageLoader.getInstance().displayImage(message.getFromUser().getHeadImg(), msgAvatrView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				}
				break;
			}
			default: {
				break;
			}
		}
		
	}
	
}
