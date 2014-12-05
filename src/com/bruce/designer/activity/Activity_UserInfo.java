package com.bruce.designer.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.activity.fragment.Fragment_MyHome;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.user.UserInfoApi;
import com.bruce.designer.api.user.UserUploadAvatarApi;
import com.bruce.designer.constants.Config;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.crop.ModifyAvatarDialog;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.User;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ImageUtil;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_UserInfo extends BaseActivity {

	private static final int HANDLER_FLAG_USERINFO = 1;
	private static final int HANDLER_FLAG_AVATAR_UPLOAD_SUCCESS = 10;
	private static final int HANDLER_FLAG_AVATAR_UPLOAD_FAILED = 11;
	

	private static final int HOST_ID = AppApplication.getUserPassport().getUserId();

	// 修改头像部分的定义-开始
	private static final int FLAG_CHOOSE_ALBUM = 5;
	private static final int FLAG_CHOOSE_CAMERA = 6;
	private static final int FLAG_MODIFY_FINISH = 7;
	private static String localTempImageFileName = "";

	public static final String IMAGE_PATH = "My_weixin";
	public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();

	public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL, "images/screenshots");
	// 修改头像部分的定义-结束

	private View titlebarView;

	private TextView titleView;
	/* 我的头像 */
	private ImageView avatarView;
	private TextView modifyAvatarView;

	private View usernameContainer, weixinNumberContainer, shopContainer, introduceContainer;
	private TextView userTypeTextView, nicknameTextView, usernameTextView, weixinNumberTextView, shopTextView, introduceTextView;

	private int queryUserId;
	/*头像的数据*/
	private byte[] avatarData;

	public static void show(Context context, int queryUserId) {
		Intent intent = new Intent(context, Activity_UserInfo.class);
		intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_ID, queryUserId);
		context.startActivity(intent);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_FLAG_USERINFO:
				Map<String, Object> userinfoDataMap = (Map<String, Object>) msg.obj;
				if (userinfoDataMap != null) {
					User userinfo = (User) userinfoDataMap.get("userinfo");
					if (userinfo != null && userinfo.getId() > 0) {
						// 刷新用户资料
						if (userinfo.getHeadImg() != null) {
							ImageLoader.getInstance().displayImage(userinfo.getHeadImg(), avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
						}
						nicknameTextView.setText(userinfo.getNickname());
						usernameTextView.setText(userinfo.getUsername());
						shopTextView.setText(userinfo.getDesignerTaobaoHomepage());
						introduceTextView.setText(userinfo.getDesignerIntroduction());
						if(userinfo.getDesignerStatus()==2){//设计师身份
							weixinNumberTextView.setText(userinfo.getWeixinNumber());
							weixinNumberContainer.setVisibility(View.VISIBLE);
							shopContainer.setVisibility(View.VISIBLE);
							introduceContainer.setVisibility(View.VISIBLE);
							userTypeTextView.setText("设计师");
						}else{
//							UiUtil.showShortToast(context, "非设计师");
							weixinNumberContainer.setVisibility(View.GONE);
							shopContainer.setVisibility(View.GONE);
							introduceContainer.setVisibility(View.GONE);
						}
					}
				}
				break;
			case HANDLER_FLAG_AVATAR_UPLOAD_SUCCESS:
				UiUtil.showShortToast(context, "新头像上传成功！");
				break;
			case HANDLER_FLAG_AVATAR_UPLOAD_FAILED:
				UiUtil.showShortToast(context, "新头像上传失败！");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);

		Intent intent = getIntent();
		queryUserId = intent.getIntExtra(ConstantsKey.BUNDLE_USER_INFO_ID, 0);

		// init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);

		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("个人资料");
		
		usernameContainer = (View) findViewById(R.id.usernameContainer);
		weixinNumberContainer = (View) findViewById(R.id.weixinNumberContainer);
		shopContainer = (View) findViewById(R.id.shopContainer);
		introduceContainer = (View) findViewById(R.id.introduceContainer);
		
		avatarView = (ImageView) findViewById(R.id.avatar);
		modifyAvatarView = (TextView) findViewById(R.id.modifyAvatar);
		if(AppApplication.isHost(queryUserId)&&!AppApplication.isGuest()){
			modifyAvatarView.setText("修改头像");
			modifyAvatarView.setOnClickListener(listener);
			usernameContainer.setVisibility(View.VISIBLE);
		}else{
			modifyAvatarView.setText("头像");
			usernameContainer.setVisibility(View.GONE);
		}
		
		userTypeTextView = (TextView) findViewById(R.id.userTypeTextView); 
		nicknameTextView = (TextView) findViewById(R.id.nickNameTextView);
		usernameTextView = (TextView) findViewById(R.id.usernameTextView);
		weixinNumberTextView = (TextView) findViewById(R.id.weixinNumberTextView);
		shopTextView = (TextView) findViewById(R.id.shopTextView);
		introduceTextView = (TextView) findViewById(R.id.introduceTextView);

		User userinfo = SharedPreferenceUtil.readObjectFromSp(User.class,
				Config.SP_CONFIG_ACCOUNT, Config.SP_KEY_USERINFO);
		if (queryUserId == HOST_ID && (userinfo != null && userinfo.getId() != null && userinfo.getId() > 0)) {
			// 从sp中读取用户资料
			Message message = handler.obtainMessage(HANDLER_FLAG_USERINFO);
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("userinfo", userinfo);
			message.obj = dataMap;
			message.sendToTarget();
		} else {
			// 启动获取个人资料详情
			getUserinfo(queryUserId);
		}
	}

	private OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.titlebar_return:
				processBeforeFinish();
				finish();
				break;
			case R.id.modifyAvatar:// 点击更换头像
				if(AppApplication.isHost(queryUserId)&&!AppApplication.isGuest()){
					// 调用选择那种方式的dialog
					ModifyAvatarDialog modifyAvatarDialog = new ModifyAvatarDialog(context) {
						// 选择本地相册
						@Override
						public void doGoToImg() {
							this.dismiss();
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_PICK);
							intent.setType("image/*");
							startActivityForResult(intent, FLAG_CHOOSE_ALBUM);
						}

						// 选择相机拍照
						@Override
						public void doGoToPhone() {
							this.dismiss();
							String status = Environment.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {
								try {
									localTempImageFileName = "";
									localTempImageFileName = String.valueOf(System.currentTimeMillis()) + ".png";
									File filePath = FILE_PIC_SCREENSHOT;
									if (!filePath.exists()) {
										filePath.mkdirs();
									}
									Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
									File f = new File(filePath, localTempImageFileName);
									// localTempImgDir和localTempImageFileName是自己定义的名字
									Uri u = Uri.fromFile(f);
									intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
									intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
									startActivityForResult(intent, FLAG_CHOOSE_CAMERA);
								} catch (ActivityNotFoundException e) {
									//
								}
							}
						}
					};
					AlignmentSpan span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
					AbsoluteSizeSpan span_size = new AbsoluteSizeSpan(25, true);
					SpannableStringBuilder spannable = new SpannableStringBuilder();
					String dTitle = "请选择图片";
					spannable.append(dTitle);
					spannable.setSpan(span, 0, dTitle.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					spannable.setSpan(span_size, 0, dTitle.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					modifyAvatarDialog.setTitle(spannable);
					modifyAvatarDialog.show();
				}

				break;
			default:
				break;
			}
		}
	};

	private void getUserinfo(final int userId) {
		// 启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				UserInfoApi api = new UserInfoApi(userId);
				ApiResult apiResult = ApiManager.invoke(context, api);

				if (apiResult != null && apiResult.getResult() == 1) {
					message = handler.obtainMessage(HANDLER_FLAG_USERINFO);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == FLAG_CHOOSE_ALBUM && resultCode == RESULT_OK) {
			if (intent != null) {
				Uri uri = intent.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
					if (null == cursor) {
						Toast.makeText(context, "图片没找到", 0).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					LogUtil.d("path=" + path);
					Intent newIntent = new Intent(this, Activity_CropAvatar.class);
					newIntent.putExtra("path", path); 
					startActivityForResult(newIntent, FLAG_MODIFY_FINISH);
				} else {
					LogUtil.d("path=" + uri.getPath());
					Intent newIntent = new Intent(this, Activity_CropAvatar.class);
					newIntent.putExtra("path", uri.getPath());
					startActivityForResult(newIntent, FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == FLAG_CHOOSE_CAMERA && resultCode == RESULT_OK) {
			File f = new File(FILE_PIC_SCREENSHOT,localTempImageFileName);
			Intent newIntent = new Intent(this, Activity_CropAvatar.class);
			newIntent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(newIntent, FLAG_MODIFY_FINISH);
		}else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK) {
			if (intent != null) {
				final String path = intent.getStringExtra("path");
				
				File file = new File(path);
				LogUtil.d( "截取到的文件大小是 = " + file.length());
				
				LogUtil.d( "截取到的图片路径是 = " + path);
				Bitmap b = BitmapFactory.decodeFile(path);
				avatarView.setImageBitmap(b);
				//展示processBar
				UiUtil.showShortToast(context, "正在上传新头像，请稍等");
				avatarData = ImageUtil.bitmap2Bytes(b);
				//提交更新头像api请求，更新成功后，清除processBar
				new Thread(new Runnable() {
					@Override
					public void run() {
						UserUploadAvatarApi api = new UserUploadAvatarApi(path, null);
						ApiResult apiResult = ApiManager.invoke(context, api);
						Message message = null;
						if (apiResult != null && apiResult.getResult() == 1) {
							message = handler.obtainMessage(HANDLER_FLAG_AVATAR_UPLOAD_SUCCESS);
						}else{
							message = handler.obtainMessage(HANDLER_FLAG_AVATAR_UPLOAD_FAILED);
						}
						message.sendToTarget();
					}
				}).start();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 退出
			processBeforeFinish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private void processBeforeFinish() {
		if(avatarData!=null&&avatarData.length>0){
			Intent intent = new Intent();
			intent.putExtra("avatarData", avatarData);
			setResult(Fragment_MyHome.RESULT_CODE_AVATAR_CHANGED, intent);
		}
	}
}
