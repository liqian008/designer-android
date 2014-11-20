package com.bruce.designer.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于处理url请求
 * @author liqian
 *
 */
public class UrlUtil {

	/**
	 * join url
	 * 
	 * @param orginalUrl
	 * @param appendUrl
	 * @return
	 */
	public static String joinUrl(String orginalUrl, String appendUrl) {
		if (orginalUrl != null && orginalUrl.length() > 0) {
			if (orginalUrl.indexOf("?") == -1) {
				return orginalUrl + "?" + appendUrl;
			} else {
				return orginalUrl + "&" + appendUrl;
			}
		} else {
			return "";
		}
	}

	public static String joinUrlForHyperLink(String origURL, String appendUrl) {
		if (origURL != null && origURL.length() > 0) {
			if (origURL.indexOf("?") == -1) {
				return origURL + "?" + appendUrl;
			} else {
				return origURL + "&amp;" + appendUrl;
			}
		} else {
			return "";
		}
	}

	/**
	 * 判断refer中是否含有问号
	 * */
	public static boolean isContainQuestionMark(String url) {
		if (!StringUtils.isBlank(url)) {
			return url.contains("?");
		}
		return false;
	}


	/**
	 * 给URL添加参数
	 * 
	 * @param url
	 * @param key
	 * @param value
	 * @return
	 */
	public static String addParameter(String url, String key, String value) {
		String param = getUrlParamString(key, value);
		return joinUrl(url, param);
	}

	public static String getUrlParamString(String key, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append(key);
		sb.append("=");

		try {
			value = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		sb.append(value);
		return sb.toString();
	}

	/**
	 * 将url中的某些参数去掉
	 * */
	public static String removeParameter(String url, String param) {
		if (url != null) {
			String regx = "(&" + param + "=[\\%0-9A-Za-z-_]*)|(" + param
					+ "=[\\%0-9A-Za-z-_]*&)";
			Pattern p = Pattern.compile(regx);
			Matcher m = p.matcher(url);
			return m.replaceAll("");
		}
		return url;
	}


}
