package com.bruce.designer.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.bruce.designer.R;
import com.bruce.designer.activity.BaseActivity;

public class Activity_Login extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ImageButton wbLoginBtn = (ImageButton) findViewById(R.id.wbLoginButton);
		wbLoginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
	}

}
