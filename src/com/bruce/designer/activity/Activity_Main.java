package com.bruce.designer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.util.UiUtil;

public class Activity_Main extends BaseActivity {
	
	private static final int TAB_NUM=4;
	private long lastQuitTime = 0;
	
	private FragmentManager fragmentManager;
	
	private Fragment[] mFragments = new Fragment[TAB_NUM];
	private FragmentTransaction fragmentTransaction;
	
	
	private ImageButton[] footerTabs = new ImageButton[TAB_NUM];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fragmentManager = getSupportFragmentManager();
		
		mFragments[0] = fragmentManager.findFragmentById(R.id.fragment_main);
		mFragments[1] = fragmentManager.findFragmentById(R.id.fragment_myprofile);
        mFragments[2] = fragmentManager.findFragmentById(R.id.fragment_msgbox);
        mFragments[3] = fragmentManager.findFragmentById(R.id.fragment_myprofile);
        
        footerTabs[0] = (ImageButton) findViewById(R.id.btnTabMain);
        footerTabs[1] = (ImageButton) findViewById(R.id.btnTabHotAlbum);
        footerTabs[2] = (ImageButton) findViewById(R.id.btnTabMsgbox);
        footerTabs[3] = (ImageButton) findViewById(R.id.btnTabProfile);
        
        for(int i=0; i<footerTabs.length;i++){
        	footerTabs[i].setOnClickListener(tabOnclickListener);
        }
		highLight(0);
	}

	

	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 退出
			long currentTime = System.currentTimeMillis();
			if (lastQuitTime <= 0 || currentTime - lastQuitTime > 2000) {
				lastQuitTime = System.currentTimeMillis();
				UiUtil.showShortToast(context, "再次点击后即可退出应用");
			} else {
				AppManager.getInstance().exitApp(context);
			}
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
		fragmentTransaction = fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);  
        fragmentTransaction.show(mFragments[index]).commit();
//        mFragments[index].onResume();
	}
	
	

	private OnClickListener tabOnclickListener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch(view.getId()){
				case R.id.btnTabMain:{
					highLight(0);
					break;
				}
				case R.id.btnTabHotAlbum:{
					highLight(1);
					break;
				}
				case R.id.btnTabMsgbox:{
					highLight(2);
					break;
				}
				case R.id.btnTabProfile:{
					highLight(3);
					break;
				}default:{
					break;
				}
			}
		}
	};
	
}
