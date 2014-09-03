package com.ihome.entity;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsInfo {
    /**
     * 短信内容
     */
    private String smsbody;
    /**
     * 发送短信的电话号码
     */
    private String phoneNumber;
    /**
     * 对话的序号，如100，与同一个手机号互发的短信，其序号是相同的 
     */
    private String thread_id;
    /**
     * 发送短信的日期和时间
     */
    private String date;
    /**
     * 发送短信人的姓名
     */
    private String name;
    /**
     * 短信类型1是接收到的，2是已发出
     */
    private String type;
    
    /**
     * 存储一个会话中共有多少条短信
     */
    int message_count;
    /**
     * 这个会话中已经读取的短信条数
     */
    int readcount;
    
    public int getReadcount() {
		return readcount;
	}
	public void setReadcount(int readcount) {
		this.readcount = readcount;
	}
	public int getMessage_count() {
		return message_count;
	}
	public void setMessage_count(int message_count) {
		this.message_count = message_count;
	}
	public String getSmsbody() {
        return smsbody;
    }
    public void setSmsbody(String smsbody) {
        this.smsbody = smsbody;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	public String getDate() {
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String d=sdf.format(new Date(Long.parseLong(date)));
        return d;
    }
    public void setDate(String date) {
        this.date = date;
    } 
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
	public String getThread_id() {
		return thread_id;
	}
	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}
	@Override
	public String toString() {
		return "SmsInfo [smsbody=" + smsbody + ", phoneNumber=" + phoneNumber
				+ ", thread_id=" + thread_id + ", date=" + date + ", name="
				+ name + ", type=" + type + ", message_count=" + message_count
				+ ", readcount=" + readcount + "]";
	}

}
