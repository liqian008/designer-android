package com.bruce.designer.activity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.message.MessageListApi;
import com.bruce.designer.api.message.PostChatApi;
import com.bruce.designer.broadcast.NotificationBuilder;
import com.bruce.designer.constants.Config;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Message;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.DipUtil;
import com.bruce.designer.util.MessageUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 私信消息对话页面
 * @author liqian
 *
 */
public class Activity_MessageChat extends BaseActivity implements OnRefreshListener2<ListView>{
	
	private static final int HANDLER_FLAG_CHAT_LIST = 1;

	protected static final int HANDLER_FLAG_CHAT_POST = 11;
	
	private View titlebarView;
	private TextView titleView;
	
	private ListView messageListView;
	private MessageListAdapter messageListAdapter;
	private PullToRefreshListView pullRefreshView;

	private int messageType;
	private String nickname;
	private String avatarUrl;

	private EditText messageInput;
	
	private long messageTailId;
	
	/**
	 * Chat消息的messageType 实为对方的userId
	 * @param context
	 * @param messageType
	 * @param nickname 
	 */
	public static void show(Context context, int messageType, String nickname, String avatarUrl){
		Intent intent = new Intent(context, Activity_MessageChat.class);
		intent.putExtra("messageType", messageType);
		intent.putExtra("nickname", nickname);
		intent.putExtra("avatarUrl", avatarUrl);
		context.startActivity(intent);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_chat);
		
		Intent intent = getIntent();
		//获取messageType
		messageType =  intent.getIntExtra("messageType", 0); 
		nickname =  intent.getStringExtra("nickname");
		avatarUrl =  intent.getStringExtra("avatarUrl");
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(onclickListener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText(nickname);
		
		View commentPanel = (View) findViewById(R.id.commentPanel);
		commentPanel.setVisibility(View.VISIBLE);
		
		//评论框
		messageInput = (EditText) findViewById(R.id.commentInput);
		Button btnCommentPost = (Button) findViewById(R.id.btnCommentPost);
		btnCommentPost.setOnClickListener(onclickListener);
		
		pullRefreshView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullRefreshView.setMode(Mode.PULL_FROM_END);//在聊天界面，上拉刷新为获取最新数据
		pullRefreshView.setOnRefreshListener(this);
		
		messageListView = pullRefreshView.getRefreshableView();// (ListView)findViewById(R.id.msgDialog);
		messageListAdapter = new MessageListAdapter(context, null);
		messageListView.setAdapter(messageListAdapter);
		
		//获取消息列表
		getMessageList(0);
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
			Message message = getItem(position);
			if(message!=null){
				View itemView = LayoutInflater.from(context).inflate(R.layout.item_msgchat_view, null);
				
				RelativeLayout messageContainer = (RelativeLayout) itemView.findViewById(R.id.messageContainer);
				RelativeLayout myMessageContainer = (RelativeLayout) itemView.findViewById(R.id.myMessageContainer);
				if(message.getFromId()!=Config.HOST_ID){//需要展示对方的对话消息
					
					messageContainer.setVisibility(View.VISIBLE);
					myMessageContainer.setVisibility(View.GONE);
					
					TextView chatTimetView = (TextView) itemView.findViewById(R.id.chatTime);
					chatTimetView.setText(TimeUtil.displayTime(message.getCreateTime()));
					
					GradientDrawable timeTextDrawable = new GradientDrawable();
					timeTextDrawable.setColor(getResources().getColor(R.color.grey_button_normal_color));
					float timeRadius = DipUtil.calcFromDip((Activity) context, 3);
					timeTextDrawable.setCornerRadius(timeRadius);
					chatTimetView.setBackground(timeTextDrawable);
					
					TextView msgContentView = (TextView) itemView.findViewById(R.id.msgContent);
					msgContentView.setText(message.getMessage());
					
					GradientDrawable chatTextDrawable = new GradientDrawable();
					chatTextDrawable.setColor(getResources().getColor(R.color.grey_button_active_color));
					float radius = DipUtil.calcFromDip((Activity) context, 6);
					chatTextDrawable.setCornerRadius(radius);
					
					msgContentView.setBackground(timeTextDrawable);
					
					//私信消息需要使用fromUser的头像
					ImageView msgAvatrView = (ImageView) itemView.findViewById(R.id.msgAvatar);
					ImageLoader.getInstance().displayImage(avatarUrl, msgAvatrView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				}else{//需要展示自己的对话消息
					TextView myChatTimetView = (TextView) itemView.findViewById(R.id.myChatTime);
					myChatTimetView.setText(TimeUtil.displayTime(message.getCreateTime()));
					
					GradientDrawable timeTextDrawable = new GradientDrawable();
					timeTextDrawable.setColor(getResources().getColor(R.color.grey_button_normal_color));
					float timeRadius = DipUtil.calcFromDip((Activity) context, 3);
					timeTextDrawable.setCornerRadius(timeRadius);
					myChatTimetView.setBackground(timeTextDrawable);
					
					TextView myMsgContentView = (TextView) itemView.findViewById(R.id.myMsgContent);
					myMsgContentView.setText(message.getMessage());
					
					GradientDrawable chatTextDrawable = new GradientDrawable();
					chatTextDrawable.setColor(getResources().getColor(R.color.green_button_normal_color));
					float radius = DipUtil.calcFromDip((Activity) context, 6);
					chatTextDrawable.setCornerRadius(radius);
					myMsgContentView.setBackground(chatTextDrawable);
					
					messageContainer.setVisibility(View.GONE);
					myMessageContainer.setVisibility(View.VISIBLE);
				}
				return itemView;
			}
			return null;
		}
	}
	
	/**
	 * 获取关注列表
	 */
	private void getMessageList(final long messageTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				android.os.Message message;
				MessageListApi api = new MessageListApi(messageType, messageTailId); 
				ApiResult jsonResult = ApiManager.invoke(context, api);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_CHAT_LIST);
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
				case HANDLER_FLAG_CHAT_LIST:
					pullRefreshView.onRefreshComplete();
					Map<String, Object> messagesDataMap = (Map<String, Object>) msg.obj;
					if(messagesDataMap!=null){
						//解析响应数据
						Long fromTailId = (Long) messagesDataMap.get("fromTailId");
						Long newTailId = (Long) messagesDataMap.get("newTailId");
						
						List<Message> messageList = (List<Message>)  messagesDataMap.get("messageList");
						if(messageList!=null&&messageList.size()>0){
							if(newTailId!=null&&newTailId>0){//还有可加载的数据
								messageTailId = newTailId;
								pullRefreshView.setMode(Mode.BOTH);
							}else{
								messageTailId = 0;
								pullRefreshView.setMode(Mode.PULL_FROM_END);//禁用下拉刷新查询历史消息 
							}
							
							Collections.reverse(messageList);//聊天界面需要反转list，保证最新消息在最下方
							List<Message> oldMessageList = messageListAdapter.getMessageList();
							//判断加载位置，以确定是list增量还是覆盖
							boolean fallloadAppend = fromTailId!=null&&fromTailId>0;
							if(fallloadAppend){//加载更多操作，需添加至list的开始
								oldMessageList.addAll(0, messageList);
							}else{//下拉加载，需覆盖原数据
								oldMessageList = null;
								oldMessageList = messageList;
							}
							messageListAdapter.setMessageList(oldMessageList);
							messageListAdapter.notifyDataSetChanged();
							if(!fallloadAppend){
								//非加载更多的场景，需要直接滚动到底部
								messageListView.setSelection(messageListView.getBottom());
							}
						}
					}
					break;
				case HANDLER_FLAG_CHAT_POST:
					NotificationBuilder.createNotification(context, "消息发送成功...");
					messageInput.setText("");//清空评论框内容
					//隐藏软键盘
					InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(messageInput.getWindowToken(), 0);
					//重新加载评论列表
					getMessageList(0);
					break;
				default:
					break;
			}
		}
	};
	
	/**
	 * 下拉刷新
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMessageList(messageTailId);
	}
	
	/**
	 * 上拉刷新
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMessageList(0);
	}

	private OnClickListener onclickListener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.titlebar_return:
				finish();
				break;
			case R.id.btnCommentPost:
				//检查内容不为空
				//启动线程发布回复消息
				postChat(messageType, messageInput.getText().toString());
				break;
			default:
				break;
			}
		}
	};
	
	
	/**
	 * 回复聊天消息
	 */
	private void postChat(final int toId, final String content) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				android.os.Message message;
				PostChatApi api = new PostChatApi(toId, content);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_CHAT_POST);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	
}
