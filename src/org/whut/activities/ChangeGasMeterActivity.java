package org.whut.activities;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.TextUtils;
import org.whut.database.service.imp.TaskRepairServiceDao;
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
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint({ "HandlerLeak", "SdCardPath", "SimpleDateFormat", "UseSparseArrays" })
public class ChangeGasMeterActivity extends Activity{
	
	private String oldPath = "/sdcard/gasManagement/config/Repair.xml";
	private static final String newPath = "/sdcard/gasManagement/data/";
	
	private boolean MODE_TAG;
	private String id;
	private String address;
	private String userName;
	private int FAILURE_FLAG;
	private String description;

	private RelativeLayout rl_nbc,rl_obc,rl_ni,rl_oi;

	private TextView tv_nbc_value,tv_obc_value,tv_ni_value,tv_oi_value;

	private TaskRepairServiceDao dao;

	private TextView btn_save;

	private ProgressDialog dialog;
	private Timer timer;
	private LayoutInflater inflater;

	private String OldBarcode;
	private String NewBarcode;
	private int OldIndication;
	private int NewIndication;
	
	private MyBroadcastReceiver receiver;
	private Handler handler;
	
	private String activity = "org.whut.activities.ChangeGasMeterActivity";
	private String cmd = "scan";
	private SoundPool sp;
	private Map<Integer,Integer> soundMap;
	
	//旧0新1
	private int CLICK_TAG = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_change_gas_meter);

		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		id = getIntent().getStringExtra("id");
		address = getIntent().getStringExtra("address");
		userName = getIntent().getStringExtra("userName");
		FAILURE_FLAG = getIntent().getIntExtra("FAILURE_FLAG", -1);
		description  = getIntent().getStringExtra("description");

		dao = new TaskRepairServiceDao(this);

		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在扫描条码，请稍后...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		
		handler = new Handler(){
			public void handleMessage(Message msg) {
				switch(msg.what){
					case 0:
						//未检测到条码
						Builder builder = new AlertDialog.Builder(ChangeGasMeterActivity.this);
						builder.setTitle("错误提示").setMessage("未检测到卡表条码，请重试！").setPositiveButton("确定", null).show();
						break;
					case 1:
						switch(CLICK_TAG){
							case 0://旧表
								OldBarcode = msg.obj.toString();
								tv_obc_value.setText(OldBarcode);
								break;
							case 1:
								NewBarcode = msg.obj.toString();
								tv_nbc_value.setText(NewBarcode);
								break;
						}
						break;
				}
			};
		};
		
		//注册receiver
		
		receiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(activity);
		registerReceiver(receiver, filter);
		
		initSoundPool();
		
		//启动服务
		Intent start = new Intent(ChangeGasMeterActivity.this, ScanBarCodeService.class);
		startService(start);
		
		
		
		rl_obc = (RelativeLayout) findViewById(R.id.old_bar_code);
		rl_nbc = (RelativeLayout) findViewById(R.id.new_bar_code);
		rl_oi = (RelativeLayout) findViewById(R.id.old_indication);
		rl_ni = (RelativeLayout) findViewById(R.id.new_indication);

		tv_obc_value = (TextView) findViewById(R.id.obc_value1);
		tv_nbc_value = (TextView) findViewById(R.id.nbc_value1);
		tv_oi_value = (TextView) findViewById(R.id.oi_value1);
		tv_ni_value = (TextView) findViewById(R.id.ni_value1);

		btn_save = (TextView) findViewById(R.id.btn_save_result);

		inflater = LayoutInflater.from(this);

		
		
		rl_obc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CLICK_TAG = 0;
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
				
				sendCmd();
				
				
			}
		});
		
		rl_nbc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CLICK_TAG = 1;
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
		

		rl_oi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder builder = new AlertDialog.Builder(ChangeGasMeterActivity.this);
				LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.dialog_view_number_picker, null);

				final NumberPicker np_hunderd = (NumberPicker)parent.findViewById(R.id.np_hundred);
				final NumberPicker np_ten = (NumberPicker)parent.findViewById(R.id.np_ten);
				final NumberPicker np_unit = (NumberPicker)parent.findViewById(R.id.np_unit);

				np_hunderd.setMaxValue(9);
				np_hunderd.setMinValue(0);
				np_hunderd.setFocusable(true);
				np_hunderd.setFocusableInTouchMode(true);

				np_ten.setMaxValue(9);
				np_ten.setMinValue(0);
				np_ten.setFocusable(true);
				np_ten.setFocusableInTouchMode(true);

				np_unit.setMaxValue(9);
				np_unit.setMinValue(0);
				np_unit.setFocusable(true);
				np_unit.setFocusableInTouchMode(true);


				builder.setTitle("请输入旧表读数").setView(parent);
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						OldIndication = np_hunderd.getValue()*100+np_ten.getValue()*10+np_unit.getValue();
						tv_oi_value.setText(OldIndication+"");

					}
				}).setNegativeButton("取消", null).show();
			}
		});

		rl_ni.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			Builder builder = new AlertDialog.Builder(ChangeGasMeterActivity.this);
			LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.dialog_view_number_picker, null);
			
			final NumberPicker np_hunderd = (NumberPicker)parent.findViewById(R.id.np_hundred);
			final NumberPicker np_ten = (NumberPicker)parent.findViewById(R.id.np_ten);
			final NumberPicker np_unit = (NumberPicker)parent.findViewById(R.id.np_unit);

			np_hunderd.setMaxValue(9);
			np_hunderd.setMinValue(0);
			np_hunderd.setFocusable(true);
			np_hunderd.setFocusableInTouchMode(true);

			np_ten.setMaxValue(9);
			np_ten.setMinValue(0);
			np_ten.setFocusable(true);
			np_ten.setFocusableInTouchMode(true);

			np_unit.setMaxValue(9);
			np_unit.setMinValue(0);
			np_unit.setFocusable(true);
			np_unit.setFocusableInTouchMode(true);
			
			builder.setTitle("请输入新表读数").setView(parent);
			
			builder.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					NewIndication = np_hunderd.getValue()*100+np_ten.getValue()*10+np_unit.getValue();
					tv_ni_value.setText(NewIndication+"");
				}
			}).setNegativeButton("取消", null).show();
			
			}
		});
		
		
		btn_save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(TextUtils.isEmpty(tv_nbc_value.getText().toString())||
						TextUtils.isEmpty(tv_obc_value.getText().toString())||
						TextUtils.isEmpty(tv_oi_value.getText().toString())||
						TextUtils.isEmpty(tv_ni_value.getText().toString())){
					//四项任意一项为空
					Builder builder = new AlertDialog.Builder(ChangeGasMeterActivity.this);
					builder.setTitle("提示").setMessage("请完整填写上述四项信息！").setPositiveButton("确定", null).show();
				}else{
					//关闭服务
					endService();
					
					//复制模板xml
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					String time = df.format(new Date());
					Log.i("msg", "当前时间---》"+time);
					String NewFileName = "Repair"+"-"+userName+"-"+time+".xml";
					Log.i("msg", "新文件名：----》"+NewFileName);								
					CommonUtils.copyFile(oldPath, newPath, NewFileName);
					
					//更新xml数据
					String filePath = newPath + NewFileName;
					
					//将结果更新存入xml
					try {
						XmlUtils.SaveRepairResultToXml(id,(FAILURE_FLAG+""),description,OldBarcode,NewBarcode,(OldIndication+""),(NewIndication+""),filePath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//将结果更新存入数据库
					dao.updateRepairResult(Integer.parseInt(id), FAILURE_FLAG , 1, userName,description,filePath,time,OldBarcode,NewBarcode,OldIndication+"",NewIndication+"");
					
					Intent it = new Intent(ChangeGasMeterActivity.this,UploadForRepairActivity.class);
					it.putExtra("MODE_TAG", MODE_TAG);
					it.putExtra("id", id);
					it.putExtra("address", address);
					it.putExtra("userName", userName);
					it.putExtra("oldBarCode", OldBarcode);
					it.putExtra("newBarCode", NewBarcode);
					it.putExtra("oldIndication", OldIndication+"");
					it.putExtra("newIndication", NewIndication+"");
					it.putExtra("type", FAILURE_FLAG);
					it.putExtra("description", description);
					it.putExtra("finishTime", time);
					it.putExtra("filePath", filePath);
					startActivity(it);
					finish();
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
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	/***
	 * 发送扫卡指令
	 */
	private void sendCmd(){
		
		Intent ac = new Intent();
		ac.setAction("org.whut.services.ScanBarCodeService");
		ac.putExtra("activity", activity);
		sendBroadcast(ac);
		
		Intent sendToService = new Intent(ChangeGasMeterActivity.this,ScanBarCodeService.class);
		sendToService.putExtra("cmd", cmd);
		startService(sendToService);

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
	
	private void endService(){
		Intent ac = new Intent();
		ac.setAction("org.whut.services.ScanBarCodeService");
		ac.putExtra("activity", activity);
		ac.putExtra("closeflag", true);
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
	
	
	
	
	class MyBroadcastReceiver extends BroadcastReceiver{

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
