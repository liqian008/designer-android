package com.bruce.designer.activity.fragment;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.bruce.designer.constants.ConstantDesigner;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Message;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 我的个人资料的Fragment
 * @author liqian
 *
 */
public class Fragment_Msgbox extends Fragment implements OnRefreshListener2<ListView> {
	
	
	private static final int HANDLER_FLAG_USERBOX = 1;
	
	
	private View titlebarView;

	private TextView titleView;
	
	private MessageBoxAdapter messageBoxAdapter;
	
	private Activity context;
	
	private LayoutInflater inflater;
	
	private PullToRefreshListView pullRefresh;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		this.inflater = inflater;
		
		View mainView = inflater.inflate(R.layout.activity_msg_box, null);
		
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
		
		pullRefresh = (PullToRefreshListView) mainView.findViewById(R.id.pull_refresh_list);
		pullRefresh.setMode(Mode.BOTH);
		pullRefresh.setOnRefreshListener(this);
		
		ListView msgboxView = pullRefresh.getRefreshableView();
		messageBoxAdapter = new MessageBoxAdapter(context, null);
		msgboxView.setAdapter(messageBoxAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		//获取消息列表
		getMessageBox(0);
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
			//TODO 暂未使用convertView
			
			final Message message = getItem(position);
			if(message!=null){
				View itemView = inflater.inflate(R.layout.item_msgbox_view, null);
				
				TextView msgTitleView = (TextView) itemView.findViewById(R.id.msgTitle);
				msgTitleView.setText(buildMessageTitle(message.getMessageType(), null, message.getUnread()));
				 
				TextView msgContentView = (TextView) itemView.findViewById(R.id.msgContent);
				msgContentView.setText(message.getMessage());
				
				TextView msgPubTimeView = (TextView) itemView.findViewById(R.id.msgPubTime);
				msgPubTimeView.setText(TimeUtil.displayTime(message.getCreateTime()));
				
				//头像
				ImageView msgAvatrView = (ImageView) itemView.findViewById(R.id.msgAvatar);
				displayAvatarView(msgAvatrView, message);
				
				itemView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						if(isChatMessage(message.getMessageType())){//私信消息
							Activity_MessageChat.show(context, message.getMessageType());
						}else{//普通消息
							Activity_MessageList.show(context, message.getMessageType());
						}
					}
				});
				return itemView;
			}
			return null;
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
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
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
					pullRefresh.onRefreshComplete();
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
	
	public static void displayAvatarView(ImageView msgAvatrView, Message message) {
		switch (message.getMessageType()) {
			case ConstantDesigner.MESSAGE_TYPE_SYSTEM: {
				msgAvatrView.setImageResource(R.drawable.icon_msgbox_sys);
				break;
			}
			case ConstantDesigner.MESSAGE_TYPE_FOLLOW: {
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
	
	
	/**
	 * 构造消息title
	 * @param messageType
	 * @return
	 */
	public static String buildMessageTitle(int messageType, String nickname, int unreadAmount) {
		String result = null;
		if(messageType==ConstantDesigner.MESSAGE_TYPE_SYSTEM) {
				result ="系统消息";
			} else if(messageType==ConstantDesigner.MESSAGE_TYPE_FOLLOW) {
				result ="关注消息";
			}else if(messageType==ConstantDesigner.MESSAGE_TYPE_COMMENT) {
				result ="评论消息";
			}else if(messageType==ConstantDesigner.MESSAGE_TYPE_LIKE) {
				result ="赞消息";
			}else if(messageType==ConstantDesigner.MESSAGE_TYPE_FAVORITIES) {
				result ="收藏消息";
			}else if(messageType==ConstantDesigner.MESSAGE_TYPE_AT) {
				result ="@消息";
			}else if(isChatMessage(messageType)){
				if(nickname==null){
					result= "私信消息";
				}else{
					result = "私信 - " + nickname;
				}
			}
		if(unreadAmount>=0){
			return result + " (未读 "+unreadAmount+")";
		}else{
			return result;
		}
	}
	
	public static boolean isChatMessage(int messageType){
		return messageType>=10000; 
	}
	
	public static boolean isBroadcastMessage(int messageType){
		return messageType == ConstantDesigner.MESSAGE_TYPE_SYSTEM; 
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		//获取消息列表
		getMessageBox(0);
	}


	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	}
}
