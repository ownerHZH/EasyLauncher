package com.ihome.easylauncher.ui;

import java.util.List;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.ihome.utils.JsonParser;
import com.ihome.easylauncher.R;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MessageEditActivity extends Activity {

	private EditText phoneNumber,messageContent;
	private Button btnSearch,btnVoice,btnSend;
	private Context context;
	private final static int ACTION_PICK_CONTACT=0x101;
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog iatDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_edit);
		context=MessageEditActivity.this;
		phoneNumber=(EditText) findViewById(R.id.etNumber);
		messageContent=(EditText) findViewById(R.id.etContent);
		btnSearch=(Button) findViewById(R.id.btnSearch);
		btnVoice=(Button) findViewById(R.id.btnVoice);
		btnSend=(Button) findViewById(R.id.btnSendButton);
		
		btnSearch.setOnClickListener(onclicklistener);
		btnVoice.setOnClickListener(onclicklistener);
		btnSend.setOnClickListener(onclicklistener);
		
		Intent fromDialogIntent=getIntent();
		if(fromDialogIntent!=null)
		{
			//从主界面的弹出框跳转而来
			String num=fromDialogIntent.getStringExtra("number");
			if(num!=null)
			{
				phoneNumber.setText(num);
			}			
		}
		
		mIat=SpeechRecognizer.createRecognizer(this,  new InitListener() {
			
			@Override
			public void onInit(int code) {
				Log.e("mIat code==", code+"");
			}
		});
		setParam();
		
		iatDialog = new RecognizerDialog(context, new InitListener() {
			
			@Override
			public void onInit(int code) {
				Log.e("code==", code+"");
			}
		});
		
        //注册短信发送接收器
        registerReceiver(sms_receiver, new IntentFilter("SENT_SMS_ACTION"));
	}
	
	private  void setParam() {
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS,"4000");
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS,"1000");
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT,"1");
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, "/sdcard/iflytek/wavaudio.pcm");
	}
	
	 @Override
     protected void onDestroy() {
        unregisterReceiver(sms_receiver);
        // 退出时释放连接
        if(mIat!=null)
        {
        	if(mIat.isListening())
        	{
        		mIat.stopListening();
        	}
    		mIat.cancel();
    		mIat.destroy();
        }
        
		if(iatDialog!=null)
		{
			//Log.e("MessageEditActivity onDestroy====", "onDestroy");
			//iatDialog.setListener(null);
			if(iatDialog.isShowing())
			{
				iatDialog.dismiss();
			}
			iatDialog.cancel();
			iatDialog.destroy();
		}
        super.onDestroy();
     }
	  
	  /**
		 * 听写UI监听器
		 */
		private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
			public void onResult(RecognizerResult results, boolean isLast) {
				String text = JsonParser.parseIatResult(results.getResultString());
				messageContent.append(text);
				messageContent.setSelection(messageContent.length());
			}

			/**
			 * 识别回调错误.
			 */
			public void onError(SpeechError error) {
				//showTip(error.getPlainDescription(true));
			}

		};
	 //讯飞语音监听器
	 private RecognizerListener mRecoListener=new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onEndOfSpeech() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(SpeechError error) {
			error.getPlainDescription(true);
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			
			String text = JsonParser.parseIatResult(results.getResultString());
			messageContent.append(text);
			messageContent.setSelection(messageContent.length());
			if(isLast) {
				//messageContent.setText(text);
			}
		}

		@Override
		public void onVolumeChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	 
	private OnClickListener onclicklistener=new OnClickListener() {				
		@SuppressWarnings("unused")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnSearch:
				//搜索联系人按钮
				Intent contactintent = new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(contactintent, ACTION_PICK_CONTACT);
				break;
		    case R.id.btnVoice:
				//点击说话
		    	messageContent.setText(null);
		    	if (true) {
					// 显示听写对话框
		    		//iatDialog.setListener(null);
					iatDialog.setListener(recognizerDialogListener);
					iatDialog.show();
					//showTip(getString(R.string.text_begin));
				} else {//自定义对话框的调用
					// 不显示听写对话框
					int ret = mIat.startListening(mRecoListener);
					if(ret != ErrorCode.SUCCESS){
						//showTip("听写失败,错误码：" + ret);
					}else {
						//showTip(getString(R.string.text_begin));
					}
				}
				break;
		    case R.id.btnSendButton:
				//发送
		    	String conent=messageContent.getText().toString();
		    	String number=phoneNumber.getText().toString().trim();
		    	if(number!=null&&!number.isEmpty())
		    	{
		    		if(conent!=null&&!conent.isEmpty())
		    		{
		    			sendMessage(conent,number);
		    		}else
		    		{
		    			Toast.makeText(context, "发送的内容不能为空", Toast.LENGTH_SHORT).show();
		    		}
		    	}else
		    	{
		    		Toast.makeText(context, "请输入发送的号码", Toast.LENGTH_SHORT).show();
		    	}
		    	
				break;
			}
		}		
	};
	
	/**
	 * 发送短信
	 * @param conent 内容
	 * @param number 号码
	 */
	private void sendMessage(String conent, String number) {
		SmsManager smsManager = SmsManager.getDefault();
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, sentIntent, 0);
		 //短信字数大于70，自动分条
        List<String> ms = smsManager.divideMessage(conent);
        
        for(String str : ms )
        {
            //短信发送
            smsManager.sendTextMessage(number, null, str, pendingIntent, null);
        }
	}
	
	//短信发送接收器
    private BroadcastReceiver sms_receiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context _context, Intent _intent) {
			 switch (getResultCode()) {  
                case Activity.RESULT_OK:  
	                  Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show(); 
	                  messageContent.setText("");
			          break;  
		        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:   
		         case SmsManager.RESULT_ERROR_RADIO_OFF:  
		         case SmsManager.RESULT_ERROR_NULL_PDU:  
		        	 Toast.makeText(context, "短信发送失败", Toast.LENGTH_SHORT).show();
		              break;
		         case SmsManager.RESULT_ERROR_NO_SERVICE:
		        	 Toast.makeText(context, "无网络", Toast.LENGTH_SHORT).show();
		              break;
		       }
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_message_edit, menu);
		return false;
	}
	
	@Override
	protected void onResume() {
		SpeechUtility.getUtility().checkServiceInstalled();
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==ACTION_PICK_CONTACT)
		{
			//选择联系人的回调函数
			if(resultCode== Activity.RESULT_OK)
			{
				Uri contactData = data.getData(); 				 
		        Cursor c = getContentResolver().query(contactData, null, null, null, null); 		 
		        c.moveToFirst(); 		 
		        String phoneNum=this.getContactPhone(c); 
		        phoneNumber.setText(phoneNum); 
		        if(!c.isClosed())
		        {
		        	c.close();
		        }
			}
		}
	}
	
	//获取联系人电话 
	private String getContactPhone(Cursor cursor) 
	{  
	    int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);   
	    int phoneNum = cursor.getInt(phoneColumn);  
	    String phoneResult=""; 
	    if (phoneNum > 0) 
	    { 
	        // 获得联系人的ID号 
	        int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID); 
	        String contactId = cursor.getString(idColumn); 
            // 获得联系人的电话号码的cursor; 
            Cursor phones = getContentResolver().query( 
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
            null, 
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId,  
            null, null); 
            if (phones.moveToFirst()) 
            { 
                // 遍历所有的电话号码 
                for (;!phones.isAfterLast();phones.moveToNext()) 
                {                                             
                    int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER); 
                    int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE); 
                    int phone_type = phones.getInt(typeindex); 
                    String phoneNumber = phones.getString(index); 
                    switch(phone_type) 
                    { 
                        case 2: 
                            phoneResult=phoneNumber; 
                        break; 
                    } 
                } 
                if (!phones.isClosed()) 
                { 
                       phones.close(); 
                } 
            } 
	    } 
	    return phoneResult; 
	}

}
