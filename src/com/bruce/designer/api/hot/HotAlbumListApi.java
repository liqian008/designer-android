package com.bruce.designer.api.hot;

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

public class HotAlbumListApi extends AbstractApi{
	

	private Map<String, String> paramMap = null;
	
	public HotAlbumListApi(int mode){
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
			String hotAlbumListStr = jsonData.getString("hotAlbumList");
			if(hotAlbumListStr!=null){
				List<Album> albumList = JsonUtil.gson.fromJson(hotAlbumListStr, new TypeToken<List<Album>>(){}.getType());
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
		return "hotAlbums.cmd";
	}

}
