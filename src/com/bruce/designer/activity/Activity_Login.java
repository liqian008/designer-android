package com.bruce.designer.activity;

import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.account.GuestLoginApi;
import com.bruce.designer.api.account.OAuthRegisteApi;
import com.bruce.designer.api.account.WeiboLoginApi;
import com.bruce.designer.constants.ConstantOAuth;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.User;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.StringUtils;
import com.bruce.designer.util.UiUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

public class Activity_Login extends BaseActivity{
	/*默认处理*/
	private static final int HANDLER_FLAG_ERROR = 0;
	/*微博登录成功*/
	private static final int HANDLER_FLAG_WEIBO_LOGIN_SUCCESS = 1;
	/*微博登录失败*/
	private static final int HANDLER_FLAG_WEIBO_LOGIN_FAILED = 2;
	/*测试登录成功*/
	private static final int HANDLER_TEST_LOGIN_SUCCEED = 10;
	
	protected static final int HANDLER_FLAG_BIND_SUCCESS = 20;
	protected static final int HANDLER_FLAG_BIND_FAILED = 21;
	
	private ProgressDialog progressDialog;
	
	private Context context;
	private SsoHandler mSsoHandler; 
	private Oauth2AccessToken mAccessToken;
	
	private View snsLoginContainer;
	private View bindLoginContainer;
	
	private EditText loginEmail;
	private EditText loginNickname;
	private EditText loginPassword;
	private Button btnBind;
	
	private String uid;
	private String uname;
	private String accessToken;
	private String refreshToken;
	private long expiresTime;
	
	public static void show(Context context){
		Intent intent = new Intent(context, Activity_Login.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = Activity_Login.this;
		setContentView(R.layout.activity_login);
		
		snsLoginContainer = findViewById(R.id.snsLoginContainer);
		bindLoginContainer = findViewById(R.id.bindLoginContainer);
		
		ImageView wbLoginBtn = (ImageView) findViewById(R.id.wbLoginButton);
		ImageView guestLoginButton = (ImageView) findViewById(R.id.guestLoginButton);
		ImageView qqLoginButton = (ImageView) findViewById(R.id.qqLoginButton);
		wbLoginBtn.setOnClickListener(onClickListener);
		guestLoginButton.setOnClickListener(onClickListener);
		qqLoginButton.setOnClickListener(onClickListener);
		
		loginEmail= (EditText)findViewById(R.id.loginEmailText);
		loginNickname= (EditText)findViewById(R.id.loginNicknameText);
		loginPassword= (EditText)findViewById(R.id.loginPasswordText);
		btnBind= (Button)findViewById(R.id.btnBind);
		btnBind.setOnClickListener(onClickListener);
		
		progressDialog = ProgressDialog.show(context, null, "登录中...", true, false);
		progressDialog.dismiss();
	}

	/**
     * Sina微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

		@Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
            	uid = mAccessToken.getUid();
            	accessToken = mAccessToken.getToken();
            	refreshToken = mAccessToken.getRefreshToken();
            	expiresTime = mAccessToken.getExpiresTime();
            	
            	progressDialog.setMessage("微博验证通过，正在获取用户详细资料");
            	LogUtil.d("=============="+mAccessToken.getUid()+"========"+ mAccessToken.getToken());
                //向服务器提交，验证token
                weiboLogin(uid, accessToken, refreshToken, expiresTime);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                UiUtil.showShortToast(context, message);
            }
        }

        @Override
        public void onCancel() {
        	progressDialog.dismiss();
            UiUtil.showShortToast(context, "您已取消微博登录");
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	progressDialog.dismiss();
        	UiUtil.showShortToast(context, "Auth exception : " + e.getMessage());
        }
    }
    
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    
    private Handler loginHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch(msg.what){
				case HANDLER_FLAG_WEIBO_LOGIN_SUCCESS:
				case HANDLER_TEST_LOGIN_SUCCEED:
					Map<String, Object> dataMap = (Map<String, Object>) msg.obj;
					UserPassport userPassport = (UserPassport) dataMap.get("userPassport");
					User hostUser = (User) dataMap.get("hostUser");
					if(userPassport!=null){//之前绑定过，可以直接获取数据
						//TODO 
						UiUtil.showShortToast(context, "您已成功登录，正在进入主屏页..");
						//设置对象缓存
						AppApplication.setUserPassport(userPassport);
						AppApplication.setHostUser(hostUser);
						
						//直接跳转至主屏界面
						Activity_Main.show(context);
						finish();
					}else{//db未查到，认为是新用户，则必须要进行绑定
						boolean needBind = (Boolean) dataMap.get("needBind");
						uname = (String) dataMap.get("thirdpartyUname");
						if(needBind){
							uname = uname==null?"":uname;
							
							loginNickname.setText(uname);
							UiUtil.showShortToast(context, "您好，首次登录需要绑定本站账户");
							//显示绑定对话
							snsLoginContainer.setVisibility(View.GONE);
							bindLoginContainer.setVisibility(View.VISIBLE);
						}
					}
					break;
				case HANDLER_FLAG_WEIBO_LOGIN_FAILED:
					break;
				case HANDLER_FLAG_BIND_SUCCESS:
					Map<String, Object> bindDataMap = (Map<String, Object>) msg.obj;
					UserPassport bindUserPassport = (UserPassport) bindDataMap.get("userPassport");
					User bindHostUser = (User) bindDataMap.get("hostUser");
					//设置对象缓存
					AppApplication.setUserPassport(bindUserPassport);
					AppApplication.setHostUser(bindHostUser);
					UiUtil.showShortToast(context, "您已成功绑定金玩儿网账户，正在进入主屏页..");
					
					//绑定成功，跳转至主屏界面
					Activity_Main.show(context);
					finish();
				case HANDLER_FLAG_BIND_FAILED:
					//绑定失败
				default:
					break;
			}
		};
	};
    
	private OnClickListener onClickListener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.wbLoginButton:
				progressDialog.show();
				//跳转wb oauth
				WeiboAuth mWeiboAuth = new WeiboAuth(context, ConstantOAuth.APP_KEY, ConstantOAuth.REDIRECT_URL, ConstantOAuth.SCOPE);
				//SSO登录
				mSsoHandler = new SsoHandler((Activity) context, mWeiboAuth);
				mSsoHandler.authorize(new AuthListener());
				break;
			case R.id.guestLoginButton: //【游客登录】按钮
				progressDialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						ApiResult jsonResult = ApiManager.invoke(context,new GuestLoginApi());
						if (jsonResult != null && jsonResult.getResult() == 1) {
							Message message = loginHandler.obtainMessage(HANDLER_TEST_LOGIN_SUCCEED);
							message.obj = jsonResult.getData();
							message.sendToTarget();
						}
					}
				}).start();
				
				break;
			case R.id.btnBind: //绑定按钮
				final String username = loginEmail.getText().toString();
				final String nickname = loginNickname.getText().toString();
				final String password = loginPassword.getText().toString();
				//检查数据输入是否完整
				if(!checkBindInput()){
					break;
				}
				progressDialog.show();
				weiboOAuthRegiste(username, nickname, password, uid, uname, accessToken, refreshToken, expiresTime);
				
				break;
			default:
				break;
			}
		}
	};
	
	
	/**
     * 微博登录，根据用户的accessToken换取用户资料
     * @param accessToken
     * @param expiresTime 
     * @param refreshToken 
     * @param thirdpartyType
     */
    private void weiboLogin(final String uid, final String accessToken, final String refreshToken, final long expiresTime) {
		//启动线程获取用户数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				WeiboLoginApi api = new WeiboLoginApi(uid, accessToken, refreshToken, expiresTime);
				ApiResult apiResult = ApiManager.invoke(context, api);
				if(apiResult!=null&&apiResult.getResult()==1){
					Message message = loginHandler.obtainMessage(HANDLER_FLAG_WEIBO_LOGIN_SUCCESS);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}else{//数据异常
					UiUtil.showShortToast(context, "登录失败，请重试");
				}
			}
		}).start();
	}
    
    /**
     * 填写email完成与sina的绑定注册
     * @param email
     * @param nickname
     * @param password
     * @param uid
     * @param uname
     * @param accessToken
     * @param refreshToken
     * @param expiresTime
     */
    private void weiboOAuthRegiste(final String email, final String nickname, final String password, final String uid, final String uname,  final String accessToken, final String refreshToken, final long expiresTime) {
		//启动线程绑定微博&账户
		new Thread(new Runnable() {
			@Override
			public void run() {
				OAuthRegisteApi api = new OAuthRegisteApi(email, nickname, password, uid, uname, accessToken, refreshToken, expiresTime);
				ApiResult apiResult = ApiManager.invoke(context, api);
				if(apiResult!=null&&apiResult.getResult()==1){
					Message message = loginHandler.obtainMessage(HANDLER_FLAG_BIND_SUCCESS);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}else{//数据异常
					
				}
			}
		}).start();
	}
    
    /**
     * 检查绑定输入
     * @param field
     * @return
     */
    private boolean checkBindInput(){
		String emailVal = loginEmail.getText().toString();
		if(StringUtils.isBlank(emailVal)){
			UiUtil.showShortToast(context, "Email地址不合法");
			return false;
		}
		String nicknameVal = loginNickname.getText().toString();
		if(StringUtils.isBlank(nicknameVal)){
			UiUtil.showShortToast(context, "昵称输入不能为空");
			return false;
		}
		String passwordVal = loginPassword.getText().toString();
		if(StringUtils.isBlank(passwordVal)){
			UiUtil.showShortToast(context, "密码输入不能为空");
			return false;
    	}
    	return true;
    }
    
	
}
