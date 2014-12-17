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
import com.bruce.designer.util.StringUtils;
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
	protected ApiResult processApiResult(int result, int errorcode, String message, String dataStr) {
		if(result==1){
			JSONObject jsonData;
			Map<String, Object> dataMap = new HashMap<String, Object>();
			try {
				jsonData = new JSONObject(dataStr);
				int fromTailId = jsonData.optInt("fromTailId", 0);
				int newTailId = jsonData.optInt("newTailId", 0);
				
				List<Album> albumList = null;
				String albumListStr = jsonData.optString("albumList", "");
				if(!StringUtils.isBlank(albumListStr)){
					albumList = JsonUtil.gson.fromJson(albumListStr, new TypeToken<List<Album>>(){}.getType());
				}
				dataMap.put("fromTailId", fromTailId);
				dataMap.put("newTailId", newTailId);
				dataMap.put("albumList", albumList);
				return ResponseBuilderUtil.buildSuccessResult(dataMap);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	@Override
	protected String getApiMethodName() {
		return "followAlbum.cmd";
	}

	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return true;
	}
}
