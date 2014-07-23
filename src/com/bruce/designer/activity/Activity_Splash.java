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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.system.SystemCheckApi;
import com.bruce.designer.model.VersionCheckResult;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UiUtil;

public class Activity_Splash extends BaseActivity {
	
	/* 检查更新 */
	private static final int CHECK_UPDATE = 0;
	/* 有新下载 */
	private static final int DOWNLOADING = 1;
	/* 下载完成 */
	private static final int DOWNLOAD_OVER = 2;
	
	// 提示标题
	private String updateTitle = "发现新版本客户端";
	// 提示语
	private String updateMsg = "有最新的软件包哦，亲快下载吧~";
	// 返回的安装包url
	private String apkUrl = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";

	private Dialog downloadPromptDialog;
	private Dialog downloadingDialog;

	/* 下载包安装路径 */
	private static final String savePath = "/sdcard/updatedemo/";
	private static final String saveFileName = savePath+ "UpdateDemoRelease.apk";

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
				updateStatus = versionCheckResult.getUpdateStatus();//new Random(System.currentTimeMillis()).nextInt(3);
				processDownloadDialog(updateStatus, apkUrl);
				
				break;
			case DOWNLOADING:
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_OVER:
				installApk();
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

		// ImageButton wbLoginBtnView = (ImageButton)
		// findViewById(R.id.wbLoginButton);
		// wbLoginBtnView.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(context, Activity_Main.class);
		// startActivity(intent);
		// finish();
		// }
		// });
	}

	/**
	 * 禁止使用退出键
	 */
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		boolean flag = false;
//		return flag;
//	}

	/**
	 * 
	 * 构造下载的提示框（建议更新）
	 * @param updateStatus， 0无更新/1为建议更新/2为强制更新
	 * @param apkUrl
	 */
	private void processDownloadDialog(int updateStatus, final String apkUrl) {
		if (updateStatus == 1) {
			downloadPromptDialog = UiUtil.showAlertDialog(context, updateTitle, updateMsg, "立即体验", downloadListener, "下次再说", ignoreListener);
		}else if (updateStatus == 2) {
			downloadPromptDialog = UiUtil.showAlertDialog(context, updateTitle, updateMsg, "立即体验", downloadListener, null, null);
		}else{
			//无更新
//			Activity_Login.show(context);
//			finish();
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
		final LayoutInflater inflater = LayoutInflater.from(context);
		View downloadingView = inflater.inflate(R.layout.download_progress, null);
		mProgress = (ProgressBar) downloadingView.findViewById(R.id.progress);
		downloadingDialog = UiUtil.showAlertDialog(context, false, downloadingView, "客户端版本更新", null, "取消", quitDownloadListener, null, null);
//		downloadingDialog = UiUtil.showAlertDialog(context, false, downloadingView, "客户端版本更新", null, null, null, null, null);
		downloadingDialog.show();
		//开启下载线程
		downloadApk(apkUrl);
	}

	/**
	 * 点击下载的listener
	 */
	private DialogInterface.OnClickListener downloadListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			showDownloadingDialog(apkUrl);
		}
	};
	
	/**
	 * 点击下次再说的listener
	 */
	private DialogInterface.OnClickListener ignoreListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
//			Activity_Login.show(context);
//			finish();
			jumpToNextActivity();
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
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					handler.sendEmptyMessage(DOWNLOADING);
					if (numread <= 0) {
						// 下载完成通知安装
						handler.sendEmptyMessage(DOWNLOAD_OVER);
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
	 * @param url
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
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
	 * 跳转至下一activity(未登录需要跳到登录页，已登录则需要跳转到首页)
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
	
	

}