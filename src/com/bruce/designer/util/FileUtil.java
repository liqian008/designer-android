package com.bruce.designer.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.bruce.designer.AppApplication;
import android.content.Context;
import android.util.Log;

public class FileUtil {
	
	/**
	 * 写入文件
	 * @param o
	 * @param fileName
	 */
	public static synchronized void writeObjectToFile(Object data, String fileName) {
		GZIPOutputStream gzipOutputStream = null;
		FileOutputStream fileOutputStream = null;
		final Context context = AppApplication.getApplication();
		try {
			fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			gzipOutputStream = new GZIPOutputStream(fileOutputStream);
			gzipOutputStream.write(JsonUtil.gson.toJson(data).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(gzipOutputStream!=null){
				try {
					gzipOutputStream.close();
					gzipOutputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fileOutputStream!=null){
				try {
					fileOutputStream.close();
					fileOutputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 从文件中读取对象
	 * @param clazz
	 * @param fileName
	 * @return
	 */
	public static synchronized <T> T readObjectFromFile(Class<T> clazz, String fileName) {
		final Context context = AppApplication.getApplication();
//		File accountFile = new File(context.getFilesDir(), fileName);
		GZIPInputStream gzipInputStream = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = context.openFileInput(fileName);
			gzipInputStream = new GZIPInputStream(fileInputStream);
			byte[] data = toByteArray(gzipInputStream);
			if (data == null) {
				return null;
			}
			String result = new String(data);
			String log = String.format("get data from file(%s)\n%s", fileName, result);
			Log.v("cylog", log);
			return JsonUtil.gson.fromJson(result, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(gzipInputStream!=null){
				try {
					gzipInputStream.close();
					gzipInputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fileInputStream!=null){
				try {
					fileInputStream.close();
					fileInputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 读取字节数组
	 * @param input
	 * @return
	 */
	private static byte[] toByteArray(InputStream input) {
		if (input == null) {
			return null;
		}
		ByteArrayOutputStream output = null;
		byte[] result = null;
		try {
			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 100];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			result = output.toByteArray();
		} catch (Exception e) {
		} finally {
			try {
				input.close();
				input = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				output.close();
				output = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
