package com.bruce.designer.util.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageLoader {

	private static ExecutorService executorService;

	private static ImageLoader instance = new ImageLoader();

	private ImageLoader() {
		executorService = Executors.newFixedThreadPool(5);
	}

	public static ImageLoader getInstance() {
		if (instance == null) {
			instance = new ImageLoader();
		}
		return instance;
	}

	public static void loadImage(String imageUrl, ImageView imageView) {
		MemoryCache cache = MemoryCache.getInstance();
		 Bitmap bitmap = cache.get(imageUrl);
         if (bitmap != null){
        	 imageView.setImageBitmap(bitmap);
         }else {
			executorService.execute(new ImageLoadThread(imageUrl, imageView));
		}
		return;
	}

}
