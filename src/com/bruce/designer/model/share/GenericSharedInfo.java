package com.bruce.designer.model.share;

import java.io.Serializable;

public class GenericSharedInfo implements Serializable {

	private static final long serialVersionUID = -569894714360245691L;

	private WxSharedInfo wxSharedInfo;
	private WeiboShareInfo weiboSharedInfo;

	public WxSharedInfo getWxSharedInfo() {
		return wxSharedInfo;
	}

	public void setWxSharedInfo(WxSharedInfo wxSharedInfo) {
		this.wxSharedInfo = wxSharedInfo;
	}

	public WeiboShareInfo getWeiboSharedInfo() {
		return weiboSharedInfo;
	}

	public void setWeiboSharedInfo(WeiboShareInfo weiboSharedInfo) {
		this.weiboSharedInfo = weiboSharedInfo;
	}

	public static class WxSharedInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		private int scene;
		private String title;
		private String content;
		private String iconUrl;
		private String link;
		private byte[] iconBytes;

		public WxSharedInfo(String title, String content, String iconUrl,
				String link) {
			super();
			this.title = title;
			this.content = content;
			this.iconUrl = iconUrl;
			this.link = link;
		}
		
		public int getScene() {
			return scene;
		}

		public void setScene(int scene) {
			this.scene = scene;
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

		public String getIconUrl() {
			return iconUrl;
		}

		public void setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public byte[] getIconBytes() {
			return iconBytes;
		}

		public void setIconBytes(byte[] iconBytes) {
			this.iconBytes = iconBytes;
		}

	}

	public static class WeiboShareInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		private String title;
		private String content;
		private String iconUrl;
		private String link;

		public WeiboShareInfo(String title, String content, String iconUrl,
				String link) {
			super();
			this.title = title;
			this.content = content;
			this.iconUrl = iconUrl;
			this.link = link;
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

		public String getIconUrl() {
			return iconUrl;
		}

		public void setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

	}

}
