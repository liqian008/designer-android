package com.bruce.designer.model.share;

public class SharedInfo {

	private String title;
	private String content;
	private String url;
	private String imageUrl;
	private byte[] bytes;

	public SharedInfo() {
		super();
	}

	public SharedInfo(String title, String content, String url, String imageUrl) {
		super();
		this.title = title;
		this.content = content;
		this.url = url;
		this.imageUrl = imageUrl;
	}

	public SharedInfo(String title, String content, String url, byte[] bytes) {
		super();
		this.title = title;
		this.content = content;
		this.url = url;
		this.bytes = bytes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
