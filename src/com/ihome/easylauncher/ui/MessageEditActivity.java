package com.ihome.easylauncher.ui;

import java.util.ArrayList;
import java.util.List;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.ihome.utils.Config;
import com.ihome.utils.Constants;
import com.ihone.easylauncher.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	private BaiduASRDigitalDialog mDialog = null;
	private DialogRecognitionListener mRecognitionListener;
	private Context context;
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
		btnSend=(Button) findViewById(R.id.btnSend);
		
		btnSearch.setOnClickListener(onclicklistener);
		btnVoice.setOnClickListener(onclicklistener);
		btnSend.setOnClickListener(onclicklistener);
		
		mRecognitionListener = new DialogRecognitionListener() {

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                	messageContent.setText(rs.get(0));
                }

            }
        };
        //注册短信发送接收器
        registerReceiver(sms_receiver, new IntentFilter("SENT_SMS_ACTION"));
	}
	
	 @Override
	    protected void onDestroy() {
	        if (mDialog != null) {
	            mDialog.dismiss();
	        }
	        unregisterReceiver(sms_receiver);
	        super.onDestroy();
	    }
	
	private OnClickListener onclicklistener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnSearch:
				//搜索联系人按钮
				break;
		    case R.id.btnVoice:
				//点击说话
		    	messageContent.setText(null);
//              if (mDialog == null || mCurrentTheme != Config.DIALOG_THEME) {
                  //mCurrentTheme = Config.DIALOG_THEME;
                  if (mDialog != null) {
                      mDialog.dismiss();
                  }
                  Bundle params = new Bundle();
                  params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
                  params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
                  params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
                  mDialog = new BaiduASRDigitalDialog(context, params);
                  mDialog.setDialogRecognitionListener(mRecognitionListener);
//              }
                  mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
                  mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                      Config.getCurrentLanguage());
                  Log.e("DEBUG", "Config.PLAY_START_SOUND = "+Config.PLAY_START_SOUND);
                  mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
                  mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
                  mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
                  mDialog.show();
				break;
		    case R.id.btnSend:
				//发送
		    	String conent=messageContent.getText().toString();
		    	String number=phoneNumber.getText().toString();
		    	if(number!=null&&number!="")
		    	{
		    		if(conent!=null&&conent!="")
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

}
