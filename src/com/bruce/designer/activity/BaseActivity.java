package com.bruce.designer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.bruce.designer.AppManager;

public class BaseActivity extends FragmentActivity {

	protected Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;
		AppManager.getInstance().addActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().finishActivity(this);
	}

}
