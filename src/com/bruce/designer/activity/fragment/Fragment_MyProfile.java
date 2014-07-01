package com.bruce.designer.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_UserEdit;

/**
 * 我的个人资料的Fragment
 * @author liqian
 *
 */
public class Fragment_MyProfile extends Fragment {
	
	private Activity context;
	private LayoutInflater inflater;
	
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
		
		
		View snsBtnContainer = (View) mainView.findViewById(R.id.snsBtnContainer);
		snsBtnContainer.setVisibility(View.GONE);
		View editBtnContainer = (View) mainView.findViewById(R.id.editBtnContainer);
		editBtnContainer.setVisibility(View.VISIBLE);
		
		Button btnEditMyInfo = (Button) mainView.findViewById(R.id.btnEditMyInfo);
		btnEditMyInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Activity_UserEdit.show(getActivity());
//				TODO 测试
//				AppManager.getInstance().finishAllActivity();
//				Activity_Login.show(getActivity());
			}
		});
		
		
	}
	
}
