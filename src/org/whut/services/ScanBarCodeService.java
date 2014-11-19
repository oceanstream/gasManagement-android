package org.whut.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;


import com.example.scandemo.SerialPort;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScanBarCodeService extends Service{

	private SerialPort mSerialPort;
	private boolean run = true;
	private ReadThread mReadThread;
	
	//接收数据缓冲
	public StringBuffer data_buffer = new StringBuffer();  
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	
	
	public String activity = null;
	public String data;
	
	private MyBroadcastReceiver myReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("MyService", "onBind");
		return null;
	}
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("MyService", "onCreate");
		super.onCreate();
		//初始化
		init();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
			
		Log.i("MyService", "onStartCommand");
		String cmd_arr = intent.getStringExtra("cmd");

		if (cmd_arr == null)
			return 0; // 没收到命令直接返回
		
		
		if(mSerialPort.scaner_trig_stat() == true){
			mSerialPort.scaner_trigoff();
		}
		
		mSerialPort.scaner_trigon();  //触发扫描
		
		return 0;
	}
	
	@Override
	public void onDestroy() {
		Log.i("MyService", "onDestory");
		if (mReadThread != null){
			run = false; 
		}
		mSerialPort.scaner_poweroff(); 		// 关闭电源
		mSerialPort.close(0); 				// 关闭串口
		unregisterReceiver(myReceiver); 		// 卸载注册
		super.onDestroy();
	}
	
	private void init(){
		try {
			mSerialPort = new SerialPort(0, 9600, 0);// scaner
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mSerialPort.scaner_poweron();
		
		byte[] temp = new byte[16];
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			mInputStream.read(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		myReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("org.whut.services.ScanBarCodeService");
		registerReceiver(myReceiver, filter);
		
		
		mReadThread = new ReadThread();
		mReadThread.start();
		
	}
	
	private class ReadThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while(run){
				int size;
				try {
					byte[] buffer = new byte[512];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					if(size>0){
//						data = Tools.Bytes2HexString(buffer, size);
						/*中文字符编码*/
//						data = new String(buffer, 0, size, "GB2312");
//						data = new String(buffer, 0, size, "UTF-8");
						data = new String(buffer, 0, size);
						
						data_buffer.append(data);
						
						data = null;
						
						if(data_buffer != null && data_buffer.length() != 0 && activity != null){
							Log.i("msg", "----->databuffer="+data_buffer.toString());
							Intent serviceIntent = new Intent();
							serviceIntent.setAction("org.whut.activities.InstallOperationActivity");
							serviceIntent.putExtra("result", data_buffer.toString());
							Log.i("msg", data_buffer.toString());
							data_buffer.setLength(0);  //清空缓存数据
							sendBroadcast(serviceIntent);
							
						}	
					}	
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
					return;
				}
			}
		}
		
	}
	
	private class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("MyService", "onReceive");
			String ac = intent.getStringExtra("activity");
			if(ac != null){
				Log.i("msg", "receiver from ----->"+ac);
				//获取activity
				activity = ac;
				if (intent.getBooleanExtra("stopflag", false)){
					mSerialPort.scanertrigeroff();
					mSerialPort.scaner_poweroff(); 		// 关闭电源
					stopSelf(); // 收到停止服务信号
				}
				
			}
		}
		
	}

}
