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
		db.execSQL("create table if not exists user(id integer primary key autoincrement, userName varchar(255),password varchar(255),workId integer,name varchar(255))");		
		db.execSQL("create table if not exists taskinstall(id integer primary key,address varchar(255),barcode varchar(255),indication varchar(255),isComplete integer,postDate varchar(255),uploadFlag integer,userName varchar(255),filePath varchar(255))");
		db.execSQL("create table if not exists taskrepair(id integer primary key,address varchar(255),type integer,description varchar(255),postDate varchar(255),oldBarCode varchar(255),oldIndication varchar(255),newBarCode varchar(255),newIndication varchar(255),isUpdate integer,isComplete integer,uploadFlag integer, filePath varchar(255),userName varchar(255),finishTime varchar(255))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS USER");
		db.execSQL("DROP TABLE IF EXISTS TASKINSTALL");
		db.execSQL("DROP TABLE IF EXISTS TASKREPAIR");
		onCreate(db);
	}

}
