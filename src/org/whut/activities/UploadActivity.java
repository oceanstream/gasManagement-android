package org.whut.activities;




import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.whut.client.MyClient;
import org.whut.database.service.imp.TaskInstallServiceDao;
import org.whut.gasmanagement.R;
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


public class UploadActivity extends Activity{

	private TextView tv_address;
	private TextView tv_barCode;
	private TextView tv_userName;
	private TextView tv_indication;

	private boolean MODE_TAG;

	private String id;
	private String address;
	private String barCode;
	private String userName;
	private String indication;
	private String filePath;

	private RelativeLayout btn_upload_now;
	private RelativeLayout btn_upload_later;

	private Button btn_text;

	private Handler handler;
	private TaskInstallServiceDao dao;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		id = getIntent().getStringExtra("id");
		address = getIntent().getStringExtra("address");
		barCode = getIntent().getStringExtra("barCode");
		userName = getIntent().getStringExtra("userName");
		indication = getIntent().getStringExtra("indication");
		filePath = getIntent().getStringExtra("filePath");


		tv_address = (TextView) findViewById(R.id.address_value);
		tv_barCode = (TextView) findViewById(R.id.barCode_value);
		tv_userName = (TextView) findViewById(R.id.userName_value);
		tv_indication = (TextView) findViewById(R.id.indication_input);

		btn_upload_now = (RelativeLayout) findViewById(R.id.upload_now);
		btn_upload_later = (RelativeLayout) findViewById(R.id.upload_later);

		btn_text = (Button) findViewById(R.id.btn_later);

		dao = new TaskInstallServiceDao(this);

		handler  = new Handler(){

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
					//修改对应xml文件中uploadFlag
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


		tv_address.setText(address);
		tv_barCode.setText(barCode);
		tv_userName.setText(userName);
		tv_indication.setText(indication);

		if(MODE_TAG){
			//有网模式

			btn_upload_now.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new Thread(new UploadThread()).start();
				}
			});

			btn_upload_later.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "您可以在“查询-查询操作历史”中上传此次结果！", Toast.LENGTH_SHORT).show();
					finish();
				}
			});






		}else{
			//无网模式
			btn_upload_now.setVisibility(View.GONE);
			btn_text.setText("确定");
			btn_upload_later.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Toast.makeText(getApplicationContext(), "结果已在本地保存，请在有网模式中上传或手动选择对应文件上传！", Toast.LENGTH_SHORT).show();
					finish();

				}
			});

		}



	}

	class UploadThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Message msg = Message.obtain();

			try {

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", id));
				params.add(new BasicNameValuePair("barCode", barCode));
				params.add(new BasicNameValuePair("indication", indication));
				String result = MyClient.getInstance().doPost(UrlStrings.BASE_URL+"ICCard/rest/installationService/postInstallationTasks", params);

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
