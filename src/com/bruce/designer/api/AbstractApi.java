package com.bruce.designer.api;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import com.bruce.designer.constants.Config;
import com.bruce.designer.exception.DesignerException;
import com.bruce.designer.exception.ErrorCode;
import com.bruce.designer.model.result.ApiResult;

public abstract class AbstractApi {
	
	/*是否是多媒体请求*/
	protected boolean multipart = false;
	
	//protected byte[] multipartData = null;
	protected String filePath;
	
	
	/**
	 * api url
	 * @return
	 */
	protected String getRequestUri(){
		return Config.JINWAN_API_PREFIX;
	}
	
	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return true;
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
	protected abstract ApiResult processApiResult(int result, int errorcode, String message, String dataStr);
	
	
	public final ApiResult processResponse(String response) throws Exception{
		JSONObject jsonObject = new JSONObject(response);
		int result = jsonObject.optInt("result", 0);
		int errorcode = jsonObject.optInt("errorcode");
		String message = jsonObject.optString("message");
		String dataStr = jsonObject.optString("data");
		
		if(result!=1){//处理平台级的异常
			if(errorcode == ErrorCode.SYSTEM_MISSING_PARAM){
				throw new DesignerException(ErrorCode.SYSTEM_MISSING_PARAM);
			}
		}
		//交由子类处理业务数据
		return processApiResult(result, errorcode, message, dataStr);
	}

	public boolean isMultipart() {
		return multipart;
	}

	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

//	public byte[] getMultipartData() {
//		if(isMultipart()){
//			return multipartData;
//		}
//		return null;
//	}
//
//	public void setMultipartData(byte[] multipartData) {
//		this.multipartData = multipartData;
//	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
//	public final ApiResult processResponse(String response) throws Exception{
//		int errorcode = 0;
//		JSONObject jsonObject = new JSONObject(response);
//		int result = jsonObject.getInt("result");
//		if(result==1){//成功响应
//			String dataStr = jsonObject.optString("data", null);
//			//交由子类处理业务数据
//			if(dataStr!=null){
//				return processResultData(dataStr);
//			}
//			return ResponseBuilderUtil.buildSuccessResult();
//		}else{//错误响应
//			errorcode = jsonObject.getInt("errorcode");
//			String message = jsonObject.getString("message");
//			//t票失效或secretKey不正确或sig不匹配等情况下，需要抛出相应异常，以便进行特殊处理
//			boolean authencatiedError = true;
//			if(authencatiedError){
//				//TODO 抛出特定异常，通常需要跳转回登录界面
//				throw new Exception("requestUri: "+ getClass().getSimpleName());
//			}else{
//				apiResult = new ApiResult(result, null, errorcode, message);
//			}
//		}
//		return apiResult;
//	}
	
	
}
