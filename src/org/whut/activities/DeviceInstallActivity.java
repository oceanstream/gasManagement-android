package org.whut.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.whut.adapters.MyAdapter;
import org.whut.gasmanagement.R;
import org.whut.utils.XmlUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
				list = XmlUtils.getDataFromXML(file);
				adapter = new MyAdapter(this, list);
				listView.setAdapter(adapter);
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
