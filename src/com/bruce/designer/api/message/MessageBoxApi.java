package com.bruce.designer.api.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.Message;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

public class MessageBoxApi extends AbstractApi {

	private Map<String, String> paramMap = null;
	
	public MessageBoxApi(){
//		paramMap = new TreeMap<String, String>();
	}

	@Override
	protected ApiResult processApiResult(int result, int errorcode, String message, String dataStr) {
		if(result==1){
			JSONObject jsonData;
			Map<String, Object> dataMap = new HashMap<String, Object>();
			try {
				jsonData = new JSONObject(dataStr);
				String messageBoxListStr = jsonData.optString("messageBoxList");
				if(messageBoxListStr!=null){
					List<Message> messageBoxList = JsonUtil.gson.fromJson(messageBoxListStr, new TypeToken<List<Message>>() {}.getType());
					if (messageBoxList != null) {
						dataMap.put("messageBoxList", messageBoxList);
						return ResponseBuilderUtil.buildSuccessResult(dataMap);
					}
				}
			}catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}


	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if(paramMap!=null){
			dataMap.putAll(paramMap);
		}
	}

	@Override
	protected String getApiMethodName() {
		return "messageBox.cmd";
	}
	
	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return false;
	}

}
