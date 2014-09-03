package com.ihome.entity;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsInfo {
    /**
     * ��������
     */
    private String smsbody;
    /**
     * ���Ͷ��ŵĵ绰����
     */
    private String phoneNumber;
    /**
     * �Ի�����ţ���100����ͬһ���ֻ��Ż����Ķ��ţ����������ͬ�� 
     */
    private String thread_id;
    /**
     * ���Ͷ��ŵ����ں�ʱ��
     */
    private String date;
    /**
     * ���Ͷ����˵�����
     */
    private String name;
    /**
     * ��������1�ǽ��յ��ģ�2���ѷ���
     */
    private String type;
    
    /**
     * �洢һ���Ự�й��ж���������
     */
    int message_count;
    /**
     * ����Ự���Ѿ���ȡ�Ķ�������
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
