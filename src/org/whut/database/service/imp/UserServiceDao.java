package org.whut.database.service.imp;


import org.whut.database.DBHelper;
import org.whut.database.entities.User;
import org.whut.database.service.UserService;
import org.whut.utils.MD5Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserServiceDao implements UserService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;
	
	public UserServiceDao(Context context){
		mySQLite = DBHelper.getInstane(context);
		db = mySQLite.getWritableDatabase();
	}

	@Override
	public void addUser(User user) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into user(userName,password) values (?,?)", new Object[]{user.getUserName(),user.getPassword()});
		db.setTransactionSuccessful();
		Log.i("msg", "添加成功！userdao===>"+user.getPassword());
		db.endTransaction();
	}

	@Override
	public boolean validateUser(User user) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from user where userName=?", new String[]{user.getUserName()});
		while(cursor.moveToNext()){
			if((MD5Utils.string2MD5(user.getPassword())).equals(cursor.getString(cursor.getColumnIndex("password")))){
				return true;
			}
		}
		return false;
	}
	


}
