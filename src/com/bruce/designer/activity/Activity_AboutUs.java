package com.bruce.designer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.constants.Config;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.StringUtils;
import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_AboutUs extends BaseActivity {

	private View titlebarView;
	private TextView titleView;
	private TextView version;
	private ImageView wxmpQrcodeImgView;

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
		titleView.setText("金玩儿网");

		version = (TextView) findViewById(R.id.version);
		version.setText("版本号: " + Config.APP_VERSION_NAME);
		
		wxmpQrcodeImgView = (ImageView) findViewById(R.id.wxmpQrcodeImg);
		
		String wxmpQrcodeUrl = SharedPreferenceUtil.getSharePreStr(context, Config.SP_KEY_WEIXINMP_QRCODE_URL, "");
		if(!StringUtils.isBlank(wxmpQrcodeUrl)){
			ImageLoader.getInstance().displayImage(wxmpQrcodeUrl, wxmpQrcodeImgView, UniversalImageUtil.DEFAULT_ALPHA0_DISPLAY_OPTION); 
		}
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
