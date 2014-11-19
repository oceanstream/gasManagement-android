package org.whut.activities;

import java.util.Timer;
import java.util.TimerTask;

import org.whut.client.MyClient;
import org.whut.database.entities.User;
import org.whut.database.service.imp.UserServiceDao;
import org.whut.gasmanagement.R;
import org.whut.utils.MD5Utils;
import org.whut.utils.UrlStrings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity{

	
	private boolean MODE_TAG;
	private String userName;
	
	private EditText edt_username;
	private EditText edt_password;
	private RelativeLayout btn_login;
	private ImageView btn_clear;
	private CheckBox btn_show;
	private ProgressDialog dialog;
	private Timer timer;

	//监听EditText
	private TextWatcher watcher;

	private Handler handler;
	
	private UserServiceDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		dao = new UserServiceDao(this);
		
		
		edt_username = (EditText) findViewById(R.id.edt_username);
		edt_password = (EditText) findViewById(R.id.edt_password);
		btn_login = (RelativeLayout) findViewById(R.id.btn_login);
		btn_clear =  (ImageView) findViewById(R.id.img_clear);
		btn_show = (CheckBox) findViewById(R.id.img_show);
		
		edt_username.setText("root");
		edt_password.setText("root");
		
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在登录，请稍后...");
		dialog.setCancelable(false);

		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case 0:
					timer.cancel();
					dialog.cancel();
					Toast.makeText(getApplicationContext(), "用户名或密码不能为空，请重新输入！", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					timer.cancel();
					dialog.cancel();
					Toast.makeText(getApplicationContext(), "用户名或密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					timer.cancel();
					dialog.cancel();
					Toast.makeText(getApplicationContext(), "登录成功，正在为您跳转...", Toast.LENGTH_SHORT).show();
					
					if(msg.obj!=null&&(msg.obj.toString().equals("no_internet"))){
						//无网模式登录成功
						userName = edt_username.getText().toString();
					}else{
						//有网模式登录成功
						userName = edt_username.getText().toString();
						//将用户名、密码存入本地数据库
						User user = new User(userName,MD5Utils.string2MD5(edt_password.getText().toString()));
						dao.addUser(user);
					}
					
					Intent it = new Intent(LoginActivity.this,MainActivity.class);
					it.putExtra("MODE_TAG", MODE_TAG);
					it.putExtra("userName", userName);
					startActivity(it);
					finish();
					break;
				case 3:
					Toast.makeText(getApplicationContext(), "登录超时，请重试！", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};

		watcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.toString()!=null){
					btn_clear.setVisibility(View.VISIBLE);
					btn_clear.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							edt_username.setText("");
						}
					});
				}else{
					btn_clear.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		};



		edt_username.addTextChangedListener(watcher);

		btn_show.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					btn_show.setBackgroundResource(R.drawable.show_btn_pressed);
					edt_password.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
				}else{
					btn_show.setBackgroundResource(R.drawable.show_btn_normal);
					edt_password.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
				}
			}
		});


		btn_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.show();
				timer = new Timer();
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						dialog.cancel();
						Message msg = Message.obtain();
						msg.what=3;
						handler.sendMessage(msg);
					}
				}, 7000);
				new Thread(new LoginThread()).start();
			}
		});
	}


	class LoginThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = Message.obtain();
			if(TextUtils.isEmpty(edt_username.getText().toString())||TextUtils.isEmpty(edt_password.getText().toString())){
				//用户名或密码为空
				msg.what = 0;
				handler.sendMessage(msg);
			}else{
				try{
					if(MODE_TAG){
						//有网模式
						boolean result = MyClient.getInstance().login(edt_username.getText().toString(), edt_password.getText().toString(), UrlStrings.BASE_URL+"ICCard/rest/userService/getSessionId");
							Log.i("msg", result+"");
						if(!result){
								//登录失败
								msg.what = 1;
								handler.sendMessage(msg);
							}else{
								msg.what = 2;
								handler.sendMessage(msg);
							}
					}else{
						boolean result = dao.validateUser(new User(edt_username.getText().toString(),edt_password.getText().toString()));
						if(result){
							//本地验证成功
							msg.what = 2;
							msg.obj = "no_internet";
							handler.sendMessage(msg);
						}else{
							//本地验证失败
							msg.what = 1;
							handler.sendMessage(msg);
						}
						
					}
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}

		}
	}
}
