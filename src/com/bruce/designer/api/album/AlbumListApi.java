package com.bruce.designer.api.album;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

public class AlbumListApi extends AbstractApi{
	
	private static final String REQUESTS_URI = Config.JINWAN_API_ALBUMS;

	private Map<String, String> paramMap = null;
	
	public AlbumListApi(int designerId, int albumsTailId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("designerId", String.valueOf(designerId));
		paramMap.put("albumsTailId", String.valueOf(albumsTailId));
	}
	
	@Override
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	@Override
	public RequestMethodEnum getRequestMethod() {
		return RequestMethodEnum.POST;
	}

	@Override
	public String getRequestUri() {
		return REQUESTS_URI;
	}
	
	@Override
	public JsonResultBean processResponse(String response) {
		JsonResultBean jsonResult = null;
		if(response!=null){
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);
				int result = jsonObject.getInt("result");
				if(result==1){//成功响应
					JSONObject jsonData = jsonObject.getJSONObject("data");
					int resTailId = jsonData.getInt("albumTailId");
					String albumListStr = jsonData.getString("albumList");
					List<Album> albumList = JsonUtil.gson.fromJson(albumListStr, new TypeToken<List<Album>>(){}.getType());
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("albumTailId", resTailId);
					map.put("albumList", albumList);
					jsonResult = new JsonResultBean(result, map, 0, null);
				}else{//错误响应
					int errorcode = jsonObject.getInt("errorcode");
					String message = jsonObject.getString("message");
					jsonResult = new JsonResultBean(result, null, errorcode, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
}
