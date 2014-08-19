package com.bruce.designer.api.message;

import java.util.Map;
import java.util.TreeMap;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * 提交评论api
 * @author liqian
 *
 */
public class PostChatApi extends AbstractApi { 

	private Map<String, String> paramMap = null;

	public PostChatApi(int toId, String content) {
		paramMap = new TreeMap<String, String>();
		paramMap.put("toId", String.valueOf(toId));
		paramMap.put("content", String.valueOf(content));
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
		return "postChat.cmd";
	}

}
