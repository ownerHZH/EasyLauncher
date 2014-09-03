package com.ihome.fragment;

import com.baidu.location.LocationClient;
import com.ihome.easylauncher.basedao.ITellLocation;
import com.ihome.easylauncher.ui.BaiDuMapActivity;
import com.ihome.easylauncher.ui.FlashLightActivity;
import com.ihome.easylauncher.ui.MusicListActivity;
import com.ihome.easylauncher.ui.NewsActivity;
import com.ihome.easylauncher.EasyLauncherApplication;
import com.ihome.easylauncher.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SecondFragment extends Fragment{
	LinearLayout linNet,linNews,linMusic,linRead,linMovie,linLight,linPosition,linSetting;
	Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.activity_second_fragment, container, false);
		
		linNet=(LinearLayout) view.findViewById(R.id.linNet);
		linNews=(LinearLayout) view.findViewById(R.id.linNews);
		linMusic=(LinearLayout) view.findViewById(R.id.linMusic);
		linRead=(LinearLayout) view.findViewById(R.id.linRead);
		linMovie=(LinearLayout) view.findViewById(R.id.linMovie);
		linLight=(LinearLayout) view.findViewById(R.id.linLight);
		linPosition=(LinearLayout) view.findViewById(R.id.linPosition);
		linSetting=(LinearLayout) view.findViewById(R.id.linSetting);
			
		linNet.setOnClickListener(linclicklistener);
		linNews.setOnClickListener(linclicklistener);
		linMusic.setOnClickListener(linclicklistener);
		linRead.setOnClickListener(linclicklistener);
		linMovie.setOnClickListener(linclicklistener);
		linLight.setOnClickListener(linclicklistener);
		linPosition.setOnClickListener(linclicklistener);
		linSetting.setOnClickListener(linclicklistener);
				
		return view;
	}
	
	private OnClickListener linclicklistener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.linNet:
				//上网点击事件
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse("http://www.hao123.com");   
				intent.setData(content_url);  
				startActivity(intent);
				break;
			case R.id.linNews:
			    //新闻点击事件	
				Intent newsintent = new Intent();  
				newsintent.setClass(context, NewsActivity.class);
				startActivity(newsintent);
				break;
			case R.id.linMusic:
				//音乐点击事件
				 Intent it = new Intent();
				 it.setClass(context, MusicListActivity.class);
				 startActivity(it);
				break;
			case R.id.linRead:
				//读书点击事件
				break;
			case R.id.linMovie:
				//电影点击事件
				Intent movieintent = new Intent(Intent.ACTION_VIEW);
				//movieintent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory() + "/3.mp4"), "video/mp4");
				movieintent.setType(Video.Media.CONTENT_TYPE);//video/mp4
				startActivity(movieintent);
				break;
			case R.id.linLight:
				//手电筒点击事件FlashLightActivity
				Intent flashintent = new Intent();  
				flashintent.setClass(context, FlashLightActivity.class);
				startActivity(flashintent);
				break;
			case R.id.linPosition:
				//位置点击事件
				Intent bdintent = new Intent();  
				bdintent.setClass(context, BaiDuMapActivity.class);
				startActivity(bdintent);				
				break;
			case R.id.linSetting:
				//设置点击事件
				break;
			}
		}
	};
	
	 //绑定Activity
	@Override
	public void onAttach(Activity activity) {
		Log.e("onAttach","onAttach--");
		context=activity;
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}
