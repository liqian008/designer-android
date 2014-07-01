package com.bruce.designer.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.bruce.designer.R;

public class Activity_Splash extends BaseActivity {

	// 提示语
	private String updateMsg = "有最新的软件包哦，亲快下载吧~";
	// 返回的安装包url
	private String apkUrl = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";

	private Dialog noticeDialog;
	private Dialog downloadDialog;

	/* 下载包安装路径 */
	private static final String savePath = "/sdcard/updatedemo/";
	private static final String saveFileName = savePath+ "UpdateDemoRelease.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;
	/* 检查更新 */
	private static final int CHECK_UPDATE = 0;
	/* 有新下载 */
	private static final int DOWNLOADING = 1;
	/* 下载完成 */
	private static final int DOWNLOAD_OVER = 2;
	/* 下载进度 */
	private int progress;
	/* 是否中断 */
	private boolean interceptFlag = false;
	/* 下载线程 */
	private Thread downLoadThread;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_UPDATE:
				int updateStatus = 0;//new Random(System.currentTimeMillis()).nextInt(3);
				showDownloadPrompt(updateStatus, apkUrl);
				break;
			case DOWNLOADING:
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_OVER:
				installApk();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = Activity_Splash.this;
		setContentView(R.layout.activity_splash);

//		UpdateManager updateManager = new UpdateManager(context);
//		updateManager.checkResult(false);

//		 启动线程
		 Thread thread = new Thread(new Runnable() {
			 @Override
			 public void run() {
				 //TODO 检查客户端版本
				 try {
					 Thread.sleep(2000);
					 handler.obtainMessage(CHECK_UPDATE).sendToTarget();
				 } catch (InterruptedException e) {
					 e.printStackTrace();
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
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean flag = false;
		return flag;
	}

	/**
	 * 
	 * 构造下载的提示框（建议更新）
	 * @param updateStatus， 0无更新/1为建议更新/2为强制更新
	 * @param apkUrl
	 */
	private void showDownloadPrompt(int updateStatus, final String apkUrl) {
		if (updateStatus >= 1) {
			AlertDialog.Builder builder = new Builder(context);
			builder.setTitle("软件版本更新");
			builder.setMessage(updateMsg);
			builder.setCancelable(false); 
			builder.setPositiveButton("立即体验", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					showDownloadDialog(apkUrl);
				}
			});
			if (updateStatus == 2) {// 非强制更新情况下，用户可以取消
				builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Activity_Login.show(context);
						finish();
					}
				});
			}
			noticeDialog = builder.create();
			noticeDialog.show();
		}else{
			Activity_Login.show(context);
			finish();
		}
		
	}

	/**
	 * 下载中的展示框
	 * 
	 * @param apkUrl
	 */
	private void showDownloadDialog(String apkUrl) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("软件版本更新");
		builder.setCancelable(false);
		final LayoutInflater inflater = LayoutInflater.from(context);
		View progressView = inflater.inflate(R.layout.download_progress, null);
		mProgress = (ProgressBar) progressView.findViewById(R.id.progress);
		builder.setView(progressView);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
				Activity_Login.show(context);
				finish();
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk(apkUrl);
	}

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

}
