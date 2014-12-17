package com.bruce.designer.activity;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.user.PostPushSettingsApi;
import com.bruce.designer.constants.Config;
import com.bruce.designer.constants.ConstantsStatEvent;
import com.bruce.designer.handler.DesignerHandler;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.DesignerUtil;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.view.SwitcherView;

/**
 * 细力度的pushSettings
 * @author liqian
 *
 */
public class Activity_Settings_Push extends BaseActivity {
	

	protected static final int HANDLER_FLAG_PUSHMASK_READ_RESULT = 0;
	protected static final int HANDLER_FLAG_PUSHMASK_WRITE_RESULT = 1;
	
//	public static final int RESULT_CODE_SETTINGS_PUSH = 10;
	
	private static final int followedSettings = 1;//被关注时的推送flag
	private static final int commentedSettings = 2;//被评论时的推送flag
	private static final int likedSettings = 4;//被赞时的推送flag
	private static final int favoritedSettings = 8;//被收藏时的推送flag
	private static final int chatedSettings = 16;//私信消息的推送flag
	
	
	private long cachedPushMask = Long.MAX_VALUE;

	
	private View titlebarView;
	private TextView titleView;
	private SwitcherView pushFollowedSwitcher, pushChatedSwitcher, pushLikedSwitcher, pushFavoritedSwitcher, pushCommentedSwitcher;
	
	private Handler handler;
	private OnClickListener onClickListener;
	
	public static void show(Context context) {
		if(!AppApplication.isGuest()){//游客身份不能操作
			Intent intent = new Intent(context, Activity_Settings_Push.class);
			context.startActivity(intent);
		}else{
			DesignerUtil.guideGuestLogin(context, "提示", "游客身份无法进行推送设置，请先登录");
		}
	}
	
	public Handler initHandler(){
		Handler handler = new DesignerHandler(Activity_Settings_Push.this){
			@SuppressWarnings("unchecked")
			public void processHandlerMessage(Message msg) {
				ApiResult apiResult = (ApiResult) msg.obj;
				boolean successResult = (apiResult!=null&&apiResult.getResult()==1);
				
				switch (msg.what) {
				case HANDLER_FLAG_PUSHMASK_READ_RESULT:
					if(successResult){
						Map<String, Object> pushSettingsDataMap = (Map<String, Object>) apiResult.getData();
						Long pushMask = (Long) pushSettingsDataMap.get("pushMask");
						initPushSettings(pushMask==null?0:pushMask);
						SharedPreferenceUtil.putSharePre(context,  Config.SP_KEY_BAIDU_PUSH_MASK, (pushMask==null?0:pushMask));
					}else{
						UiUtil.showShortToast(context, "获取推送设置失败，请重试");
					}
					break;
				case HANDLER_FLAG_PUSHMASK_WRITE_RESULT:
					//do nothing
					break;
				default:
					break;
				}
			}
		};
		return handler;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_push);
		handler = initHandler();
		onClickListener = initListener();

		initView();
		
		//进入时记录pushMask
		cachedPushMask = SharedPreferenceUtil.getSharePreLong(context, Config.SP_KEY_BAIDU_PUSH_MASK, Long.MAX_VALUE);
		//启动线程获取server端的pushSettings
		loadPushSettings();
	}

	

	private void initView() {
		// init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(onClickListener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("推送设置");

		//初始化push的选项值
		final long cachedPushMask = SharedPreferenceUtil.getSharePreLong(context, Config.SP_KEY_BAIDU_PUSH_MASK, 31L);
		
		//被关注通知开关
		pushFollowedSwitcher = (SwitcherView)findViewById(R.id.push_followed_switcher);
		//被评论通知开关
		pushCommentedSwitcher = (SwitcherView)findViewById(R.id.push_commented_switcher);
		//被收藏通知开关
		pushFavoritedSwitcher = (SwitcherView)findViewById(R.id.push_liked_switcher);
		//被赞通知开关
		pushLikedSwitcher = (SwitcherView)findViewById(R.id.push_favorited_switcher);
		//私信通知开关
		pushChatedSwitcher = (SwitcherView)findViewById(R.id.push_chated_switcher);
		
		initPushSettings(cachedPushMask);
		
		pushChatedSwitcher.OnChangedListener(new SwitcherView.OnChangedListener() {
			@Override
			public void OnChanged(boolean checkState) {
				StatService.onEvent(context, ConstantsStatEvent.EVENT_UPDATE_PUSH_SETTING, "私信消息"+checkState);
				
				long cachedPushMask = SharedPreferenceUtil.getSharePreLong(context,  Config.SP_KEY_BAIDU_PUSH_MASK, Long.MAX_VALUE);
				long newPushMask = (cachedPushMask | chatedSettings);//默认处理为打开
				if (!checkState) {
					newPushMask = (cachedPushMask - chatedSettings);
				}
				savePushMask(newPushMask);
			}
		});
		
		pushFollowedSwitcher.OnChangedListener(new SwitcherView.OnChangedListener() {
			@Override
			public void OnChanged(boolean checkState) {
				StatService.onEvent(context, ConstantsStatEvent.EVENT_UPDATE_PUSH_SETTING, "关注消息"+checkState);
				
				long cachedPushMask = SharedPreferenceUtil.getSharePreLong(context,  Config.SP_KEY_BAIDU_PUSH_MASK, Long.MAX_VALUE);
				long newPushMask = (cachedPushMask | followedSettings);//默认处理为打开
				if (!checkState) {
					newPushMask = (cachedPushMask - followedSettings);
				}
				savePushMask(newPushMask);
			}
		});
		
		pushFavoritedSwitcher.OnChangedListener(new SwitcherView.OnChangedListener() {
			@Override
			public void OnChanged(boolean checkState) {
				StatService.onEvent(context, ConstantsStatEvent.EVENT_UPDATE_PUSH_SETTING, "收藏消息"+checkState);
				
				long cachedPushMask = SharedPreferenceUtil.getSharePreLong(context,  Config.SP_KEY_BAIDU_PUSH_MASK, Long.MAX_VALUE);
				long newPushMask = (cachedPushMask | favoritedSettings);//默认处理为打开
				if (!checkState) {
					newPushMask = (cachedPushMask - favoritedSettings);
				}
				savePushMask(newPushMask);
			}
		});
		
		pushLikedSwitcher.OnChangedListener(new SwitcherView.OnChangedListener() {
			@Override
			public void OnChanged(boolean checkState) {
				StatService.onEvent(context, ConstantsStatEvent.EVENT_UPDATE_PUSH_SETTING, "赞消息"+checkState);
				
				long cachedPushMask = SharedPreferenceUtil.getSharePreLong(context,  Config.SP_KEY_BAIDU_PUSH_MASK, Long.MAX_VALUE);
				long newPushMask = (cachedPushMask | likedSettings);//默认处理为打开
				if (!checkState) {
					newPushMask = (cachedPushMask - likedSettings);
				}
				savePushMask(newPushMask);
			}
		});
		
		pushCommentedSwitcher.OnChangedListener(new SwitcherView.OnChangedListener() {
			@Override
			public void OnChanged(boolean checkState) {
				StatService.onEvent(context, ConstantsStatEvent.EVENT_UPDATE_PUSH_SETTING, "评论消息"+checkState);
				
				long cachedPushMask = SharedPreferenceUtil.getSharePreLong(context,  Config.SP_KEY_BAIDU_PUSH_MASK, Long.MAX_VALUE);
				long newPushMask = (cachedPushMask | commentedSettings);//默认处理为打开
				if (!checkState) {
					newPushMask = (cachedPushMask - commentedSettings);
				}
				savePushMask(newPushMask);
			}
		});
	}

	private OnClickListener  initListener(){
		OnClickListener listener = new OnSingleClickListener() {
			@Override
			public void onSingleClick(View view) {
				switch (view.getId()) {
				case R.id.titlebar_return:
					processBeforeFinish();
					finish();
					break;
				default:
					break;
				}
			}
		};
		return listener;
	}
	
	/**
	 * 加载push设置
	 * @param pushMask
	 */
	private void initPushSettings(long pushMask) {
		//被关注
		long followedFlag = pushMask & followedSettings;
		pushFollowedSwitcher.setCheckState(followedFlag ==followedSettings);
		
		//被评论
		long commentedFlag = pushMask & commentedSettings;
		pushCommentedSwitcher.setCheckState(commentedFlag ==commentedSettings);
		
		//被收藏
		long favoritedFlag = pushMask & favoritedSettings;
		pushFavoritedSwitcher.setCheckState(favoritedFlag == favoritedSettings);
		
		//被赞
		long likedFlag = pushMask & likedSettings;
		pushLikedSwitcher.setCheckState(likedFlag == likedSettings);
				
		//私信
		long chatedFlag = pushMask & chatedSettings;
		pushChatedSwitcher.setCheckState(chatedFlag == chatedSettings);
	}
	
	private void loadPushSettings() {
		// 启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				PostPushSettingsApi api = new PostPushSettingsApi(0, Long.MAX_VALUE);
				ApiResult apiResult = ApiManager.invoke(context, api);
				message = handler.obtainMessage(HANDLER_FLAG_PUSHMASK_READ_RESULT);
				message.obj = apiResult;
				message.sendToTarget();
				
				
//				if (apiResult != null && apiResult.getResult() == 1) {
//					message = handler.obtainMessage(HANDLER_FLAG_PUSHMASK_READ_RESULT);
//					message.obj = apiResult.getData();
//					System.err.println("====apiResult.getData()"+apiResult.getData());
//					message.sendToTarget();
//				}
			}
		});
		thread.start();
	}
	
	
	private void updatePushSettings(final long pushMask) {
		// 启动线程更新push设置
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				PostPushSettingsApi api = new PostPushSettingsApi(1, pushMask);
				ApiManager.invoke(context, api);
			}
		});
		thread.start();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 退出
			processBeforeFinish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void processBeforeFinish() {
		long latestPushMask = SharedPreferenceUtil.getSharePreLong(context, Config.SP_KEY_BAIDU_PUSH_MASK , Long.MAX_VALUE);
		
		if(cachedPushMask!=latestPushMask){//进入与返回时两次push设置不一致，说明有修改
			updatePushSettings(latestPushMask);
//			if(cachedPushMask==0 &&latestPushMask>0 ){//在push设置中开启了push
//				PushManager.resumeWork(context);
//			}
//			if(cachedPushMask!=0&&latestPushMask<=0 ){//在push设中全部关闭了
//				PushManager.stopWork(context);
//			}
		}
	}
	
	public void savePushMask(long pushMask){
		if(pushMask<0){
			pushMask = Long.MAX_VALUE;
		}
		SharedPreferenceUtil.putSharePre(context, Config.SP_KEY_BAIDU_PUSH_MASK, pushMask);
	}
}
