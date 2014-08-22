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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.account.TestLoginApi;
import com.bruce.designer.api.account.WeiboLoginApi;
import com.bruce.designer.constants.Config;
import com.bruce.designer.constants.ConstantOAuth;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.UiUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.UIUtils;

public class Activity_Login extends BaseActivity{
	/*默认处理*/
	private static final int HANDLER_FLAG_ERROR = 0;
	/*微博登录成功*/
	private static final int HANDLER_FLAG_WB_LOGIN_SUCCESS = 1;
	/*微博登录失败*/
	private static final int HANDLER_FLAG_WB_LOGIN_FAILED = 2;
	/*测试登录成功*/
	private static final int HANDLER_TEST_LOGIN_SUCCEED = 10;
	
	private ProgressDialog progressDialog;
	
	private Context context;
	private SsoHandler mSsoHandler; 
	private Oauth2AccessToken mAccessToken;
	
	
	public static void show(Context context){
		Intent intent = new Intent(context, Activity_Login.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = Activity_Login.this;
		setContentView(R.layout.activity_login);
		
		ImageView wbLoginBtn = (ImageView) findViewById(R.id.wbLoginButton);
		ImageView qqLoginButton = (ImageView) findViewById(R.id.qqLoginButton);
//		Button skipLoginBtn = (Button) findViewById(R.id.skipLoginButton);
		
		wbLoginBtn.setOnClickListener(listener);
		qqLoginButton.setOnClickListener(listener);
//		skipLoginBtn.setOnClickListener(listener);
		
		progressDialog = ProgressDialog.show(context, "title", "content", true, false);
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
            	String uid = mAccessToken.getUid();
            	String accessToken = mAccessToken.getToken();
            	String refreshToken = mAccessToken.getRefreshToken();
            	long expiresTime = mAccessToken.getExpiresTime();
            	
            	LogUtil.d("=============="+mAccessToken.getUid()+"========"+ mAccessToken.getToken());
                // 显示 Token
//            	Toast.makeText(context,  mAccessToken.getUid()+"========"+ mAccessToken.getToken(), Toast.LENGTH_LONG).show();
//                Toast.makeText(context, R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                
//                Activity_Login_Bind.show(context);
//                finish();
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
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(context, R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(context, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
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
    
    /**
     * 根据用户的accessToken换取用户资料
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
					//weibo登录成功，两种情况
					boolean result = true;
					if(result){//直接返回用户ticket信息
						Message message = loginHandler.obtainMessage(HANDLER_TEST_LOGIN_SUCCEED);
						message.obj = apiResult.getData();
						message.sendToTarget();
						
						//跳转至主屏界面
//						Activity_Main.show(context);
//						finish();
					}else{//第一次登录，需要完善用户资料
					}
				}else{//发送失败消息
//					oAuthLoginListener.loginFailed();
				}
			}
		}).start();
	}
    
    
    private Handler loginHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
				case HANDLER_FLAG_WB_LOGIN_SUCCESS:
					progressDialog.dismiss();
					
					//1、直接登录进入
					//2、需要绑定账户
				case HANDLER_TEST_LOGIN_SUCCEED:
					progressDialog.dismiss();
					
//					UiUtil.showShortToast(context, "新浪微博登录成功，正在为您跳转至内容页...");
					Map<String, Object> dataMap = (Map<String, Object>) msg.obj;
					UserPassport userPassport = (UserPassport) dataMap.get("userPassport");
					if(userPassport!=null){
//						SharedPreferenceUtil.writeObjectToSp(userPassport, Config.SP_CONFIG_ACCOUNT,  Config.SP_KEY_USERPASSPORT);
						//设置对象缓存
						AppApplication.setUserPassport(userPassport);
					}
					//跳转至主屏界面
					Activity_Main.show(context);
					finish();
					break;
				case HANDLER_FLAG_WB_LOGIN_FAILED:
					progressDialog.dismiss();
					break;
				default:
					break;
			}
		};
	};
    
//    class WeiboLoginListener implements OAuthLoginListener{
//		@Override
//		public void loginComplete(UserPassport userPassport) {
//			UiUtil.showShortToast(context, "登录成功");
////			SharedPreferenceUtil.writeObjectToSp(userPassport, Config.SP_CONFIG_ACCOUNT, Config.SP_KEY_USERPASSPORT);
//		}
//		@Override
//		public void needComplete() {
//			UiUtil.showShortToast(context, "登录成功，需要完善个人资料");
//		}
//
//		@Override
//		public void loginFailed() {
//			UiUtil.showShortToast(context, "登录失败");
//		}
//    	
//    }
    
    
	private OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.wbLoginButton:
				progressDialog.show();
				
				//跳转wb oauth
				WeiboAuth mWeiboAuth = new WeiboAuth(context, ConstantOAuth.APP_KEY,
						ConstantOAuth.REDIRECT_URL, ConstantOAuth.SCOPE);
				//SSO登录
				mSsoHandler = new SsoHandler((Activity) context, mWeiboAuth);
				mSsoHandler.authorize(new AuthListener());
				
				
//				UiUtil.showShortToast(context, "微博登录功能未开放，请点击下方【直接登录】按钮");
				
				break;
//			case R.id.skipLoginButton://游客登录
//				Intent intent = new Intent(context, Activity_Main.class);
//				startActivity(intent);
//				finish();
//				break;
			case R.id.qqLoginButton:
				progressDialog.show();
				
//				Activity_Login_Bind.show(context);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						ApiResult jsonResult = ApiManager.invoke(context,new TestLoginApi());
						if (jsonResult != null && jsonResult.getResult() == 1) {
							Message message = loginHandler.obtainMessage(HANDLER_TEST_LOGIN_SUCCEED);
							message.obj = jsonResult.getData();
							message.sendToTarget();
						}
					}
				}).start();
				
				break;
			default:
				break;
			}
		}
	};
    
	
	
	
	
	
	
}
