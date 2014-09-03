package com.ihome.easylauncher.ui;

import java.util.ArrayList;
import java.util.List;

import com.ihome.adapter.SmsListAdapter;
import com.ihome.entity.SmsInfo;
import com.ihome.service.SmsContentService;
import com.ihome.utils.Constants;
import com.ihome.easylauncher.R;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SendMessageActivity extends Activity {

	Button newMessage;//新建短信按钮
	ListView lvMessage;//短信显示列表
	Context context;
	
	List<SmsInfo> list=new ArrayList<SmsInfo>();  
	SmsListAdapter adapter;
	SmsContentService smsService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_send_message);
		context=SendMessageActivity.this;
		newMessage=(Button) findViewById(R.id.btnNewmessage);
		lvMessage=(ListView) findViewById(R.id.lvMessage);
		
		newMessage.setOnClickListener(onclicklistener);
		smsService=new SmsContentService(context);
		list.addAll(smsService.getSmsInfoGroupByThreadId());
		adapter=new SmsListAdapter(context, list);
		lvMessage.setAdapter(adapter);
		
		//smsService.getSmsInfoGroupByThreadId();//
		lvMessage.setOnItemClickListener(onitemclicklistener);
		//smsService.getThreadsInfo();//获取会话数量 讨巧做法
	}
	
	private OnItemClickListener onitemclicklistener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			/*SmsInfo smsinfo=list.get(position);
			List<SmsInfo> newListData=smsService.getSmsInfo(smsinfo.getThread_id());
			list.clear();
			list.addAll(newListData);
			adapter.notifyDataSetChanged();	
			Log.e("fdf", "fsfs");
			lvMessage.setOnItemClickListener(null);*/
			
			SmsInfo smsinfo=list.get(position);
			Intent showMessageIntent=new Intent();
			showMessageIntent.putExtra("thread_id", smsinfo.getThread_id());
			showMessageIntent.putExtra("phoneNumber", smsinfo.getPhoneNumber());
			showMessageIntent.setClass(context, MessageDetailActivity.class);
			startActivity(showMessageIntent);
		}
	};

	
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
