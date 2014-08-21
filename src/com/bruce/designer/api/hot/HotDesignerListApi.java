package com.bruce.designer.api.hot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.User;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

public class HotDesignerListApi extends AbstractApi{
	

	private Map<String, String> paramMap = null;
	
	public HotDesignerListApi(int mode){
		paramMap = new TreeMap<String, String>();
		paramMap.put("mode", String.valueOf(mode));
	}
	
	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if(paramMap!=null){
			dataMap.putAll(paramMap);
		}
	}
	
	@Override
	protected ApiResult processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			String hotDesignerListStr = jsonData.getString("hotDesignerList");
			if(hotDesignerListStr!=null){
				List<User> designerList = JsonUtil.gson.fromJson(hotDesignerListStr, new TypeToken<List<User>>(){}.getType());
				dataMap.put("designerList", designerList);
				return ResponseBuilderUtil.buildSuccessResult(dataMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	@Override
	protected String getApiMethodName() {
		return "hotDesigners.cmd";
	}

}
