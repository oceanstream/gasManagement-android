package org.whut.adapters;

import org.whut.gasmanagement.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyGridAdapter extends BaseAdapter{

	private Context context;
	private int[] colors;
	private int[] images;
	private String[] functions;
	private LayoutInflater inflater;
	private int Height;
	
	public MyGridAdapter(Context context,int[] images,String[] functions,int[] colors,int screenHeight){
		this.context = context;
		this.colors = colors;
		this.images = images;
		this.functions = functions;
		inflater = LayoutInflater.from(context);
		this.Height = screenHeight;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return images[position];
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
			convertView = inflater.inflate(R.layout.gridview_item_main, null);	
			
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,(Height/2-18));
			Log.i("msg", "adapter------>"+params.height);
			convertView.setLayoutParams(params);

			ViewHolder holder = new ViewHolder();
			holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.iv_layout);
			holder.imageView =(ImageView) convertView.findViewById(R.id.iv_icon);
			holder.textView = (TextView) convertView.findViewById(R.id.tv_function);
			convertView.setTag(holder);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		
				
		holder.relativeLayout.setBackgroundResource(colors[position]);
		holder.imageView.setImageDrawable(context.getResources().getDrawable(images[position]));
		
		
		AssetManager am = context.getAssets();
		Typeface tf = Typeface.createFromAsset(am, "font/hwcy.ttf");
		holder.textView.setTypeface(tf);
		holder.textView.setText(functions[position]);
	//	holder.textView.getPaint().setFakeBoldText(true);
		
		
		
		return convertView;
	}

	class ViewHolder{
		RelativeLayout relativeLayout;
		ImageView imageView;
		TextView textView;

	}



}
