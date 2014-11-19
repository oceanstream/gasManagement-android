package org.whut.database.service.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.whut.database.DBHelper;
import org.whut.database.entities.TaskInstall;
import org.whut.database.service.TaskInstallService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskInstallServiceDao implements TaskInstallService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;
	
	public TaskInstallServiceDao(Context context){
		mySQLite = DBHelper.getInstane(context);
		db = mySQLite.getWritableDatabase();
	}
	
	@Override
	public void addTaskInstall(List<TaskInstall> list) {
		// TODO Auto-generated method stub
		for(TaskInstall ti : list){
			if(!isTaskInstallAdded(ti)){
				db.beginTransaction();
				db.execSQL("insert into taskinstall(id,address,barcode,indication,isComplete,postDate,uploadFlag,userName) values(?,?,?,?,?,?,?,?)",
						new Object[]{ti.getId(),ti.getAddress(),ti.getBarCode(),ti.getIndication(),ti.getIsComplete(),ti.getPostDate(),ti.getUploadFlag(),ti.getUserName()});
				db.setTransactionSuccessful();
				db.endTransaction();
				Log.i("msg", "安装任务"+ti.getId()+"已添加！");
			}
		}
	}

	@Override
	public boolean isTaskInstallAdded(TaskInstall ti) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from taskinstall where id=?", new String[]{ti.getId()+""});
		while(cursor.moveToNext()){
			return true;
		}
		return false;
	}

	@Override
	public List<HashMap<String, String>> getTaskInstallations(String userName) {
		// TODO Auto-generated method stub
		List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		Cursor cursor = db.rawQuery("select * from taskinstall where userName=?",new String[]{userName});
		while(cursor.moveToNext()){
			HashMap<String,String> params = new HashMap<String, String>();
			params.put("taskName", "安装任务"+cursor.getInt(cursor.getColumnIndex("id")));
			params.put("id", cursor.getInt(cursor.getColumnIndex("id"))+"");
			params.put("address", cursor.getString(cursor.getColumnIndex("address")));
			params.put("postDate", cursor.getString(cursor.getColumnIndex("postDate")));
			params.put("userName", cursor.getString(cursor.getColumnIndex("userName")));
			params.put("isComplete", cursor.getInt(cursor.getColumnIndex("isComplete"))+"");
			Log.i("msg", "安装任务"+cursor.getInt(cursor.getColumnIndex("id"))+"====>"+cursor.getInt(cursor.getColumnIndex("isComplete")));
			params.put("uploadFlag", cursor.getInt(cursor.getColumnIndex("uploadFlag"))+"");
			list.add(params);
		}
		return list;
	}

	@Override
	public void updateTaskInstallResult(int id,String barCode,String indication,String filePath) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update taskinstall set isComplete=?,barCode=?,indication=?,filePath=? where id=? ",new Object[]{1,barCode,indication,filePath,id});
		db.setTransactionSuccessful();
		Log.i("msg", "taskinstall update success--->"+ id);
		db.endTransaction();
	}

	@Override
	public void updateUploadFlag(int id) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update taskinstall set uploadFlag=? where id=?",new Object[]{1,id});
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public HashMap<String, String> getIntentParams(String id) {
		// TODO Auto-generated method stub
		HashMap<String,String> intentParams = new HashMap<String, String>();
		Cursor cursor = db.rawQuery("select * from taskinstall where id=?", new String[]{id});
		while(cursor.moveToNext()){
			intentParams.put("address", cursor.getString(cursor.getColumnIndex("address")));
			intentParams.put("barCode", cursor.getString(cursor.getColumnIndex("barcode")));
			intentParams.put("userName", cursor.getString(cursor.getColumnIndex("userName")));
			intentParams.put("indication", cursor.getString(cursor.getColumnIndex("indication")));
			intentParams.put("filePath", cursor.getString(cursor.getColumnIndex("filePath")));
		}
		return intentParams;
	}

}
