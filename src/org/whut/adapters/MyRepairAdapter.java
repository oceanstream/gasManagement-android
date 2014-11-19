package org.whut.adapters;

import java.util.HashMap;
import java.util.List;

import org.whut.gasmanagement.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyRepairAdapter extends BaseAdapter{

	private List<HashMap<String,String>> list;
	private LayoutInflater inflater;
	private Context context;
	
	public MyRepairAdapter(Context context,List<HashMap<String,String>> list){
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView==null){
			convertView = inflater.inflate(R.layout.listitem_repair, null);
			ViewHolder holder = new ViewHolder();
			holder.tv_taskName = (TextView) convertView.findViewById(R.id.content_name_repair);
			holder.tv_isComplete = (TextView) convertView.findViewById(R.id.isComplete_repair);
			holder.tv_isUpload = (TextView) convertView.findViewById(R.id.isUpload_repair);
			holder.tv_isUpdate = (TextView) convertView.findViewById(R.id.isChanged_repair);
			holder.tv_address = (TextView) convertView.findViewById(R.id.address_repair);
			holder.tv_postDate = (TextView) convertView.findViewById(R.id.postDate_repair);
			holder.tv_failure_type = (TextView) convertView.findViewById(R.id.userName_repair);
			holder.clearTag = (ImageView) convertView.findViewById(R.id.clearTag_repair);
			convertView.setTag(holder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		
		holder.tv_taskName.setText(list.get(position).get("taskName"));
		holder.tv_address.setText("地址："+list.get(position).get("address"));
		holder.tv_postDate.setText("下发时间："+list.get(position).get("postDate"));
		
		
		switch(Integer.parseInt(list.get(position).get("type"))){
			case -1:
				holder.tv_failure_type.setText("故障类型：未知");
				break;
			case 0:
				holder.tv_failure_type.setText("故障类型：电池耗尽");
				break;
			case 1:
				holder.tv_failure_type.setText("故障类型：开关未开启");
				break;
			case 2:
				holder.tv_failure_type.setText("故障类型：其它");
				break;
		}
		
		
		
		switch(Integer.parseInt(list.get(position).get("isComplete"))){
			case 0:
				holder.tv_isComplete.setText("未完成");
				holder.tv_isComplete.setBackgroundResource(R.color.lightgray);
				break;
			case 1:
				holder.tv_isComplete.setText("已完成");
				holder.tv_isComplete.setBackgroundResource(R.color.bisque);
				break;
		}
		
		switch(Integer.parseInt(list.get(position).get("uploadFlag"))){
		case 0:
			holder.tv_isUpload.setText("未上传");
			holder.tv_isUpload.setBackgroundResource(R.color.lightgray);
			break;
		case 1:
			holder.tv_isUpload.setText("已上传");
			holder.tv_isUpload.setBackgroundResource(R.color.lightskyblue);
			break;
	}	
		switch(Integer.parseInt(list.get(position).get("isUpdate"))){
			case 0:
				//未换表
				holder.tv_isUpdate.setVisibility(View.VISIBLE);
				holder.tv_isUpdate.setText("未换表");
				holder.tv_isUpdate.setBackgroundResource(R.color.lightgray);
				break;
			case 1:
				//换表
				holder.tv_isUpdate.setVisibility(View.VISIBLE);
				holder.tv_isUpdate.setText("已换表");
				holder.tv_isUpdate.setBackgroundResource(R.color.lightyellow);
				break;
			case -1:
				//待定
				holder.tv_isUpdate.setVisibility(View.GONE);
				break;
		}
		
		
		if(Integer.parseInt(list.get(position).get("isComplete"))==1&&
				Integer.parseInt(list.get(position).get("uploadFlag"))==1){
			holder.clearTag.setVisibility(View.VISIBLE);
		}else{
			holder.clearTag.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	class ViewHolder{
		TextView tv_taskName;
		TextView tv_isComplete;
		TextView tv_isUpload;
		TextView tv_isUpdate;
		TextView tv_address;
		TextView tv_postDate;
		TextView tv_failure_type;
		ImageView clearTag;
	}

}
