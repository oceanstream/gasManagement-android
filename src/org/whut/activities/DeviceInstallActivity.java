package org.whut.activities;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.whut.adapters.MyAdapter;
import org.whut.database.entities.TaskInstall;
import org.whut.gasmanagement.R;
import org.whut.utils.CommonUtils;
import org.whut.utils.XmlUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceInstallActivity extends Activity{


	private ListView listView;
	private List<HashMap<String,String>> list;
	private MyAdapter adapter;
	
	
	
	
	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_install);

		listView = (ListView) findViewById(R.id.listView);
		

		File file = new File("/sdcard/gasManagement/config/Installation.xml");

		if(file.exists()){
			try{
				
				//从XML解析数据
				List<TaskInstall> listTask = XmlUtils.getDataFromXML(file);
			
				Log.i("msg", "listTask=" + listTask.toString());
				
				//存入数据库
				XmlUtils.SaveToDatabase(listTask,this);
				
				//再从数据库获取所有安装数据
				list  = CommonUtils.getTaskInstall(this);
				Log.i("msg", "===第一条任务状态为====》"+list.get(0).get("isComplete"));
				Log.i("msg", "===第二条任务状态为===》"+list.get(1).get("isComplete"));
				
				//设置adapter
				adapter = new MyAdapter(this, list);
				
				listView.setAdapter(adapter);
				
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
							if(Integer.parseInt(list.get(arg2).get("isComplete"))==1){
								Builder builder = new AlertDialog.Builder(DeviceInstallActivity.this);
								builder.setTitle("提示").setMessage("该任务已完成，请选择未完成的任务！").setPositiveButton("确定", null).show();
							}else{
								Intent it = new Intent(DeviceInstallActivity.this,InstallOperationActivity.class);
								it.putExtra("id", list.get(arg2).get("id"));
								it.putExtra("address", list.get(arg2).get("address"));
								it.putExtra("userName", list.get(arg2).get("userName"));
								startActivity(it);
								finish();
							}
					}
				});
				
			}catch(Exception e){
				e.printStackTrace();
			}	
		}else{
			listView.setVisibility(View.GONE);
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示").setMessage("检测到本机无安装任务文件，是否从服务器端下载？").setPositiveButton("是", 
					new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent it = new Intent(DeviceInstallActivity.this,GetConfigFileActivity.class);
					startActivity(it);
					finish();
				}
			}).setNegativeButton("否", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "请手动添加\"Installation.xml\"或在查询页面下载！", Toast.LENGTH_SHORT).show();
				}
			}).show();
		}
		


	}
}
