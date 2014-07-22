package com.bruce.designer.api;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import com.bruce.designer.constants.Config;
import com.bruce.designer.model.result.ApiResult;

public abstract class AbstractApi {
	
	private ApiResult apiResult = null;
	
	/**
	 * api url
	 * @return
	 */
	protected String getRequestUri(){
		return Config.JINWAN_API_PREFIX;
	}
	
	/*抽象方法，子类需要构造业务数据*/
	protected abstract void fillDataMap(Map<String, String> dataMap);
	/*抽象方法，子类需要构造apiMethodName*/
	protected abstract String getApiMethodName();
	
	public final Map<String, String> getParamMap(){
		Map<String, String> paramMap = new TreeMap<String, String>();
		//构造method名称
		paramMap.put("cmd_method", getApiMethodName());
		//构造业务请求参数
		fillDataMap(paramMap);
		return paramMap;
	}
	
	/*抽象方法，统一后可均使用post*/
	protected final RequestMethodEnum getRequestMethod(){
		return RequestMethodEnum.POST;
	}
	
	/**
	 * 抽象方法，子类需要对响应做处理
	 * @param data
	 * @return
	 */
	protected abstract ApiResult processResultData(String data);
	
	
	public final ApiResult processResponse(String response) throws Exception{
		int errorcode = 0;
		JSONObject jsonObject = new JSONObject(response);
		int result = jsonObject.getInt("result");
		if(result==1){//成功响应
			String dataStr = jsonObject.getString("data");
			//交由子类处理业务数据
			Map<String, Object> dataMap = null;//
			if(dataStr!=null){
//				dataMap = processResultData(dataStr);
				return processResultData(dataStr);
			}
			return new ApiResult(result, dataMap, errorcode, null);
		}else{//错误响应
			errorcode = jsonObject.getInt("errorcode");
			String message = jsonObject.getString("message");
			//t票失效或secretKey不正确或sig不匹配等情况下，需要抛出相应异常，以便进行特殊处理
			boolean authencatiedError = true;
			if(authencatiedError){
				//TODO 抛出特定异常，通常需要跳转回登录界面
				throw new Exception();
			}else{
				apiResult = new ApiResult(result, null, errorcode, message);
			}
		}
		return apiResult;
	}
	
}
