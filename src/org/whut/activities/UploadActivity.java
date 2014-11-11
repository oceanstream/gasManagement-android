package org.whut.activities;




import org.whut.gasmanagement.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class UploadActivity extends Activity{

	private TextView tv_address;
	private TextView tv_barCode;
	private TextView tv_userName;
	private TextView tv_indication;
	
	private String id;
	private String address;
	private String barCode;
	private String userName;
	private String indication;
	
	private RelativeLayout btn_upload_now;
	private RelativeLayout btn_upload_later;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		id = getIntent().getStringExtra("id");
		address = getIntent().getStringExtra("address");
		barCode = getIntent().getStringExtra("barCode");
		userName = getIntent().getStringExtra("userName");
		indication = getIntent().getStringExtra("indication");
		
		tv_address = (TextView) findViewById(R.id.address_value);
		tv_barCode = (TextView) findViewById(R.id.barCode_value);
		tv_userName = (TextView) findViewById(R.id.userName_value);
		tv_indication = (TextView) findViewById(R.id.indication_input);

		btn_upload_now = (RelativeLayout) findViewById(R.id.upload_now);
		btn_upload_later = (RelativeLayout) findViewById(R.id.upload_later);
		
		tv_address.setText(address);
		tv_barCode.setText(barCode);
		tv_userName.setText(userName);
		tv_indication.setText(indication);
		
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
	}

	class UploadThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {

			//	String result = MyClient.getInstance().doSendFile(new File("/sdcard/gasManagement/config/Installation.xml"),UrlStrings.BASE_URL+"ICCard/rest/installationService/postInstallationTasks");

				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}



		}

	}
}
