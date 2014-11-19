package org.whut.adapters;

import java.util.HashMap;
import java.util.List;

import org.whut.gasmanagement.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyInstallAdapter extends BaseAdapter{

	private List<HashMap<String,String>> list;
	private LayoutInflater inflater;
	private Context context;
	
	public MyInstallAdapter(Context context,List<HashMap<String,String>> list){
		this.context = context;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.listitem_install, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.content_name);
			holder.isComplete = (TextView) convertView.findViewById(R.id.isComplete);
			holder.isUpload = (TextView) convertView.findViewById(R.id.isUpload);
			holder.address = (TextView) convertView.findViewById(R.id.address);
			holder.date = (TextView) convertView.findViewById(R.id.postDate);
			holder.username = (TextView) convertView.findViewById(R.id.userName);
			holder.clearTag = (ImageView) convertView.findViewById(R.id.clearTag);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.title.setText(list.get(position).get("taskName"));
		holder.address.setText("地址："+list.get(position).get("address"));
		holder.date.setText("下发时间："+list.get(position).get("postDate"));
		holder.username.setText("用户名："+list.get(position).get("userName"));
		
		
		switch(Integer.parseInt(list.get(position).get("isComplete"))){
		case 0:
			holder.isComplete.setText("未完成");
			holder.isComplete.setBackgroundResource(R.color.lightgray);
			break;
		case 1:
			holder.isComplete.setText("已完成");
			holder.isComplete.setBackgroundResource(R.color.bisque);
			break;
		}
		
		switch(Integer.parseInt(list.get(position).get("uploadFlag"))){
		case 0:
			holder.isUpload.setText("未上传");
			holder.isUpload.setBackgroundResource(R.color.lightgray);
			break;
		case 1:
			holder.isUpload.setText("已上传");
			holder.isUpload.setBackgroundResource(R.color.lightskyblue);
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
		TextView title;
		TextView isUpload;
		TextView isComplete;
		TextView address;
		TextView date;
		TextView username;
		ImageView clearTag;
	}
}
