package com.bruce.designer.util.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

	private static final String TAG = "MemoryCache";
	private static HashMap<String, SoftReference<Bitmap>> imageCache = null;

	public MemoryCache() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	private static MemoryCache instance = new MemoryCache();

	public static MemoryCache getInstance() {
		if (instance == null) {
			instance = new MemoryCache();
		}
		return instance;
	}

	public boolean containsKey(String key) {
		return imageCache.containsKey(key);
	}
	
	public void put(String imageUrl, SoftReference<Bitmap> softReference) {
		imageCache.put(imageUrl, softReference);
	}
	
	public Bitmap get(String imageUrl) {
		boolean contains = imageCache.containsKey(imageUrl);
		Log.v(TAG, "image cache contains");
		if (contains) {
			 SoftReference<Bitmap> reference = imageCache.get(imageUrl); 
			 Bitmap bitmap = reference.get();
			 return bitmap;
		}
		return null;
	}
	
	public void clear(){
		imageCache.clear();
	}

	

}
