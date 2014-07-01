package com.bruce.designer.model;

public class UserPassport {

	private int userId;
	private String ticket;
	private String secretKey;
	
	
	public UserPassport(int userId, String ticket, String secretKey) {
		super();
		this.userId = userId;
		this.ticket = ticket;
		this.secretKey = secretKey;
	}
	
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
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	

}
