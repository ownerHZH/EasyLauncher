package com.ihome.adapter;

import java.util.List;

import com.ihone.easylauncher.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeatherProvinceCityAdapter extends BaseAdapter {

	List<String> list;
	Context context;
	LayoutInflater inflater;
	public WeatherProvinceCityAdapter(Context context,List<String> list)
	{
		this.context=context;
		this.list=list;
		inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		String name=list.get(position);
		if(v==null)
		{
			holder=new ViewHolder();
			v=inflater.inflate(R.layout.weather_province_city_choose_item, null);
			holder.tvName=(TextView) v;
			v.setTag(holder);
		}else
		{
			holder=(ViewHolder) v.getTag();
		}
		holder.tvName.setText(name);
		return v;
	}
	
	class ViewHolder
	{
		TextView tvName;
	}

}
