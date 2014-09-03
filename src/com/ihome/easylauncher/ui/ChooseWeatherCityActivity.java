package com.ihome.easylauncher.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.ihome.adapter.WeatherProvinceCityAdapter;
import com.ihome.easylauncher.EasyLauncherApplication;
import com.ihome.service.WeatherBean;
import com.ihome.service.WebServiceHelper;
import com.ihome.easylauncher.R;
import com.ihome.easylauncher.R.layout;
import com.ihome.easylauncher.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChooseWeatherCityActivity extends Activity {

	ListView listView;
	List<String> list=new ArrayList<String>();
	WeatherProvinceCityAdapter adapter;
	private WebServiceHelper weatherHelper;
	private Context context;
	private FutureTask<List<String>> future;//�����ʱ����
	SharedPreferences sharedPreferences;//��ѡ����ƫ������
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		weatherHelper=new WebServiceHelper();				
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_weather_city);
		context=ChooseWeatherCityActivity.this;		
		listView=(ListView) findViewById(R.id.lvCitys);
		beforeBindData("");//��ʼ��ȡ���ݵ�׼��  �� bindData() һ��ʹ��
		adapter=new WeatherProvinceCityAdapter(context, list);
		listView.setAdapter(adapter);
		sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();//ƫ�����ó�ʼ��
		bindData();	//��ȡ���� ��beforeBindData() һ��ʹ��	
	}
	
	private OnItemClickListener provinceclicklistener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			beforeBindData(list.get(position));
			bindData();
		}
	};
	
	private OnItemClickListener cityclicklistener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			//���з��ص���Ϊ  ���죨57516�� Ҫɾ����������� ֻ���ǵ����ĳ���������Ȼ ��ѯ��������
			String citystr=list.get(position);
			citystr=citystr.substring(0, citystr.indexOf("("));

			//�洢ѡ���Ҫ���������ĳ�����Ϣ��SharedPreferences       	
        	SharedPreferences.Editor editor = sharedPreferences.edit();
        	editor.putString("WeatherCity",citystr);
        	editor.commit();
        	setResult(Activity.RESULT_OK);
        	finish();
		}
	};

	/**
	 * ��ʼ��ȡ���ݵ�׼��
	 * ����Ϊ�վͻ�ȡ���е�ʡ��
	 * ������Ϊ�վͻ�ȡ��Ӧʡ�еĳ����б�
	 * @param province
	 */
	private void beforeBindData(String province) {
		if(future!=null)
		{
			future.cancel(true);
			future=null;
		}
		future=new FutureTask<List<String>>(new GetListCallable(province));
		new Thread(future).start();//��ʼִ��
	}

	//��ȡ��Ҫ������
	private void bindData() {
		try {
			List<String> tempList=future.get();
			list.clear();
			list.addAll(tempList);
			adapter.notifyDataSetChanged();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ȡʡ�л��߳������б�
	 * @author owner
	 * @date 2014-08-29
	 */
	private class GetListCallable implements Callable<List<String>>
	{
        String province;
        GetListCallable(String province)
        {
        	this.province=province;
        }
		@Override
		public List<String> call() throws Exception {
			if(province==null||province.isEmpty())
			{
				listView.setOnItemClickListener(provinceclicklistener);
				return weatherHelper.getProvince();
			}else
			{
				listView.setOnItemClickListener(cityclicklistener);
				return weatherHelper.getCitys(province);
			}
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_choose_weather_city, menu);
		return false;
	}

}
