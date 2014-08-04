package com.bruce.designer.api.album;

import java.util.Map;
import java.util.TreeMap;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * 提交收藏api
 * @author liqian
 *
 */
public class PostFavoriteApi extends AbstractApi { 

	private Map<String, String> paramMap = null;

	public PostFavoriteApi(int albumId, int designerId, int mode) {
		paramMap = new TreeMap<String, String>();
		paramMap.put("albumId", String.valueOf(albumId));
		paramMap.put("designerId", String.valueOf(designerId));
		paramMap.put("mode", String.valueOf(mode));
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
		return "favorite.cmd";
	}

}
