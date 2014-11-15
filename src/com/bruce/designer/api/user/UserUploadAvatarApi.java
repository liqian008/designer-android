package com.bruce.designer.api.user;

import java.util.Map;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * 用户上传头像api
 * @author liqian
 *
 */
public class UserUploadAvatarApi extends AbstractApi{
	
	public UserUploadAvatarApi(byte[] multipartData){
		this.multipartData = multipartData;
	}
	
	@Override
	protected ApiResult processApiResult(int result, int errorcode, String message, String dataStr) {
		if(result==1){
			return ResponseBuilderUtil.buildSuccessResult();
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		//do nothing
	}

	@Override
	protected String getApiMethodName() {
		return "uploadAvatar.cmd";
	}
	
	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return super.needAuth();
	}
}
