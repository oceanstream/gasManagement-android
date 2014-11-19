package org.whut.activities;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.whut.gasmanagement.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;


public class LoadingActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		//2秒后自动跳转
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run(){
				// TODO Auto-generated method stub
				//创建文件夹
				InitFolders();
				Intent it = new Intent(LoadingActivity.this,ModeChooseActivity.class);
				startActivity(it);
				finish();
			}
		}, 3000);
	}

	@SuppressLint("SdCardPath")
	private void InitFolders() {
		// TODO Auto-generated method stub
		File f1 = new File("/sdcard/gasManagement/");
		if(!f1.exists()){
			f1.mkdirs();
		}
		
		File f2 = new File("/sdcard/gasManagement/config/");
		if(!f2.exists()){
			f2.mkdirs();
		}
		
		File f3 = new File("/sdcard/gasManagement/data/");
		if(!f3.exists()){
			f3.mkdirs();
		}
	}
}
