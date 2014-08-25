package com.ihome.fragment;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.ihome.service.WeatherBean;
import com.ihome.service.WebServiceHelper;
import com.ihone.easylauncher.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FirstFragment extends Fragment {
	
	private TextView tvDate;//日期
	private TextView tvTime;//时间
	private TextView tvAlarm;//闹铃
	private LinearLayout linDatetime,linWeather;//闹铃。天气
	
	private ImageView picfrom,picto;//两个天气图标 阴--》多云
	private TextView city,tempeture,weather;//城市、温度、天气状况
	
	private LinearLayout linCamera,linAlbum,
	          linPersonOne,linPersonTwo,linPersonThree,linApps,linContacts;
	
	private Context context;//上下文
	private FutureTask<WeatherBean> future;//处理耗时任务
	
	private final Timer timer=new Timer(); //定时器
	private TimerTask timerTask; //定时器处理任务
	
	private static final int REFRESH_WEATHER=0x321;//更新天气的标志
	private static final int ALARM_ACTITY_REQUEST=0x322;//打开闹钟设置界面
	
	//事件的接收、处理handler
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_WEATHER:
				refreshWeather("北京");//更新天气
				break;

			default:
				break;
			}
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("onCreateView", "onCreateView--");
		View view=inflater.inflate(R.layout.activity_first_fragment, container, false);
		init(view);//初始化布局中的组件
		registerBoradcastReceiver();//注册广播接收器
		refreshWeather("北京");//更新天气
		timerTask=new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(REFRESH_WEATHER);
			}
		};
		timer.schedule(timerTask, 0, 1000*60*60*3);//天气服务器是每2.5小时更新，我这里每三小时去更新一次天气状况
		
		return view;
	}
	
	/**
	 * 获取设置好的下一个闹钟时间
	 */
	private void getAlarmInfo()
	{
		String str = Settings.System.getString(context.getContentResolver(),  
                Settings.System.NEXT_ALARM_FORMATTED);
		if(str!=null&&!str.trim().equals(""))
		{
			tvAlarm.setText("闹钟  "+str);
		}
		/*final String tag_alarm = "tag_alarm";  
		Uri uri = Uri.parse("content://com.android.alarmclock/alarm");  
		Cursor c = context.getContentResolver().query(uri, null, null, null, null);  
		Log.i(tag_alarm, "no of records are " + c.getCount());  
		Log.i(tag_alarm, "no of columns are " + c.getColumnCount());  
		if (c != null) {  
		    String names[] = c.getColumnNames();  
		    for (String temp : names) {  
		        System.out.println(temp);  
		    }  
		    if (c.moveToFirst()) {  
		        do {  
		            for (int j = 0; j < c.getColumnCount(); j++) {  
		                Log.i(tag_alarm, c.getColumnName(j)  
		                        + " which has value " + c.getString(j));  
		            }  
		        } while (c.moveToNext());  
		    }
		    c.close();
		}*/
	}

	/**
	 * 初始化布局组件
	 * @param view
	 */
	private void init(View view) {
		linDatetime=(LinearLayout) view.findViewById(R.id.lindatatime);
		tvDate=(TextView) view.findViewById(R.id.textViewDate);
		tvTime=(TextView) view.findViewById(R.id.textViewTime);
		tvAlarm=(TextView) view.findViewById(R.id.textViewAlarm);
		tvDate.setText(getDate());
		tvTime.setText(getTime());
		
		picfrom=(ImageView) view.findViewById(R.id.imageViewfrom);
		picto=(ImageView) view.findViewById(R.id.imageViewto);
		city=(TextView) view.findViewById(R.id.textViewcity);
		tempeture=(TextView) view.findViewById(R.id.textViewtem);
		weather=(TextView) view.findViewById(R.id.textViewwea);
		linWeather=(LinearLayout) view.findViewById(R.id.linweather);
		
		linCamera=(LinearLayout) view.findViewById(R.id.linCamera);
		linAlbum=(LinearLayout) view.findViewById(R.id.linAlbum);
        linPersonOne=(LinearLayout) view.findViewById(R.id.linPersonone);
        linPersonTwo=(LinearLayout) view.findViewById(R.id.linPersontwo);
        linPersonThree=(LinearLayout) view.findViewById(R.id.linPersonthree);
        linApps=(LinearLayout) view.findViewById(R.id.linApps);
        linContacts=(LinearLayout) view.findViewById(R.id.linContacts);
		
		linDatetime.setOnClickListener(onclicklistener);
		linWeather.setOnClickListener(onclicklistener);
		
		linCamera.setOnClickListener(onclicklistener);
		linAlbum.setOnClickListener(onclicklistener);
		linPersonOne.setOnClickListener(onclicklistener);
		linPersonTwo.setOnClickListener(onclicklistener);
		linPersonThree.setOnClickListener(onclicklistener);
		linApps.setOnClickListener(onclicklistener);
		linContacts.setOnClickListener(onclicklistener);
	}

	/**
	 * 根据城市名获取天气状况
	 * @param cityString
	 */
	private void refreshWeather(String cityString) {
		if(future!=null)
		{
			future.cancel(true);
		}
		future=null;
		future=new FutureTask<WeatherBean>(new GetWeatherCallable(cityString));
		new Thread(future).start();
		try {
			WeatherBean bean=future.get();
			if(bean!=null)
			{
				city.setText(bean.getCityName());
				weather.setText(bean.getTodayWeather());
				tempeture.setText(bean.getTodayTempeture());
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> o=(List<Map<String,Object>>)bean.getList();
				if(o!=null&&o.size()>0)
				{
					List<Integer> inL=(List<Integer>) o.get(0).get("icons");
					picfrom.setImageResource(getRId(inL.get(0)));
					picto.setImageResource(getRId(inL.get(1)));
				}
				
			}else
			{
				Log.e("天气为空", "天气为空");
			}
		} catch (InterruptedException e) {
			Log.e("InterruptedException", "InterruptedException");
			e.printStackTrace();
		} catch (ExecutionException e) {
			weather.setText("服务暂停");
			city.setText("");
			tempeture.setText("");
			picfrom.setImageResource(R.drawable.b_32);
			picto.setImageResource(R.drawable.b_32);
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过返回的天气图片显示响应的图片
	 * 通过数字 0 得到 R.drawable.b_0	
	 * @param integer
	 * @return
	 */
	public int getRId(Integer integer) {
		R.drawable drawables=new R.drawable();
		//默认的id
		int resId=R.drawable.b_32;
		try {
			//根据字符串字段名，取字段
			Field field=R.drawable.class.getField("b_"+integer);
			//取值
			resId=(Integer)field.get(drawables);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resId;
	}

	/**
	 * FutureTask的执行类，返回天气结果
	 * @author owner
	 *
	 */
	class GetWeatherCallable implements Callable<WeatherBean>
	{
		private String city;
		GetWeatherCallable(String city)
		{
			this.city=city;
		}
		@Override
		public WeatherBean call() throws Exception {
			// TODO Auto-generated method stub
			return new WebServiceHelper().getWeatherByCity(city);
		}
		
	}
	
	/**
	 * 组件的点击处理事件
	 */
	private OnClickListener onclicklistener=new OnClickListener() {
				
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lindatatime:
				Intent alarmas = new Intent(AlarmClock.ACTION_SET_ALARM);
				startActivity(alarmas);
				break;
				
			case R.id.linweather:
				//天气选择点击事件
				break;
				
			case R.id.linAlbum:
				//相册点击事件
				Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(picture, 1);
				break;
			case R.id.linApps:
				//应用点击事件
				break;
			case R.id.linCamera:
				//照相点击事件
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
				break;
			case R.id.linContacts:
				//联系人点击事件
				Intent contactintent = new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(contactintent, 1);
				break;
			case R.id.linPersonone:
				//家人1点击事件
				break;
			case R.id.linPersontwo:
				//家人2点击事件
				break;
			case R.id.linPersonthree:
				//家人3点击事件
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 获取系统的日期
	 * @return 2014-08-22
	 */
	private String getDate()
	{
		String[] dt=getDateTime().split(" ");
		if(dt!=null&&dt.length>0)
		{
			return dt[0];
		}else
		{
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间
			return formatter.format(curDate);
		}
		
	}
	
	/**
	 * 获取系统的时间
	 * 
	 * @return 23:32
	 */
	private String getTime()
	{
		String[] dt=getDateTime().split(" ");
		if(dt!=null&&dt.length>0)
		{
			return dt[1];
		}else
		{
			SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm");
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间
			return formatter.format(curDate);
		}
	}
	/**
	 * 获取系统的日期和时间
	 * @return 2014-08-22 23:32
	 */
	private String getDateTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		return formatter.format(curDate);
	}
	@Override
	public void onDestroy() {
		Log.e("onDestroy","onDestroy--");
		if(mBroadcastReceiver!=null&&context!=null)
		    context.unregisterReceiver(mBroadcastReceiver);
		if(timer!=null)
			timer.cancel();
		super.onDestroy();
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("onCreate","onCreate--");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		Log.e("onDestroyView","onDestroyView--");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		Log.e("onDetach","onDetach--");
		super.onDetach();
	}

	@Override
	public void onPause() {
		Log.e("onPause","onPause--");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.e("onResume","onResume--");
		super.onResume();
		getAlarmInfo();//获取下一个闹钟时间
	}

	@Override
	public void onStart() {
		Log.e("onStart","onStart--");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.e("onStop","onStop--");
		super.onStop();
	}


	/**
	 * 系统日期、时间改变的广播接收器
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if(action.equals(Intent.ACTION_DATE_CHANGED)){  
            	//日期变化
            	tvDate.setText(getDate());
            } else if(action.equals(Intent.ACTION_TIME_CHANGED))
            {
            	//时间变化
            	tvTime.setText(getTime());
            }else if(action.equals(Intent.ACTION_TIME_TICK))
            {
        		tvTime.setText(getTime());
            }
        }  
          
    };  
    
    /**
     * 注册系统时间、日期改变的广播
     */
    public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);  
        myIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        myIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        //注册广播        
        context.registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }
    
	@Override
	public void onAttach(Activity activity) {
		Log.e("onAttach","onAttach--");
		context=activity;
		super.onAttach(activity);
	}			
}
