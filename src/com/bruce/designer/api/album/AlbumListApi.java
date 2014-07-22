package com.bruce.designer.api.album;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

public class AlbumListApi extends AbstractApi{
	

	private Map<String, String> paramMap = null;
	
	public AlbumListApi(int designerId, int albumsTailId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("designerId", String.valueOf(designerId));
		paramMap.put("albumsTailId", String.valueOf(albumsTailId));
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
			int fromTailId = jsonData.getInt("fromTailId");
			int newTailId = jsonData.getInt("newTailId");
			String albumListStr = jsonData.getString("albumList");
			if(albumListStr!=null){
				List<Album> albumList = JsonUtil.gson.fromJson(albumListStr, new TypeToken<List<Album>>(){}.getType());
				dataMap.put("fromTailId", fromTailId);
				dataMap.put("newTailId", newTailId);
				dataMap.put("albumList", albumList);
				return ResponseBuilderUtil.buildSuccessResult(dataMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	@Override
	protected String getApiMethodName() {
		return "latestAlbum.cmd";
	}

}
