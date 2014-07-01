package com.bruce.designer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.designer.R;

public class Activity_UserEdit extends BaseActivity {
	
	private View titlebarView;

	private TextView titleView;
	/*我的头像*/
	private ImageView avatarView;
	
	private int userId;

	
	public static void show(Context context){
		Intent intent = new Intent(context, Activity_UserEdit.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("我");
		
	}
	
	
	
}
