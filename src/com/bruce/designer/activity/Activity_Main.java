package com.bruce.designer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.activity.fragment.Fragment_Msgbox.MessageListener;
import com.bruce.designer.constants.Config;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.util.ApplicationUtil;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.UiUtil;

public class Activity_Main extends BaseActivity implements MessageListener{
	
	private static final int TAB_NUM = 4;
	private long lastQuitTime = 0;
	private int currentTabIndex = 0 ;
	
	private FragmentManager fragmentManager;
	
	private Fragment[] mFragments = new Fragment[TAB_NUM];
	private FragmentTransaction fragmentTransaction;
	
	private ImageButton[] footerTabs = new ImageButton[TAB_NUM];
	
	private ImageView unreadMsgIndicator;
	
	
	public static void show(Context context){
		Intent intent = new Intent(context, Activity_Main.class);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//初始化baiduPush
		initBaiduPush(context);
		
		unreadMsgIndicator = (ImageView)findViewById(R.id.unreadMsgIndicator);
		
		fragmentManager = getSupportFragmentManager();
        mFragments[0] = fragmentManager.findFragmentById(R.id.fragment_main);
		mFragments[1] = fragmentManager.findFragmentById(R.id.fragment_hot_albums);
        mFragments[2] = fragmentManager.findFragmentById(R.id.fragment_msgbox);
        mFragments[3] = fragmentManager.findFragmentById(R.id.fragment_myhome);
        
        footerTabs[0] = (ImageButton) findViewById(R.id.btnTabMain);
        footerTabs[1] = (ImageButton) findViewById(R.id.btnTabHotAlbums);
        footerTabs[2] = (ImageButton) findViewById(R.id.btnTabMsgbox);
        footerTabs[3] = (ImageButton) findViewById(R.id.btnTabHome);
        
        for(int i=0; i<footerTabs.length;i++){
        	footerTabs[i].setOnClickListener(tabOnclickListener);
        }
		highLight(0);
	}

	

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 退出
			if(currentTabIndex!=0){//需要先将焦点回到tab0
				highLight(0);
			}else{
				long currentTime = System.currentTimeMillis();
				if (lastQuitTime <= 0 || currentTime - lastQuitTime > 2000) {
					lastQuitTime = System.currentTimeMillis();
					
					UiUtil.showShortToast(context, "再次点击后即可退出应用");
				} else {
					AppManager.getInstance().exitApp(context);
				}
			}
		}else if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 物理菜单键进入设置界面
			Intent intent = new Intent(context, Activity_Settings.class);
			startActivity(intent);
		} else {
			flag = super.onKeyUp(keyCode, event);
		}
		return flag;
	}

	
	
	
	/**
	 * highLight
	 * @param index
	 */
	private void highLight(int index) {
		if(index>=TAB_NUM||index<0){
			index = 0;
		}
		currentTabIndex = index;
		for(ImageButton tab:footerTabs){
			tab.setBackgroundResource(R.color.tab_normal_color);
		}
		footerTabs[index].setBackgroundResource(R.color.tab_active_color);
		showFragment(index);
	}
	
	/**
	 * 显示fragment
	 * @param index
	 */
	private void showFragment(int index) {
		fragmentTransaction = fragmentManager.beginTransaction();
		for(int i=0;i<TAB_NUM;i++){
			fragmentTransaction.hide(mFragments[i]);
		}
		fragmentTransaction.show(mFragments[index]).commit();
        mFragments[index].onResume();
	}
	
	

	private OnClickListener tabOnclickListener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch(view.getId()){
				case R.id.btnTabMain:{
					highLight(0);
					break;
				}
				case R.id.btnTabHotAlbums:{
					highLight(1);
					break;
				}
//				case R.id.btnTabHotDesigners:{
//					highLight(2);
//					break;
//				}
				case R.id.btnTabMsgbox:{
					highLight(2);
					break;
				}
				case R.id.btnTabHome:{
					highLight(3);
					break;
				}default:{
					break;
				}
			}
		}
	};
	
	/**
	 * 初始化百度push
	 * @param context
	 */
	private void initBaiduPush(Context context) {
//		PushSettings.enableDebugMode(context, true);
		PushManager.startWork(context,  PushConstants.LOGIN_TYPE_API_KEY, ApplicationUtil.getMetaValue(context, "baidu_push_api_key"));
		//读取push的settings
		long pushMask = SharedPreferenceUtil.getSharePreLong(context, Config.SP_KEY_BAIDU_PUSH_MASK , Long.MAX_VALUE);
		if(pushMask==0l){//用户关闭push，则关闭push
			PushManager.stopWork(context);
		}
	}

	
	/**
	 * 处理有未读消息时
	 */
	@Override
	public void unreadMsgNotify() {
		if(unreadMsgIndicator!=null){
			unreadMsgIndicator.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void unreadMsgClear() {
		if(unreadMsgIndicator!=null){
			unreadMsgIndicator.setVisibility(View.GONE);
		}
	}
	
}
