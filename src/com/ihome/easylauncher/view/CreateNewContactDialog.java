package com.ihome.easylauncher.view;

import com.ihome.easylauncher.EasyLauncherApplication;
import com.ihome.easylauncher.basedao.ITellCreateContactComplete;
import com.ihome.easylauncher.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 新建联系人的弹出框
 * @author owner
 * @date 2014-08-29
 */
public class CreateNewContactDialog extends Dialog {

	private Context context;
	Button btnConfirme;
	private BtnClickListener btnlistener;
	private int id;
	private EditText etName,etPhoneNumber;
	private ITellCreateContactComplete iTellCreateContactComplete;
	
	public void setiTellCreateContactComplete(
			ITellCreateContactComplete iTellCreateContactComplete) {
		this.iTellCreateContactComplete = iTellCreateContactComplete;
	}
	public CreateNewContactDialog(Context context, int theme, int id) {		
		super(context, theme);
		this.context=context;
		this.id=id;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_new_create_contact_layout);
		
		LayoutParams optionsparams =getWindow().getAttributes();
		optionsparams.height = android.app.ActionBar.LayoutParams.MATCH_PARENT;
		optionsparams.width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
		getWindow().setAttributes(optionsparams);	
		
		btnlistener=new BtnClickListener();
		etName=(EditText) findViewById(R.id.etName);
		etPhoneNumber=(EditText) findViewById(R.id.etPhoneNumber);
		btnConfirme=(Button) findViewById(R.id.btnConfirme);
		btnConfirme.setOnClickListener(btnlistener);
	}
	
	public void setNameNumber(String name,String number)
	{
		etName.setText(name);
		etPhoneNumber.setText(number);
	}
	
	/**
	 * 按钮监听器
	 * @author owner
	 * @date 2014-08-29
	 */
	 private class BtnClickListener implements android.view.View.OnClickListener {

         @Override
         public void onClick(View v) {
              switch (v.getId()) {
	            case R.id.btnConfirme:
				    confirmeMethod();
					break;
			}
         }

         /**
          * 确认点击事件
          */
		private void confirmeMethod() {
			String phoneName=etName.getText().toString();
			String phoneNum=etPhoneNumber.getText().toString();
			if(phoneName!=null&&!phoneName.isEmpty())
			{
				if(phoneNum!=null&&!phoneNum.isEmpty())
				{
					addContact(phoneName,phoneNum);//插入数据库
					//保存到偏好设置里面
					SharedPreferences sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();
			    	SharedPreferences.Editor editor = sharedPreferences.edit();
			    	editor.putString("phoneName"+id,phoneName);
			    	editor.putString("phoneNum"+id,phoneNum);
			    	editor.commit();
			    	//取消弹出框
			    	CreateNewContactDialog.this.dismiss();
			    	iTellCreateContactComplete.createComplete();//通知数据准备完成，回到主界面
				}else
				{
					Toast.makeText(context, "号码不能为空！", Toast.LENGTH_SHORT).show();
				}
			}else
			{
				Toast.makeText(context, "姓名不能为空！", Toast.LENGTH_SHORT).show();
			}
		}
    }
	 
	 //把添加的联系人插入系统数据库
	 public void addContact(String name, String phoneNum) {
	        ContentValues values = new ContentValues();
	        Uri rawContactUri = context.getContentResolver().insert(
	                RawContacts.CONTENT_URI, values);
	        long rawContactId = ContentUris.parseId(rawContactUri);
	        // 向data表插入数据
	        if (name != "") {
	            values.clear();
	            values.put(Data.RAW_CONTACT_ID, rawContactId);
	            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
	            values.put(StructuredName.GIVEN_NAME, name);
	            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
	                    values);
	        }
	        // 向data表插入电话号码
	        if (phoneNum != "") {
	            values.clear();
	            values.put(Data.RAW_CONTACT_ID, rawContactId);
	            values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
	            values.put(Phone.NUMBER, phoneNum);
	            values.put(Phone.TYPE, Phone.TYPE_MOBILE);
	            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
	                    values);
	        }
	    }

}
