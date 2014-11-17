package com.bruce.designer.model.share;


public class SharedInfoBuilder {
	
//	/**
//	 * 初始化通用的分享对象
//	 * @param album
//	 * @return
//	 */
//	public static GenericSharedInfo initGeneralSharedInfo(Album album) {
//		if(album!=null){
//			GenericSharedInfo generalSharedInfo = new GenericSharedInfo();
//			WxShareInfo wxShareInfo = album.getWxShareInfo();
//			if(wxShareInfo!=null){
//				//微信分享对象
//				if(!StringUtils.isBlank(wxShareInfo.getTitle())&&!StringUtils.isBlank(wxShareInfo.getContent())&&!StringUtils.isBlank(wxShareInfo.getLink())&&!StringUtils.isBlank(wxShareInfo.getIconUrl())){
//					GenericSharedInfo.WxShareInfo generalWxShareInfo = new GenericSharedInfo.WxShareInfo(wxShareInfo.getTitle(), wxShareInfo.getContent(),wxShareInfo.getIconUrl(),wxShareInfo.getLink());
//					generalSharedInfo.setWxShareInfo(generalWxShareInfo);
//				}
//				//微博分享对象
//				if(!StringUtils.isBlank(wxShareInfo.getTitle())&&!StringUtils.isBlank(wxShareInfo.getContent())&&!StringUtils.isBlank(wxShareInfo.getLink())&&!StringUtils.isBlank(wxShareInfo.getIconUrl())){
//					GenericSharedInfo.WeiboShareInfo generalWeiboShareInfo = new GenericSharedInfo.WeiboShareInfo(wxShareInfo.getTitle(), wxShareInfo.getContent(),wxShareInfo.getIconUrl(),wxShareInfo.getLink());
//					generalSharedInfo.setWeiboShareInfo(generalWeiboShareInfo);
//				}
//			}
//			return generalSharedInfo;
//		}
//		return null;
//	}


}
