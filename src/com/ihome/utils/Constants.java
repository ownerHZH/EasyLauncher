package com.ihome.utils;

import com.iflytek.cloud.SpeechConstant;

public class Constants {

	//Ѷ��KEY
    public static final String API_KEY = SpeechConstant.APPID+"=53fbf6c8";  
	/**
	 * ���еĶ���
	 */
	public static final String SMS_URI_ALL = "content://sms/";
	/**
	 * �ռ������
	 */
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	/**
	 * �ѷ��Ͷ���
	 */
	public static final String SMS_URI_SEND = "content://sms/sent";
	/**
	 * �ݸ������
	 */
	public static final String SMS_URI_DRAFT = "content://sms/draft";
	
	public static final String SMS_URI_CONVERSATION_COUNT="content://sms/conversations";

}
