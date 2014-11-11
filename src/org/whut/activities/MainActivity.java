package org.whut.activities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.whut.adapters.MyGridAdapter;
import org.whut.application.MyApplication;
import org.whut.client.MyClient;
import org.whut.gasmanagement.R;
import org.whut.utils.CommonUtils;
import org.whut.utils.UrlStrings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity{

	private Handler handler;

	private GridView gridView;
	private MyGridAdapter adapter;

	private Animation animation1,animation2,animation3,animation4;

	private static final int[] colors = {R.drawable.common_background_selector_install,R.drawable.common_background_selector_repair,R.drawable.common_background_selector_getconfig,R.drawable.common_background_selector_writecard};
	private static final int[] images = {R.drawable.install,R.drawable.repair,R.drawable.getconfig,R.drawable.writecard};
	private static final String[] functions = {"安装","维修","查询","写卡"};

	private int screenHeight,topbarHeight;

	
	
	/** 
	 * 菜单、返回键响应 
	 */  
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if(keyCode == KeyEvent.KEYCODE_BACK)  {   
			exitBy2Click();
		}  
		return false;  
	}  
	/** 
	 * 双击退出函数 
	 */  
	private static Boolean isExit = false;  

	private void exitBy2Click() {  
		Timer tExit = null;  
		if (isExit == false) {  
			isExit = true; // 准备退出  
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
			tExit = new Timer();  
			tExit.schedule(new TimerTask() {  
				@Override  
				public void run() {  
					isExit = false; // 取消退出  
				}  
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  

		} else {  
			MyApplication.getInstance().exit();
		}  
	}
	
	
	
	@SuppressLint("HandlerLeak")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case 0:
					Toast.makeText(getApplicationContext(), "任务文件下载失败，请在“查询”界面重新下载！", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					break;
				}
			}
		};

		gridView = (GridView) findViewById(R.id.gridView);

		animation1 = AnimationUtils.loadAnimation(this, R.anim.griditem_onclick1);
		animation2 = AnimationUtils.loadAnimation(this, R.anim.griditem_onclick2);
		animation3 = AnimationUtils.loadAnimation(this, R.anim.griditem_onclick3);
		animation4 = AnimationUtils.loadAnimation(this, R.anim.griditem_onclick4);

		animation1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MainActivity.this,DeviceInstallActivity.class);
				startActivity(it);
			}
		});


		animation2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MainActivity.this,DeviceRepairActivity.class);
				startActivity(it);
			}
		});


		animation3.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MainActivity.this,GetConfigFileActivity.class);
				startActivity(it);

			}
		});


		animation4.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MainActivity.this,WriteCardActivity.class);
				startActivity(it);

			}
		});


		WindowManager wm = this.getWindowManager();

		screenHeight = wm.getDefaultDisplay().getHeight();

		final RelativeLayout layout = (RelativeLayout) findViewById(R.id.include);

		layout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			boolean isFirst = true;

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				if(isFirst){
					isFirst = false;
					topbarHeight = layout.getMeasuredHeight();
					adapter = new MyGridAdapter(MainActivity.this, images, functions, colors,(screenHeight-topbarHeight));
					gridView.setAdapter(adapter);
				}
			}
		});


		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(CommonUtils.isFastDoubleClickForMain()){
					return;
				}else{
					switch(arg2){
					case 0:
						arg0.startAnimation(animation1);
						break;
					case 1:
						arg0.startAnimation(animation2);
						break;
					case 2:
						arg0.startAnimation(animation3);
						break;
					case 3:
						arg0.startAnimation(animation4);
						break;
					}
				}
			}
		});


//		new Thread(new GetConfigFileThread()).start();
//		new Thread(new GetCurrentUserThread()).start();


	}

	class GetConfigFileThread implements Runnable{

		private InputStream inStream;
		private Message msg = Message.obtain();

		@SuppressLint("SdCardPath")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (this) {
				inStream = MyClient.getInstance().DoGetConfigFile(UrlStrings.BASE_URL+"ICCard/rest/installationService/getInstallationTasks");
				try{
					if(!CommonUtils.SaveConfigFiles(inStream,"Installation.xml","/sdcard/gasManagement/config")){;
					msg.what=0;
					handler.sendMessage(msg);
					}else{
						msg.what=1;
						handler.sendMessage(msg);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	class GetCurrentUserThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (this) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				try {
					String result = MyClient.getInstance().doPost(UrlStrings.BASE_URL+"ICCard/rest/userService/getCurrentUser",params);
					Log.i("msg", "current user:"+result);
					Message msg = Message.obtain();
					msg.obj = result;
					msg.what = 2;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}
