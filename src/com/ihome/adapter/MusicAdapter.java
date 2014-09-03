package com.ihome.adapter;

import java.util.List;

import com.ihome.entity.MusicEntity;
import com.ihome.easylauncher.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {

	Context context;
	List<MusicEntity> musicEntityList;
	LayoutInflater inflater;
	
	public MusicAdapter(Context context,List<MusicEntity> musicEntityList)
	{
		this.context=context;
		this.musicEntityList=musicEntityList;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return musicEntityList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicEntityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		MusicEntity me=(MusicEntity) getItem(position);
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.music_list_item, null);
			holder=new ViewHolder();
			holder.name=(TextView) convertView.findViewById(R.id.tvMName);
			convertView.setTag(holder);
		}else
		{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.name.setText(me.getName());
		return convertView;
	}
	
	class ViewHolder
	{
		TextView name;
	}

}
