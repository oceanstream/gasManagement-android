package org.whut.activities;

import org.whut.gasmanagement.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ModeChooseActivity extends Activity implements OnClickListener{

	private TextView tv_internet;
	private TextView tv_no_internet;
	private static boolean MODE_TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mode_choose);
		
		tv_internet = (TextView) findViewById(R.id.mode_internet);
		
		tv_no_internet = (TextView) findViewById(R.id.mode_no_internet);
		
		
		tv_internet.setOnClickListener(this);
		tv_no_internet.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.mode_internet:
			MODE_TAG = true;
			Intent it = new Intent(this,LoginActivity.class);
			it.putExtra("MODE_TAG", MODE_TAG);
			startActivity(it);
			finish();
			break;
		case R.id.mode_no_internet:
			MODE_TAG = false;
			Intent it2 = new Intent(this,LoginActivity.class);
			it2.putExtra("MODE_TAG", MODE_TAG);
			startActivity(it2);
			finish();
			break;
		}
	}
}
