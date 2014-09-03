package com.ihome.adapter;

import java.util.List;

import com.ihome.entity.NewsEntity;
import com.ihome.easylauncher.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {

	List<NewsEntity> data;
	Context context;
	LayoutInflater inflater;
	
	public NewsAdapter(Context context,List<NewsEntity> data)
	{
		this.context=context;
		this.data=data;
		inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		NewsEntity entity=(NewsEntity) getItem(position);
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.news_list_item, null);
			holder=new ViewHolder();
			holder.tvTime=(TextView) convertView.findViewById(R.id.tvTime);
			holder.tvTitle=(TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvUrl=(TextView) convertView.findViewById(R.id.tvUrl);
			convertView.setTag(holder);
		}else
		{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.tvTime.setText(entity.getTime());
		holder.tvTitle.setText(entity.getTitle());
		holder.tvUrl.setText(entity.getUrl());
		
		return convertView;
	}

	class ViewHolder
	{
		TextView tvTitle;
		TextView tvTime;
		TextView tvUrl;
	}
	
}
