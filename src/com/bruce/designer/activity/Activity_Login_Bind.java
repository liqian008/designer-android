package com.bruce.designer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bruce.designer.R;

public class Activity_Login_Bind extends BaseActivity{

	public static void show(Context context){
		Intent intent = new Intent(context, Activity_Login_Bind.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = Activity_Login_Bind.this;
		setContentView(R.layout.activity_login_bind);
	}
    
}
