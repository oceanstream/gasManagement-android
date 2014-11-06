package org.whut.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static DBHelper instance;
	
	public DBHelper(Context context){
		super(context, "db", null, 1);
	}
	
	public static DBHelper getInstane(Context context){
		if(instance==null){
			synchronized (DBHelper.class) {
				if(instance==null){
					instance = new DBHelper(context);
				}
			}
		}
		return instance;
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exsists user(id integer primary key autoincrement, username varchar(255),workid integer,name varchar(255))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS USER");
		onCreate(db);
	}

}
