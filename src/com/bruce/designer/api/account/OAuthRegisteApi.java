package com.bruce.designer.api.account;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.User;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * 绑定注册新账户
 * @author liqian
 *
 */
public class OAuthRegisteApi extends AbstractApi{
	
	private Map<String, String> paramMap = null;
	
	public OAuthRegisteApi(String thirdpartyType, String username, String nickname ,String password, String thirdpartyUid,  String thirdpartyUname,  String thirdpartyAvatar, String accessToken, String refreshToken, long expiresTime){
		paramMap = new TreeMap<String, String>();
		paramMap.put("thirdpartyType", thirdpartyType);
		
		paramMap.put("username", username);
		paramMap.put("nickname", nickname);
		paramMap.put("password", password);
		
		paramMap.put("thirdpartyUid", String.valueOf(thirdpartyUid));
		paramMap.put("thirdpartyUname", String.valueOf(thirdpartyUname));
		paramMap.put("thirdpartyAvatar", String.valueOf(thirdpartyAvatar));
		paramMap.put("thirdpartyAccessToken", accessToken);
		paramMap.put("thirdpartyRefreshToken", refreshToken);
		paramMap.put("thirdpartyExpireIn", String.valueOf(expiresTime));
	}
	
	@Override
	protected String getApiMethodName() {
		return "oauthRegiste.cmd";
	}
	
	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return false;
	}
	
	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if(paramMap!=null){
			dataMap.putAll(paramMap);
		}
	}

	@Override
	protected ApiResult processApiResult(int result, int errorcode, String message, String dataStr) {
		if(result==1){
			JSONObject jsonData;
			Map<String, Object> dataMap = new HashMap<String, Object>();
			try {
				jsonData = new JSONObject(dataStr);
				String userPassportStr = jsonData.optString("userPassport");
				UserPassport userPassport = JsonUtil.gson.fromJson(userPassportStr, UserPassport.class);
				
				String hostUserStr = jsonData.optString("hostUser");
				User hostUser = JsonUtil.gson.fromJson(hostUserStr, User.class);
				if(userPassport!=null&&hostUser!=null){
					dataMap.put("userPassport", userPassport);
					dataMap.put("hostUser", hostUser);
					return ResponseBuilderUtil.buildSuccessResult(dataMap);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	

}
