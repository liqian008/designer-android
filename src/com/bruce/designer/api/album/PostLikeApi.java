package com.bruce.designer.api.album;

import java.util.Map;
import java.util.TreeMap;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * 提交赞api
 * @author liqian
 *
 */
public class PostLikeApi extends AbstractApi { 

	private Map<String, String> paramMap = null;

	public PostLikeApi(int albumId, int designerId) {
		paramMap = new TreeMap<String, String>();
		paramMap.put("albumId", String.valueOf(albumId));
		paramMap.put("designerId", String.valueOf(designerId));
	}

	
	@Override
	protected ApiResult processResultData(String dataStr) {
		return ResponseBuilderUtil.buildSuccessResult();
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if(paramMap!=null){
			dataMap.putAll(paramMap);
		}
	}

	@Override
	protected String getApiMethodName() {
		return "like.cmd";
	}

}
