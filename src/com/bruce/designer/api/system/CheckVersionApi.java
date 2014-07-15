package com.bruce.designer.api.system;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.AppApplication;
import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;

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
		paramMap = new TreeMap<String, String>();
		//客户端版本号
		paramMap.put("versionCode", String.valueOf(AppApplication.getVersionCode()));
		paramMap.put("versionName", AppApplication.getVersionName());
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
	protected Map<String, Object> processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			int updateType = jsonData.getInt("updateType");
			String updateMessage = jsonData.getString("updateMessage");
			String updateUrl = jsonData.getString("updateUrl");
			dataMap.put("updateType", updateType);
			dataMap.put("updateMessage", updateMessage);
			dataMap.put("updateUrl", updateUrl);
			
//			//构造用户登录状态
//			int needLogin = jsonData.getInt("needLogin");
//			dataMap.put("needLogin", needLogin);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataMap;
	}

}
