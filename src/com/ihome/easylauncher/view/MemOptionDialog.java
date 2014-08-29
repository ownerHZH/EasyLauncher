package com.ihome.easylauncher.view;

import com.ihome.easylauncher.basedao.ITellCreateContactComplete;
import com.ihome.easylauncher.basedao.ITellToJump;
import com.ihome.easylauncher.ui.ChooseMemberActivity;
import com.ihome.easylauncher.ui.MessageEditActivity;
import com.ihome.fragment.FirstFragment;
import com.ihone.easylauncher.EasyLauncherApplication;
import com.ihone.easylauncher.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 选择主界面家庭联系人的弹出框
 * @author owner
 * @date 2014-08-27
 */
public class MemOptionDialog extends Dialog {

	private Context context;
	ViewStub mtViewStub,mtrViewStub;
	boolean f=true;
	TextView tvName,tvPhoneNumber;
	ImageView ivOptions;
	//由于在布局文件中用的是ViewStub控件，所以相应的组件要在布局控件初始化的时候初始化组件
	Button btnCall,btnSendMessage,btnEdit,btnExchange,btnDelete;
	private BtnClickListener btnlistener;
	private String name,number;
	private int id;
	public void setId(int id) {
		this.id = id;
	}

	private ITellToJump iTellToJump;
	
	public void setiTellToJump(ITellToJump iTellToJump) {
		this.iTellToJump = iTellToJump;
	}
	
	public MemOptionDialog(Context context, int theme, int id) {		
		super(context, theme);
		this.context=context;
		this.id=id;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_select_dialog);
		
		LayoutParams optionsparams =getWindow().getAttributes();
		optionsparams.height = android.app.ActionBar.LayoutParams.MATCH_PARENT;
		optionsparams.width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
		getWindow().setAttributes(optionsparams);	
		
		btnlistener=new BtnClickListener();
		
		mtViewStub=(ViewStub) findViewById(R.id.vStubTwoBtn);
		mtrViewStub=(ViewStub) findViewById(R.id.vStubThreeBtn);
		
		//两个按钮的界面显示 三个按钮的界面隐藏
		if(mtrViewStub!=null)
		{
			mtrViewStub.setVisibility(View.GONE);
		}
		if(mtViewStub!=null)
		{
			mtViewStub.setVisibility(View.VISIBLE);
			
			btnCall=(Button) findViewById(R.id.btnCall);
			btnSendMessage=(Button) findViewById(R.id.btnSendMessage);
			btnCall.setOnClickListener(btnlistener);
			btnSendMessage.setOnClickListener(btnlistener);
		}
		
		tvName=(TextView)findViewById(R.id.tvName);
		tvPhoneNumber=(TextView) findViewById(R.id.tvPhoneNumber);
		ivOptions=(ImageView) findViewById(R.id.ivOptions);
									
		ivOptions.setOnClickListener(btnlistener);
		
	}
	//设置联系人的姓名
	public void setNameText(String name)
	{
		tvName.setText(name);
		this.name=name;
	}
	//设置联系人的电话号码
	public void setNumberText(String number)
	{
		tvPhoneNumber.setText(number);
		this.number=number;
	}
	
	/**
	 * 按钮监听器
	 * @author owner
	 * @date 2014-08-27
	 */
	 private class BtnClickListener implements android.view.View.OnClickListener {

         @Override
         public void onClick(View v) {
              switch (v.getId()) {
			case R.id.ivOptions:
				if(f)
				{
					//两个按钮的界面隐藏，三个按钮的界面显示
					ivOptions.setImageResource(R.drawable.icon_option1);
					if(mtViewStub!=null)
					{
						mtViewStub.setVisibility(View.GONE);
					}
					if(mtrViewStub!=null)
					{
						mtrViewStub.setVisibility(View.VISIBLE);
						
						if(btnEdit==null)
						{
							btnEdit=(Button) findViewById(R.id.btnEdit);
							btnEdit.setOnClickListener(btnlistener);
						}
						if(btnExchange==null)
						{
							btnExchange=(Button) findViewById(R.id.btnExchange);
							btnExchange.setOnClickListener(btnlistener);
						}
						if(btnDelete==null)
						{
							btnDelete=(Button) findViewById(R.id.btnDelete);														
							btnDelete.setOnClickListener(btnlistener);
						}																		
					}									    
				}else
				{
					ivOptions.setImageResource(R.drawable.icon_option);
					//两个按钮的界面显示 三个按钮的界面隐藏
					if(mtrViewStub!=null)
					{
						mtrViewStub.setVisibility(View.GONE);
					}
					if(mtViewStub!=null)
					{
						mtViewStub.setVisibility(View.VISIBLE);
						if(btnCall==null)
						{
							btnCall=(Button) findViewById(R.id.btnCall);
							btnCall.setOnClickListener(btnlistener);
						}
						if(btnSendMessage==null)
						{
							btnSendMessage=(Button) findViewById(R.id.btnSendMessage);							
							btnSendMessage.setOnClickListener(btnlistener);
						}													
					}
				}
				f=!f;
				break;
            case R.id.btnCall:
            	//传入服务， parse（）解析号码
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                //通知activtity处理传入的call服务
                context.startActivity(intent);
				break;
            case R.id.btnSendMessage:
            	//跳转到发短信的界面
            	Intent smsg=new Intent();
            	smsg.setClass(context, MessageEditActivity.class);
            	smsg.putExtra("number", number);
            	context.startActivity(smsg);
				break;
            case R.id.btnEdit:           	
				//通知打开编辑弹出框
            	MemOptionDialog.this.dismiss();
				iTellToJump.tellToOpenCreateDialog(id, name, number);          					
				break;
            case R.id.btnExchange:
            	//跳转到选择界面
            	iTellToJump.jumpToChooseMemActivity(id);
            	MemOptionDialog.this.dismiss();
				break;
            case R.id.btnDelete:
            	//删除家庭成员 把偏好设置里面对应的成员设置为空，
            	//主要是设置电话号码为空，因为主界面是判断电话号码是否为空进行不同的操作
            	SharedPreferences sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();
	        	SharedPreferences.Editor editor = sharedPreferences.edit();
	        	editor.putString("phoneName"+id,"家人"+id);
	        	editor.putString("phoneNum"+id,"");
	        	editor.commit();
	        	iTellToJump.tellToRefreshMember();//通知更新人员数据
	        	MemOptionDialog.this.dismiss();
				break;
			}
         }
    }

}
