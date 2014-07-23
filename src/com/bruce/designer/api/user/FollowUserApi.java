package com.bruce.designer.api.user;

import java.util.Map;
import java.util.TreeMap;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ResponseBuilderUtil;

public class FollowUserApi extends AbstractApi {

	private Map<String, String> paramMap = null;

	@Override
	protected String getApiMethodName() {
		return "follow.cmd";
	}

	public FollowUserApi(int designerId, int mode) {
		paramMap = new TreeMap<String, String>();
		paramMap.put("designerId", String.valueOf(designerId));
		paramMap.put("mode", String.valueOf(mode));
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if (paramMap != null) {
			dataMap.putAll(paramMap);
		}
	}
	
	@Override
	protected ApiResult processResultData(String dataStr) {
		return ResponseBuilderUtil.buildSuccessResult();
	}

	

}
