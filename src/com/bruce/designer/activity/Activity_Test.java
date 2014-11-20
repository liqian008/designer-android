package com.bruce.designer.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.GridView;

import com.bruce.designer.R;
import com.bruce.designer.adapter.GridAdapter;

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
