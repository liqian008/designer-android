package com.bruce.designer.api.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.AppApplication;
import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.UserFan;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 检查版本更新API
 * @author liqian
 *
 */
public class CheckVersionApi extends AbstractApi {

	private String REQUESTS_URI = null;

	private Map<String, String> paramMap = null;

	public CheckVersionApi(int userId) {
		REQUESTS_URI = Config.JINWAN_API_PREFIX + "/checkVersion.json";
	}

	@Override
	public Map<String, String> getParamMap() {
//		paramMap = new TreeMap<String, String>();
//		//客户端版本号
//		paramMap.put("clientVersion", AppApplication.getVersionName());
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
		if (response != null) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				int result = jsonObject.getInt("result");
				if (result == 1) {// 成功响应
					JSONObject jsonData = jsonObject.getJSONObject("data");
					String fanListStr = jsonData.getString("fanList");
					List<UserFan> fanList = JsonUtil.gson.fromJson(fanListStr,
							new TypeToken<List<UserFan>>() {
							}.getType());
					if (fanList != null) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("fanList", fanList);
						jsonResult = new JsonResultBean(result, map, 0, null);
					}
				} else {// 错误响应
					int errorcode = jsonObject.getInt("errorcode");
					String message = jsonObject.getString("message");
					jsonResult = new JsonResultBean(result, null, errorcode,
							message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonResult;
	}

}
