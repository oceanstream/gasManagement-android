package org.whut.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.TextUtils;
import org.whut.client.MyClient;
import org.whut.database.service.imp.TaskRepairServiceDao;
import org.whut.gasmanagement.R;
import org.whut.utils.CommonUtils;
import org.whut.utils.UrlStrings;
import org.whut.utils.XmlUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



@SuppressLint("HandlerLeak")
public class NotChangeGasMeterActivity extends Activity{

	private boolean MODE_TAG;
	private String id;
	private String address;
	private String userName;
	private int UPDATE_FLAG;
	private int FAILURE_FLAG;
	private String finishTime;
	private String description;
	private String filePath;
	
	private TaskRepairServiceDao dao;
	private Handler handler;
	
	private TextView tv_address,tv_failure_type,tv_failure_description,
	tv_userName,tv_finishTime,tv_isUpdate;
	
	private RelativeLayout upload_now,upload_later;
	
	private Button tv_yes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_not_change_gas_meter);
		
		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		id = getIntent().getStringExtra("id");
		address = getIntent().getStringExtra("address");
		userName = getIntent().getStringExtra("userName");
		UPDATE_FLAG = getIntent().getIntExtra("UPDATE_FLAG", -1);
		FAILURE_FLAG = getIntent().getIntExtra("FAILURE_FLAG", -1);
		finishTime = getIntent().getStringExtra("finishTime");
		description  = getIntent().getStringExtra("description");
		filePath = getIntent().getStringExtra("filePath");
		
		dao = new TaskRepairServiceDao(this);
		
		tv_address = (TextView) findViewById(R.id.address_value);
		tv_failure_type = (TextView) findViewById(R.id.failure_value);
		tv_failure_description = (TextView) findViewById(R.id.description_value);
		tv_userName = (TextView) findViewById(R.id.userName_value);
		tv_finishTime = (TextView) findViewById(R.id.finishTime_value);
		tv_isUpdate  = (TextView) findViewById(R.id.isUpdate_value);
		
		upload_now = (RelativeLayout) findViewById(R.id.upload_now);
		upload_later = (RelativeLayout) findViewById(R.id.upload_later);
		
		tv_yes = (Button) findViewById(R.id.btn_later);
		
		
		tv_address.setText(address);
		
		switch(FAILURE_FLAG){
		case 0:
			tv_failure_type.setText("卡表电池耗尽");
			break;
		case 1:
			tv_failure_type.setText("卡表开关未开启");
			break;
		case 2:
			tv_failure_type.setText("其它");
			break;
		}
		
		if(TextUtils.isEmpty(description)){
			tv_failure_description.setText("无");
		}else{
			tv_failure_description.setText(description);
		}
		
		tv_userName.setText(userName);
		
		
		
		tv_finishTime.setText(CommonUtils.formatTime(finishTime));
		
		tv_isUpdate.setText("未换表");
		
		if(MODE_TAG){
			//有网模式
			upload_now.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new Thread(new UploadThread()).start();
				}
			});
			
			upload_later.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "结果已在本地保存，请手动上传维修结果，或在有网模式中上传！", Toast.LENGTH_SHORT).show();
					finish();
				}
			});
			
		}else{
			//无网模式
			
		}
		
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
					case 0:
						Toast.makeText(getApplicationContext(), "上传失败，请稍后再试！", Toast.LENGTH_SHORT).show();
						break;
					case 1:
						//更新本地数据库
						dao.updateUploadFlag(Integer.parseInt(id));
						
						//更新xml
						
						try {
							XmlUtils.updateUploadFlag(id,filePath);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
						Toast.makeText(getApplicationContext(), "结果上传成功！", Toast.LENGTH_SHORT).show();
						finish();
						break;
				}
			}
			
		};
		
		
		
		
		
		
		
		
	}
	
	class UploadThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Message msg = Message.obtain();
			
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", id));
				params.add(new BasicNameValuePair("isUpdate", UPDATE_FLAG+""));
				params.add(new BasicNameValuePair("type", FAILURE_FLAG+""));
				params.add(new BasicNameValuePair("description", description));
				String result = MyClient.getInstance().doPost(UrlStrings.BASE_URL+"ICCard/rest/repairService/postRepairTasks", params);
				
				Log.i("msg", "post upload ---->"+result);
				
			//	String result = "SUCCESS";
				
				if(result.equals("SUCCESS")){
					msg.what = 1;
				}else{
					msg.what = 0;
				}
				handler.sendMessage(msg);	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
	}
}
