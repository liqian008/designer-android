package com.bruce.designer.util.cache;

import java.lang.ref.SoftReference;

import com.bruce.designer.util.HttpClientUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageLoadThread implements Runnable {

	private String imageUrl;
	private ImageView imageView;

	public ImageLoadThread(String imageUrl, ImageView imageView) {
		this.imageUrl = imageUrl;
		this.imageView = imageView;
	}

	@Override
	public void run() {
//		try {
		Bitmap bitmap = HttpClientUtil.getNetBitmap(imageUrl);
		if(bitmap!=null){
			MemoryCache.getInstance().put(imageUrl, new SoftReference<Bitmap>(bitmap));    
			Activity activity = (Activity) imageView.getContext();
			activity.runOnUiThread(new BitmapDisplayer(bitmap, imageView));
		}
//		} catch (AppException e) {
//			e.printStackTrace();
//		}
	}

	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;

		public BitmapDisplayer(Bitmap b, ImageView imageView) {
			bitmap = b;
			this.imageView = imageView;
		}

		public void run() {
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);
		}
	}
}
