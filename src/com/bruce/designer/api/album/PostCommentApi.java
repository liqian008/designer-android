package com.bruce.designer.api.album;

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
public class PostCommentApi extends AbstractApi { 

	private Map<String, String> paramMap = null;

	public PostCommentApi(int albumId, int designerId, int toId, String comment) {
		paramMap = new TreeMap<String, String>();
		paramMap.put("albumId", String.valueOf(albumId));
		paramMap.put("designerId", String.valueOf(designerId));
		paramMap.put("toId", String.valueOf(toId));
		paramMap.put("comment", String.valueOf(comment));
	}

	
	@Override
	protected ApiResult processApiResult(int result, int errorcode, String message, String dataStr) {
		if(result==1){
			return ResponseBuilderUtil.buildSuccessResult();
		}else{
			return ResponseBuilderUtil.buildErrorResult(0);
		}
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if(paramMap!=null){
			dataMap.putAll(paramMap);
		}
	}

	@Override
	protected String getApiMethodName() {
		return "postComment.cmd";
	}

	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return true;
	}
}
