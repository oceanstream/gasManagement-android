package org.whut.database.service.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.whut.database.DBHelper;
import org.whut.database.entities.TaskRepair;
import org.whut.database.service.TaskRepairService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskRepairServiceDao implements TaskRepairService{

	
	private DBHelper mySQLite;
	private SQLiteDatabase db;
	
	public TaskRepairServiceDao(Context context){
		mySQLite = DBHelper.getInstane(context);
		db = mySQLite.getWritableDatabase();
	}
	
	
	@Override
	public void addTaskRepair(List<TaskRepair> list) {
		// TODO Auto-generated method stub
		for(TaskRepair tr:list){
			if(!isTaskRepairAdded(tr)){
				db.beginTransaction();
				db.execSQL("insert into taskrepair(id,address,postDate,type,isComplete,uploadFlag,isUpdate,userName)values(?,?,?,?,?,?,?,?)",
						new Object[]{tr.getId(),tr.getAddress(),tr.getPostDate(),tr.getType(),tr.getIsComplete(),tr.getUploadFlag(),tr.getIsUpdate(),tr.getUserName()});
				Log.i("msg", "维修任务----》"+tr.getId()+"已添加");
				db.setTransactionSuccessful();
				db.endTransaction();
			}
		}
		
		
		
	}

	@Override
	public boolean isTaskRepairAdded(TaskRepair tr) {
		// TODO Auto-generated method stub
		
		Cursor cursor = db.rawQuery("select * from taskrepair where id=?", new String[]{tr.getId()+""});
		while(cursor.moveToNext()){
			return true;
		}
		return false;
	}

	@Override
	public List<HashMap<String, String>> getTaskRepairs(String userName) {
		// TODO Auto-generated method stub
		List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		Cursor cursor = db.rawQuery("select * from taskrepair where userName=?",new String[]{userName});
		while(cursor.moveToNext()){
			HashMap<String,String> params = new HashMap<String, String>();
			params.put("taskName", "维修任务"+cursor.getInt(cursor.getColumnIndex("id")));
			params.put("id", cursor.getInt(cursor.getColumnIndex("id"))+"");
			params.put("address", cursor.getString(cursor.getColumnIndex("address")));
			params.put("type", cursor.getInt(cursor.getColumnIndex("type"))+"");
			params.put("postDate", cursor.getString(cursor.getColumnIndex("postDate")));
			params.put("userName", cursor.getString(cursor.getColumnIndex("userName")));
			params.put("isComplete", cursor.getInt(cursor.getColumnIndex("isComplete"))+"");
			params.put("uploadFlag", cursor.getInt(cursor.getColumnIndex("uploadFlag"))+"");
			params.put("isUpdate", cursor.getInt(cursor.getColumnIndex("isUpdate"))+"");
			params.put("finishTime", cursor.getString(cursor.getColumnIndex("finishTime")));
			params.put("filePath", cursor.getString(cursor.getColumnIndex("filePath")));
			params.put("description", cursor.getString(cursor.getColumnIndex("description")));
			Log.i("msg","GetTaskRepairs=====>isUpdate"+ cursor.getInt(cursor.getColumnIndex("isUpdate")));
			Log.i("msg","GetTaskRepairs=====>type"+ cursor.getInt(cursor.getColumnIndex("isUpdate")));
			list.add(params);
		}
		return list;
	}


	@Override
	public void updateRepairResult(int id,int type,int isUpdate,String userName,String description,String filePath,String finishTime) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update taskrepair set type=?,isUpdate=?,userName=?,description=?,filePath=?,finishTime=? , isComplete=? where id=?",
				new Object[]{type,isUpdate,userName,description,filePath,finishTime,1,id});
		Log.i("msg", "更新维修任务"+id+"======>"+"isUpdate="+isUpdate);
		db.setTransactionSuccessful();
		db.endTransaction();
		
	}
	
	public void updateRepairResult(int id,int type,int isUpdate,String userName,String description,String filePath,String finishTime,String oldBarCode,String newBarCode,String oldIndication,String newIndication){
		db.beginTransaction();
		db.execSQL("update taskrepair set type=?,isUpdate=?,userName=?,description=?,filePath=?,finishTime=? , isComplete=? , oldBarCode=? , newBarCode=? , oldIndication = ? , newIndication = ? where id=?",
				new Object[]{type,isUpdate,userName,description,filePath,finishTime,1,oldBarCode,newBarCode,oldIndication,newIndication,id});
		Log.i("msg", "更新维修任务"+id+"======>"+"isUpdate="+isUpdate);
		db.setTransactionSuccessful();
		db.endTransaction();
	}


	@Override
	public void updateUploadFlag(int id) {
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("update taskrepair set uploadFlag=? where id=?",new Object[]{1,id});
		db.setTransactionSuccessful();
		db.endTransaction();
	}


	@Override
	public HashMap<String,String> getIntentParams(int id) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from taskrepair where id=?", new String[]{id+""});
		HashMap<String,String> params = new HashMap<String, String>();
		while(cursor.moveToNext()){
			params.put("address", cursor.getString(cursor.getColumnIndex("address")));
			params.put("userName", cursor.getString(cursor.getColumnIndex("userName")));
			params.put("oldBarCode", cursor.getString(cursor.getColumnIndex("oldBarCode")));
			params.put("newBarCode", cursor.getString(cursor.getColumnIndex("newBarCode")));
			params.put("oldIndication", cursor.getString(cursor.getColumnIndex("oldIndication")));
			params.put("newIndication", cursor.getString(cursor.getColumnIndex("newIndication")));
			params.put("type", cursor.getString(cursor.getColumnIndex("type")));
			params.put("description", cursor.getString(cursor.getColumnIndex("description")));
			params.put("finishTime", cursor.getString(cursor.getColumnIndex("finishTime")));
			params.put("filePath", cursor.getString(cursor.getColumnIndex("filePath")));
		}
		return params;
	}

}
