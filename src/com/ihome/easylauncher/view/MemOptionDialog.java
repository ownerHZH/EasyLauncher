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
 * ѡ���������ͥ��ϵ�˵ĵ�����
 * @author owner
 * @date 2014-08-27
 */
public class MemOptionDialog extends Dialog {

	private Context context;
	ViewStub mtViewStub,mtrViewStub;
	boolean f=true;
	TextView tvName,tvPhoneNumber;
	ImageView ivOptions;
	//�����ڲ����ļ����õ���ViewStub�ؼ���������Ӧ�����Ҫ�ڲ��ֿؼ���ʼ����ʱ���ʼ�����
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
		
		//������ť�Ľ�����ʾ ������ť�Ľ�������
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
	//������ϵ�˵�����
	public void setNameText(String name)
	{
		tvName.setText(name);
		this.name=name;
	}
	//������ϵ�˵ĵ绰����
	public void setNumberText(String number)
	{
		tvPhoneNumber.setText(number);
		this.number=number;
	}
	
	/**
	 * ��ť������
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
					//������ť�Ľ������أ�������ť�Ľ�����ʾ
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
					//������ť�Ľ�����ʾ ������ť�Ľ�������
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
            	//������� parse������������
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                //֪ͨactivtity�������call����
                context.startActivity(intent);
				break;
            case R.id.btnSendMessage:
            	//��ת�������ŵĽ���
            	Intent smsg=new Intent();
            	smsg.setClass(context, MessageEditActivity.class);
            	smsg.putExtra("number", number);
            	context.startActivity(smsg);
				break;
            case R.id.btnEdit:           	
				//֪ͨ�򿪱༭������
            	MemOptionDialog.this.dismiss();
				iTellToJump.tellToOpenCreateDialog(id, name, number);          					
				break;
            case R.id.btnExchange:
            	//��ת��ѡ�����
            	iTellToJump.jumpToChooseMemActivity(id);
            	MemOptionDialog.this.dismiss();
				break;
            case R.id.btnDelete:
            	//ɾ����ͥ��Ա ��ƫ�����������Ӧ�ĳ�Ա����Ϊ�գ�
            	//��Ҫ�����õ绰����Ϊ�գ���Ϊ���������жϵ绰�����Ƿ�Ϊ�ս��в�ͬ�Ĳ���
            	SharedPreferences sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();
	        	SharedPreferences.Editor editor = sharedPreferences.edit();
	        	editor.putString("phoneName"+id,"����"+id);
	        	editor.putString("phoneNum"+id,"");
	        	editor.commit();
	        	iTellToJump.tellToRefreshMember();//֪ͨ������Ա����
	        	MemOptionDialog.this.dismiss();
				break;
			}
         }
    }

}
