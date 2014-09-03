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
	private FutureTask<List<String>> future;//处理耗时任务
	SharedPreferences sharedPreferences;//所选城市偏好设置
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		weatherHelper=new WebServiceHelper();				
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_weather_city);
		context=ChooseWeatherCityActivity.this;		
		listView=(ListView) findViewById(R.id.lvCitys);
		beforeBindData("");//开始获取数据的准备  与 bindData() 一起使用
		adapter=new WeatherProvinceCityAdapter(context, list);
		listView.setAdapter(adapter);
		sharedPreferences =EasyLauncherApplication.getDefaultSharedPreferences();//偏好设置初始化
		bindData();	//获取数据 与beforeBindData() 一起使用	
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
			//城市返回的数为  重庆（57516） 要删掉后面的括号 只能是单纯的城市名，不然 查询天气出错
			String citystr=list.get(position);
			citystr=citystr.substring(0, citystr.indexOf("("));

			//存储选择的要更新天气的城市信息到SharedPreferences       	
        	SharedPreferences.Editor editor = sharedPreferences.edit();
        	editor.putString("WeatherCity",citystr);
        	editor.commit();
        	setResult(Activity.RESULT_OK);
        	finish();
		}
	};

	/**
	 * 开始获取数据的准备
	 * 参数为空就获取所有的省市
	 * 参数不为空就获取对应省市的城市列表
	 * @param province
	 */
	private void beforeBindData(String province) {
		if(future!=null)
		{
			future.cancel(true);
			future=null;
		}
		future=new FutureTask<List<String>>(new GetListCallable(province));
		new Thread(future).start();//开始执行
	}

	//获取需要的数据
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
	 * 获取省市或者城市名列表
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
