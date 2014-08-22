package com.bruce.designer.view;

import android.app.Dialog;
import android.content.Context;

/**
 * http://www.open-open.com/lib/view/open1335576985577.html
 * @author liqian
 *
 */
public class CustomizeProgressDialog extends Dialog {

	private CustomizeProgressDialog customizeProgressDialog;

	public CustomizeProgressDialog(Context context) {
		super(context);
	}

	public CustomizeProgressDialog(Context context, int theme) {
		super(context, theme);
	}

//	public CustomizeProgressDialog createDialog(Context context) {
//		customizeProgressDialog = new CustomizeProgressDialog(context);
//		 customizeProgressDialog.setContentView(R.layout.customize_progress_dialog);
//		customizeProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
//
//		return customizeProgressDialog;
//	}
//
//	public CustomizeProgressDialog setTitile(String strTitle) {
//		return customizeProgressDialog;
//	}
//
//	public CustomizeProgressDialog setMessage(String strMessage) {
//		TextView tvMsg = (TextView) customizeProgressDialog.findViewById(R.id.id_tv_loadingmsg);
//		if (tvMsg != null) {
//			tvMsg.setText(strMessage);
//		}
//		return customizeProgressDialog;
//	}

}
