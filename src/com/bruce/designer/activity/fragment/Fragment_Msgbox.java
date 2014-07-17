package com.bruce.designer.activity.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bruce.designer.activity.Activity_Msgbox;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.user.UserFansApi;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.UserFan;
import com.bruce.designer.model.result.ApiResult;

/**
 * 我的个人资料的Fragment
 * @author liqian
 *
 */
public class Fragment_Msgbox extends Fragment {
	
	private View titlebarView;

	private TextView titleView;
	
	private FansListAdapter fansListAdapter;
	
	private Activity context;
	
	private LayoutInflater inflater;
	
	private int userId = 100012;
	
	private int[] msgAvatarIds = new int[]{R.drawable.icon_msgbox_at,R.drawable.icon_msgbox_comment,R.drawable.icon_msgbox_sys,R.drawable.icon_msgbox_like};
	
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
		titleView.setText("我的消息");
		
		ListView msgboxView = (ListView)mainView.findViewById(R.id.msgBox);
		fansListAdapter = new FansListAdapter(context, null);
		msgboxView.setAdapter(fansListAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		//获取关注列表
		getFans(0);
		//TODO 需要增加下拉刷新
	}
	
	class FansListAdapter extends BaseAdapter {

		private List<UserFan> fanUserList;
		private Context context;
		
		public FansListAdapter(Context context, List<UserFan> fanUserList) {
			this.context = context;
			this.fanUserList = fanUserList;
		}
		
		public List<UserFan> getFanUserList() {
			return fanUserList;
		}

		public void setFanUserList(List<UserFan> fanUserList) {
			this.fanUserList = fanUserList;
		}

		@Override
		public int getCount() {
			if (fanUserList != null) {
				return fanUserList.size();
			}
			return 0;
		}

		@Override
		public UserFan getItem(int position) {
			if (fanUserList != null) {
				return fanUserList.get(position);
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
			if(getItem(position)!=null){
				final UserFan user = getItem(position);
				View itemView = LayoutInflater.from(context).inflate(R.layout.item_msgbox_view, null);
				
				itemView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						Activity_Msgbox.show(context);
					}
				});
				
				
//				ImageView msgAvatrView = (ImageView) itemView.findViewById(R.id.msgAvatar);
//				Random random = new Random(System.currentTimeMillis());
//				int randomResIndex = random.nextInt(msgAvatarIds.length);
//				msgAvatrView.setBackgroundResource(msgAvatarIds[randomResIndex]);
				
//				TextView msgTitleView = (TextView) itemView.findViewById(R.id.msgTitle);
//				msgTitleView.setText(user.getFanUser().getNickname());
//				
//				TextView msgContentView = (TextView) itemView.findViewById(R.id.msgContent);
//				msgContentView.setText(user.getFanUser().getNickname());
				
				return itemView;
			}
			return null;
		}
	}
	
	/**
	 * 获取关注列表
	 * @param fansTailId
	 */
	private void getFans(final int fansTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				UserFansApi api = new UserFansApi(userId);
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
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 0:
					Map<String, Object> userFansDataMap = (Map<String, Object>) msg.obj;
					if(userFansDataMap!=null){
//						List<UserFan> fanList = (List<UserFan>)  userFansDataMap.get("fanList");
//						if(fanList!=null&&fanList.size()>0){
//							fansListAdapter.setFanUserList(fanList);
//							fansListAdapter.notifyDataSetChanged();
//						}
					
						List<UserFan> fanList = new ArrayList<UserFan>();
						for(int i=0;i<10;i++){
							UserFan userFan = new UserFan();
							fanList.add(userFan);
						}
						fansListAdapter.setFanUserList(fanList);
						fansListAdapter.notifyDataSetChanged();
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
	
}
