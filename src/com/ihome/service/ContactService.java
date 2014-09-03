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
	 /**��ϵ����ʾ����**/  
	 private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
	   
	 /**�绰����**/  
	 private static final int PHONES_NUMBER_INDEX = 1;  
	   
	 /**ͷ��ID**/  
	 private static final int PHONES_PHOTO_ID_INDEX = 2;  
	  
	 /**��ϵ�˵�ID**/  
	 private static final int PHONES_CONTACT_ID_INDEX = 3; 
	 private static final int PHONES__RAW_CONTACT_ID_INDEX = 4;
	private Context context;
	public ContactService(Context context)
	{
		this.context=context;
	}
	//��ȡͨѶ¼��ϵ��
	public List<Contact> getPhoneContacts() {			

		List<Contact> contacts = new ArrayList<Contact>();

		ContentResolver resolver = context.getContentResolver();
		// ��ȡ�ֻ���ϵ��
		//��content:// com.android.contacts/data/phones����
		//���url ��Ӧ��contacts�� �� raw_contacts�� �Լ� data��
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// �õ��ֻ�����
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// �õ���ϵ������
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// �õ���ϵ��ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				Long rawcontactid = phoneCursor.getLong(PHONES__RAW_CONTACT_ID_INDEX);//data�е���ϵ��ID
				// �õ���ϵ��ͷ��ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				// �õ���ϵ��ͷ��Bitamp
				Bitmap contactPhoto = null;
				
				MoreDetailInfo(resolver, rawcontactid);//������ϸ��Ϣ

				// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
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
						//����
						//Log.e("����--",vc);
					}else if(type.equals(Phone.CONTENT_ITEM_TYPE))
					{
						//�ڶ����ֻ�����
						//Log.e("�ֻ�����--",vc);
					}else if(type.equals(Email.CONTENT_ITEM_TYPE))
					{
						//email
						//Log.e("email--",vc);
					}else if(type.equals(Organization.CONTENT_ITEM_TYPE))
					{
						//������֯
						//Log.e("������֯--",vc);
					}else if(type.equals(Im.CONTENT_ITEM_TYPE))
					{
						//������ϵ�˺�
						//Log.e("������ϵ�˺�--",vc);
					}else if(type.equals(Nickname.CONTENT_ITEM_TYPE))
					{
						//����
						//Log.e("����--",vc);
					}else if(type.equals(Note.CONTENT_ITEM_TYPE))
					{
						//��ע
						//Log.e("��ע--",vc);
					}else if(type.equals(StructuredPostal.CONTENT_ITEM_TYPE))
					{
						//�ʱ��ַ
						//Log.e("��ַ--",vc);
					}else if(type.equals(GroupMembership.CONTENT_ITEM_TYPE))
					{
						//������
						//Log.e("������--",vc);
					}else if(type.equals(Website.CONTENT_ITEM_TYPE))
					{
						//������ַ
						//Log.e("������ַ--",vc);
					}else if(type.equals(Event.CONTENT_ITEM_TYPE))
					{
						//contact_event
						//Log.e("contact_event--",vc);
					}else if(type.equals(Relation.CONTENT_ITEM_TYPE))
					{
						//��ϵ
						//Log.e("��ϵ--",vc);
					}
				}
									
			}
		}
		phoneCur.close();
		/////////////////////////////////////////
	}
}
