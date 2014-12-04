package com.bruce.designer.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.system.SystemCheckApi;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.User;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.VersionCheckResult;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.StringUtils;
import com.bruce.designer.util.UiUtil;

public class Activity_Splash extends BaseActivity {
	
	/* 检查更新 */
	private static final int CHECK_UPDATE = 0;
	/* 有新下载 */
	private static final int DOWNLOADING = 1;
	/* 下载完成 */
	private static final int DOWNLOAD_OVER = 2;
	
	private Dialog downloadPromptDialog;
	private Dialog downloadingDialog;

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;
	/* 下载进度 */
	private int progress;
	/* 是否中断 */
	private boolean interceptFlag = false;
	/* 下载线程 */
	private Thread downLoadThread;
	
	private int updateStatus = 0;
	/*需要跳转至登录界面的标志*/
	private boolean needLogin = true; 
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_UPDATE:
				Map<String, Object> dataMap = (Map<String, Object>) msg.obj;
				//判断是否需要用户登录
				needLogin = (Boolean) dataMap.get("needLogin");
				
				
				VersionCheckResult versionCheckResult = (VersionCheckResult) dataMap.get("versionCheckResult");
				
//				versionCheckResult = new VersionCheckResult();
//				versionCheckResult.setUpdateStatus(1);
//				versionCheckResult.setUpdateTitle("title");
//				versionCheckResult.setUpdateRemark("content");
//				versionCheckResult.setUpdateUrl("http://gdown.baidu.com/data/wisegame/aaded26929762c22/WeChat_462.apk");
				
				if(versionCheckResult!=null){
					updateStatus = versionCheckResult.getUpdateStatus();
					processUpdateResult(versionCheckResult);
				}
				break;
			case DOWNLOADING:
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_OVER:
				String apkFilePath = (String) msg.obj;
				installApk(apkFilePath);
				break;
			default:
				//出错了
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.context = Activity_Splash.this;
		setContentView(R.layout.activity_splash);
		
		boolean appFirstOpen = SharedPreferenceUtil.getSharePreBoolean(context, Config.SP_KEY_APP_FIRST_OPEN, true);
		//第一次打开需要创建快捷方式
		if(appFirstOpen){
			createDesktopShortCut();
			SharedPreferenceUtil.putSharePre(context, Config.SP_KEY_APP_FIRST_OPEN, false);
		}

		//启动线程
		 Thread thread = new Thread(new Runnable() {
			 private boolean testApi = false;

			@Override
			 public void run() {
				 if(testApi){
					//测试mcap api
					 ApiResult apiResult = ApiManager.invoke(context, new SystemCheckApi());
				 }else{
					 //TODO 检查客户端版本
					 try {
						Thread.sleep(1000);
						ApiResult jsonResult = ApiManager.invoke(context, new SystemCheckApi());
						if (jsonResult != null && jsonResult.getResult() == 1) {
							Message message = handler.obtainMessage(CHECK_UPDATE);
							message.obj = jsonResult.getData();
							message.sendToTarget();
						}
					 } catch (Exception e) {
						 e.printStackTrace();
					 }
				 }
			 }
		 });
		 thread.start();
		 
		
	}

	/**
	 * 处理检查更新的结果，决定是否构造下载的提示框
	 * @param versionCheckResult
	 */
	private void processUpdateResult(VersionCheckResult versionCheckResult) {
		int updateStatus = versionCheckResult.getUpdateStatus();//0无更新/1为建议更新/2为强制更新
		String title = versionCheckResult.getUpdateTitle();
		String message = versionCheckResult.getUpdateRemark();
		String apkUrl = versionCheckResult.getUpdateUrl();
		
		String agreeText = versionCheckResult.getAgreeText();
		agreeText = StringUtils.isBlank(agreeText)?"立刻体验":agreeText;
		String deniedText = versionCheckResult.getDeniedText();
		deniedText = StringUtils.isBlank(deniedText)?"下次再说":deniedText;
		
		if (updateStatus == 1) {
			downloadPromptDialog = UiUtil.showAlertDialog(context, title, message, agreeText, new DownloadListener(apkUrl), deniedText, ignoreListener);
		}else if (updateStatus == 2) {
			downloadPromptDialog = UiUtil.showAlertDialog(context, title, message, agreeText, new DownloadListener(apkUrl), deniedText, quitListener);
		}else{
			//无更新
			jumpToNextActivity();
			return;
		}
		if(downloadPromptDialog!=null){
			downloadPromptDialog.show();
		}
	}

	/**
	 * 客户端下载中的展示框
	 * 
	 * @param apkUrl
	 */
	private void showDownloadingDialog(String apkUrl) {
		View downloadingView = inflater.inflate(R.layout.download_progress, null);
		mProgress = (ProgressBar) downloadingView.findViewById(R.id.progress);
		downloadingDialog = UiUtil.showAlertDialog(context, false, downloadingView, "客户端版本更新", null, "取消", quitDownloadListener, null, null);
		downloadingDialog.show();
		//开启下载线程
		downloadApk(apkUrl);
	}

	class DownloadListener implements  DialogInterface.OnClickListener{
		
		private String apkUrl;

		public DownloadListener(String apkUrl){
			this.apkUrl = apkUrl;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int arg1) {
			dialog.dismiss();
			showDownloadingDialog(apkUrl);
		}
		
	}
	
	/**
	 * 点击下次再说的listener
	 */
	private DialogInterface.OnClickListener ignoreListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			jumpToNextActivity();
		}
	};
	
	/**
	 * 退出的listener
	 */
	private DialogInterface.OnClickListener quitListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			AppManager.getInstance().exitApp(context);
		}
	};
	
	/**
	 * 取消下载的listener
	 */
	private DialogInterface.OnClickListener quitDownloadListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			interceptFlag = true;
			
			if(updateStatus==1){//建议更新策略可以取消下载
				jumpToNextActivity();
			}else if(updateStatus==2){//强制更新策略，取消下载则直接退出客户端
				AppManager.getInstance().exitApp(context);
			}
		}
	};
	
	/**
	 * 下载线程
	 */
	private class DownloadRunnable implements Runnable {
		private String downloadUrl;
		
		public DownloadRunnable(String downloadUrl) {
			this.downloadUrl = downloadUrl;
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(downloadUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				File downloadDir = context.getExternalFilesDir("/newVersion");
				if (!downloadDir.exists()) {
					downloadDir.mkdir();
				}
				String apkFileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
				File apkFile = new File(downloadDir, apkFileName);
				FileOutputStream fos = new FileOutputStream(apkFile);

				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					handler.sendEmptyMessage(DOWNLOADING);
					if (numread <= 0) {
						String apkFilePath = apkFile.getAbsolutePath();
						Message message = handler.obtainMessage(DOWNLOAD_OVER);
						message.obj = apkFilePath;
						// 下载完成通知安装
						message.sendToTarget();
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 下载apk
	 * 
	 * @param apkUrl
	 * @param url
	 */
	private void downloadApk(String apkUrl) {
		downLoadThread = new Thread(new DownloadRunnable(apkUrl));
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param apkFilePath
	 */
	private void installApk(String apkFilePath) {
		File apkfile = new File(apkFilePath);
		if (!apkfile.exists()) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
		finish();
	}
	
	/**
	 * 根据登录状态跳转至下一activity
	 * 未登录需要跳到登录页，已登录则需要跳转到首页
	 */
	public void jumpToNextActivity(){
		if(needLogin){
			Activity_Login.show(context);
			finish();
		}else{
			Activity_Main.show(context);
			finish();
		}
	}
	
	
	/**
	 * 创建快捷方式
	 */
	private void createDesktopShortCut() {
		// 创建快捷方式的Intent
		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutIntent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));

		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		Intent intent = new Intent(getApplicationContext(), Activity_Splash.class);

		// 下面两个属性是为了当应用程序卸载时桌面上的快捷方式会删除
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		// 点击快捷图片，运行的程序主入口
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播。OK
		sendBroadcast(shortcutIntent);
	}
}