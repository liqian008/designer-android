package com.bruce.designer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bruce.designer.R;

public class Activity_Settings extends BaseActivity {
	
	private View titlebarView;
	private TextView titleView;
	
	
	public static void show(Context context){
		Intent intent = new Intent(context, Activity_Settings.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("设置");
		
	}
}
