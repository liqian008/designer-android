package com.bruce.designer.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_AboutUs;
import com.bruce.designer.activity.Activity_Settings;
import com.bruce.designer.activity.Activity_UserEdit;
import com.bruce.designer.listener.OnSingleClickListener;

/**
 * 我的个人资料的Fragment
 * @author liqian
 *
 */
public class Fragment_MyProfile extends Fragment {
	
	private Activity context;
	private LayoutInflater inflater;
	
	private ImageButton btnSettings;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		this.inflater = inflater;
		
		View mainView = inflater.inflate(R.layout.activity_user_info, null);
		
		initView(mainView);
		
		return mainView;
	}

	private void initView(View mainView) {

		View titlebarIcon = (View) mainView.findViewById(R.id.titlebar_icon);
		titlebarIcon.setVisibility(View.GONE);
		
		TextView titlebarTitle = (TextView) mainView.findViewById(R.id.titlebar_title);
		titlebarTitle.setText("爬行蜗牛");
		
		//setting按钮及点击事件
		btnSettings = (ImageButton) mainView.findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(listener);
		btnSettings.setVisibility(View.VISIBLE);
		
		
		View snsBtnContainer = (View) mainView.findViewById(R.id.snsBtnContainer);
		snsBtnContainer.setVisibility(View.GONE);
		View editBtnContainer = (View) mainView.findViewById(R.id.editBtnContainer);
		editBtnContainer.setVisibility(View.VISIBLE);
		
		Button btnEditMyInfo = (Button) mainView.findViewById(R.id.btnEditMyInfo);
		btnEditMyInfo.setOnClickListener(listener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	private OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.btnEditMyInfo:
				Activity_UserEdit.show(getActivity());
				break;
			case R.id.btnSettings:
				Activity_Settings.show(context);
				break;
			default:
				break;
			}
		}
	};
	
	
	
}
