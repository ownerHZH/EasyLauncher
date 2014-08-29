package com.ihome.entity;

import java.io.InputStream;

import android.graphics.Bitmap;

public class MessageInfo {
     String name;
     Bitmap contactPhoto;
     String smsContent;
     String smsDate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Bitmap getContactPhoto() {
		return contactPhoto;
	}
	public void setContactPhoto(Bitmap bitmap) {
		this.contactPhoto = bitmap;
	}
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	public String getSmsDate() {
		return smsDate;
	}
	public void setSmsDate(String smsDate) {
		this.smsDate = smsDate;
	}
}
