package com.bruce.designer.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.constants.Config;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.share.GenericSharedInfo;
import com.bruce.designer.model.share.GenericSharedInfo.WxSharedInfo;
import com.bruce.designer.util.HttpClientUtil;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.util.UrlUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

public class SharePanelView extends PopupWindow {

	private static final int HANDLER_FLAG_IMAGE_DOWNLOAD_FINISH = 1;
	
	private Context context;
	private Button shareBtnCancel;
	private View contentView;
	private ImageView shareToWxFriend;
	private ImageView shareToWxTimeline;
	private GenericSharedInfo generalSharedInfo;
	
	
	Handler shareHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_FLAG_IMAGE_DOWNLOAD_FINISH://图片下载完成(微信专用)
				
				GenericSharedInfo.WxSharedInfo wxSharedInfo = (GenericSharedInfo.WxSharedInfo) msg.obj;
				WXWebpageObject webpage = new WXWebpageObject();
				int scene = wxSharedInfo.getScene();
				String link = wxSharedInfo.getLink();
				//分享链接增加来路的统计（客户端channel后增加微信朋友圈的标志）
				link = UrlUtil.addParameter(link, Config.CHANNEL_FLAG, AppApplication.getChannel()+"_wxshare_"+scene);
				webpage.webpageUrl = wxSharedInfo.getLink();
				WXMediaMessage wxMessage = new WXMediaMessage(webpage);
				wxMessage.title = wxSharedInfo.getTitle();
				wxMessage.description = wxSharedInfo.getContent();
				wxMessage.thumbData = wxSharedInfo.getIconBytes();
				
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.message = wxMessage;
				req.scene = scene;
//				UiUtil.showShortToast(context, "调用微信API");
				AppApplication.getWxApi().sendReq(req);
				break;
			default:
				break;
			}
		};
	};

	public SharePanelView(Context context, GenericSharedInfo generalSharedInfo) {
		super(context);
		this.context = context;
		this.generalSharedInfo = generalSharedInfo;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.share_panel, null);
		// 取消按钮
		shareBtnCancel = (Button) contentView.findViewById(R.id.shareBtnCancel);
		shareBtnCancel = (Button) contentView.findViewById(R.id.shareBtnCancel);
		shareBtnCancel.setOnClickListener(onClickListener);

		shareToWxFriend = (ImageView) contentView.findViewById(R.id.shareToWxFriend);
		shareToWxTimeline = (ImageView) contentView.findViewById(R.id.shareToWxTimeline);
		shareToWxFriend.setOnClickListener(onClickListener);
		shareToWxTimeline.setOnClickListener(onClickListener);

		// 设置SelectPicPopupWindow的View
		this.setContentView(contentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		setAnimationStyle(R.anim.anim_alpha_in);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x30000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框

		contentView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = contentView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}
	
	/**
	 * 在指定view的底部展示
	 * @param view
	 */
	public void show(View view) {
		showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * 分享到微信
	 * 
	 * @param sharedInfo
	 * @param shareScene
	 */
	public void shareToWx(GenericSharedInfo sharedInfo, int shareScene) {
		//需要先启动线程下载图片
		if (sharedInfo != null) {
			WxSharedInfo wxSharedInfo = sharedInfo.getWxSharedInfo();
			if(wxSharedInfo!=null){
				wxSharedInfo.setScene(shareScene);
			}
			Thread thread = new Thread(new ImageDownloadThread(wxSharedInfo));
			thread.start();
		}
	}

	private OnClickListener onClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			dismiss();
			switch (v.getId()) {
			case R.id.shareBtnCancel:
				// 隐藏分享菜单消失
				break;
			case R.id.shareToWxTimeline:
				if (generalSharedInfo != null) {
					UiUtil.showShortToast(context, "分享到朋友圈");
					shareToWx(generalSharedInfo, SendMessageToWX.Req.WXSceneTimeline);
				}
				break;
			case R.id.shareToWxFriend:
				if (generalSharedInfo != null) {
					UiUtil.showShortToast(context, "分享给朋友");
					shareToWx(generalSharedInfo, SendMessageToWX.Req.WXSceneSession);
				}
				break;
			default:
				break;
			}
		}
	};

	class ImageDownloadThread implements Runnable{
		private GenericSharedInfo.WxSharedInfo generalWxSharedInfo;
		
		private ImageDownloadThread(GenericSharedInfo.WxSharedInfo generalWxSharedInfo){
			this.generalWxSharedInfo = generalWxSharedInfo;
		}
		
		@Override
		public void run() {
			if(generalSharedInfo!=null){
				LogUtil.d("shareImageUrl: "+generalWxSharedInfo.getIconUrl());
				byte[] imageBytes = HttpClientUtil.getBytesFromUrl(generalWxSharedInfo.getIconUrl());
				if(imageBytes!=null){
					Message message = shareHandler.obtainMessage(HANDLER_FLAG_IMAGE_DOWNLOAD_FINISH);
					generalWxSharedInfo.setIconBytes(imageBytes);
					message.obj = generalWxSharedInfo;
					message.sendToTarget();
				}
			}
		}
	}
	
}