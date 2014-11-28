package org.whut.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.TextUtils;
import org.whut.database.service.imp.TaskRepairServiceDao;
import org.whut.gasmanagement.R;
import org.whut.utils.CommonUtils;
import org.whut.utils.XmlUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "SimpleDateFormat", "SdCardPath" })
public class RepairOperationActivity extends Activity{

	private RelativeLayout type_choose;
	private RelativeLayout update_choose;
	
	private TextView type_value;
	private TextView update_value;
	
	

	private int FAILURE_FLAG = -1;
	/***
	 *  -1 未知
	 *  0 不换
	 *  1 换
	 */
	private int UPDATE_FLAG = -1;
	
	private EditText edt_description;
	private String description;
	
	private TextView next_step;
	
	private boolean MODE_TAG;
	private String id;
	private String address;
	private String userName;
	private int type;
	
	private TaskRepairServiceDao dao;
	
	//文件存储相关
	private static final String oldPath = "/sdcard/gasManagement/config/Repair.xml";
	private static final String newPath = "/sdcard/gasManagement/data/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repair_operation);
		
		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		id = getIntent().getStringExtra("id");
		address = getIntent().getStringExtra("address");
		userName = getIntent().getStringExtra("userName");
		type = Integer.parseInt(getIntent().getStringExtra("type"));
		
		dao = new TaskRepairServiceDao(this);
		
		type_choose = (RelativeLayout) findViewById(R.id.type_choose);
		update_choose = (RelativeLayout) findViewById(R.id.update_choose);
		type_value = (TextView) findViewById(R.id.type_value);
		update_value = (TextView) findViewById(R.id.update_value);
		edt_description = (EditText) findViewById(R.id.description);
		next_step = (TextView) findViewById(R.id.nextStep);
	
		type_choose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder builder = new AlertDialog.Builder(RepairOperationActivity.this);
				builder.setTitle("故障类型判定").setItems(new String[]{"卡表电池耗尽","卡表开关未开启","其它"}, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(which){
						case 0:
							type_value.setText("卡表电池耗尽");
							FAILURE_FLAG = 0;
							edt_description.setVisibility(View.GONE);
							break;
						case 1:
							type_value.setText("卡表开关未开启");
							FAILURE_FLAG = 1;
							edt_description.setVisibility(View.GONE);
							break;
						case 2:
							type_value.setText("其它");
							FAILURE_FLAG = 2;
							edt_description.setVisibility(View.VISIBLE);
							break;
						}
					}
				}).show();
			}
		});
		
		
		update_choose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder builder =  new AlertDialog.Builder(RepairOperationActivity.this);
				builder.setTitle("维修类型判定").setItems(new String[]{"不换表","换表"}, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(which){
							case 0:
								update_value.setText("不换表");
								UPDATE_FLAG = 0;
								break;
							case 1:
								update_value.setText("换表");
								UPDATE_FLAG = 1;
								break;
						}
					}
				}).show();
			}
		});
		
		
		next_step.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				description = edt_description.getText().toString();
				
				//未输入结果
				if(FAILURE_FLAG == -1 || UPDATE_FLAG == -1){
					Toast.makeText(RepairOperationActivity.this, "请选择故障类型，并判定是否需要更换燃气卡表！", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//其他，但未输入描述
				if(FAILURE_FLAG == 2 && TextUtils.isEmpty(edt_description.getText().toString())){
					Toast.makeText(RepairOperationActivity.this, "请输入故障描述！", Toast.LENGTH_SHORT).show();
					return;
				}
				
				
				switch(UPDATE_FLAG){
					case 0:
						//不换表
	
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
							XmlUtils.SaveRepairResultToXml(id,(type+""),(UPDATE_FLAG+""),description,filePath);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
											
						Log.i("msg", Integer.parseInt(id)+","+FAILURE_FLAG+","+UPDATE_FLAG+","+userName+","+description+","+filePath+","+time);
						
						
						dao.updateRepairResult(Integer.parseInt(id),FAILURE_FLAG,UPDATE_FLAG,userName,description,filePath,time);
						
						Intent it = new Intent(RepairOperationActivity.this,NotChangeGasMeterActivity.class);
						it.putExtra("MODE_TAG", MODE_TAG);
						it.putExtra("id", id);
						it.putExtra("address", address);
						it.putExtra("userName", userName);
						it.putExtra("UPDATE_FLAG", UPDATE_FLAG);
						it.putExtra("FAILURE_FLAG", FAILURE_FLAG);
						it.putExtra("description", description);
						it.putExtra("filePath", filePath);
						it.putExtra("finishTime", time);
						startActivity(it);
						finish();
						break;
					case 1:
						//换表
						Intent it2 = new Intent(RepairOperationActivity.this,ChangeGasMeterActivity.class);
						it2.putExtra("MODE_TAG", MODE_TAG);
						it2.putExtra("id", id);
						it2.putExtra("address", address);
						it2.putExtra("userName", userName);
						it2.putExtra("UPDATE_FLAG", UPDATE_FLAG);
						it2.putExtra("FAILURE_FLAG", FAILURE_FLAG);
						it2.putExtra("description", description);
						startActivity(it2);
						finish();
						break;
				}
				
			}
		});
		
		
	}
	
}
