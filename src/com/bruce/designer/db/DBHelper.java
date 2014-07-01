package com.bruce.designer.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	private static DBHelper instance;
	private static int version = 1;

	private Context context;
	private SQLiteDatabase db;
	
	public DBHelper(Context context) {
		super(context, "jinwanr.db", null, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		try {
			initDB();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public static synchronized DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

//	public SQLiteDatabase getDB() {
//		return instance.getWritableDatabase();
//	}
	
	private void initDB() throws IOException {
		AssetManager assetManager = context.getAssets();
		InputStream inStream = assetManager.open("jinwanr.sql");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inStream));

		StringBuffer sql = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sql.append(line);
		}
		reader.close();

		Log.d("SQLite", "Init db " + sql.toString());
		db.execSQL(sql.toString());
		Log.i("SQLite", "Init database");
	}

	public SQLiteDatabase getDb() {
		return db;
	}

}