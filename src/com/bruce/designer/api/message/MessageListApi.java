package com.bruce.designer.api.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.Message;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

public class MessageListApi extends AbstractApi {

private Map<String, String> paramMap = null;
	
	public MessageListApi(int messageType, long messageTailId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("messageType", String.valueOf(messageType));
		paramMap.put("messageTailId", String.valueOf(messageTailId));
	}

	@Override
	protected ApiResult processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			String messageListStr = jsonData.optString("messageList");
			long fromTailId = jsonData.optLong("fromTailId", 0);
			long newTailId = jsonData.optLong("newTailId", 0);
			if(messageListStr!=null){
				List<Message> messageList = JsonUtil.gson.fromJson(messageListStr, new TypeToken<List<Message>>() {}.getType());
				if (messageList != null) {
					dataMap.put("messageList", messageList);
					dataMap.put("fromTailId", fromTailId);
					dataMap.put("newTailId", newTailId);
					return ResponseBuilderUtil.buildSuccessResult(dataMap);
				}
			}
		}catch (JSONException e) {
			e.printStackTrace();
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
		return "messageList.cmd";
	}

}
