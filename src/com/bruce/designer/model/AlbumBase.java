package com.bruce.designer.model;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.List;

import com.bruce.designer.model.share.GenericSharedInfo;

public class AlbumBase implements Serializable{

	private static final long serialVersionUID = -6085739230899814437L;
	
	private long browseCount;

	private long commentCount;

	private long likeCount;

	private long favoriteCount;
	
	private boolean isLike;
	
	private boolean isFavorite;
	
	/*专辑排序值*/
	private long sort;

	/*专辑在mobile端的url*/
	private String itemMobileUrl;
	
	private List<String> tagList;

	private List<AlbumSlide> slideList;

	private AlbumAuthorInfo authorInfo;
	
	private GenericSharedInfo genericSharedInfo;
	
	public long getBrowseCount() {
		return browseCount;
	}

	public void setBrowseCount(long browseCount) {
		this.browseCount = browseCount;
	}

	public long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(long commentCount) {
		this.commentCount = commentCount;
	}

	public long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	public long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	
	public boolean isLike() {
		return isLike;
	}

	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

	public List<AlbumSlide> getSlideList() {
		return slideList;
	}

	public void setSlideList(List<AlbumSlide> slideList) {
		this.slideList = slideList;
	}

	public AlbumAuthorInfo getAuthorInfo() {
		return authorInfo;
	}

	public void setAuthorInfo(AlbumAuthorInfo authorInfo) {
		this.authorInfo = authorInfo;
	}

	public String getItemMobileUrl() {
		return itemMobileUrl;
	}

	public void setItemMobileUrl(String itemMobileUrl) {
		this.itemMobileUrl = itemMobileUrl;
	}

	public GenericSharedInfo getGenericSharedInfo() {
		return genericSharedInfo;
	}

	public void setGenericSharedInfo(GenericSharedInfo genericSharedInfo) {
		this.genericSharedInfo = genericSharedInfo;
	}

	public long getSort() {
		return sort;
	}

	public void setSort(long sort) {
		this.sort = sort;
	}
	
}

