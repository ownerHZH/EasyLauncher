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
	
	private TextView tvDate;//����
	private TextView tvTime;//ʱ��
	private TextView tvAlarm;//����
	private LinearLayout linDatetime,linWeather;//���塣����
	
	private ImageView picfrom,picto;//��������ͼ�� ��--������
	private TextView city,tempeture,weather;//���С��¶ȡ�����״��
	
	private LinearLayout linCamera,linAlbum,
	          linPersonOne,linPersonTwo,linPersonThree,linApps,linContacts;
	
	private Context context;//������
	private FutureTask<WeatherBean> future;//�����ʱ����
	
	private final Timer timer=new Timer(); //��ʱ��
	private TimerTask timerTask; //��ʱ����������
	
	private static final int REFRESH_WEATHER=0x321;//���������ı�־
	private static final int ALARM_ACTITY_REQUEST=0x322;//���������ý���
	
	//�¼��Ľ��ա�����handler
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_WEATHER:
				refreshWeather("����");//��������
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
		init(view);//��ʼ�������е����
		registerBoradcastReceiver();//ע��㲥������
		refreshWeather("����");//��������
		timerTask=new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(REFRESH_WEATHER);
			}
		};
		timer.schedule(timerTask, 0, 1000*60*60*3);//������������ÿ2.5Сʱ���£�������ÿ��Сʱȥ����һ������״��
		
		return view;
	}
	
	/**
	 * ��ȡ���úõ���һ������ʱ��
	 */
	private void getAlarmInfo()
	{
		String str = Settings.System.getString(context.getContentResolver(),  
                Settings.System.NEXT_ALARM_FORMATTED);
		if(str!=null&&!str.trim().equals(""))
		{
			tvAlarm.setText("����  "+str);
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
	 * ��ʼ���������
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
	 * ���ݳ�������ȡ����״��
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
				Log.e("����Ϊ��", "����Ϊ��");
			}
		} catch (InterruptedException e) {
			Log.e("InterruptedException", "InterruptedException");
			e.printStackTrace();
		} catch (ExecutionException e) {
			weather.setText("������ͣ");
			city.setText("");
			tempeture.setText("");
			picfrom.setImageResource(R.drawable.b_32);
			picto.setImageResource(R.drawable.b_32);
			e.printStackTrace();
		}
	}
	
	/**
	 * ͨ�����ص�����ͼƬ��ʾ��Ӧ��ͼƬ
	 * ͨ������ 0 �õ� R.drawable.b_0	
	 * @param integer
	 * @return
	 */
	public int getRId(Integer integer) {
		R.drawable drawables=new R.drawable();
		//Ĭ�ϵ�id
		int resId=R.drawable.b_32;
		try {
			//�����ַ����ֶ�����ȡ�ֶ�
			Field field=R.drawable.class.getField("b_"+integer);
			//ȡֵ
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
	 * FutureTask��ִ���࣬�����������
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
	 * ����ĵ�������¼�
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
				//����ѡ�����¼�
				break;
				
			case R.id.linAlbum:
				//������¼�
				Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(picture, 1);
				break;
			case R.id.linApps:
				//Ӧ�õ���¼�
				break;
			case R.id.linCamera:
				//�������¼�
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
				break;
			case R.id.linContacts:
				//��ϵ�˵���¼�
				Intent contactintent = new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(contactintent, 1);
				break;
			case R.id.linPersonone:
				//����1����¼�
				break;
			case R.id.linPersontwo:
				//����2����¼�
				break;
			case R.id.linPersonthree:
				//����3����¼�
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * ��ȡϵͳ������
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
			Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
			return formatter.format(curDate);
		}
		
	}
	
	/**
	 * ��ȡϵͳ��ʱ��
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
			Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
			return formatter.format(curDate);
		}
	}
	/**
	 * ��ȡϵͳ�����ں�ʱ��
	 * @return 2014-08-22 23:32
	 */
	private String getDateTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
		Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
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
		getAlarmInfo();//��ȡ��һ������ʱ��
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
	 * ϵͳ���ڡ�ʱ��ı�Ĺ㲥������
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if(action.equals(Intent.ACTION_DATE_CHANGED)){  
            	//���ڱ仯
            	tvDate.setText(getDate());
            } else if(action.equals(Intent.ACTION_TIME_CHANGED))
            {
            	//ʱ��仯
            	tvTime.setText(getTime());
            }else if(action.equals(Intent.ACTION_TIME_TICK))
            {
        		tvTime.setText(getTime());
            }
        }  
          
    };  
    
    /**
     * ע��ϵͳʱ�䡢���ڸı�Ĺ㲥
     */
    public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);  
        myIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        myIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        //ע��㲥        
        context.registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }
    
	@Override
	public void onAttach(Activity activity) {
		Log.e("onAttach","onAttach--");
		context=activity;
		super.onAttach(activity);
	}			
}
