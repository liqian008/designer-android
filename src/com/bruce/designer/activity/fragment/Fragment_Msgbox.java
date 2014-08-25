package com.bruce.designer.activity.fragment;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
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
import com.bruce.designer.activity.Activity_MessageChat;
import com.bruce.designer.activity.Activity_MessageList;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.message.MessageBoxApi;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Message;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.MessageUtil;
import com.bruce.designer.util.TimeUtil;
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
	
	private static final int HANDLER_FLAG_USERBOX = 1;
	
	private View titlebarView;

	private TextView titleView;
	
	private MessageBoxAdapter messageBoxAdapter;
	
	private Activity activity;
	
	private LayoutInflater inflater;
	
	private PullToRefreshListView pullRefreshView; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();
		this.inflater = inflater;
		
		View mainView = inflater.inflate(R.layout.fragment_msgbox, null);
		
		initView(mainView);
		return mainView;
	}

	
	private void initView(View mainView) {
		View titlebarIcon = (View) mainView.findViewById(R.id.titlebar_icon);
		titlebarIcon.setVisibility(View.GONE);
		//init view
		titlebarView = mainView.findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		titleView = (TextView) mainView.findViewById(R.id.titlebar_title);
		titleView.setText("消息中心");
		
		pullRefreshView = (PullToRefreshListView) mainView.findViewById(R.id.pull_refresh_list);
		pullRefreshView.setMode(Mode.PULL_FROM_START);
		pullRefreshView.setOnRefreshListener(this);
		
		ListView msgboxView = pullRefreshView.getRefreshableView();
		messageBoxAdapter = new MessageBoxAdapter(activity, null);
		msgboxView.setAdapter(messageBoxAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		//获取消息列表
//		getMessageBox(0);
		pullRefreshView.setRefreshing(false);
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
						Activity_MessageChat.show(context, message.getMessageType(), chatNickname, message.getChatUser().getHeadImg());
					}else{//普通消息
						Activity_MessageList.show(context, message.getMessageType());
					}
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 获取关注列表
	 * @param xxxId
	 */
	private void getMessageBox(final int xxxId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				android.os.Message message;
				MessageBoxApi api = new MessageBoxApi();
				ApiResult jsonResult = ApiManager.invoke(activity, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_USERBOX);
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
				case HANDLER_FLAG_USERBOX:
					pullRefreshView.onRefreshComplete();
					Map<String, Object> userFansDataMap = (Map<String, Object>) msg.obj;
					if(userFansDataMap!=null){
						List<Message> messageBoxList = (List<Message>)  userFansDataMap.get("messageBoxList");
						if(messageBoxList!=null&&messageBoxList.size()>0){
							messageBoxAdapter.setMessageBoxList(messageBoxList);
							messageBoxAdapter.notifyDataSetChanged();
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
			default:
				break;
			}
		}
	};
	

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		//获取消息列表
		getMessageBox(0);
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
}
