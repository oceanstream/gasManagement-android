package org.whut.activities;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.whut.adapters.MyInstallAdapter;
import org.whut.database.entities.TaskInstall;
import org.whut.database.service.imp.TaskInstallServiceDao;
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

	
	private boolean MODE_TAG;

	private ListView listView;
	private List<HashMap<String,String>> list;
	private MyInstallAdapter adapter;

	private TaskInstallServiceDao dao;

	private String userName;


	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_install);

		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		userName = getIntent().getStringExtra("userName");
		dao = new TaskInstallServiceDao(this);
		
		listView = (ListView) findViewById(R.id.listView);


		File file = new File("/sdcard/gasManagement/config/Installation.xml");

		if(file.exists()){
			try{

				//从XML解析数据
				List<TaskInstall> listTask = XmlUtils.getInstallDataFromXML(file);

				Log.i("msg", "listTask=" + listTask.toString());

				//存入数据库
				XmlUtils.SaveInstallToDatabase(listTask,this);

				//再从数据库获取所有安装数据
				list  = CommonUtils.getTaskInstall(this,userName);
				Log.i("msg", "===第一条任务状态为====》"+list.get(0).get("isComplete"));
				Log.i("msg", "===第二条任务状态为===》"+list.get(1).get("isComplete"));

				//设置adapter
				adapter = new MyInstallAdapter(this, list);

				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						int completeFlag = Integer.parseInt(list.get(arg2).get("isComplete"));
						int uploadFlag = Integer.parseInt(list.get(arg2).get("uploadFlag"));
						if(completeFlag==1&&uploadFlag==1){
							Builder builder = new AlertDialog.Builder(DeviceInstallActivity.this);
							builder.setTitle("提示").setMessage("该任务已完成，请选择未完成的任务！").setPositiveButton("确定", null).show();
						}else if(completeFlag==1&&uploadFlag==0){
							Builder builder = new AlertDialog.Builder(DeviceInstallActivity.this);
							if(!MODE_TAG){
								//无网模式
								builder.setTitle("提示").setMessage("该任务已在本地完成，请在有网模式中上传结果或手动上传！").setPositiveButton("确定", null).show();
								
							}else{
								//有网模式
								builder.setTitle("提示").setMessage("该任务已完成，但未上传结果，是否上传？").setPositiveButton("确定", new OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Intent it = new Intent(DeviceInstallActivity.this,UploadActivity.class);
										String id = list.get(arg2).get("id");
										HashMap<String,String> intentParams = dao.getIntentParams(id);
										it.putExtra("id", id);
										it.putExtra("address", intentParams.get("address"));
										it.putExtra("barCode", intentParams.get("barCode"));
										it.putExtra("userName", intentParams.get("userName"));
										it.putExtra("indication", intentParams.get("indication"));
										it.putExtra("filePath", intentParams.get("filePath"));
										it.putExtra("MODE_TAG", MODE_TAG);
										startActivity(it);
										finish();
									}
								}).setNegativeButton("取消", null).show();
							}
						}else{
							Intent it = new Intent(DeviceInstallActivity.this,InstallOperationActivity.class);
							it.putExtra("MODE_TAG", MODE_TAG);
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
