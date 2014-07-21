package com.bruce.designer.util;

import com.bruce.designer.model.result.ApiResult;

/**
 * 构造Response对象api
 * 
 * @author liqian
 * 
 */
public final class ResponseBuilderUtil {
	
	public final static int RESULT_SUCCESS = 0X01;

	public final static int RESULT_FAILED = 0X00;

	private ResponseBuilderUtil() {
		super();
	}

	public static ApiResult buildSuccessResult() {
		return buildSuccessResult(null);
	}

	/**
	 * 构造成功的响应对象
	 * 
	 * @param data
	 * @return
	 */
	public static ApiResult buildSuccessResult(Object data) {
		ApiResult responseResult = new ApiResult();
		responseResult.setResult(RESULT_SUCCESS);
		if (data != null) {
			responseResult.setData(data);
		}
		return responseResult;
	}

	public static ApiResult buildErrorResult(int errorCode) {
		ApiResult responseResult = new ApiResult();
		responseResult.setResult(RESULT_FAILED);
		responseResult.setErrorcode(errorCode);
		return responseResult;
	}

	public static ApiResult buildErrorResult(int errorCode, String errorMsg) {
		ApiResult responseResult = new ApiResult();
		responseResult.setResult(RESULT_FAILED);
		responseResult.setErrorcode(errorCode);
		responseResult.setMessage(errorMsg);
		return responseResult;
	}


}
