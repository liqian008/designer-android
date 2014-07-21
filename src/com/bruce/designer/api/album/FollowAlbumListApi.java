package com.bruce.designer.api.album;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.Album;
import com.bruce.designer.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

public class FollowAlbumListApi extends AbstractApi{
	
	private Map<String, String> paramMap = null;
	
	public FollowAlbumListApi(int albumsTailId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("albumsTailId", String.valueOf(albumsTailId));
	}
	
	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if(paramMap!=null){
			dataMap.putAll(paramMap);
		}
	}

	@Override
	protected Map<String, Object> processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			int resTailId = jsonData.getInt("albumTailId");
			String albumListStr = jsonData.getString("albumList");
			if(albumListStr!=null){
				List<Album> albumList = JsonUtil.gson.fromJson(albumListStr, new TypeToken<List<Album>>(){}.getType());
				dataMap.put("albumTailId", resTailId);
				dataMap.put("albumList", albumList);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataMap;
	}

	@Override
	protected String getApiMethodName() {
		return "followAlbum.cmd";
	}

}
