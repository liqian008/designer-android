package com.bruce.designer.api;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.model.Album;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.JsonUtil;

public abstract class AbstractApi {

	private JsonResultBean jsonResult = null;
	
	protected abstract Map<String, String> getParamMap();

	protected abstract String getRequestUri();
	
	protected RequestMethodEnum getRequestMethod(){
		return RequestMethodEnum.POST;
	}
	
	/**
	 * 抽象方法，子类需要对响应做处理
	 * @param response
	 * @return 
	 */
	protected abstract JsonResultBean processResponse(String response);
	
	protected JsonResultBean process(String response) throws Exception{
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			int result = jsonObject.getInt("result");
			//t票失效，sig不匹配等情况下，需要抛出相应异常，以便进行特别处理
			if(result==100||result==101){
				throw new Exception();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
}
