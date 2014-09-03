package com.ihome.easylauncher.view;

import com.ihome.easylauncher.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

/**
 * 新建联系人的弹出框
 * @author owner
 * @date 2014-08-29
 */
public class DeleteAppDialog extends Dialog {

	private Context context;
	private Button delete;
	private BtnClickListener btnlistener;
	private ResolveInfo resolveInfo;

	public DeleteAppDialog(Context context, int theme,ResolveInfo resolveInfo) {		
		super(context, theme);
		this.context=context;
		this.resolveInfo=resolveInfo;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_app_dialog_layout);
		
		LayoutParams optionsparams =getWindow().getAttributes();
		optionsparams.height = android.app.ActionBar.LayoutParams.MATCH_PARENT;
		optionsparams.width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
		getWindow().setAttributes(optionsparams);	
		
		btnlistener=new BtnClickListener();
		delete=(Button) findViewById(R.id.deleteBtn);
		delete.setOnClickListener(btnlistener);
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
	            case R.id.deleteBtn:
	            	Uri packageURI = Uri.parse("package:"+resolveInfo.activityInfo.packageName); 
					//创建Intent意图 
					Intent intent = new Intent(Intent.ACTION_DELETE,packageURI); 
					//执行卸载程序 
					context.startActivity(intent);
					DeleteAppDialog.this.dismiss();
					break;
			}
         }
      }
}
