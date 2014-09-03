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
	
	private TextView tvDate;//����
	private TextView tvTime;//ʱ��
	private TextView tvAlarm;//����
	private LinearLayout linDatetime,linWeather;//���塣����
	
	private ImageView picfrom,picto;//��������ͼ�� ��--������
	private TextView city,tempeture,weather;//���С��¶ȡ�����״��
	
	private LinearLayout linCamera,linAlbum,
	          linPersonOne,linPersonTwo,linPersonThree,linApps,linContacts;
	private TextView tvpone,tvptwo,tvpthree;//������Ա����
	private TextView tvponen,tvptwon,tvpthreen;//������Ա�ĵ绰���� Ϊ���صġ�
	
	private Context context;//������
	private FutureTask<WeatherBean> future;//�����ʱ����
	private WebServiceHelper weatherHelper;
	
	private final Timer timer=new Timer(); //��ʱ��
	private TimerTask timerTask; //��ʱ����������
	
	MemOptionDialog memOptionDialog;//����������
	
	SharedPreferences sharedPreferences;
			
	private static final int REFRESH_WEATHER=0x321;//���������ı�־
	private static final int ALARM_ACTITY_REQUEST=0x322;//���������ý���
	public static final int CHOOSE_CONTACT_PERSON=0x323;//��ȡͨѶ¼�е�һ����
	public static final int CHOOSE_WEATHER_CITY=0x324;//��ת������ѡ��Ľ���
	
	//�¼��Ľ��ա�����handler
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_WEATHER:
				//����ƫ�������еĳ������� Ĭ�ϳ���Ϊ ����
				String cstr=sharedPreferences.getString("WeatherCity", "����");
				refreshWeather(cstr);//��������
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
		weatherHelper=new WebServiceHelper();//�����������
		sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();//ƫ������
		init(view);//��ʼ�������е����
		registerBoradcastReceiver();//ע��㲥������
		//refreshWeather("����");//��������
		timerTask=new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(REFRESH_WEATHER);
			}
		};
		timer.schedule(timerTask, 0, 1000*60*60*3);//������������ÿ2.5Сʱ���£�������ÿ��Сʱȥ����һ������״��
		getMemberInfo(1,2,3);//��ʼ��ȡ��ͥ��Ա��Ϣ
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
			return weatherHelper.getWeatherByCity(city);
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
				Intent weather=new Intent();
				weather.setClass(context, ChooseWeatherCityActivity.class);
				startActivityForResult(weather, CHOOSE_WEATHER_CITY);
				break;
				
			case R.id.linAlbum:
				//������¼�
			    Intent picture = new Intent();
			    picture.setType("image/*");
			    picture.setAction(Intent.ACTION_VIEW);
			    startActivity(picture);
				break;
			case R.id.linApps:
				//Ӧ�õ���¼�
				Intent appIntent=new Intent();
				appIntent.setClass(context, AppsActivity.class);
				startActivity(appIntent);
				break;
			case R.id.linCamera:
				//�������¼�
                Intent intent = new Intent(); //���������
                intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
                startActivity(intent);
				break;
			case R.id.linContacts:
				//��ϵ�˵���¼�
				Intent contactintent = new Intent();
				contactintent.setAction(Intent.ACTION_VIEW);
				contactintent.setData(ContactsContract.Contacts.CONTENT_URI);
				contactintent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivity(contactintent);
				break;
			case R.id.linPersonone:
				//����1����¼�
				//�ж��Ƿ��Ѿ��г�Ա
				String isHaveMem1=tvponen.getText().toString().trim();
				if(isHaveMem1==null||isHaveMem1=="")
				{
					Intent persononeIntent=new Intent();
					persononeIntent.setClass(context, ChooseMemberActivity.class);
					persononeIntent.putExtra("id", 1);
					startActivityForResult(persononeIntent, CHOOSE_CONTACT_PERSON);
				}else
				{
					//������
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
				//����2����¼�
				//�ж��Ƿ��Ѿ��г�Ա
				String isHaveMem2=tvptwon.getText().toString().trim();
				if(isHaveMem2==null||isHaveMem2=="")
				{
					Intent persontwoIntent=new Intent();
					persontwoIntent.setClass(context, ChooseMemberActivity.class);
					persontwoIntent.putExtra("id", 2);
					startActivityForResult(persontwoIntent, CHOOSE_CONTACT_PERSON);
				}else
				{
					//������
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
				//����3����¼�
				//�ж��Ƿ��Ѿ��г�Ա
				String isHaveMem3=tvpthreen.getText().toString().trim();
				if(isHaveMem3==null||isHaveMem3=="")
				{
					Intent personthreeIntent=new Intent();
					personthreeIntent.setClass(context, ChooseMemberActivity.class);
					personthreeIntent.putExtra("id", 3);
					startActivityForResult(personthreeIntent, CHOOSE_CONTACT_PERSON);
				}else
				{
					//������
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
				//��ȡ�趨�ļ�ͥ��Ա
				getMemberInfo(1,2,3);
			}
		}else if(requestCode==CHOOSE_WEATHER_CITY)
		{
			if(resultCode==Activity.RESULT_OK)
			{
				//������ѡ��ĳ�������
				handler.sendEmptyMessage(REFRESH_WEATHER);
			}
		}
	}

	//��ȡ����������ʾ�ĳ�Ա��Ϣ
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
        		tvDate.setText(getDate());
            }else if(action.equals(Intent.ACTION_TIMEZONE_CHANGED))
            {
        		tvTime.setText(getTime());
        		tvDate.setText(getDate());
            }
        }  
          
    };  
    
    /**
     * ע��ϵͳʱ�䡢���ڸı�Ĺ㲥
     */
    public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(Intent.ACTION_DATE_CHANGED); //���ڸı�
        myIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);//ÿ��ı�һ��
        myIntentFilter.addAction(Intent.ACTION_TIME_TICK);//ÿ���Ӹı�һ��
        myIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//ʱ���ı�
        
        //ע��㲥        
        context.registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }
    
    //��Activity
	@Override
	public void onAttach(Activity activity) {
		Log.e("onAttach","onAttach--");
		context=activity;
		super.onAttach(activity);
	}

	//�ص����� ��ͥ��Աѡ���滻�����ĵ��ú���
	@Override
	public void jumpToChooseMemActivity(int id) {
		Intent personthreeIntent=new Intent();
		personthreeIntent.setClass(context, ChooseMemberActivity.class);
		personthreeIntent.putExtra("id", id);
		startActivityForResult(personthreeIntent, CHOOSE_CONTACT_PERSON);
	}

	//������Ա
	@Override
	public void tellToRefreshMember() {
		getMemberInfo(1,2,3);
	}

	//�༭��ɻص�����
	@Override
	public void createComplete() {
		//�༭��ɲ���
		getMemberInfo(1,2,3);
	}

	//֪ͨ�򿪱༭������
	@Override
	public void tellToOpenCreateDialog(int id,String name1,String number1) {
		CreateNewContactDialog createdialog=new CreateNewContactDialog(context,R.style.dialog,id);				
		createdialog.show();
		createdialog.setiTellCreateContactComplete(FirstFragment.this);//ע��ص��ӿ�
		createdialog.setNameNumber(name1, number1);
	}	
}
