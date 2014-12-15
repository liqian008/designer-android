package com.bruce.designer.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.baidu.mobstat.StatService;

public class BaseFragment extends Fragment{
	
	protected Activity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this); 
	}
	
	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this); 
	}
}
