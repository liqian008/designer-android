package com.bruce.designer.api.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.User;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.JsonUtil;

public class UserInfoApi extends AbstractApi{
	
	private String REQUESTS_URI=null;
	
	
	private Map<String, String> paramMap = null;
	
	public UserInfoApi(int  userId){
		REQUESTS_URI = Config.JINWAN_API_PREFIX+"/"+userId+"/info.json";
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
	
	@Override
	public JsonResultBean processResponse(String response) {
		JsonResultBean jsonResult = null;
		if(response!=null){
			try {
				JSONObject jsonObject = new JSONObject(response);
				int result = jsonObject.getInt("result");
				if(result==1){//成功响应
					JSONObject jsonData = jsonObject.getJSONObject("data");
					int followsCount = jsonData.getInt("followsCount");
					int fansCount = jsonData.getInt("fansCount");
					
					String userinfoStr = jsonData.getString("userinfo");
					User userinfo = JsonUtil.gson.fromJson(userinfoStr, User.class);
					if(userinfo!=null){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("userinfo", userinfo);
						map.put("followsCount", followsCount);
						map.put("fansCount", fansCount);
						jsonResult = new JsonResultBean(result, map, 0, null);
					}
				}else{//错误响应
					int errorcode = jsonObject.getInt("errorcode");
					String message = jsonObject.getString("message");
					jsonResult = new JsonResultBean(result, null, errorcode, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
}
