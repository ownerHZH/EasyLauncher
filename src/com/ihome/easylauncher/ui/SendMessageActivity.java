package com.ihome.easylauncher.ui;

import com.ihone.easylauncher.R;
import com.ihone.easylauncher.R.layout;
import com.ihone.easylauncher.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class SendMessageActivity extends Activity {

	Button newMessage;//新建短信按钮
	ListView lvMessage;//短信显示列表
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_send_message);
		context=SendMessageActivity.this;
		newMessage=(Button) findViewById(R.id.btnNewmessage);
		lvMessage=(ListView) findViewById(R.id.lvMessage);
		
		newMessage.setOnClickListener(onclicklistener);
	}
	
	private OnClickListener onclicklistener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnNewmessage:
				Intent edit=new Intent();
				edit.setClass(context, MessageEditActivity.class);
				startActivity(edit);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_send_message, menu);
		return false;
	}

}
