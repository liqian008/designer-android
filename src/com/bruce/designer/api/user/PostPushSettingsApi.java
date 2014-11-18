package com.bruce.designer.api.user;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ResponseBuilderUtil;

public class PostPushSettingsApi extends AbstractApi {

	private int mode;
	
	private Map<String, String> paramMap = null;

	@Override
	protected String getApiMethodName() {
		return "pushSettings.cmd";
	}

	public PostPushSettingsApi(int mode, long pushMask) {
		this.mode = mode;
		paramMap = new TreeMap<String, String>();
		paramMap.put("mode", String.valueOf(mode));//1为写，0为读
		paramMap.put("pushMask", String.valueOf(pushMask));
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if (paramMap != null) {
			dataMap.putAll(paramMap);
		}
	}

	@Override
	protected ApiResult processApiResult(int result, int errorcode, String message, String dataStr) {
		if (result == 1) {
			if(mode==0){//读操作，需要获取返回的结果
				long pushMask = 0l;
				JSONObject jsonData;
				Map<String, Object> dataMap = new HashMap<String, Object>();
				try {
					jsonData = new JSONObject(dataStr);
					pushMask = jsonData.optLong("pushMask");
					dataMap.put("pushMask", pushMask); 
				}catch(Exception e){
				}
				return ResponseBuilderUtil.buildSuccessResult(dataMap);
			}else{
				return ResponseBuilderUtil.buildSuccessResult();
			}
		} else {
			return ResponseBuilderUtil.buildErrorResult(0);
		}
	}

	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return true;
	}
}
