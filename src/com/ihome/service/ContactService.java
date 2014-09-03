package com.ihome.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Contacts.GroupMembership;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import com.ihome.entity.Contact;
import com.ihome.easylauncher.R;

@SuppressWarnings("deprecation")
public class ContactService {
	 private static final String[] PHONES_PROJECTION = new String[] {  
	       Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID,Phone.RAW_CONTACT_ID};
	 /**联系人显示名称**/  
	 private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
	   
	 /**电话号码**/  
	 private static final int PHONES_NUMBER_INDEX = 1;  
	   
	 /**头像ID**/  
	 private static final int PHONES_PHOTO_ID_INDEX = 2;  
	  
	 /**联系人的ID**/  
	 private static final int PHONES_CONTACT_ID_INDEX = 3; 
	 private static final int PHONES__RAW_CONTACT_ID_INDEX = 4;
	private Context context;
	public ContactService(Context context)
	{
		this.context=context;
	}
	//获取通讯录联系人
	public List<Contact> getPhoneContacts() {			

		List<Contact> contacts = new ArrayList<Contact>();

		ContentResolver resolver = context.getContentResolver();
		// 获取手机联系人
		//“content:// com.android.contacts/data/phones”。
		//这个url 对应着contacts表 和 raw_contacts表 以及 data表
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				Long rawcontactid = phoneCursor.getLong(PHONES__RAW_CONTACT_ID_INDEX);//data中的联系人ID
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				
				MoreDetailInfo(resolver, rawcontactid);//更多详细信息

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.b_32);
				}
				Contact contact = new Contact();
				contact.setName(contactName);
				contact.setNumber1(phoneNumber);
				contact.setHeadBitMap(contactPhoto);
				contacts.add(contact);
			}
			phoneCursor.close();
		}
		return contacts;
	}
	private void MoreDetailInfo(ContentResolver resolver, Long rawcontactid) {
		/////////////////////////////////////////
		Cursor phoneCur = resolver.query(Data.CONTENT_URI, null,
				  ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + "=?", new
				  String[] { rawcontactid+"" }, null);
		if(phoneCur!=null)
		{
			int mimetypeindex=phoneCur.getColumnIndex(Data.MIMETYPE);
			int data1index=phoneCur.getColumnIndex(Data.DATA1);
			int invgindex=phoneCur.getColumnIndex(Data.IN_VISIBLE_GROUP);
			while(phoneCur.moveToNext())
			{
				String type=phoneCur.getString(mimetypeindex);
				String vc=phoneCur.getString(data1index);
				if(vc!=null&&!vc.equals(""))
				{
					if(type.equals(StructuredName.CONTENT_ITEM_TYPE))
					{
						//姓名
						//Log.e("姓名--",vc);
					}else if(type.equals(Phone.CONTENT_ITEM_TYPE))
					{
						//第二个手机号码
						//Log.e("手机号码--",vc);
					}else if(type.equals(Email.CONTENT_ITEM_TYPE))
					{
						//email
						//Log.e("email--",vc);
					}else if(type.equals(Organization.CONTENT_ITEM_TYPE))
					{
						//所在组织
						//Log.e("所在组织--",vc);
					}else if(type.equals(Im.CONTENT_ITEM_TYPE))
					{
						//其他联系账号
						//Log.e("其他联系账号--",vc);
					}else if(type.equals(Nickname.CONTENT_ITEM_TYPE))
					{
						//别名
						//Log.e("别名--",vc);
					}else if(type.equals(Note.CONTENT_ITEM_TYPE))
					{
						//备注
						//Log.e("备注--",vc);
					}else if(type.equals(StructuredPostal.CONTENT_ITEM_TYPE))
					{
						//邮编地址
						//Log.e("地址--",vc);
					}else if(type.equals(GroupMembership.CONTENT_ITEM_TYPE))
					{
						//所在组
						//Log.e("所在组--",vc);
					}else if(type.equals(Website.CONTENT_ITEM_TYPE))
					{
						//个人网址
						//Log.e("个人网址--",vc);
					}else if(type.equals(Event.CONTENT_ITEM_TYPE))
					{
						//contact_event
						//Log.e("contact_event--",vc);
					}else if(type.equals(Relation.CONTENT_ITEM_TYPE))
					{
						//关系
						//Log.e("关系--",vc);
					}
				}
									
			}
		}
		phoneCur.close();
		/////////////////////////////////////////
	}
}
