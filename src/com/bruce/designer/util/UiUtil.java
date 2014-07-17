package com.bruce.designer.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UiUtil {

	/**
	 * short toast
	 * 
	 * @param context
	 * @param text
	 */
	public static void showShortToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * long toast
	 * 
	 * @param context
	 * @param text
	 */
	public static void showLongToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	
	public static AlertDialog showAlertDialog(Context context, String title,
			String message, String negativeBtnText,
			DialogInterface.OnClickListener negativeBtnListener, String positiveBtnText,
			DialogInterface.OnClickListener positiveBtnListener) {
		return showAlertDialog(context, false, null, title, message, negativeBtnText, negativeBtnListener, positiveBtnText, positiveBtnListener);
	}
	
	/**
	 * 
	 * @param context
	 * @param cacelable
	 * @param view
	 * @param title
	 * @param message
	 * @param negativeBtnText
	 * @param negativeBtnListener
	 * @param positiveBtnText
	 * @param positiveBtnListener
	 * @return
	 */
	public static AlertDialog showAlertDialog(Context context, boolean cacelable, View view, String title,
			String message, String negativeBtnText,
			DialogInterface.OnClickListener negativeBtnListener, String positiveBtnText,
			DialogInterface.OnClickListener positiveBtnListener) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setCancelable(cacelable);
		if(view!=null&&view.getParent()==null){//添加view
			builder.setView(view);
		}
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(cacelable);
		builder.setNegativeButton(negativeBtnText, negativeBtnListener);
		builder.setPositiveButton(positiveBtnText, positiveBtnListener);
		AlertDialog dialog = builder.create();
		return dialog;
	}
	
	
	
	
	

	/**
	 * 重新计算listView的高度，以解决list存在于scrollView下的bug
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}
