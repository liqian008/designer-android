package com.bruce.designer.crop;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bruce.designer.R;

public class ModifyAvatarDialog extends Dialog implements OnClickListener {

	private LayoutInflater inflater;

	private Button mAlbum;

	private Button mCamera;

	private Button mCancel;

	public ModifyAvatarDialog(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
	}

	public ModifyAvatarDialog(Context context, int theme) {
		super(context, theme);
		inflater = LayoutInflater.from(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(inflater.inflate(
				R.layout.avatar_modify_choose_dialog, null));
		mAlbum = (Button) this.findViewById(R.id.gl_choose_album);
		mCamera = (Button) this.findViewById(R.id.gl_choose_camera);
		mCancel = (Button) this.findViewById(R.id.gl_choose_cancel);
		mAlbum.setOnClickListener(this);
		mCamera.setOnClickListener(this);
		mCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gl_choose_album:
			doGoToImg();
			break;
		case R.id.gl_choose_camera:
			doGoToPhone();
			break;
		case R.id.gl_choose_cancel:
			dismiss();
			break;
		}
	}

	public void doGoToImg() {
	}

	public void doGoToPhone() {
	}
}
