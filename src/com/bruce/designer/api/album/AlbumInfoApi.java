package com.bruce.designer.api.album;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;

public class AlbumInfoApi extends AbstractApi{
	
	
	
	private Map<String, String> paramMap = null;
	
	public AlbumInfoApi(int albumId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("albumId", String.valueOf(albumId));
	}
	
	
//	@Override
//	public JsonResultBean processResponse(String response) {
//		JsonResultBean jsonResult = null;
//		if(response!=null){
//			try {
//				JSONObject jsonObject = new JSONObject(response);
//				int result = jsonObject.getInt("result");
//				if(result==1){//成功响应
//					JSONObject jsonData = jsonObject.getJSONObject("data");
//					String albumInfoStr = jsonData.getString("albumInfo");
//					Album albumInfo = JsonUtil.gson.fromJson(albumInfoStr, Album.class);
//					if(albumInfo!=null){
//						jsonResult = new JsonResultBean(result, albumInfo, 0, null);
//					}
//				}else{//错误响应
//					int errorcode = jsonObject.getInt("errorcode");
//					String message = jsonObject.getString("message");
//					jsonResult = new JsonResultBean(result, null, errorcode, message);
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		return jsonResult;
//	}

	@Override
	protected ApiResult processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			String albumInfoStr = jsonData.getString("albumInfo");
			Album albumInfo = JsonUtil.gson.fromJson(albumInfoStr, Album.class);
			if(albumInfo!=null){
				dataMap.put("albumInfo", albumInfo);
				return ResponseBuilderUtil.buildSuccessResult(dataMap);
			}
		} catch (JSONException e) {
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
		return "albumInfo.cmd";
	}
	
}
