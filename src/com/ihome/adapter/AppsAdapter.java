package com.ihome.adapter;

import java.util.List;

import com.ihome.easylauncher.R;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppsAdapter extends BaseAdapter {

	private Context context;
	private List<ResolveInfo> apps;
	LayoutInflater inflater;
	
	public AppsAdapter(Context context,List<ResolveInfo> apps)
	{
		this.context=context;
		this.apps=apps;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return apps.size();
	}

	@Override
	public Object getItem(int arg0) {
		return apps.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		final ViewHolder holder;
		final ResolveInfo info=apps.get(position);
		if(info!=null)
		{
			if(v==null)
			{
				v=inflater.inflate(R.layout.apps_gridview_item, null);
				holder=new ViewHolder();
				holder.ivHead=(ImageView) v.findViewById(R.id.ivHeadico);
				holder.tvName=(TextView) v.findViewById(R.id.tvName);
				v.setTag(holder);
			}else
			{
				holder=(ViewHolder) v.getTag();
			}
			//Log.e("----name----", info.activityInfo.name);
			//Log.e("----packageName----", info.activityInfo.packageName);
			holder.tvName.setText(info.loadLabel(context.getPackageManager()));
			holder.ivHead.setImageDrawable(info.activityInfo.loadIcon(context.getPackageManager()));
		}
		
		return v;
	}
	
	class ViewHolder
	{
		ImageView ivHead;
		TextView tvName;
	}

}
