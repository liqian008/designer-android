package com.bruce.designer.api.album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumFavorite;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 我收藏的专辑列表api
 * @author liqian
 *
 */
public class FavoriteAlbumsListApi extends AbstractApi{
	
	private Map<String, String> paramMap = null;
	
	public FavoriteAlbumsListApi(int tailId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("tailId", String.valueOf(tailId));
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
				int fromTailId = jsonData.getInt("fromTailId");
				int newTailId = jsonData.getInt("newTailId");
				String favoriteListStr = jsonData.getString("favoriteList");
				if(favoriteListStr!=null){
					List<AlbumFavorite> favoriteList = JsonUtil.gson.fromJson(favoriteListStr, new TypeToken<List<AlbumFavorite>>(){}.getType());
					//需要在此将转为albumList，送给ListView adapter
					List<Album> albumList = new ArrayList<Album>();
					if(favoriteList!=null&&favoriteList.size()>0){
						for(AlbumFavorite favorite: favoriteList){
							Album album = favorite.getAlbum();
							if(album!=null){
								albumList.add(album);
							}
						}
					}
					
					dataMap.put("albumList", albumList);
					dataMap.put("fromTailId", fromTailId);
					dataMap.put("newTailId", newTailId);
					return ResponseBuilderUtil.buildSuccessResult(dataMap);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	@Override
	protected String getApiMethodName() {
		return "favoriteAlbums.cmd";
	}

	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return true;
	}
}
