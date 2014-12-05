package com.bruce.designer.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.bruce.designer.api.account.OAuthBindApi;
import com.bruce.designer.api.account.OAuthRegisteApi;
import com.bruce.designer.api.account.WeiboLoginApi;
import com.bruce.designer.api.account.WeixinLoginApi;
import com.bruce.designer.constants.ConstantOAuth;
import com.bruce.designer.constants.ConstantsKey;
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
import com.tencent.mm.sdk.modelmsg.SendAuth;

public class Activity_Login extends BaseActivity{
	
	static Map<String, String> thirdpartyMap = new HashMap<String, String>();
	static {
		thirdpartyMap.put("1", "新浪微博");
		thirdpartyMap.put("2", "腾讯QQ");
		thirdpartyMap.put("3", "微信");
	}
	
	/*默认处理*/
	private static final int HANDLER_FLAG_ERROR = 0;
	/*微博登录成功*/
	private static final int HANDLER_FLAG_WEIBO_LOGIN_SUCCESS = 10;
	/*微博登录失败*/
	private static final int HANDLER_FLAG_WEIBO_LOGIN_FAILED = 11;
	
	/*微信登录成功*/
	private static final int HANDLER_FLAG_WEIXIN_LOGIN_SUCCESS = 30;
	/*微信登录失败*/
	private static final int HANDLER_FLAG_WEIXIN_LOGIN_FAILED = 31;
	
	/*测试登录成功*/
	private static final int HANDLER_GUEST_LOGIN_SUCCEED = 100000;
	
	protected static final int HANDLER_FLAG_BIND_SUCCESS = 1;
	protected static final int HANDLER_FLAG_BIND_FAILED = 2;
	
	private ProgressDialog progressDialog;
	
	private Context context;
	private SsoHandler mSsoHandler; 
	private Oauth2AccessToken mAccessToken;
	
	private View snsLoginContainer, accountContainer, bindContainer, registeContainer;
	
	private Button bindTab, registeTab;
	private EditText loginEmail, loginPassword;
	
	private EditText registeEmailText, registeNicknameText, registePasswordText, registeRepasswordText;
	
	private Button btnBind;
	
	private String thirdpartyType;
	private String uid, uname, uavatar, accessToken, refreshToken; 
	private long expiresTime;
	
	/**
	 * 微信登录的receiver
	 */
	private BroadcastReceiver weixinOAuthReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			thirdpartyType = "3";//微信登录类型
			String code = intent.getStringExtra("weixin_oauth_code");
			String state = intent.getStringExtra("weixin_oauth_state");
			UiUtil.showShortToast(context, "接收到微信登录广播, code: "+code+", state: "+state);
			//启动线程进行微信登录
			weixinLogin(code, state);
		}
	};
	
	
	public static void show(Context context){
		Intent intent = new Intent(context, Activity_Login.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = Activity_Login.this;
		setContentView(R.layout.activity_login);
		
		//注册receiver，用于接收微信登录的广播
		registerReceiver(weixinOAuthReceiver, new IntentFilter(ConstantsKey.BROADCAST_ACTION_WEIXIN_LOGIN));
		
		snsLoginContainer = findViewById(R.id.snsLoginContainer);
		accountContainer = findViewById(R.id.accountContainer);
		bindContainer = findViewById(R.id.bindContainer);
		registeContainer = findViewById(R.id.registeContainer);
		
		bindTab = (Button)findViewById(R.id.bindTab);
		bindTab.setOnClickListener(onClickListener);//点击事件
		registeTab = (Button)findViewById(R.id.registeTab);
		registeTab.setOnClickListener(onClickListener);//点击事件
		
		
		ImageView weiboLoginBtn = (ImageView) findViewById(R.id.weiboLoginButton);
		ImageView guestLoginButton = (ImageView) findViewById(R.id.guestLoginButton);
		ImageView qqLoginButton = (ImageView) findViewById(R.id.qqLoginButton);
		ImageView weixinLoginButton = (ImageView) findViewById(R.id.weixinLoginButton);
		
		weiboLoginBtn.setOnClickListener(onClickListener);
		guestLoginButton.setOnClickListener(onClickListener);
		qqLoginButton.setOnClickListener(onClickListener);
		weixinLoginButton.setOnClickListener(onClickListener);
		
		//绑定事件
		loginEmail= (EditText)findViewById(R.id.loginEmailText);
		loginPassword= (EditText)findViewById(R.id.loginPasswordText);
		btnBind= (Button)findViewById(R.id.btnBind);
		btnBind.setOnClickListener(onClickListener);
		
		//注册事件
		registeEmailText= (EditText)findViewById(R.id.registeEmailText);
		registeNicknameText= (EditText)findViewById(R.id.registeNicknameText);
		registePasswordText = (EditText)findViewById(R.id.registePasswordText);
		registeRepasswordText = (EditText)findViewById(R.id.registeRepasswordText);
		
		progressDialog = ProgressDialog.show(context, null, "登录中...", true, false);
		progressDialog.dismiss();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//注销weixinLoginReceiver
		unregisterReceiver(weixinOAuthReceiver);
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
            	thirdpartyType = "1";//新浪微博账户类型
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
				case HANDLER_GUEST_LOGIN_SUCCEED:
					Map<String, Object> dataMap = (Map<String, Object>) msg.obj;
					UserPassport userPassport = (UserPassport) dataMap.get("userPassport");
					User hostUser = (User) dataMap.get("hostUser");
					if(userPassport!=null){//之前绑定过，可以直接获取数据
						//TODO
						UiUtil.showLongToast(context, "您已成功登录，正在进入主界面..");
						//设置对象缓存
						AppApplication.setUserPassport(userPassport);
						AppApplication.setHostUser(hostUser);
						
						//直接跳转至主屏界面
						Activity_Main.show(context);
						finish();
					}else{//server未查到，认为是新用户，则必须要进行绑定（注册或绑定）
						boolean needBind = (Boolean) dataMap.get("needBind");
						thirdpartyType = (String) dataMap.get("thirdpartyType");
						uname = (String) dataMap.get("thirdpartyUname");
						uavatar = (String) dataMap.get("thirdpartyAvatar");
						if(needBind){
							uname = uname==null?"":uname;
							uavatar = uavatar==null?"":uavatar;
							
							registeNicknameText.setText(uname);
							UiUtil.showLongToast(context, "这是您首次使用["+thirdpartyMap.get(thirdpartyType)+"]登录。如果您之前曾使用其他账户系统登录过本站账户，则可进行绑定操作；如若没有，则需要注册新账户");
							//显示绑定对话
							snsLoginContainer.setVisibility(View.GONE);
							accountContainer.setVisibility(View.VISIBLE);
						}
					}
					break;
				case HANDLER_FLAG_WEIBO_LOGIN_FAILED:
					UiUtil.showShortToast(context, "新浪微博登录失败，请重试");
					progressDialog.dismiss();
					break;
				case HANDLER_FLAG_WEIXIN_LOGIN_FAILED:
					UiUtil.showShortToast(context, "微信登录失败，请重试");
					progressDialog.dismiss();
					break;
				case HANDLER_FLAG_BIND_SUCCESS:
					Map<String, Object> bindDataMap = (Map<String, Object>) msg.obj;
					UserPassport bindUserPassport = (UserPassport) bindDataMap.get("userPassport");
					User bindHostUser = (User) bindDataMap.get("hostUser");
					//设置对象缓存
					AppApplication.setUserPassport(bindUserPassport);
					AppApplication.setHostUser(bindHostUser);
					UiUtil.showShortToast(context, "您已成功绑定金玩儿网账户，精彩内容即将开启..");
					
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
			case R.id.weiboLoginButton:
				progressDialog.setMessage("新浪微博登录中...");
				progressDialog.show();
				//跳转wb oauth
				WeiboAuth mWeiboAuth = new WeiboAuth(context, ConstantOAuth.APP_KEY, ConstantOAuth.REDIRECT_URL, ConstantOAuth.SCOPE);
				//SSO登录
				mSsoHandler = new SsoHandler((Activity) context, mWeiboAuth);
				mSsoHandler.authorize(new AuthListener());
				break;
			case R.id.weixinLoginButton://微信登录
				progressDialog.setMessage("微信登录中...");
				progressDialog.show();
				SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "";
				//微信登录
				AppApplication.getWxApi().sendReq(req);
				break;
			case R.id.guestLoginButton: //【游客登录】按钮
				progressDialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						ApiResult jsonResult = ApiManager.invoke(context,new GuestLoginApi());
						if (jsonResult != null && jsonResult.getResult() == 1) {
							Message message = loginHandler.obtainMessage(HANDLER_GUEST_LOGIN_SUCCEED);
							message.obj = jsonResult.getData();
							message.sendToTarget();
						}
					}
				}).start();
				
				break;
			case R.id.bindTab: //绑定资料按钮
				//显示绑定登录layout, 隐藏注册layout
				bindContainer.setVisibility(View.VISIBLE);
				registeContainer.setVisibility(View.GONE);
				
				bindTab.setBackgroundResource(R.drawable.button_orange_bg);
				bindTab.setTextColor(getResources().getColor(R.color.white));
				registeTab.setBackgroundResource(R.drawable.button_grey_bg);
				registeTab.setTextColor(getResources().getColor(R.color.black));
				
				break;
			case R.id.registeTab: //注册资料按钮
				//隐藏绑定登录layout, 显示注册layout
				bindContainer.setVisibility(View.GONE);
				registeContainer.setVisibility(View.VISIBLE);
				
				bindTab.setBackgroundResource(R.drawable.button_grey_bg);
				bindTab.setTextColor(getResources().getColor(R.color.black));
				registeTab.setBackgroundResource(R.drawable.button_orange_bg);
				registeTab.setTextColor(getResources().getColor(R.color.white));
				break;	
			case R.id.btnBind: //绑定按钮
				final String username = loginEmail.getText().toString();
				final String password = loginPassword.getText().toString();
				//检查数据输入是否完整
				if(!checkBindInput()){
					break;
				}
				progressDialog.show();
				oauthBind(thirdpartyType, username, password, uid, uname, accessToken, refreshToken, expiresTime);
				
				break;
			case R.id.btnRegiste: //注册按钮
				final String registeUsername = registeEmailText.getText().toString();
				final String registeNickname = registeNicknameText.getText().toString();
				final String registePassword = registePasswordText.getText().toString();
				//检查数据输入是否完整
				if(!checkRegisteInput()){
					break;
				}
				progressDialog.show();
				oauthRegiste(thirdpartyType, registeUsername, registeNickname, registePassword, uid, uname, accessToken, refreshToken, expiresTime);
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
//					UiUtil.showShortToast(context, "登录失败，请重试");
				}
			}
		}).start();
	}
    
    
    /**
     * 微信登录，根据用户的accessToken换取用户资料
     * @param accessToken
     * @param expiresTime 
     * @param refreshToken 
     * @param thirdpartyType
     */
    private void weixinLogin(final String code, final String state) {
		//启动线程获取用户数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				WeixinLoginApi api = new WeixinLoginApi(code, state);
				ApiResult apiResult = ApiManager.invoke(context, api);
				if(apiResult!=null&&apiResult.getResult()==1){
					//微信登录成功
					Message message = loginHandler.obtainMessage(HANDLER_FLAG_WEIXIN_LOGIN_SUCCESS);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}else{//数据异常
					//微信登录失败
					Message message = loginHandler.obtainMessage(HANDLER_FLAG_WEIXIN_LOGIN_FAILED);
					message.obj = apiResult.getMessage();
					message.sendToTarget();
				}
			}
		}).start();
	}
    
    
    
    /**
     * 绑定老用户
     * @param thirdpartyType 第三方账户类型（1微博，2QQ，3微信）
     * @param email
     * @param password
     * @param uid
     * @param uname
     * @param accessToken
     * @param refreshToken
     * @param expiresTime
     */
    private void oauthBind(final String thirdpartyType, final String email, final String password, final String uid, final String uname,  final String accessToken, final String refreshToken, final long expiresTime) {
		//启动线程绑定微博&账户
		new Thread(new Runnable() {
			@Override
			public void run() {
				OAuthBindApi api = new OAuthBindApi(thirdpartyType, email, password, uid, uname, uavatar, accessToken, refreshToken, expiresTime);
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
     * oauth绑定注册新用户
     * @param thirdpartyType 第三方账户类型（1微博，2QQ，3微信）
     * @param email
     * @param nickname
     * @param password
     * @param uid
     * @param uname
     * @param accessToken
     * @param refreshToken
     * @param expiresTime
     */
    private void oauthRegiste(final String thirdpartyType, final String email, final String nickname, final String password, final String uid, final String uname,  final String accessToken, final String refreshToken, final long expiresTime) {
		//启动线程注册账户
		new Thread(new Runnable() {
			@Override
			public void run() {
				OAuthRegisteApi api = new OAuthRegisteApi(thirdpartyType, email, nickname, password, uid, uname, uavatar, accessToken, refreshToken, expiresTime);
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
		String passwordVal = loginPassword.getText().toString();
		if(StringUtils.isBlank(passwordVal)){
			UiUtil.showShortToast(context, "密码不能为空");
			return false;
    	}
    	return true;
    }
    

    /**
     * 检查注册输入
     * @param field
     * @return
     */
    private boolean checkRegisteInput(){
		String emailVal = registeEmailText.getText().toString();
		if(StringUtils.isBlank(emailVal)){
			UiUtil.showShortToast(context, "Email地址不合法");
			return false;
		}
		String nicknameVal = registeNicknameText.getText().toString();
		if(StringUtils.isBlank(nicknameVal)){
			UiUtil.showShortToast(context, "昵称不能为空");
			return false;
		}
		String passwordVal = registePasswordText.getText().toString();
		if(StringUtils.isBlank(passwordVal)){
			UiUtil.showShortToast(context, "密码不能为空");
			return false;
    	}
		String repasswordVal = registeRepasswordText.getText().toString();
		if(StringUtils.isBlank(repasswordVal)){
			UiUtil.showShortToast(context, "确认密码不能为空");
			return false;
    	}
		
		if(!passwordVal.equals(repasswordVal)){
			UiUtil.showShortToast(context, "两次密码输入不一致");
			return false;
    	}
    	return true;
    }
}
