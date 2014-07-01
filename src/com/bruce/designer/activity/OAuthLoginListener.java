package com.bruce.designer.activity;

import com.bruce.designer.model.UserPassport;

public interface OAuthLoginListener {

	public void loginComplete(UserPassport userPassport);
	
	public void needComplete();

	public void loginFailed();

	

}
