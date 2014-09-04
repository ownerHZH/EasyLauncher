package com.ihome.easylauncher;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechUtility;
import com.ihome.easylauncher.basedao.ITellLocation;
import com.ihome.utils.Constants;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class EasyLauncherApplication extends Application {
	private static EasyLauncherApplication application;
	// 语音听写没有自带UI
	//private static SpeechRecognizer mIat;
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	//public static String bdLString="";//定位字符串
	private ITellLocation iTellLocation;
	
	public void setiTellLocation(ITellLocation iTellLocation) {
		this.iTellLocation = iTellLocation;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		SpeechUtility.createUtility(this, Constants.API_KEY);
		
		///百度定位 begin
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		InitLocation();
		//mLocationClient.start();
		SDKInitializer.initialize(this);//地图
		///百度定位 end
	}

	//全局的上下文
	public static synchronized EasyLauncherApplication getInstance() {
		if(application!=null)
		    return application;
		else
			return new EasyLauncherApplication();
	}

	//全局的SharedPreferences对象
	public static synchronized SharedPreferences getDefaultSharedPreferences(){
		return application.getSharedPreferences("easylauncher", Context.MODE_PRIVATE);
	}
	
	//初始化百度定位参数
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
		option.setOpenGps(true);
		option.setScanSpan(1000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	/**
	 * 实现百度定位实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			//logMsg(sb.toString());
			iTellLocation.onTellLocation(location);
		}
	}
	
}
