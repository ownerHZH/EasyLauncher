package com.ihome.utils;

import com.iflytek.cloud.SpeechConstant;

public class Constants {

	//讯飞KEY
    public static final String API_KEY = SpeechConstant.APPID+"=53fbf6c8";  
	/**
	 * 所有的短信
	 */
	public static final String SMS_URI_ALL = "content://sms/";
	/**
	 * 收件箱短信
	 */
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	/**
	 * 已发送短信
	 */
	public static final String SMS_URI_SEND = "content://sms/sent";
	/**
	 * 草稿箱短信
	 */
	public static final String SMS_URI_DRAFT = "content://sms/draft";
	
	public static final String SMS_URI_CONVERSATION_COUNT="content://sms/conversations";

}
