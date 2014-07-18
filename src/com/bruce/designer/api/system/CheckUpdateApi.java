package com.bruce.designer.api.system;

import java.util.Map;

import com.bruce.designer.api.AbstractApi;

/**
 * 检查版本更新API
 * @author liqian
 *
 */
public class CheckUpdateApi extends AbstractApi {

	public CheckUpdateApi() {
	}

	@Override
	protected Map<String, Object> processResultData(String data) {
		return null;
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
	}

	@Override
	protected String getApiMethodName() {
		return "test.cmd";
	}

}
