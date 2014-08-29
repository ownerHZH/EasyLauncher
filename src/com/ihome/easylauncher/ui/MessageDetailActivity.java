package com.ihome.easylauncher.ui;

import java.util.ArrayList;
import java.util.Date;
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
import com.ihome.adapter.SmsListAdapter;
import com.ihome.entity.SmsInfo;
import com.ihome.service.SmsContentService;
import com.ihome.utils.Constants;
import com.ihome.utils.JsonParser;
import com.ihone.easylauncher.EasyLauncherApplication;
import com.ihone.easylauncher.R;
import com.ihone.easylauncher.R.layout;
import com.ihone.easylauncher.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageDetailActivity extends Activity {

	protected static final int HANDLE_SCROLL_TO_BOTTOM = 0x111;
	private LinearLayout allMessageContent;
	private Button btnVoice,btnResponce;
	private EditText etResponceText;
	private ScrollView svContent;
	
	List<SmsInfo> list=new ArrayList<SmsInfo>();  
	SmsContentService smsService;
	Context context;
	LayoutInflater inflater;
	int dwidth=400,dheight=400;
	private SpeechRecognizer mIat;
	// 语音听写自带UI
	private RecognizerDialog iatDialog;
	private String NUMBER="";//对方的电话话吗
	private String THREAD_ID="";
	private TextView sendTextContainer;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);
		context=MessageDetailActivity.this;
		inflater=LayoutInflater.from(context);
		smsService=new SmsContentService(context);
		
		allMessageContent=(LinearLayout) findViewById(R.id.linAllMessage);
		btnVoice=(Button) findViewById(R.id.btnVoice);
		btnResponce=(Button) findViewById(R.id.btnReButton);
		etResponceText=(EditText) findViewById(R.id.etReText);
		svContent=(ScrollView) findViewById(R.id.svContent);
		
		btnVoice.setOnClickListener(btnonclicklistener);
		btnResponce.setOnClickListener(btnonclicklistener);
		
		DisplayMetrics dm = new DisplayMetrics();getWindowManager().getDefaultDisplay().getMetrics(dm);
		dwidth = dm.widthPixels;    //屏幕宽度
		dheight = dm.heightPixels ;//屏幕高度		
		
        mIat=SpeechRecognizer.createRecognizer(this,  new InitListener() {
			
			@Override
			public void onInit(int code) {
				Log.e("mIat code==", code+"");
			}
		});
		setParam();
		iatDialog=new RecognizerDialog(context, new InitListener() {
			
			@Override
			public void onInit(int code) {
				Log.e("code==", code+"");
			}
		});
		//获取数据
		Intent i=getIntent();
		if(i!=null)
		{
			THREAD_ID=i.getStringExtra("thread_id");
			NUMBER=i.getStringExtra("phoneNumber");
			if(THREAD_ID!=null)
			{
				List<SmsInfo> newListData=smsService.getSmsInfo(THREAD_ID);
				if(newListData!=null&&newListData.size()>0)
				{
					list.addAll(newListData);
				}
			}
		}

		loadAllMessage();//加载所有数据
		//注册短信发送接收器
		IntentFilter filter=new IntentFilter("SENT_SMS_ACTION");//发送成功与否
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");//接收发来的短信
        registerReceiver(sms_receiver,filter);
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
		
	private void loadAllMessage() {
		//把数据显示在界面中		
		for(SmsInfo sms:list)
		{
			if(sms.getType().equals("2"))//自己发送的消息
			{
				TextView rtv = createRightTextView(sms.getSmsbody());
				allMessageContent.addView(rtv);				
			}else
			{				
				TextView ltv = createLeftTextView(sms.getSmsbody());
				allMessageContent.addView(ltv);	
			}			
		}
		//ScrollView滚动到底部	
		allMessageContent.post(new Runnable() {
			
			@Override
			public void run() {
				svContent.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});			
	}

	//对方发送的TextView组件内容
	private TextView createLeftTextView(String smsBody) {
		TextView ltv=new TextView(context);
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,0.6f);
		lp.gravity=Gravity.LEFT;
		lp.topMargin=5;
		lp.bottomMargin=5;
		lp.leftMargin=5;
		ltv.setLayoutParams(lp);                                           
		ltv.setPadding(5, 5, 5, 5);
		ltv.setMaxWidth((int) (0.8*dwidth));
		ltv.setText(smsBody);
		ltv.setBackgroundResource(R.drawable.message_left);
		return ltv;
	}

	//自己发送的TextView组件内容
	private TextView createRightTextView(String smsBody) {
		TextView rtv=new TextView(context);
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,0.6f);
		lp.gravity=Gravity.RIGHT;
		lp.topMargin=5;
		lp.bottomMargin=5;
		lp.rightMargin=5;
		rtv.setLayoutParams(lp);
		rtv.setGravity(Gravity.RIGHT);
		rtv.setPadding(5, 5, 5, 5);
		rtv.setMaxWidth((int) (0.8*dwidth));
		rtv.setText(smsBody);
		rtv.setBackgroundResource(R.drawable.message_right);
		return rtv;
	}
	
	private OnClickListener btnonclicklistener=new OnClickListener() {
		
		@SuppressWarnings("unused")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnVoice:
				//语音转文字按钮事件
				//点击说话
				if(true)
				{
					etResponceText.setText(null);
					// 显示听写对话框
					iatDialog.setListener(recognizerDialogListener);
					iatDialog.show();	
				} else {//自定义对话框的调用
					// 不显示听写对话框
					int ret = mIat.startListening(mRecoListener);
					if(ret != ErrorCode.SUCCESS){
					}else {
					}
				}
				
				break;
            case R.id.btnReButton:
				//回复短信事件
            	String conent=etResponceText.getText().toString();
		    	if(NUMBER!=null&&!NUMBER.isEmpty())
		    	{
		    		if(conent!=null&&!conent.isEmpty())
		    		{
		    			sendMessage(conent,NUMBER);
		    		}else
		    		{
		    			Toast.makeText(context, "发送的内容不能为空", Toast.LENGTH_SHORT).show();
		    		}
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
        sendTextContainer=createRightTextView(conent+"---发送中...");
        allMessageContent.addView(sendTextContainer);//把内容添加上去
        //滚动条滚动到底部
        allMessageContent.post(new Runnable() {
			
			@Override
			public void run() {
				svContent.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}
	
	//短信发送接收器
    private BroadcastReceiver sms_receiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context _context, Intent _intent) {
			if(_intent.getAction().equals("SENT_SMS_ACTION"))//发送短信后信息回馈
			{
				switch (getResultCode()) {  
                case Activity.RESULT_OK:  
                	sendTextContainer.setText(sendTextContainer.getText().toString().split("---")[0]);
                	etResponceText.setText(null);
	                //发送成功，把短信存入数据库
					ContentValues c=new ContentValues();
					c.put("thread_id", THREAD_ID);
					c.put("date", new Date().getTime());
					c.put("body", sendTextContainer.getText().toString().split("---")[0]);
					getContentResolver().insert(Uri.parse(Constants.SMS_URI_SEND), c);
					//存入数据库操作 完
			          break;  
		        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:   
		         case SmsManager.RESULT_ERROR_RADIO_OFF:  
		         case SmsManager.RESULT_ERROR_NULL_PDU:  
		        	 sendTextContainer.setText(sendTextContainer.getText()+"---发送失败");
		              break;
		         case SmsManager.RESULT_ERROR_NO_SERVICE:
		        	 sendTextContainer.setText(sendTextContainer.getText()+"---暂无网络，发送失败");
		              break;
		       }
			}else if(_intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
			{
			   //收到发来的短信处理
			   SmsMessage[] messages = getMessagesFromIntent(_intent);
			   StringBuffer sbuf=new StringBuffer();
	           for (SmsMessage message : messages)
	           {
	        	   sbuf.append(message.getDisplayMessageBody());
	              /*Log.e("TAG==", message.getOriginatingAddress() + " : " +
	                  message.getDisplayOriginatingAddress() + " : " +
	                  message.getDisplayMessageBody() + " : " +
	                  message.getTimestampMillis());*/
	           }
	           TextView yxv=createLeftTextView(sbuf.toString());
	           allMessageContent.addView(yxv);//把内容添加上去
	           //滚动条滚动到底部
	           allMessageContent.post(new Runnable() {
	   			
	   			@Override
	   			public void run() {
	   				svContent.fullScroll(ScrollView.FOCUS_DOWN);
	   			}
	   		});
			}
			 
		}
	};
	
	//解析获取的短信内容
	 public final SmsMessage[] getMessagesFromIntent(Intent intent)
	    {
	        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
	        byte[][] pduObjs = new byte[messages.length][];	 
	        for (int i = 0; i < messages.length; i++)

	        {
	            pduObjs[i] = (byte[]) messages[i];
	        }

	        byte[][] pdus = new byte[pduObjs.length][];
	        int pduCount = pdus.length;
	        SmsMessage[] msgs = new SmsMessage[pduCount];
	        for (int i = 0; i < pduCount; i++)
	        {
	            pdus[i] = pduObjs[i];
	            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
	        }
	        return msgs;
	    }
	
	//讯飞语音监听器
	 private RecognizerListener mRecoListener=new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
		}

		@Override
		public void onEndOfSpeech() {
		}

		@Override
		public void onError(SpeechError error) {
			error.getPlainDescription(true);
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, String arg3) {
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			
			String text = JsonParser.parseIatResult(results.getResultString());
			etResponceText.append(text);
			etResponceText.setSelection(etResponceText.length());
			if(isLast) {
			}
		}

		@Override
		public void onVolumeChanged(int arg0) {
			
		}
		
	};
	
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			etResponceText.append(text);
			etResponceText.setSelection(etResponceText.length());
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
		}

	};	
			
	@Override
    protected void onDestroy() {
		unregisterReceiver(sms_receiver);
		if(iatDialog!=null)
		{
			if(iatDialog.isShowing())
			{
				iatDialog.dismiss();							
			}			
			iatDialog.cancel();
			iatDialog.destroy();
		}
		System.gc();
       super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_message_detail, menu);
		return false;
	}

}
