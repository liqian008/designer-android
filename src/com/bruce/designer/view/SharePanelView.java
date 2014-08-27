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
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.share.SharedInfo;
import com.bruce.designer.util.HttpClientUtil;
import com.bruce.designer.util.UiUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

public class SharePanelView extends PopupWindow {

	private static final int HANDLER_FLAG_IMAGE_DOWNLOAD_FINISH = 1;
	
	
	private Context context;
	private Button shareBtnCancel;
	private View mMenuView;
	private ImageView shareToWxFriend;
	private ImageView shareToWxTimeline;
	private SharedInfo shareInfo;
	
	
	Handler shareHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_FLAG_IMAGE_DOWNLOAD_FINISH://图片下载完成
				SharedInfo shareInfo = (SharedInfo) msg.obj;
				WXWebpageObject webpage = new WXWebpageObject();
				webpage.webpageUrl = shareInfo.getUrl();
				WXMediaMessage wxMessage = new WXMediaMessage(webpage);
				wxMessage.title = shareInfo.getTitle();
				wxMessage.description = shareInfo.getContent();
				wxMessage.thumbData = shareInfo.getBytes();

				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.message = wxMessage;
				req.scene = 1;
				AppApplication.getWxApi().sendReq(req);
				break;

			default:
				break;
			}
		};
	};

	public SharePanelView(Context context, SharedInfo shareInfo) {
		super(context);
		this.context = context;
		this.shareInfo = shareInfo;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.share_panel, null);
		// 取消按钮
		shareBtnCancel = (Button) mMenuView.findViewById(R.id.shareBtnCancel);
		shareBtnCancel = (Button) mMenuView.findViewById(R.id.shareBtnCancel);
		shareBtnCancel.setOnClickListener(onClickListener);

		shareToWxFriend = (ImageView) mMenuView
				.findViewById(R.id.shareToWxFriend);
		shareToWxTimeline = (ImageView) mMenuView
				.findViewById(R.id.shareToWxTimeline);
		shareToWxFriend.setOnClickListener(onClickListener);
		shareToWxTimeline.setOnClickListener(onClickListener);

		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
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

		mMenuView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
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

	public void show(View view) {
		showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * 分享到微信
	 * 
	 * @param shareInfo
	 * @param shareScene
	 */
	public void shareToWx(SharedInfo shareInfo, int shareScene) {
		//需要先启动线程下载图片
		
		if (shareInfo != null) {
			Thread thread = new Thread(new ImageDownloadThread(shareInfo.getImageUrl()));
			thread.start();
		}
	}

	private OnClickListener onClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			switch (v.getId()) {
			case R.id.shareBtnCancel:
				// 隐藏分享菜单消失
				dismiss();
				break;
			case R.id.shareToWxTimeline:
				if (shareInfo != null) {
					UiUtil.showShortToast(context, "分享到朋友圈");
					shareToWx(shareInfo, SendMessageToWX.Req.WXSceneTimeline);
				}
				break;
			case R.id.shareToWxFriend:
				if (shareInfo != null) {
					UiUtil.showShortToast(context, "分享给朋友");
					shareToWx(shareInfo, SendMessageToWX.Req.WXSceneSession);
				}
				break;
			default:
				break;
			}
		}
	};

	class ImageDownloadThread implements Runnable{
		private String imageUrl;
		private ImageDownloadThread(String imageUrl){
			this.imageUrl = imageUrl;
		}
		@Override
		public void run() {
			byte[] imageBytes = HttpClientUtil.getBytesFromUrl(imageUrl);
			Message message = shareHandler.obtainMessage(HANDLER_FLAG_IMAGE_DOWNLOAD_FINISH);
			message.obj = imageBytes;
			message.sendToTarget();
		}
		
	}
	
}