package com.bruce.designer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * 图片工具类
 */
public class ImageUtil {
	
	/**
	 * 从流中获取bitmap，指定压缩尺寸
	 * @param inputStream
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap decodeInputStream(InputStream inputStream,
			int maxWidth, int maxHeight) {
		if (inputStream == null) {
			return null;
		}

		if (inputStream.markSupported()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			inputStream.mark(1024 * 1024); // mark 1M
			BitmapFactory.decodeStream(inputStream, null, options);
			try {
				inputStream.reset();
			} catch (IOException e) {
				// log.d("input stream, reset failed.");
				return null;
			}
			return BitmapFactory.decodeStream(inputStream, null,
					generalOptions(options, maxWidth, maxHeight));
		} else {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			/* echo */
			{
				final byte[] buffer = new byte[4096];
				int read = 0;
				try {
					while ((read = inputStream.read(buffer)) >= 0) {
						if (read > 0) {
							bos.write(buffer, 0, read);
						}
					}
				} catch (IOException e) {
					// log.d("save input stream data, read failed.", e);
				}
			}
			final byte[] data = bos.toByteArray();
			return decodeByteArray(data, 0, data.length, maxWidth, maxHeight);
		}
	}
	
	/**
	 * 从文件中获取bitmap，指定压缩尺寸
	 * @param path
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap decodeFile(String path, int maxWidth, int maxHeight) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		return BitmapFactory.decodeFile(path,
				generalOptions(options, maxWidth, maxHeight));
	}
	
	/**
	 * 从文件中获取bitmap，使用原图
	 * @param path
	 * @return
	 */
	public static Bitmap decodeFileExact(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = 1;
		return BitmapFactory.decodeFile(path, options);
	}
	
	/**
	 * 
	 * @param resources
	 * @param resId
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap decodeResource(Resources resources, int resId,
			int maxWidth, int maxHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId, options);
		return BitmapFactory.decodeResource(resources, resId,
				generalOptions(options, maxWidth, maxHeight));
	}
	
	/**
	 * 从字节数组中获取bitmap，支持缩放
	 * @param data
	 * @param offset
	 * @param length
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap decodeByteArray(byte[] data, int offset, int length,
			int maxWidth, int maxHeight) {
		if (data == null) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, offset, length);
		return BitmapFactory.decodeByteArray(data, offset, length,
				generalOptions(options, maxWidth, maxHeight));
	}
	
	/**
	 * 构造压缩参数
	 * @param options
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static BitmapFactory.Options generalOptions(
			BitmapFactory.Options options, int maxWidth, int maxHeight) {
		if (options == null) {
			options = new BitmapFactory.Options();
		}
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = 1;
		if (options.outHeight > maxHeight) {
			options.inSampleSize = (int) Math.ceil((float) options.outHeight
					/ (float) maxHeight);
		}
		if (options.outWidth > maxWidth) {
			options.inSampleSize = Math.max((int) Math
					.ceil((float) options.outWidth / (float) maxWidth),
					options.inSampleSize);
		}
		return options;
	}

	/**
	 * imageview中的bitmap是否可用
	 * @param imageView
	 * @return
	 */
	public static boolean isImageViewBitmapValid(ImageView imageView) {
		assert imageView != null;
		final Drawable drawable = imageView.getDrawable();
		if (drawable == null) {
			return false;
		}

		if (drawable instanceof BitmapDrawable) {
			final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			return bitmap != null && !bitmap.isRecycled();
		}
		return true;
	}

	/**
	 * 将bitmap转为byte数组，默认质量为80%
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap) {
		return bitmap2Bytes(bitmap, 80);
	}

	/**
	 * 将bitmap转为byte数组
	 * @param bitmap
	 * @param quality
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap, int quality) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
			byte[] bytes = outputStream.toByteArray();
			outputStream.flush();
			outputStream.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将drawable转为bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}
}
