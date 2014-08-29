package com.ihone.easylauncher;

import com.ihome.adapter.FragementAdapter;
import com.ihome.easylauncher.ui.SendMessageActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends FragmentActivity {

	public static final int PAGE_ONE = 0;//ҳ��һ
	public static final int PAGE_TWO = 1;//ҳ���
	
	private ViewPager viewPager;
	private Button btnPhone,btnMessage;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=MainActivity.this;
		viewPager=(ViewPager) findViewById(R.id.viewPager);
		btnPhone=(Button) findViewById(R.id.btnphone);
		btnMessage=(Button) findViewById(R.id.btnmessage);
		btnPhone.setOnClickListener(onclicklistener);
		btnMessage.setOnClickListener(onclicklistener);
		
		FragementAdapter adapter = new FragementAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(adapter);
	}
	
	private OnClickListener onclicklistener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnphone:
				//��绰����¼�
				Intent intent= new Intent(Intent.ACTION_DIAL);   
				//intent.setClassName("com.android.contacts","com.android.contacts.DialtactsActivity");
				startActivity(intent); 
				break;
            case R.id.btnmessage:
				//�����ŵ���¼�
            	/*Intent mintent = new Intent(Intent.ACTION_MAIN);
            	mintent.addCategory(Intent.CATEGORY_DEFAULT);
            	mintent.setType("vnd.android-dir/mms-sms");
            	startActivity(mintent);*/
            	Intent msg= new Intent();   
            	msg.setClass(context, SendMessageActivity.class);
				startActivity(msg); 
				break;

			default:
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return false;
	}

	@Override
	public void onBackPressed() {
		//���ذ�ť������Ӧ
	}
	
	

}
