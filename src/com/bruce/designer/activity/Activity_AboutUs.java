package com.bruce.designer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.constants.Config;
import com.bruce.designer.listener.OnSingleClickListener;

public class Activity_AboutUs extends BaseActivity {

	private View titlebarView;
	private TextView titleView;
	private TextView version;

	public static void show(Context context) {
		Intent intent = new Intent(context, Activity_AboutUs.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		initView();
	}

	private void initView() {
		// init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(onclickListener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("关于我们");

		version = (TextView) findViewById(R.id.version);
		version.setText("版本号: " + Config.APP_VERSION_NAME);

	}

	private OnClickListener onclickListener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.titlebar_return:
				finish();
				break;
			default:
				break;
			}
		}
	};
}
