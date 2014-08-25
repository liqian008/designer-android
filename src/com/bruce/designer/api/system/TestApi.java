package com.bruce.designer.api.system;

import java.util.Map;
import java.util.TreeMap;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.result.ApiResult;

/**
 * 检查版本更新API
 * @author liqian
 *
 */
public class TestApi extends AbstractApi {

	private String REQUESTS_URI = null;

	private Map<String, String> paramMap = null;

	public TestApi() {
		REQUESTS_URI = Config.JINWAN_API_PREFIX;
	}

//	@Override
//	public Map<String, String> getParamMap() {
//		paramMap = new TreeMap<String, String>();
//		paramMap.put("cmd_method", "test.cmd");
//		paramMap.put("testParamA", "a1");
//		paramMap.put("testParamB", "b");
//		paramMap.put("testParamC", "c");
//		return paramMap;
//	}

	@Override
	public String getRequestUri() {
		return REQUESTS_URI;
	}

	@Override
	protected ApiResult processApiResult(int result, int errorcode, String message, String dataStr) {
		return null;
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
	}

	@Override
	protected String getApiMethodName() {
		// TODO Auto-generated method stub
		return null;
	}

}
