package com.ihome.fragment;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.ihome.easylauncher.EasyLauncherApplication;
import com.ihome.easylauncher.basedao.ITellCreateContactComplete;
import com.ihome.easylauncher.basedao.ITellToJump;
import com.ihome.easylauncher.ui.AppsActivity;
import com.ihome.easylauncher.ui.ChooseMemberActivity;
import com.ihome.easylauncher.ui.ChooseWeatherCityActivity;
import com.ihome.easylauncher.view.CreateNewContactDialog;
import com.ihome.easylauncher.view.MemOptionDialog;
import com.ihome.service.WeatherBean;
import com.ihome.service.WebServiceHelper;
import com.ihome.easylauncher.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
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

public class FirstFragment extends Fragment implements ITellToJump,ITellCreateContactComplete{
	
	private TextView tvDate;//日期
	private TextView tvTime;//时间
	private TextView tvAlarm;//闹铃
	private LinearLayout linDatetime,linWeather;//闹铃。天气
	
	private ImageView picfrom,picto;//两个天气图标 阴--》多云
	private TextView city,tempeture,weather;//城市、温度、天气状况
	
	private LinearLayout linCamera,linAlbum,
	          linPersonOne,linPersonTwo,linPersonThree,linApps,linContacts;
	private TextView tvpone,tvptwo,tvpthree;//三个成员名称
	private TextView tvponen,tvptwon,tvpthreen;//三个成员的电话号码 为隐藏的。
	
	private Context context;//上下文
	private FutureTask<WeatherBean> future;//处理耗时任务
	private WebServiceHelper weatherHelper;
	
	private final Timer timer=new Timer(); //定时器
	private TimerTask timerTask; //定时器处理任务
	
	MemOptionDialog memOptionDialog;//弹出操作框
	
	SharedPreferences sharedPreferences;
			
	private static final int REFRESH_WEATHER=0x321;//更新天气的标志
	private static final int ALARM_ACTITY_REQUEST=0x322;//打开闹钟设置界面
	public static final int CHOOSE_CONTACT_PERSON=0x323;//获取通讯录中的一个人
	public static final int CHOOSE_WEATHER_CITY=0x324;//跳转到天气选择的界面
	
	//事件的接收、处理handler
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_WEATHER:
				//更新偏好设置中的城市天气 默认城市为 北京
				String cstr=sharedPreferences.getString("WeatherCity", "北京");
				refreshWeather(cstr);//更新天气
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
		weatherHelper=new WebServiceHelper();//天气服务对象
		sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();//偏好设置
		init(view);//初始化布局中的组件
		registerBoradcastReceiver();//注册广播接收器
		//refreshWeather("北京");//更新天气
		timerTask=new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(REFRESH_WEATHER);
			}
		};
		timer.schedule(timerTask, 0, 1000*60*60*3);//天气服务器是每2.5小时更新，我这里每三小时去更新一次天气状况
		getMemberInfo(1,2,3);//初始获取家庭成员信息
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
        
        tvpone=(TextView) view.findViewById(R.id.tvPersonone);
        tvptwo=(TextView) view.findViewById(R.id.tvPersonTwo);
        tvpthree=(TextView) view.findViewById(R.id.tvPersonThree);
        
        tvponen=(TextView) view.findViewById(R.id.tvPersonOneNumber);
        tvptwon=(TextView) view.findViewById(R.id.tvPersonTwoNumber);
        tvpthreen=(TextView) view.findViewById(R.id.tvPersonThreeNumber);
		
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
			return weatherHelper.getWeatherByCity(city);
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
				Intent weather=new Intent();
				weather.setClass(context, ChooseWeatherCityActivity.class);
				startActivityForResult(weather, CHOOSE_WEATHER_CITY);
				break;
				
			case R.id.linAlbum:
				//相册点击事件
			    Intent picture = new Intent();
			    picture.setType("image/*");
			    picture.setAction(Intent.ACTION_VIEW);
			    startActivity(picture);
				break;
			case R.id.linApps:
				//应用点击事件
				Intent appIntent=new Intent();
				appIntent.setClass(context, AppsActivity.class);
				startActivity(appIntent);
				break;
			case R.id.linCamera:
				//照相点击事件
                Intent intent = new Intent(); //调用照相机
                intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
                startActivity(intent);
				break;
			case R.id.linContacts:
				//联系人点击事件
				Intent contactintent = new Intent();
				contactintent.setAction(Intent.ACTION_VIEW);
				contactintent.setData(ContactsContract.Contacts.CONTENT_URI);
				contactintent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivity(contactintent);
				break;
			case R.id.linPersonone:
				//家人1点击事件
				//判断是否已经有成员
				String isHaveMem1=tvponen.getText().toString().trim();
				if(isHaveMem1==null||isHaveMem1=="")
				{
					Intent persononeIntent=new Intent();
					persononeIntent.setClass(context, ChooseMemberActivity.class);
					persononeIntent.putExtra("id", 1);
					startActivityForResult(persononeIntent, CHOOSE_CONTACT_PERSON);
				}else
				{
					//弹出框
					if(memOptionDialog==null)
					{
						memOptionDialog=new MemOptionDialog(context,R.style.dialog,1);
						memOptionDialog.show();	
					}else if(!memOptionDialog.isShowing())
					{
						memOptionDialog.show();
					}
					memOptionDialog.setNameText(tvpone.getText().toString());
					memOptionDialog.setNumberText(tvponen.getText().toString());
					memOptionDialog.setiTellToJump(FirstFragment.this);
					memOptionDialog.setId(1);
				}
				
				break;
			case R.id.linPersontwo:
				//家人2点击事件
				//判断是否已经有成员
				String isHaveMem2=tvptwon.getText().toString().trim();
				if(isHaveMem2==null||isHaveMem2=="")
				{
					Intent persontwoIntent=new Intent();
					persontwoIntent.setClass(context, ChooseMemberActivity.class);
					persontwoIntent.putExtra("id", 2);
					startActivityForResult(persontwoIntent, CHOOSE_CONTACT_PERSON);
				}else
				{
					//弹出框
					if(memOptionDialog==null)
					{
						memOptionDialog=new MemOptionDialog(context,R.style.dialog,2);
						memOptionDialog.show();	
					}else if(!memOptionDialog.isShowing())
					{
						memOptionDialog.show();
					}
					memOptionDialog.setNameText(tvptwo.getText().toString());
					memOptionDialog.setNumberText(tvptwon.getText().toString());
					memOptionDialog.setiTellToJump(FirstFragment.this);
					memOptionDialog.setId(2);
				}
				
				break;
			case R.id.linPersonthree:
				//家人3点击事件
				//判断是否已经有成员
				String isHaveMem3=tvpthreen.getText().toString().trim();
				if(isHaveMem3==null||isHaveMem3=="")
				{
					Intent personthreeIntent=new Intent();
					personthreeIntent.setClass(context, ChooseMemberActivity.class);
					personthreeIntent.putExtra("id", 3);
					startActivityForResult(personthreeIntent, CHOOSE_CONTACT_PERSON);
				}else
				{
					//弹出框
					if(memOptionDialog==null)
					{
						memOptionDialog=new MemOptionDialog(context,R.style.dialog,3);
						memOptionDialog.show();	
					}else if(!memOptionDialog.isShowing())
					{
						memOptionDialog.show();
					}
					memOptionDialog.setNameText(tvpthree.getText().toString());
					memOptionDialog.setNumberText(tvpthreen.getText().toString());
					memOptionDialog.setiTellToJump(FirstFragment.this);
					memOptionDialog.setId(3);
				}
				
				break;

			default:
				break;
			}
		}
	};	
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==CHOOSE_CONTACT_PERSON)
		{
			if(resultCode==Activity.RESULT_OK)
			{
				//获取设定的家庭成员
				getMemberInfo(1,2,3);
			}
		}else if(requestCode==CHOOSE_WEATHER_CITY)
		{
			if(resultCode==Activity.RESULT_OK)
			{
				//更新所选择的城市天气
				handler.sendEmptyMessage(REFRESH_WEATHER);
			}
		}
	}

	//获取在主界面显示的成员信息
	private void getMemberInfo(int... args) {
		//SharedPreferences sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();
		for(int i=0;i<args.length;i++)
		{
			String name=sharedPreferences.getString("phoneName"+args[i],"");
			String number=sharedPreferences.getString("phoneNum"+args[i],"");
			if(args[i]==1)
			{
				if(name!="")
				   tvpone.setText(name);
				tvponen.setText(number);
			}else if(args[i]==2)
			{
				if(name!="")
				   tvptwo.setText(name);
				 tvptwon.setText(number);
			}else if(args[i]==3)
			{
				if(name!="")
				  tvpthree.setText(name);
				tvpthreen.setText(number);
			}
				
		}
		
	}

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
        		tvDate.setText(getDate());
            }else if(action.equals(Intent.ACTION_TIMEZONE_CHANGED))
            {
        		tvTime.setText(getTime());
        		tvDate.setText(getDate());
            }
        }  
          
    };  
    
    /**
     * 注册系统时间、日期改变的广播
     */
    public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(Intent.ACTION_DATE_CHANGED); //日期改变
        myIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);//每秒改变一次
        myIntentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟改变一次
        myIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//时区改变
        
        //注册广播        
        context.registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }
    
    //绑定Activity
	@Override
	public void onAttach(Activity activity) {
		Log.e("onAttach","onAttach--");
		context=activity;
		super.onAttach(activity);
	}

	//回调函数 家庭成员选择替换操作的调用函数
	@Override
	public void jumpToChooseMemActivity(int id) {
		Intent personthreeIntent=new Intent();
		personthreeIntent.setClass(context, ChooseMemberActivity.class);
		personthreeIntent.putExtra("id", id);
		startActivityForResult(personthreeIntent, CHOOSE_CONTACT_PERSON);
	}

	//更新人员
	@Override
	public void tellToRefreshMember() {
		getMemberInfo(1,2,3);
	}

	//编辑完成回调函数
	@Override
	public void createComplete() {
		//编辑完成操作
		getMemberInfo(1,2,3);
	}

	//通知打开编辑弹出框
	@Override
	public void tellToOpenCreateDialog(int id,String name1,String number1) {
		CreateNewContactDialog createdialog=new CreateNewContactDialog(context,R.style.dialog,id);				
		createdialog.show();
		createdialog.setiTellCreateContactComplete(FirstFragment.this);//注册回调接口
		createdialog.setNameNumber(name1, number1);
	}	
}
