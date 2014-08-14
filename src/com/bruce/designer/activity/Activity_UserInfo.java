package com.bruce.designer.activity;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.user.UserInfoApi;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.User;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_UserInfo extends BaseActivity {
	
	private static final int HANDLER_FLAG_USERINFO = 1;
	
	private View titlebarView;

	private TextView titleView;
	/*我的头像*/
	private ImageView avatarView;
	private TextView nicknameView;
	private TextView emailTextView;
	private TextView shopTextView;
	private TextView introduceTextView;
	
	private int queryUserId;

	
	public static void show(Context context, int queryUserId){
		Intent intent = new Intent(context, Activity_UserInfo.class);
		intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_ID, queryUserId);
		context.startActivity(intent);
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
				case HANDLER_FLAG_USERINFO:
					Map<String, Object> userinfoDataMap = (Map<String, Object>) msg.obj;
					if(userinfoDataMap!=null){
						User userinfo = (User) userinfoDataMap.get("userinfo");
						if(userinfo!=null&&userinfo.getId()>0){
							//刷新用户资料
							if(userinfo.getHeadImg()!=null){
								ImageLoader.getInstance().displayImage(userinfo.getHeadImg(), avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
							} 
							nicknameView.setText(userinfo.getNickname());
							emailTextView.setText(userinfo.getUsername());
							introduceTextView.setText(userinfo.getDesignerIntroduction());
						}
					}
					break;
				default: 
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		Intent intent = getIntent();
		queryUserId = intent.getIntExtra(ConstantsKey.BUNDLE_USER_INFO_ID, 0);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("个人资料");
		
		avatarView = (ImageView) findViewById(R.id.avatar);
		nicknameView = (TextView) findViewById(R.id.nickNameTextView);
		emailTextView = (TextView) findViewById(R.id.emailTextView);
		shopTextView = (TextView) findViewById(R.id.shopTextView);
		introduceTextView = (TextView) findViewById(R.id.introduceTextView);
		
		//启动线程获取个人资料
		getUserinfo(queryUserId);
	}
	
	
	private OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			finish();
		}
	};
	
	
	private void getUserinfo(final int userId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				
				UserInfoApi api = new UserInfoApi(userId);
				ApiResult apiResult = ApiManager.invoke(context, api);
				
				if(apiResult!=null&&apiResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_USERINFO);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
}