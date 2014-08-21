package com.bruce.designer.model;

import java.io.Serializable;

public class AlbumFavoriteBase implements Serializable{

	private static final long serialVersionUID = -483197532504984334L;
	
	private Album album;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

}
