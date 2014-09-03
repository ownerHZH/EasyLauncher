package com.ihome.easylauncher.ui;

import com.ihome.easylauncher.EasyLauncherApplication;
import com.ihome.easylauncher.basedao.ITellCreateContactComplete;
import com.ihome.easylauncher.view.CreateNewContactDialog;
import com.ihome.fragment.FirstFragment;
import com.ihome.easylauncher.R;
import com.ihome.easylauncher.R.layout;
import com.ihome.easylauncher.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class ChooseMemberActivity extends Activity implements ITellCreateContactComplete {

	Button create,choose;
	Context context;
	int id=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent=getIntent();
		if(intent!=null)
		{
			id=intent.getIntExtra("id", 0);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_member);
		context=ChooseMemberActivity.this;
		create=(Button) findViewById(R.id.btnCreate);
		choose=(Button) findViewById(R.id.btnChoose);
		
		choose.setOnClickListener(onclicklistener);
		create.setOnClickListener(onclicklistener);
	}
	
	private OnClickListener onclicklistener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnCreate:
				CreateNewContactDialog createdialog=new CreateNewContactDialog(context,R.style.dialog,id);
				createdialog.setiTellCreateContactComplete(ChooseMemberActivity.this);//注册回调接口
				createdialog.show();
				break;
            case R.id.btnChoose:
            	Intent contactintent = new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(contactintent, FirstFragment.CHOOSE_CONTACT_PERSON);
				break;
			}
	}};
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==FirstFragment.CHOOSE_CONTACT_PERSON)
		{
			if(resultCode==Activity.RESULT_OK)
			{
				Uri contactData = data.getData(); 
		        Cursor c = getContentResolver().query(contactData, null, null, null, null); 		 
		        c.moveToFirst(); 
		        String[] cp=this.getContactPhone(c);
		        if(cp!=null&&cp.length>1)
		        {
		        	//选择一个联系人后返回到 FirstFragment界面
		        	String phoneName=cp[0];
		        	String phoneNum=cp[1];
		        	phoneName=phoneName==null?"":phoneName;
		        	phoneNum=phoneNum==null?"":phoneNum;
		        	
		        	//存储第一个成员的信息到SharedPreferences
		        	SharedPreferences sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();
		        	if(!isDduplicate(phoneName, phoneNum, sharedPreferences))
		        	{
		        		SharedPreferences.Editor editor = sharedPreferences.edit();
			        	editor.putString("phoneName"+id,phoneName);
			        	editor.putString("phoneNum"+id,phoneNum);
			        	editor.commit();
			        	setResult(Activity.RESULT_OK);
			        	finish();
		        	}else
		        	{
		        		Toast.makeText(context, "此人已经被选择，请重选。", Toast.LENGTH_SHORT).show();
		        	}
		        			        	
		        }
		        if(!c.isClosed())
		        {
		        	c.close();
		        }
			}
		}
	}

	/**
	 * 判断选择的人是否重复
	 * @param phoneName
	 * @param phoneNum
	 * @param sharedPreferences
	 * @return 重复true or 不重复false
	 */
	private boolean isDduplicate(String phoneName, String phoneNum,
			SharedPreferences sharedPreferences) {
		for(int i=1;i<=3;i++)
		{
			if(i!=id)
			{
				String pn=sharedPreferences.getString("phoneName"+i, "");
				String pnum=sharedPreferences.getString("phoneNum"+i, "");
				if(pn.equals(phoneName)&&pnum.equals(phoneNum))
				{
					return true;
				}
			}	        		
		}
		return false;
	}
	
	//获取联系人电话 
		private String[] getContactPhone(Cursor cursor) 
		{  
		    int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);   
		    int phoneNum = cursor.getInt(phoneColumn);  
		    String[] phoneResult = new String[2]; 
		    if (phoneNum > 0) 
		    { 
		        // 获得联系人的ID号 
		        int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID); 
		        String contactId = cursor.getString(idColumn); 
	            // 获得联系人的电话号码的cursor; 
	            Cursor phones = getContentResolver().query( 
	            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
	            null, 
	            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId,  
	            null, null); 
	            if (phones.moveToFirst()) 
	            { 
	                // 遍历所有的电话号码 
	                for (;!phones.isAfterLast();phones.moveToNext()) 
	                {                                             
	                    int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER); 
	                    int nameindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
	                    int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE); 
	                    int phone_type = phones.getInt(typeindex); 
	                    String phoneNumber = phones.getString(index); 
	                    String name=phones.getString(nameindex);
	                    switch(phone_type) 
	                    { 
	                        case 2: 
	                        	phoneResult[0]=name;
	                            phoneResult[1]=phoneNumber;	                            
	                        break; 
	                    } 
	                } 
	                if (!phones.isClosed()) 
	                { 
	                       phones.close(); 
	                } 
	            } 
		    } 
		    return phoneResult; 
		}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_choose_member, menu);
		return false;
	}

	@Override
	public void createComplete() {
		//回到主界面
		setResult(Activity.RESULT_OK);
    	finish();
	}

}
