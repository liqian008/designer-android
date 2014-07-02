package com.bruce.designer.api.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.User;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

/**
 * weibo登录
 * @author liqian
 *
 */
public class WeiboLoginApi extends AbstractApi{
	
	private String REQUESTS_URI= Config.JINWAN_API_PREFIX+"/wbLogin.json";
	
	
	private Map<String, String> paramMap = null;
	
	public WeiboLoginApi(String wbUid, String accessToken){
		paramMap = new TreeMap<String, String>();
		paramMap.put("wbUid", String.valueOf(wbUid));
		paramMap.put("accessToken", accessToken);
	}
	
	@Override
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	@Override
	public RequestMethodEnum getRequestMethod() {
		return RequestMethodEnum.POST;
	}

	@Override
	public String getRequestUri() {
		return REQUESTS_URI;
	}
	
//	@Override
//	public JsonResultBean processResponse(String response) {
//		JsonResultBean jsonResult = null;
//		if(response!=null){
//			try {
//				JSONObject jsonObject = new JSONObject(response);
//				int result = jsonObject.getInt("result");
//				if(result==1){//成功响应
//					JSONObject jsonData = jsonObject.getJSONObject("data");
//					int followsCount = jsonData.getInt("followsCount");
//					int fansCount = jsonData.getInt("fansCount");
//					
//					String userinfoStr = jsonData.getString("userinfo");
//					User userinfo = JsonUtil.gson.fromJson(userinfoStr, User.class);
//					if(userinfo!=null){
//						Map<String, Object> map = new HashMap<String, Object>();
//						map.put("userinfo", userinfo);
//						map.put("followsCount", followsCount);
//						map.put("fansCount", fansCount);
//						jsonResult = new JsonResultBean(result, map, 0, null);
//					}
//				}else{//错误响应
//					int errorcode = jsonObject.getInt("errorcode");
//					String message = jsonObject.getString("message");
//					jsonResult = new JsonResultBean(result, null, errorcode, message);
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		return jsonResult;
//	}
	
	@Override
	protected Map<String, Object> processBusinessData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			int followsCount = jsonData.getInt("followsCount");
			int fansCount = jsonData.getInt("fansCount");
			
			String userinfoStr = jsonData.getString("userinfo");
			User userinfo = JsonUtil.gson.fromJson(userinfoStr, User.class);
			if(userinfo!=null){
				dataMap.put("userinfo", userinfo);
				dataMap.put("followsCount", followsCount);
				dataMap.put("fansCount", fansCount);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataMap;
	}

}
