package org.whut.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.TextUtils;
import org.whut.database.service.imp.TaskInstallServiceDao;
import org.whut.gasmanagement.R;
import org.whut.services.ScanBarCodeService;
import org.whut.utils.CommonUtils;
import org.whut.utils.XmlUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "HandlerLeak", "UseSparseArrays", "SdCardPath", "SimpleDateFormat" })
public class InstallOperationActivity extends Activity{

	private boolean MODE_TAG;
	
	//文件存储相关
	private static final String oldPath = "/sdcard/gasManagement/config/Installation.xml";
	private static final String newPath = "/sdcard/gasManagement/data/";
	
	//条码扫描相关
	private String activity = "org.whut.activities.InstallOperationActivity";
	private SoundPool sp;
	private Map<Integer,Integer> soundMap;
	private String cmd = "scan";
	
	private MyBroadcastReceiver myReceiver;


	private ImageView startScan;
	private ImageView finger;
	private Animation click;

	private ProgressDialog dialog;
	private Timer timer;

	private Handler handler;
	
	private String id;
	private String address;
	private String userName;
	private String indication;
	
	private TaskInstallServiceDao  dao;

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_install_operation);

		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		id = getIntent().getStringExtra("id");
		Log.i("msg", "id======>"+id);
		
		address = getIntent().getStringExtra("address");
		userName = getIntent().getStringExtra("userName");
		
		dao = new TaskInstallServiceDao(this);
		
		initSoundPool();
		
		//注册广播
		myReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("org.whut.activities.InstallOperationActivity");
		registerReceiver(myReceiver, filter);
		
		//启动服务
		Intent start = new Intent(InstallOperationActivity.this, ScanBarCodeService.class);
		startService(start);
		
		
		startScan = (ImageView) findViewById(R.id.scanbarcode);
		finger = (ImageView) findViewById(R.id.finger);

		initDialog();

		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case 0:
					Builder builder = new AlertDialog.Builder(InstallOperationActivity.this);
					builder.setTitle("错误提示").setMessage("未检测到卡表条码，请重试！").setPositiveButton("确定", null).show();
					break;
				case 1:
					final String barCode = msg.obj.toString();
					InstallOperationActivity.this.setContentView(R.layout.activity_install_operation_afterscan);
					
					TextView showBarCode = (TextView) InstallOperationActivity.this.findViewById(R.id.showBarCode);
					showBarCode.setText(CommonUtils.FormatBarCode(barCode));
					
					final NumberPicker picker_hundred = (NumberPicker) InstallOperationActivity.this.findViewById(R.id.hundreds);
					final NumberPicker picker_ten = (NumberPicker) InstallOperationActivity.this.findViewById(R.id.tens);
					final NumberPicker picker_unit = (NumberPicker) InstallOperationActivity.this.findViewById(R.id.units);
					
					picker_hundred.setMaxValue(9);
					picker_hundred.setMinValue(0);
					picker_hundred.setFocusable(true);
					picker_hundred.setFocusableInTouchMode(true);
					
					picker_ten.setMaxValue(9);
					picker_ten.setMinValue(0);
					picker_ten.setFocusable(true);
					picker_ten.setFocusableInTouchMode(true);
				
					picker_unit.setMaxValue(9);
					picker_unit.setMinValue(0);
					picker_unit.setFocusable(true);
					picker_unit.setFocusableInTouchMode(true);
					
					TextView btn_save = (TextView) InstallOperationActivity.this.findViewById(R.id.saveResult);
					
					btn_save.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							indication = (100*picker_hundred.getValue()+10*picker_ten.getValue()+picker_unit.getValue())+"";
							Log.i("msg", "读数为：=====》"+indication);
							if(!TextUtils.isEmpty(indication)&&CommonUtils.isNumeric(indication)){				
								
								//复制模板xml
								SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
								String time = df.format(new Date());
								Log.i("msg", "当前时间---》"+time);
								String NewFileName = "Installation"+"-"+userName+"-"+time+".xml";
								Log.i("msg", "新文件名：----》"+NewFileName);								
								CommonUtils.copyFile(oldPath, newPath, NewFileName);
								
								//更新xml数据
								String filePath = newPath + NewFileName;
								
								//将结果更新存入xml
								try {
									XmlUtils.SaveInstallResultToXml(id,barCode,indication,filePath);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//将结果更新存入数据库
								dao.updateTaskInstallResult(Integer.parseInt(id),barCode,indication,filePath);
								
								if(MODE_TAG){
									//有网模式
									//跳转至上传页面
									Intent it = new Intent(InstallOperationActivity.this,UploadActivity.class);
									it.putExtra("MODE_TAG", MODE_TAG);
									it.putExtra("id", id);
									it.putExtra("address", address);
									it.putExtra("barCode", barCode);
									it.putExtra("userName", userName);
									it.putExtra("indication", indication);
									it.putExtra("filePath", filePath);
									startActivity(it);
									finish();
								}else{
									//无网模式
									Toast.makeText(getApplicationContext(), "结果已在本地保存，请在有网模式下上传结果或手动上传！", Toast.LENGTH_SHORT).show();
									finish();
								}
							}else{
								Toast.makeText(InstallOperationActivity.this, "对不起，输入的读数不符合要求，请输入整数！", Toast.LENGTH_SHORT).show();
							}
						}
					});
					break;
				}
			}
		};

		click = AnimationUtils.loadAnimation(this, R.anim.click);

		click.setAnimationListener(new AnimationListener() {

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
				dialog.show();
				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						endCmd();
						dialog.cancel();
						Message msg = Message.obtain();
						msg.what = 0;
						handler.sendMessage(msg);
					}
				}, 7000);

				//开启条码扫描
				sendCmd();
			}
		});

		startScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!CommonUtils.isFastDoubleClickForOthers()){
					finger.startAnimation(click);
				}
			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub		
		Intent stopService = new Intent();
		stopService.setAction("org.whut.services.ScanBarCodeService");
		stopService.putExtra("stopflag", true);
		sendBroadcast(stopService);  //给服务发送广播,令服务停止
		unregisterReceiver(myReceiver);
		super.onDestroy();
	}
	
	private void initDialog(){
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在扫描条码，请稍后...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
	}

	/***
	 * 发送扫卡指令
	 */
	private void sendCmd(){
		// 给服务发送广播，内容为com.example.scandemo.MainActivity
		Intent ac = new Intent();
		ac.setAction("org.whut.services.ScanBarCodeService");
		ac.putExtra("activity", activity);
		sendBroadcast(ac);

		Intent sendToService = new Intent(InstallOperationActivity.this,ScanBarCodeService.class);
		sendToService.putExtra("cmd", cmd);
		this.startService(sendToService);
	}
	
	/***
	 * 发送结束指令
	 */
	private void endCmd(){
		Intent ac = new Intent();
		ac.setAction("org.whut.services.ScanBarCodeService");
		ac.putExtra("activity", activity);
		ac.putExtra("stopflag", true);
		sendBroadcast(ac);
	}

	//初始化声音池
	private void initSoundPool(){
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(1, sp.load(this, R.raw.msg, 1));
	}

	//播放声音池声音
	private void play(int sound, int number){
		AudioManager am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		//返回当前AlarmManager最大音量
	//	float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		//返回当前AudioManager对象的音量值
		float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
	//	float volumnRatio = audioCurrentVolume/audioMaxVolume;
		sp.play(
				soundMap.get(sound), //播放的音乐Id 
				audioCurrentVolume, //左声道音量
				audioCurrentVolume, //右声道音量
				1, //优先级，0为最低
				number, //循环次数，0无不循环，-1无永远循环
				1);//回放速度，值在0.5-2.0之间，1为正常速度
	}
	
	
	private class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String receiveData = intent.getStringExtra("result");
			if(receiveData != null && receiveData != ""){
				dialog.cancel();
				timer.cancel();
				play(1, 0);
				endCmd();
				
				//发送消息给Handler 更新UI
				
				Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = receiveData.trim();
				handler.sendMessage(msg);					
				
			}
		}
		
	}


}
