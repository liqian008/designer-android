package com.bruce.designer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.util.UiUtil;

public class Activity_Settings extends BaseActivity {

	private View titlebarView;
	private TextView titleView;
	private View aboutUsView;
	private ImageButton btnSettings;
	private Button btnLogout;

	public static void show(Context context) {
		Intent intent = new Intent(context, Activity_Settings.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		initView();
	}

	private void initView() {
		// init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("设置");

		aboutUsView = findViewById(R.id.aboutUs);
		aboutUsView.setOnClickListener(listener);
		
		btnLogout = (Button)findViewById(R.id.logout);
		btnLogout.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.titlebar_return:
				finish();
				break;
			case R.id.btnSettings:
				Activity_Settings.show(context);
				break;
			case R.id.aboutUs:
				Activity_AboutUs.show(context);
				break;
			case R.id.logout:
				AppManager.getInstance().finishAllActivity();
				Activity_Login.show(context);
				UiUtil.showShortToast(context, "注销登录成功");
				break;
			default:
				break;
			}
		}
	};
}
