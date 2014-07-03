package com.bruce.designer.api;

import java.util.Map;

import org.json.JSONObject;

import com.bruce.designer.model.json.JsonResultBean;

public abstract class AbstractApi {
	
	private JsonResultBean jsonResult = null;
	
	protected abstract Map<String, String> getParamMap();

	protected abstract String getRequestUri();
	
	protected RequestMethodEnum getRequestMethod(){
		return RequestMethodEnum.POST;
	}
	
	/**
	 * 抽象方法，子类需要对响应做处理
	 * @param data
	 * @return
	 */
	protected abstract Map<String, Object> processBusinessData(String data);
	
	public JsonResultBean processResponse(String response) throws Exception{
		int errorcode = 0;
		JSONObject jsonObject = new JSONObject(response);
		int result = jsonObject.getInt("result");
		if(result==1){//成功响应
			String dataStr = jsonObject.getString("data");
			//交由子类处理业务数据
			Map<String, Object> dataMap = processBusinessData(dataStr);
			return new JsonResultBean(result, dataMap, errorcode, null);
		}else{//错误响应
			errorcode = jsonObject.getInt("errorcode");
			String message = jsonObject.getString("message");
			//t票失效，sig不匹配等情况下，需要抛出相应异常，以便进行特殊处理
			boolean specError = true;
			if(specError){
				throw new Exception();
			}else{
				jsonResult = new JsonResultBean(result, null, errorcode, message);
			}
		}
		return jsonResult;
	}
	
}
