package com.ihome.entity;

import android.graphics.Bitmap;

public class Contact {
   public String name;   //姓名
   public String number1; //电话一
   public String number2;//电话二
   public String number3;//电话三
   public String number4;//电话四
   public Bitmap headBitMap;//头像
   @Override
public String toString() {
	return "Contact [name=" + name + ", number1=" + number1 + ", number2="
			+ number2 + ", number3=" + number3 + ", number4=" + number4
			+ ", headBitMap=" + headBitMap + ", groupId=" + groupId + "]";
}
public Bitmap getHeadBitMap() {
	return headBitMap;
}
public void setHeadBitMap(Bitmap headBitMap) {
	this.headBitMap = headBitMap;
}
public String groupId;//所属组ID
   
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getNumber1() {
	return number1;
}
public void setNumber1(String number1) {
	this.number1 = number1;
}
public String getNumber2() {
	return number2;
}
public void setNumber2(String number2) {
	this.number2 = number2;
}
public String getNumber3() {
	return number3;
}
public void setNumber3(String number3) {
	this.number3 = number3;
}
public String getNumber4() {
	return number4;
}
public void setNumber4(String number4) {
	this.number4 = number4;
}

public String getGroupId() {
	return groupId;
}
public void setGroupId(String groupId) {
	this.groupId = groupId;
}
}
