package com.bruce.designer.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import com.bruce.designer.constants.Config;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.Comment;
import com.bruce.designer.model.User;
import com.bruce.designer.model.UserFan;
import com.bruce.designer.model.UserFollow;
import com.bruce.designer.model.json.JsonResultBean;
import com.google.gson.reflect.TypeToken;

public class ApiUtil {
	
//	/**
//	 * 主屏的产品列表
//	 * @param albumTailId
//	 * @return
//	 */
//	public static JsonResultBean getAlbumList(int designerId, int albumsTailId){
//		
//		String albumListUrl = Config.JINWAN_API_ALBUMS;// + "?designerId="+designerId+"&albumsTailId="+albumTailId;
//		LogUtil.d("=====albumListUrl======"+albumListUrl);
//		String albumListJsonResult = null;
//		JsonResultBean jsonResult = null;
//		try {
//			TreeMap<String, String> paramMap = new TreeMap<String, String>();
//			paramMap.put("designerId", String.valueOf(designerId));
//			paramMap.put("albumsTailId", String.valueOf(albumsTailId));
//			
//			//albumListJsonResult = API.httpGet(albumListUrl, paramMap);
//			albumListJsonResult = API.httpGet(albumListUrl, paramMap);
//			
//			JSONObject jsonObject = new JSONObject(albumListJsonResult);
//			int result = jsonObject.getInt("result");
//			if(result==1){//成功响应
//				JSONObject jsonData = jsonObject.getJSONObject("data");
//				int resTailId = jsonData.getInt("albumTailId");
//				String albumListStr = jsonData.getString("albumList");
//				List<Album> albumList = JsonUtil.gson.fromJson(albumListStr, new TypeToken<List<Album>>(){}.getType());
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("albumTailId", resTailId);
//				map.put("albumList", albumList);
//				jsonResult = new JsonResultBean(result, map, 0, null);
//			}else{//错误响应
//				int errorcode = jsonObject.getInt("errorcode");
//				String message = jsonObject.getString("message");
//				jsonResult = new JsonResultBean(result, null, errorcode, message);
//			}
//			return jsonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====albumListJsonResult======"+albumListJsonResult);
//		return null;
//	}
//	
//	
//	/**
//	 * 获取产品详情
//	 * @param albumId
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static JsonResultBean getAlbumInfo(int albumId){
//		
//		String albumInfoUrl = Config.JINWAN_API_PREFIX+"/album/"+albumId + ".json";
//		
//		LogUtil.d("=====albumInfoUrl======"+albumInfoUrl);
//		String albumInfoJsonResult = null;
//		JsonResultBean jsonResult = null;
//		try {
//			TreeMap<String, String> paramMap = new TreeMap<String, String>();
//			paramMap.put("albumId", String.valueOf(albumId));
//			
//			albumInfoJsonResult = API.httpGet(albumInfoUrl, paramMap);
//			JSONObject jsonObject = new JSONObject(albumInfoJsonResult);
//			int result = jsonObject.getInt("result");
//			if(result==1){//成功响应
//				JSONObject jsonData = jsonObject.getJSONObject("data");
//				String albumInfoStr = jsonData.getString("albumInfo");
//				Album albumInfo = JsonUtil.gson.fromJson(albumInfoStr, Album.class);
//				if(albumInfo!=null){
//					jsonResult = new JsonResultBean(result, albumInfo, 0, null);
//				}
//			}else{//错误响应
//				int errorcode = jsonObject.getInt("errorcode");
//				String message = jsonObject.getString("message");
//				jsonResult = new JsonResultBean(result, null, errorcode, message);
//			}
//			return jsonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====albumInfoUrl======"+albumInfoJsonResult);
//		return null;
//	}
//	
//	/**
//	 * 获取产品的评论列表
//	 * @param albumId
//	 * @param commentsTailId
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static JsonResultBean getAlbumComments(int albumId, int commentsTailId){
//		String albumCommentsUrl = Config.JINWAN_API_PREFIX+"/moreComments.json";//?albumId="+albumId+"&commentsTailId="+commentsTailId;
//		
//		LogUtil.d("=====albumCommentsUrl======"+albumCommentsUrl);
//		String albumCommentsJsonResult = null;
//		JsonResultBean jsonResult = null;
//		try {
//			TreeMap<String, String> paramMap = new TreeMap<String, String>();
//			paramMap.put("albumId", String.valueOf(albumId));
//			paramMap.put("commentsTailId", String.valueOf(commentsTailId));
//			
//			albumCommentsJsonResult = API.httpGet(albumCommentsUrl, paramMap);
//			JSONObject jsonObject = new JSONObject(albumCommentsJsonResult);
//			int result = jsonObject.getInt("result");
//			if(result==1){//成功响应
//				JSONObject jsonData = jsonObject.getJSONObject("data");
//				int resTailId = jsonData.getInt("tailId");
//				String commentListStr = jsonData.getString("commentList");
//				List<Comment> commentList = JsonUtil.gson.fromJson(commentListStr, new TypeToken<List<Comment>>(){}.getType());
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("commentTailId", resTailId);
//				map.put("commentList", commentList);
//				jsonResult = new JsonResultBean(result, map, 0, null);
//			}else{//错误响应
//				int errorcode = jsonObject.getInt("errorcode");
//				String message = jsonObject.getString("message");
//				jsonResult = new JsonResultBean(result, null, errorcode, message);
//			}
//			return jsonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====albumCommentsJsonResult======"+albumCommentsJsonResult);
//		return null;
//	}
//	
//	
//	/**
//	 * 获取产品的评论列表
//	 * @param albumId
//	 * @param commentsTailId
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static JsonResultBean getUserinfo(int userId){
//		String userinfoUrl = Config.JINWAN_API_PREFIX+"/"+userId+"/info.json";
//		
//		LogUtil.d("=====userinfoUrl======"+userinfoUrl);
//		String userinfoJsonResult = null;
//		JsonResultBean jsonResult = null;
//		try {
//			userinfoJsonResult = API.httpGet(userinfoUrl, null);
//			JSONObject jsonObject = new JSONObject(userinfoJsonResult);
//			int result = jsonObject.getInt("result");
//			if(result==1){//成功响应
//				JSONObject jsonData = jsonObject.getJSONObject("data");
//				int followsCount = jsonData.getInt("followsCount");
//				int fansCount = jsonData.getInt("fansCount");
//				
//				String userinfoStr = jsonData.getString("userinfo");
//				User userinfo = JsonUtil.gson.fromJson(userinfoStr, User.class);
//				if(userinfo!=null){
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("userinfo", userinfo);
//					map.put("followsCount", followsCount);
//					map.put("fansCount", fansCount);
//					jsonResult = new JsonResultBean(result, map, 0, null);
//				}
//			}else{//错误响应
//				int errorcode = jsonObject.getInt("errorcode");
//				String message = jsonObject.getString("message");
//				jsonResult = new JsonResultBean(result, null, errorcode, message);
//			}
//			return jsonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====userinfoJsonResult======"+userinfoJsonResult);
//		return null;
//	}
//	
//	
//	/**
//	 * 获取用户的关注列表
//	 * @param albumId
//	 * @param commentsTailId
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static JsonResultBean getUserFollows(int userId){
//		String userFollowsUrl = Config.JINWAN_API_PREFIX+"/"+userId+"/follows.json";
//		
//		LogUtil.d("=====userFollowsUrl======"+userFollowsUrl);
//		String userFollowsJsonResult = null;
//		JsonResultBean jsonResult = null;
//		try {
//			
//			userFollowsJsonResult = API.httpGet(userFollowsUrl);
//			JSONObject jsonObject = new JSONObject(userFollowsJsonResult);
//			int result = jsonObject.getInt("result");
//			if(result==1){//成功响应
//				JSONObject jsonData = jsonObject.getJSONObject("data");
//				String followListStr = jsonData.getString("followList");
//				List<UserFollow> followList = JsonUtil.gson.fromJson(followListStr, new TypeToken<List<UserFollow>>(){}.getType());
//				if(followList!=null){
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("followList", followList);
//					jsonResult = new JsonResultBean(result, map, 0, null);
//				}
//			}else{//错误响应
//				int errorcode = jsonObject.getInt("errorcode");
//				String message = jsonObject.getString("message");
//				jsonResult = new JsonResultBean(result, null, errorcode, message);
//			}
//			return jsonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====userFollowsJsonResult======"+userFollowsJsonResult);
//		return null;
//	}
//	
//	/**
//	 * 获取用户的粉丝列表
//	 * @param albumId
//	 * @param commentsTailId
//	 * @return
//	 */
//	public static JsonResultBean getUserFans(int userId){
//		String userFansUrl = Config.JINWAN_API_PREFIX+"/"+userId+"/fans.json";
//		
//		LogUtil.d("=====userFansUrl======"+userFansUrl);
//		String userFansJsonResult = null;
//		JsonResultBean jsonResult = null;
//		try {
//			userFansJsonResult = API.httpGet(userFansUrl);
//			JSONObject jsonObject = new JSONObject(userFansJsonResult);
//			int result = jsonObject.getInt("result");
//			if(result==1){//成功响应
//				JSONObject jsonData = jsonObject.getJSONObject("data");
//				String fanListStr = jsonData.getString("fanList");
//				List<UserFan> fanList = JsonUtil.gson.fromJson(fanListStr, new TypeToken<List<UserFan>>(){}.getType());
//				if(fanList!=null){
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("fanList", fanList);
//					jsonResult = new JsonResultBean(result, map, 0, null);
//				}
//			}else{//错误响应
//				int errorcode = jsonObject.getInt("errorcode");
//				String message = jsonObject.getString("message");
//				jsonResult = new JsonResultBean(result, null, errorcode, message);
//			}
//			return jsonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====userFansJsonResult======"+userFansJsonResult);
//		return null;
//	}
//	
//	
//	/**
//	 * 微博登录（通常在用户首次进入，或session失效时会进入）
//	 * @param uid
//	 * @param accessToken
//	 * @param thirdpartyType
//	 * @return
//	 */
//	public static JsonResultBean wbLogin(String uid, String accessToken, int thirdpartyType){
//		String wbLoginUrl = Config.JINWAN_API_PREFIX+"/wbLogin.json";
//		
//		String wbLoginJsonResult = null;
//		JsonResultBean jsonResult = null;
//		try {
//			Map<String, String> dataMap = new HashMap<String, String>();
//			dataMap.put("uid", String.valueOf(uid));
//			dataMap.put("accessToken", String.valueOf(accessToken));
//			dataMap.put("thirdpartyType", String.valueOf(thirdpartyType));
//			
//			wbLoginJsonResult = HttpClientUtils.httpPost(wbLoginUrl, dataMap);
//			JSONObject jsonObject = new JSONObject(wbLoginJsonResult);
//			int result = jsonObject.getInt("result");
//			if(result==1){//成功响应
//				JSONObject jsonData = jsonObject.getJSONObject("data");
//				
//			}else{//错误响应
//				int errorcode = jsonObject.getInt("errorcode");
//				String message = jsonObject.getString("message");
//				jsonResult = new JsonResultBean(result, null, errorcode, message);
//			}
//			return jsonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====wbLoginJsonResult======"+wbLoginJsonResult);
//		return null;
//	}
//	
//	/**
//	 * 新用户注册（用户oauth成功后的数据提交）
//	 * @param username
//	 * @param nickname
//	 * @param password
//	 * @return
//	 */
//	public static JsonResultBean register(String username, String nickname, String password){
//		String registerUrl = Config.JINWAN_API_PREFIX+"/register.json";
//		
//		String registerJsonResult = null;
//		JsonResultBean jsonResult = null;
//		try {
//			Map<String, String> dataMap = new HashMap<String, String>();
//			dataMap.put("username", username);
//			dataMap.put("nickname", nickname);
//			dataMap.put("password", password);
//			
//			registerJsonResult = HttpClientUtils.httpPost(registerUrl, dataMap);
//			JSONObject jsonObject = new JSONObject(registerJsonResult);
//			int result = jsonObject.getInt("result");
//			if(result==1){//成功响应
//				JSONObject jsonData = jsonObject.getJSONObject("data");
//				//注册成功，理应返回类似secret等数据，维持用户id
//				
//			}else{//错误响应
//				int errorcode = jsonObject.getInt("errorcode");
//				String message = jsonObject.getString("message");
//				jsonResult = new JsonResultBean(result, null, errorcode, message);
//			}
//			return jsonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====registerJsonResult======"+registerJsonResult);
//		return null;
//	}
	
	
//	@SuppressWarnings("unchecked")
//	public static Map<String, Object> getAlbumList(int albumTailId){
//		
//		String albumListUrl = Config.JINWAN_API_ALBUMS + "?albumsTailId="+albumTailId;
//		LogUtil.d("=====albumListUrl======"+albumListUrl);
//		String albumListJsonResult = null;
//		try {
//			albumListJsonResult = API.httpGet(albumListUrl);
//			AlbumsResultWrapper albumsResult = JsonUtil.gson.fromJson(albumListJsonResult, AlbumsResultWrapper.class);
//			if(albumsResult!=null&&albumsResult.getResult()==1){
//				Map<String, Object> dataMap = (Map<String, Object>) albumsResult.getData();
//				return dataMap;
//			}else{//请求数据错误
//				LogUtil.d("=======responseBean error=======");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====albumListJsonResult======"+albumListJsonResult);
//		return null;
//	}

}
