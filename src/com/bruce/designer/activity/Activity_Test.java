package com.bruce.designer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

import com.bruce.designer.R;
import com.bruce.designer.adapter.GridAdapter;
import com.bruce.designer.util.UiUtil;

public class Activity_Test extends BaseActivity{

	private Context context;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = Activity_Test.this;
//		setContentView(R.layout.activity_test);
		
		GridView gridView = (GridView) findViewById(R.id.grid);
		gridView.setAdapter(new GridAdapter(context));
		
	}


}
