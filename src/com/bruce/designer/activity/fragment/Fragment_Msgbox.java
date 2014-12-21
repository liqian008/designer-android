package com.bruce.designer.activity.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_MessageChat;
import com.bruce.designer.activity.Activity_MessageList;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.message.MessageBoxApi;
import com.bruce.designer.broadcast.DesignerReceiver;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.constants.ConstantsStatEvent;
import com.bruce.designer.handler.DesignerHandler;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Message;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.MessageUtil;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UiUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 我的个人资料的Fragment
 * @author liqian
 *
 */
public class Fragment_Msgbox extends BaseFragment implements OnRefreshListener2<ListView> {
	
	private static final int HANDLER_FLAG_MSGBOX_RESULT = 1;
	
	private View titlebarView;

	private TextView titleView;
	
	private MessageBoxAdapter messageBoxAdapter;
	
	private Activity activity;
	
	private LayoutInflater inflater;
	
	private PullToRefreshListView pullRefreshView; 
	
	public MessageChangedListener messageListener;
	
	private List<Message> messageBoxList;
	
	private Handler handler;

	private OnClickListener onClickListener;
	/*receiver，用于接收消息阅读状态*/
	private DesignerReceiver receiver;
	private boolean messageReadChanged;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();
		this.inflater = inflater;
		
		handler = initHandler();
		onClickListener = initListener();
		
		View mainView = inflater.inflate(R.layout.fragment_msgbox, null);
		initView(mainView);
		
		
		//注册receiver，接收广播后要刷新数据
		receiver = new DesignerReceiver(){
			public void onReceive(Context context, Intent intent) {
				int key = intent.getIntExtra(ConstantsKey.BUNDLE_BROADCAST_KEY, 0);
				if(key==ConstantsKey.BROADCAST_MESSAGE_READ_CHANGED){
					messageReadChanged = true;
				}
			}
		};
		LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, new IntentFilter(ConstantsKey.BroadcastActionEnum.MESSAGE_STATUS_CHANGED.getAction()));
		
		return mainView;
	}

	
	private void initView(View mainView) {
		View titlebarIcon = (View) mainView.findViewById(R.id.titlebar_icon);
		titlebarIcon.setVisibility(View.GONE);
		//init view
		titlebarView = mainView.findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(onClickListener);
		titleView = (TextView) mainView.findViewById(R.id.titlebar_title);
		titleView.setText("消息中心");
		
		pullRefreshView = (PullToRefreshListView) mainView.findViewById(R.id.pull_refresh_list);
		pullRefreshView.setMode(Mode.PULL_FROM_START);
		pullRefreshView.setOnRefreshListener(this);
		
		ListView msgboxView = pullRefreshView.getRefreshableView();
		messageBoxAdapter = new MessageBoxAdapter(activity, new ArrayList<Message>());
		msgboxView.setAdapter(messageBoxAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		//获取消息列表
//		getMessageBox(0);
		
		//判断list中是否有数据（没有则立刻刷新，有则判断刷新时间间隔）
		if(messageReadChanged||messageBoxList==null ||messageBoxList.size()<=0){//没有则立刻刷新
			pullRefreshView.setRefreshing(false);
		}else{
			//判断上次刷新时间
			long currentTime = System.currentTimeMillis();
			String msgboxRefreshKey = getRefreshKey();
			long lastRefreshTime = SharedPreferenceUtil.getSharePreLong(activity, msgboxRefreshKey, 0l);
			long interval = currentTime - lastRefreshTime;
			
			if(interval > (TimeUtil.TIME_UNIT_MINUTE*2)){
				pullRefreshView.setRefreshing(false);
			}
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		messageListener = (MessageChangedListener) activity;
	}
	
	
	class MessageBoxAdapter extends BaseAdapter {

		private List<Message> messageBoxList;
		private Context context;
		
		public MessageBoxAdapter(Context context, List<Message> messageBoxList) {
			this.context = context;
			this.messageBoxList = messageBoxList;
		}
		
		public List<Message> getMessageBoxList() {
			return messageBoxList;
		}

		public void setMessageBoxList(List<Message> messageBoxList) {
			this.messageBoxList = messageBoxList;
		}

		@Override
		public int getCount() {
			if (messageBoxList != null) {
				return messageBoxList.size();
			}
			return 0;
		}

		@Override
		public Message getItem(int position) {
			if (messageBoxList != null) {
				return messageBoxList.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Message message = getItem(position);
			//TODO 使用convertView
			MessageViewHandler viewHolder = null;
			if(convertView==null){
				viewHolder = new MessageViewHandler();
				if(message!=null){
					convertView = inflater.inflate(R.layout.item_msgbox_view, null);
					viewHolder.messageItemView = convertView;
					viewHolder.unreadNumContainer = (View) convertView.findViewById(R.id.unreadNumContainer);
					viewHolder.unreadNumText = (TextView) convertView.findViewById(R.id.unreadNum);
					viewHolder.msgTitleView = (TextView) convertView.findViewById(R.id.msgTitle);
					viewHolder.msgContentView = (TextView) convertView.findViewById(R.id.msgContent);
					viewHolder.msgPubTimeView = (TextView) convertView.findViewById(R.id.msgPubTime);
					viewHolder.msgAvatrView = (ImageView) convertView.findViewById(R.id.msgAvatar);
					convertView.setTag(viewHolder);
				}
			}else{
				viewHolder = (MessageViewHandler) convertView.getTag();
			}

			//填充数据
			
			//未读消息数
			int unreadNum = message.getUnread();
			unreadNum = unreadNum>99?99:unreadNum;//最多显示99条
			viewHolder.unreadNumText.setText(String.valueOf(unreadNum));
			if(unreadNum>0){
				viewHolder.unreadNumContainer.setVisibility(View.VISIBLE); 
			}else{
				viewHolder.unreadNumContainer.setVisibility(View.GONE);
			}
			//消息内容
			String nickname = null;
			int messageType = message.getMessageType();
			if(MessageUtil.isChatMessage(messageType)){
				nickname = message.getChatUser().getNickname();
			}
			final String chatNickname = nickname;
			
			viewHolder.msgTitleView.setText(MessageUtil.buildMessageTitle(messageType, nickname));
			viewHolder.msgContentView.setText(message.getMessage());
			viewHolder.msgPubTimeView.setText(TimeUtil.displayTime(message.getCreateTime()));
			
			//头像
			MessageUtil.displayAvatarView(viewHolder.msgAvatrView, message);
			viewHolder.messageItemView.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View v) {
					if(MessageUtil.isChatMessage(message.getMessageType())){//私信消息
						StatService.onEvent(activity, ConstantsStatEvent.EVENT_VIEW_CHAT, "消息Fragment中打开私信");
						
						Activity_MessageChat.show(context, message.getMessageType(), chatNickname, message.getChatUser().getHeadImg());
					}else{//普通消息
						StatService.onEvent(activity, ConstantsStatEvent.EVENT_VIEW_MSGLIST, "消息Fragment中打开消息列表");
						Activity_MessageList.show(context, message.getMessageType());
					}
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 获取消息列表
	 */
	private void getMessageBox() {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				android.os.Message message;
				MessageBoxApi api = new MessageBoxApi();
				ApiResult apiResult = ApiManager.invoke(activity, api);
//				if(jsonResult!=null&&jsonResult.getResult()==1){
////					message.obj = jsonResult.getData();
//				}
				message = handler.obtainMessage(HANDLER_FLAG_MSGBOX_RESULT);
				message.obj = apiResult;
				message.sendToTarget();
			}
		});
		thread.start();
	}
	
	private Handler initHandler(){
		Handler handler = new DesignerHandler(activity){
			@SuppressWarnings("unchecked")
			public void processHandlerMessage(android.os.Message msg) {
				ApiResult apiResult = (ApiResult) msg.obj;
				boolean successResult = (apiResult!=null&&apiResult.getResult()==1);
				
				switch(msg.what){
					case HANDLER_FLAG_MSGBOX_RESULT:
						pullRefreshView.onRefreshComplete();
						if(successResult){
							messageReadChanged = false;//刷到数据后，将消息状态标记为【无变化】
							
							Map<String, Object> userFansDataMap = (Map<String, Object>) apiResult.getData();
							if(userFansDataMap!=null){
								List<Message> messageBoxResultList = (List<Message>)  userFansDataMap.get("messageBoxList");
								if(messageBoxResultList!=null&&messageBoxResultList.size()>0){
									messageBoxList = messageBoxResultList;
									//缓存本次刷新的时间
									SharedPreferenceUtil.putSharePre(activity, getRefreshKey(), System.currentTimeMillis());
									
									//判断是否有未读消息
									boolean hasUnreadMsg = false;
									for(Message message: messageBoxResultList){
										if(message.getUnread()!=null&&message.getUnread()>0){
											hasUnreadMsg = true;//有未读消息
										}
									}
									if(hasUnreadMsg){//如果有未读消息，则需要前端activity进行提示
										messageListener.unreadMsgNotify();
									}else{
										messageListener.unreadMsgClear();
									}
									messageBoxAdapter.setMessageBoxList(messageBoxResultList);
									messageBoxAdapter.notifyDataSetChanged();
								}
							}else{
								UiUtil.showShortToast(activity, "获取消息数据失败，请重试");
							}
						}
						break;
					default:
						break;
				}
			}
		};
		return handler;
	}
	
	private OnClickListener initListener(){
		OnClickListener listener = new OnSingleClickListener() {
			@Override
			public void onSingleClick(View view) {
				switch (view.getId()) {
				default:
					break;
				}
			}
		};
		return listener;
	}
	

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		//获取消息列表
		getMessageBox();
	}


	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	}
	
	
	static class MessageViewHandler{
		
		public View messageItemView;
		
		public View unreadNumContainer;
		public TextView unreadNumText;
		
		public TextView msgTitleView;
		public TextView msgContentView;
		public TextView msgPubTimeView;
		//头像
		public ImageView msgAvatrView;
	}
	
	
	public interface MessageChangedListener {
		public void unreadMsgNotify();
		public void unreadMsgClear();
	}

	/**
	 * 生成记录其刷新的sp-key
	 * @return
	 */
	private static String getRefreshKey(){
		return ConstantsKey.LAST_REFRESH_TIME_MSGBOX_PREFIX;
	}
}
