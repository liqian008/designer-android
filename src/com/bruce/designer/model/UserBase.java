package com.bruce.designer.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserBase implements Serializable{

	private static final long serialVersionUID = 7082298571476006546L;
	
	private Map<Short, AccessTokenInfo> accessTokenMap = new HashMap<Short, AccessTokenInfo>();

	public Map<Short, AccessTokenInfo> getAccessTokenMap() {
		return accessTokenMap;
	}

	public void setAccessTokenMap(Map<Short, AccessTokenInfo> accessTokenMap) {
		this.accessTokenMap = accessTokenMap;
	}
	
}
