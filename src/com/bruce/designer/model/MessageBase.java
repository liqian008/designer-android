package com.bruce.designer.model;

public class MessageBase{
	
    private User fromUser;
    private User chatUser;

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}
    
	
	public User getChatUser() {
		return chatUser;
	}

	public void setChatUser(User chatUser) {
		this.chatUser = chatUser;
	}
    
}