package org.whut.database.service.imp;

import java.util.HashMap;

import org.whut.database.DBHelper;
import org.whut.database.service.UserService;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UserServiceDao implements UserService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;
	
	public UserServiceDao(Context context){
		mySQLite = DBHelper.getInstane(context);
		db = mySQLite.getWritableDatabase();
	}
	
	@Override
	public void addUser(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into user(userName,name,workId,sex) values(?,?,?,?)", 
				new String[]{params.get("userName"),params.get("name"),params.get("workId"),params.get("sex")});
		db.setTransactionSuccessful();
		db.endTransaction();
		
	}

}
