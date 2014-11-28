package org.whut.activities;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.whut.adapters.MyRepairAdapter;
import org.whut.database.entities.TaskRepair;
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
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class DeviceRepairActivity extends Activity{

	private boolean MODE_TAG ;
	private String userName;

	private ListView listView;
	private List<HashMap<String,String>> list;
	private MyRepairAdapter adapter;

	private TaskRepairServiceDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_device_repair);

		MODE_TAG = getIntent().getBooleanExtra("MODE_TAG", false);
		userName = getIntent().getStringExtra("userName");

		listView = (ListView) findViewById(R.id.listView);

		dao = new TaskRepairServiceDao(this);

		File file = new File("/sdcard/gasManagement/config/Repair.xml");

		if(file.exists()){
			try{

				//从XML解析数据
				List<TaskRepair> listTask = XmlUtils.getRepairDateFromXml(file);

				XmlUtils.SaveRepairToDatabase(listTask,this);

				list =  CommonUtils.getTaskRepair(this,userName);

				adapter = new MyRepairAdapter(this, list);

				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int arg2, long arg3) {
						// TODO Auto-generated method stub
						int completeFlag = Integer.parseInt(list.get(arg2).get("isComplete"));
						int uploadFlag = Integer.parseInt(list.get(arg2).get("uploadFlag"));

						if(completeFlag==1&&uploadFlag==1){
							//该项任务已完成,跳转至维修任务详情页，显示此次维修任务的详细信息
							Builder builder = new AlertDialog.Builder(DeviceRepairActivity.this);
							builder.setTitle("提示").setMessage("任务已完成！是否查看详情？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									//根据是否换表来跳转
									switch(Integer.parseInt(list.get(arg2).get("isUpdate"))){
									case 0:
										//未换表 ==》NotChangeGasMeterActivity

										Intent it = new Intent(DeviceRepairActivity.this,NotChangeGasMeterActivity.class);
										it.putExtra("MODE_TAG", MODE_TAG);
										it.putExtra("id", list.get(arg2).get("id"));
										it.putExtra("address", list.get(arg2).get("address"));
										it.putExtra("userName", userName);
										it.putExtra("UPDATE_FLAG", Integer.parseInt(list.get(arg2).get("isUpdate")));
										it.putExtra("FAILURE_FLAG", Integer.parseInt(list.get(arg2).get("type")));
										it.putExtra("finishTime", list.get(arg2).get("finishTime"));
										it.putExtra("description", list.get(arg2).get("description"));
										it.putExtra("filePath", list.get(arg2).get("filePath"));
										
										it.putExtra("COMPLETE_TAG", true);
										
										//完成标识，提示下一Activity
										startActivity(it);
										finish();



										break;
									case 1:
										//换表 ==》UploadForRepairActivity

										Intent it2 = new Intent(DeviceRepairActivity.this,UploadForRepairActivity.class);

										HashMap<String,String> params = dao.getIntentParams(Integer.parseInt(list.get(arg2).get("id")));

										it2.putExtra("MODE_TAG", MODE_TAG);
										it2.putExtra("id", list.get(arg2).get("id"));
										it2.putExtra("address", params.get("address"));
										it2.putExtra("userName", params.get("userName"));
										it2.putExtra("oldBarCode", params.get("oldBarCode"));
										it2.putExtra("newBarCode", params.get("newBarCode"));
										it2.putExtra("oldIndication", params.get("oldIndication"));
										it2.putExtra("newIndication", params.get("newIndication"));
										it2.putExtra("type", Integer.parseInt(params.get("type")));
										it2.putExtra("description", params.get("description"));
										it2.putExtra("finishTime", params.get("finishTime"));
										it2.putExtra("filePath", params.get("filePath"));
										//完成标识，提示下一Activity
										it2.putExtra("COMPLETE_TAG", true);
										
										startActivity(it2);
										finish();	


										break;
									}

								}
							}).setNegativeButton("取消", null).show();
						}else if(completeFlag==1&&uploadFlag==0){
							//该项任务本地完成，未上传
							switch(Integer.parseInt(list.get(arg2).get("isUpdate"))){
							case 0:
								//未换表
								Builder builder = new AlertDialog.Builder(DeviceRepairActivity.this);
								if(MODE_TAG){
									//有网
									builder.setMessage("该任务已经完成，但未上传，是否现在上传？");

								}else{
									builder.setMessage("该任务已经完成，请在有网模式中上传或手动上传！是否查看任务详情？");
								}

								builder.setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Intent it = new Intent(DeviceRepairActivity.this,NotChangeGasMeterActivity.class);
										it.putExtra("MODE_TAG", MODE_TAG);
										it.putExtra("id", list.get(arg2).get("id"));
										it.putExtra("address", list.get(arg2).get("address"));
										it.putExtra("userName", userName);
										it.putExtra("UPDATE_FLAG", Integer.parseInt(list.get(arg2).get("isUpdate")));
										it.putExtra("FAILURE_FLAG", Integer.parseInt(list.get(arg2).get("type")));
										it.putExtra("finishTime", list.get(arg2).get("finishTime"));
										it.putExtra("description", list.get(arg2).get("description"));
										it.putExtra("filePath", list.get(arg2).get("filePath"));
										startActivity(it);
										finish();
									}
								}).setNegativeButton("取消", null).show();

								break;
							case 1:
								//换表
								Builder builder2 = new AlertDialog.Builder(DeviceRepairActivity.this);
								if(MODE_TAG){
									builder2.setMessage("该任务已经完成，但未上传，是否现在上传？");
								}else{
									builder2.setMessage("该任务已经完成，请在有网模式中上传或手动上传！是否查看任务详情？");
								}
								builder2.setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Intent it = new Intent(DeviceRepairActivity.this,UploadForRepairActivity.class);

										HashMap<String,String> params = dao.getIntentParams(Integer.parseInt(list.get(arg2).get("id")));

										it.putExtra("MODE_TAG", MODE_TAG);
										it.putExtra("id", list.get(arg2).get("id"));
										it.putExtra("address", params.get("address"));
										it.putExtra("userName", params.get("userName"));
										it.putExtra("oldBarCode", params.get("oldBarCode"));
										it.putExtra("newBarCode", params.get("newBarCode"));
										it.putExtra("oldIndication", params.get("oldIndication"));
										it.putExtra("newIndication", params.get("newIndication"));
										it.putExtra("type", Integer.parseInt(params.get("type")));
										it.putExtra("description", params.get("description"));
										it.putExtra("finishTime", params.get("finishTime"));
										it.putExtra("filePath", params.get("filePath"));
										startActivity(it);
										finish();	
									}
								}).setNegativeButton("取消", null).show();

								break;
							}

						}else{
							//该项任务还未完成，开始执行。
							Intent it = new Intent(DeviceRepairActivity.this,RepairOperationActivity.class);
							it.putExtra("MODE_TAG", MODE_TAG);
							it.putExtra("id", list.get(arg2).get("id"));
							it.putExtra("address", list.get(arg2).get("address"));
							it.putExtra("userName", list.get(arg2).get("userName"));
							it.putExtra("type", list.get(arg2).get("type"));
							startActivity(it);
							finish();
						}

					}
				});
			}catch(Exception e){
				e.printStackTrace();
			}

		}else{
			listView.setVisibility(View.GONE);
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示").setMessage("检测到本机无维修任务文件，是否从服务器端下载？").setPositiveButton("是", 
					new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent it = new Intent(DeviceRepairActivity.this,GetConfigFileActivity.class);
					startActivity(it);
					finish();
				}
			}).setNegativeButton("否", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "请手动添加\"Installation.xml\"或在查询页面下载！", Toast.LENGTH_SHORT).show();
				}
			}).show();
		}

	}
}
