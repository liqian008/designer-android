package com.bruce.designer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Window;

import com.baidu.mobstat.StatService;
import com.bruce.designer.AppManager;

public class BaseActivity extends FragmentActivity {

	protected Context context;
	protected LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;
		inflater = LayoutInflater.from(this);    
		
		AppManager.getInstance().addActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(context);	
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(context);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().finishActivity(this);
	}
	

}
