package com.ihone.easylauncher;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.ihome.utils.Constants;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class EasyLauncherApplication extends Application {
	private static EasyLauncherApplication application;
	// 语音听写没有自带UI
	//private static SpeechRecognizer mIat;
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		SpeechUtility.createUtility(this, Constants.API_KEY);
	}

	//全局的上下文
	public static synchronized EasyLauncherApplication getInstance() {
		if(application!=null)
		    return application;
		else
			return new EasyLauncherApplication();
	}

	//全局的SharedPreferences对象
	public static synchronized SharedPreferences getDefaultSharedPreferences(){
		return application.getSharedPreferences("easylauncher", Context.MODE_PRIVATE);
	}
	
}
