package com.bruce.designer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.api.ApiWrapper;
import com.bruce.designer.api.account.WeiboLoginApi;
import com.bruce.designer.constants.ConstantOAuth;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.SharedPreferenceUtil;
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
	private static final int HANDLER_FLAG_WB_LOGIN_SUCCESS = 1;
	/*微博登录失败*/
	private static final int HANDLER_FLAG_WB_LOGIN_FAILED = 2;
	
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
		
		Button wbLoginBtn = (Button) findViewById(R.id.wbLoginButton);
		Button guestLoginBtn = (Button) findViewById(R.id.guestLoginButton);
		
		wbLoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//跳转wb oauth
				WeiboAuth mWeiboAuth = new WeiboAuth(context, ConstantOAuth.APP_KEY,
						ConstantOAuth.REDIRECT_URL, ConstantOAuth.SCOPE);
				//SSO登录
				mSsoHandler = new SsoHandler((Activity) context, mWeiboAuth);
				mSsoHandler.authorize(new AuthListener());
			}
		});
		
		guestLoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, Activity_Main.class);
				startActivity(intent);
				finish();
			}
		});
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
            	short thirdpartyTypeWb = 1;
            	
            	LogUtil.d("=============="+mAccessToken.getUid()+"========"+ mAccessToken.getToken());
                // 显示 Token
            	Toast.makeText(context,  mAccessToken.getUid()+"========"+ mAccessToken.getToken(), Toast.LENGTH_LONG).show();
                Toast.makeText(context, R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                
                Activity_Login_Bind.show(context);
                finish();
                //TODO 向服务器提交token
//                weiboLogin(uid, accessToken, new WeiboLoginListener());
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
     * @param thirdpartyType
     */
    private void weiboLogin(final String uid, final String accessToken, final OAuthLoginListener oAuthLoginListener) {
		//启动线程获取用户数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				WeiboLoginApi api = new WeiboLoginApi(uid, accessToken);
				JsonResultBean jsonResult = ApiWrapper.invoke(context, api);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					//weibo登录成功，两种情况
					boolean result = true;
					if(result){//直接返回用户ticket信息
						UserPassport userPassport = new UserPassport(1, "asdfghjkl", "");
						oAuthLoginListener.loginComplete(userPassport);
					}else{//第一次登录，需要完善用户资料
						oAuthLoginListener.needComplete();
					}
				}else{//发送失败消息
					oAuthLoginListener.loginFailed();
				}
			}
		}).start();
	}
    
    
//    private Handler loginHandler = new Handler(){
//		@SuppressWarnings("unchecked")
//		public void handleMessage(Message msg) {
//			switch(msg.what){
//				case HANDLER_FLAG_WB_LOGIN_SUCCESS:
//					//1、直接登录进入
//					
//					//2、需要绑定账户
//					
//					break;
//				case HANDLER_FLAG_WB_LOGIN_FAILED:
//					
//					break;
//				default:
//					break;
//			}
//		};
//	};
    
    class WeiboLoginListener implements OAuthLoginListener{
		@Override
		public void loginComplete(UserPassport userPassport) {
			UiUtil.showShortToast(context, "登录成功");
//			SharedPreferenceUtil.writeObjectToSp(userPassport, Config.SP_CONFIG_ACCOUNT, Config.SP_KEY_USERPASSPORT);
		}
		@Override
		public void needComplete() {
			UiUtil.showShortToast(context, "登录成功，需要完善个人资料");
		}

		@Override
		public void loginFailed() {
			UiUtil.showShortToast(context, "登录失败");
		}
    	
    }
    
    
    
    
    
}
