package com.bruce.designer.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
import com.bruce.designer.AppApplication;
import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.constants.Config;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.view.SwitcherView;
import com.bruce.designer.view.SwitcherView.OnChangedListener;

public class Activity_Settings extends BaseActivity {

	private View titlebarView;
	private TextView titleView;
	private View aboutUsView;
	private View clearCacheView;
	private View websiteView;
	private SwitcherView pushSwitcher;

	private Button btnLogout;

	public static void show(Context context) {
		Intent intent = new Intent(context, Activity_Settings.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		initView();
	}

	private void initView() {
		// init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("设置");

		aboutUsView = findViewById(R.id.aboutUs);
		aboutUsView.setOnClickListener(listener);

		clearCacheView = findViewById(R.id.clearCache);
		clearCacheView.setOnClickListener(listener);
		
		websiteView = findViewById(R.id.websiteView);
		websiteView.setOnClickListener(listener);

		btnLogout = (Button) findViewById(R.id.logout);
		btnLogout.setOnClickListener(listener);
		
		pushSwitcher = (SwitcherView)findViewById(R.id.push_settings_switcher);
		//初始化push的选项值
		boolean pushOn = SharedPreferenceUtil.getSharePreBoolean(context, Config.SP_KEY_BAIDU_PUSH, false);
		pushSwitcher.setCheckState(pushOn);
		
		pushSwitcher.OnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(boolean checkState) {
				if (checkState) {
					SharedPreferenceUtil.putSharePre(context,  Config.SP_KEY_BAIDU_PUSH , true);
					UiUtil.showShortToast(context,"消息推送开启");
					PushManager.resumeWork(context);
				} else {
					SharedPreferenceUtil.putSharePre(context,  Config.SP_KEY_BAIDU_PUSH , false);
					PushManager.stopWork(context);
					UiUtil.showShortToast(context, "消息推送关闭");
				}
				StatService.onEvent(context, "设置消息推送", "新状态: "+checkState);
			}
		});
	}

	

	/**
	 * 创建一个包含自定义view的PopupWindow
	 * 
	 * @param context
	 * @return
	 */
	private PopupWindow makePopupWindow(Context context) {
		PopupWindow popWindow = new PopupWindow(context);
		View contentView = LayoutInflater.from(this).inflate(R.layout.popup_window, null);
		popWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		//以下两行实现点击back按钮消失
		ColorDrawable dw = new ColorDrawable(-00000);
		popWindow.setBackgroundDrawable(dw);
		
		// 设置PopupWindow外部区域是否可触摸
		popWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		popWindow.setTouchable(true); // 设置PopupWindow可触摸
		popWindow.setOutsideTouchable(true);// 设置非PopupWindow区域可触摸
		return popWindow;
	}
	
	
	private OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {

			switch (view.getId()) {
			case R.id.titlebar_return:
				finish();
				break;
			case R.id.btnSettings:
				Activity_Settings.show(context);
				break;
			case R.id.aboutUs:
				Activity_AboutUs.show(context);
				break;
			case R.id.clearCache:
				// 弹起popWindow
				// 新建一个poppopWindow，并显示里面的内容
//				PopupWindow popupWindow = makePopupWindow(context);
//				//指定显示位置
//				popupWindow.showAsDropDown(clearCacheView);
				
				AlertDialog clearDialog = UiUtil.showAlertDialog(context, "清除缓存", "根据缓存文件大小，清理时间从几秒到几分钟不等，请耐心等待", "清理", null, "取消", null);
				clearDialog.show();
				break;
			case R.id.websiteView:
				//启动web浏览器访问主页
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(Config.JINWAN_WEB_DOMAIN);
				intent.setData(content_url);  
				startActivity(intent);
				break;
			case R.id.logout:
				//TODO 发起线程禁用pushToken
				PushManager.stopWork(context);
				
				//清除本机的登录信息
				AppApplication.clearAccount();
				AppManager.getInstance().finishAllActivity();
				Activity_Login.show(context);
				UiUtil.showShortToast(context, "注销登录成功");
				break;
			default:
				break;
			}
		}
	};
	
	
	
	
}
