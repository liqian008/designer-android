package com.bruce.designer.model;

import java.io.Serializable;

public class AlbumAuthorInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String designerAvatar;
	
	private String designerNickname;
	/*关注状态*/
	private boolean followed;
	
	
	public AlbumAuthorInfo(String designerAvatar, String designerNickname,
			boolean followStatus) {
		super();
		this.designerAvatar = designerAvatar;
		this.designerNickname = designerNickname;
		this.followed = followStatus;
	}
	public String getDesignerAvatar() {
		return designerAvatar;
	}
	public void setDesignerAvatar(String designerAvatar) {
		this.designerAvatar = designerAvatar;
	}
	public String getDesignerNickname() {
		return designerNickname;
	}
	public void setDesignerNickname(String designerNickname) {
		this.designerNickname = designerNickname;
	}
	public boolean isFollowed() {
		return followed;
	}
	public void setFollowed(boolean followed) {
		this.followed = followed;
	}
	
	
	
}
