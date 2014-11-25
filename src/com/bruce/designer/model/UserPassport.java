package com.bruce.designer.model;

public class UserPassport {

	private int userId;
	private String ticket;
	private String userSecretKey;
	
	public UserPassport(int userId, String ticket) {
		super();
		this.userId = userId;
		this.ticket = ticket;
	}
	
//	public UserPassport(int userId, String ticket, String userSecretKey) {
//		super();
//		this.userId = userId;
//		this.ticket = ticket;
//		this.userSecretKey = userSecretKey;
//	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getUserSecretKey() {
		return userSecretKey;
	}

	public void setUserSecretKey(String userSecretKey) {
		this.userSecretKey = userSecretKey;
	}

}
