package org.whut.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.whut.database.service.imp.TaskRepairServiceDao;
import org.whut.gasmanagement.R;
import org.whut.utils.CommonUtils;
import org.whut.utils.XmlUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class UploadForRepairActivity extends Activity{

	private boolean MODE_TAG;
	private String id;
	private String address;
	private String oldBarCode;
	private String newBarCode;
	private String oldIndication;
	private String newIndication;
	private String userName;
	private int type;
	private String description;
	private String finishTime;
	private String filePath;

	private TextView tv_address_value,tv_obc_value,tv_nbc_value,tv_oi_value,tv_ni_value;
	private TextView tv_userName_value,tv_type_value,tv_description_value,tv_finishTime_value;

	private RelativeLayout upload_now;
	private RelativeLayout upload_later;

	private Button btn_text;
	private ImageView divider_img;

	private Handler handler;

	private TaskRepairServiceDao dao;

	private boolean COMPLETE_TAG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_for_repair);

		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		id = getIntent().getStringExtra("id");
		address = getIntent().getStringExtra("address");
		oldBarCode = getIntent().getStringExtra("oldBarCode");
		newBarCode = getIntent().getStringExtra("newBarCode");
		oldIndication = getIntent().getStringExtra("oldIndication");
		newIndication = getIntent().getStringExtra("newIndication");
		userName = getIntent().getStringExtra("userName");
		type = getIntent().getIntExtra("type", -1);
		description  = getIntent().getStringExtra("description");
		finishTime = getIntent().getStringExtra("finishTime");

		COMPLETE_TAG = getIntent().getBooleanExtra("COMPLETE_TAG", false);

		filePath = getIntent().getStringExtra("filePath");
		dao = new TaskRepairServiceDao(this);

		tv_address_value = (TextView) findViewById(R.id.repair_address_value);
		tv_obc_value = (TextView) findViewById(R.id.repair_obc_value);
		tv_nbc_value = (TextView) findViewById(R.id.repair_nbc_value);
		tv_oi_value = (TextView) findViewById(R.id.repair_oi_value);
		tv_ni_value = (TextView) findViewById(R.id.repair_ni_value);
		tv_userName_value = (TextView) findViewById(R.id.repair_un_value);
		tv_type_value = (TextView) findViewById(R.id.repair_ft_value);
		tv_description_value = (TextView) findViewById(R.id.repair_d_value);
		tv_finishTime_value = (TextView) findViewById(R.id.repair_finishtime_value);

		upload_now = (RelativeLayout) findViewById(R.id.upload_now);
		upload_later = (RelativeLayout) findViewById(R.id.upload_later);

		btn_text = (Button) findViewById(R.id.btn_later);

		divider_img = (ImageView) findViewById(R.id.divider_img);
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case 0:
					Toast.makeText(getApplicationContext(), "上传失败，请稍后再试！", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					//修改数据库中uploadFlag
					dao.updateUploadFlag(Integer.parseInt(id));
					//修改对应文件中的uploadFlag
					try {
						XmlUtils.updateUploadFlag(id,filePath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(getApplicationContext(), "上传成功！", Toast.LENGTH_SHORT).show();
					finish();
					break;
				}
			}
		};

		tv_address_value.setText(address);
		tv_obc_value.setText(oldBarCode);
		tv_nbc_value.setText(newBarCode);
		tv_oi_value.setText(oldIndication);
		tv_ni_value.setText(newIndication);
		tv_userName_value.setText(userName);

		switch(type){
		case -1:
			tv_type_value.setText("未知");
			break;
		case 0:
			tv_type_value.setText("卡表电池耗尽");
			break;
		case 1:
			tv_type_value.setText("卡表开关未开启");
			break;
		case 2:
			tv_type_value.setText("其它");
			break;
		}

		if(description.equals("")||description==null){
			tv_description_value.setText("无");
		}else{
			tv_description_value.setText(description);
		}

		tv_finishTime_value.setText(CommonUtils.formatTime(finishTime));

		if(MODE_TAG){
			//有网模式
			if(COMPLETE_TAG){
				//已完成，显示详情
				upload_now.setVisibility(View.GONE);
				divider_img.setVisibility(View.GONE);
				btn_text.setText("确定");
				upload_later.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});	
			}else{
				upload_now.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new Thread(new UploadResultThread()).start();
					}
				});

				upload_later.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "结果已在本地保存！可稍后上传或者手动选择对应文件上传！", Toast.LENGTH_SHORT).show();
						finish();
					}
				});	
			}
			
			
			
			

		}else{
			//无网模式
			upload_now.setVisibility(View.GONE);
			divider_img.setVisibility(View.GONE);
			btn_text.setText("确定");
			upload_later.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(COMPLETE_TAG){
						finish();
					}else{
						Toast.makeText(getApplicationContext(), "结果已在本地保存！请在有网模式中上传或手动上传！", Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			});

		}
	}

	class UploadResultThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = Message.obtain();

			try {

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", id));
				params.add(new BasicNameValuePair("newBarCode", newBarCode));
				params.add(new BasicNameValuePair("oldBarCode", oldBarCode));
				params.add(new BasicNameValuePair("newIndication", newIndication));
				params.add(new BasicNameValuePair("oldIndication", oldIndication));
				params.add(new BasicNameValuePair("type", type+""));
				params.add(new BasicNameValuePair("description", description));

				//	String result = MyClient.getInstance().doPost("", params);

				//	Log.i("msg", "post upload ---->"+result);

				String result = "SUCCESS";

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
