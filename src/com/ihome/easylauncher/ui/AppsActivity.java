package com.ihome.easylauncher.ui;

import java.util.ArrayList;
import java.util.List;

import com.ihome.adapter.AppsAdapter;
import com.ihome.easylauncher.view.DeleteAppDialog;
import com.ihome.easylauncher.R;
import com.ihome.easylauncher.R.layout;
import com.ihome.easylauncher.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class AppsActivity extends Activity {

	private Context context;
	private GridView gridView;
	private List<ResolveInfo> apps=new ArrayList<ResolveInfo>();
	private AppsAdapter adapter;
	private MyInstalledReceiver installedReceiver=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		context=AppsActivity.this;
		loadApps();//加载app信息
		setContentView(R.layout.activity_apps);
		gridView=(GridView) findViewById(R.id.gvApp);
		adapter=new AppsAdapter(context, apps);
		gridView.setAdapter(adapter);
		
		gridView.setOnItemClickListener(gvonitemclick);
		
		gridView.setOnItemLongClickListener(gvonitemlongclick);
	}
	
	/*
	 * 查找所有的应用信息
	 */
	private void loadApps()
	{
		System.gc();
		apps.clear();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);           
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);              
        List<ResolveInfo>  app = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        apps.addAll(app);
	}
	
	private OnItemLongClickListener gvonitemlongclick=new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			DeleteAppDialog deleteAppDialog=new DeleteAppDialog(context,R.style.dialog,apps.get(position));
			deleteAppDialog.show();
			return true;
		}
	};
	
	private OnItemClickListener gvonitemclick=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			ResolveInfo r=apps.get(position);
			Intent rz=new Intent(Intent.ACTION_MAIN);
			rz.addCategory(Intent.CATEGORY_LAUNCHER);
			rz.setComponent(new ComponentName(r.activityInfo.packageName, r.activityInfo.name));
			startActivity(rz);
		}
	};
	
	@Override  
    public void onStart(){  
        super.onStart();  
        if(installedReceiver==null)
        {
        	installedReceiver = new MyInstalledReceiver();  
            IntentFilter filter = new IntentFilter();  
              
            filter.addAction("android.intent.action.PACKAGE_ADDED");  
            filter.addAction("android.intent.action.PACKAGE_REMOVED");  
            filter.addDataScheme("package");  
              
            this.registerReceiver(installedReceiver, filter);
        }     
    }
	
	@Override  
    public void onDestroy(){  
        if(installedReceiver != null) {  
            this.unregisterReceiver(installedReceiver);  
        }  
          
        super.onDestroy();  
    }
	
	//应用的安装卸载监听广播
    public class MyInstalledReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
      
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {     // install  
                String packageName = intent.getDataString(); 
                loadApps();
                adapter.notifyDataSetChanged();
            }  
      
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {   // uninstall  
                String packageName = intent.getDataString(); 
                loadApps();
                adapter.notifyDataSetChanged();
            }  
        }  
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_apps, menu);
		return false;
	}

}
