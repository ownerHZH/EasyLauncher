package com.ihome.adapter;

import java.util.List;

import com.ihome.entity.SmsInfo;
import com.ihone.easylauncher.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

 public class SmsListAdapter extends BaseAdapter {
        private LayoutInflater layoutinflater;
        List<SmsInfo> infos;
        public SmsListAdapter(Context c,List<SmsInfo> infos) {
        	this.infos=infos;
            layoutinflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return infos.size();
        }
        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 ViewHolder holder;
			 SmsInfo smsInfo=infos.get(position);
			 if (convertView == null) {
				    holder=new ViewHolder();
				    convertView = layoutinflater.inflate(R.layout.smsitem, null);
				    holder.smsBody=(TextView) convertView.findViewById(R.id.TextView_SmsBody);
				    holder.smsName=(TextView) convertView.findViewById(R.id.TextView_SmsName);
				    holder.smsDate=(TextView) convertView.findViewById(R.id.TextView_SmsDate);
				    convertView.setTag(holder);
	            }else
	            {
	            	holder=(ViewHolder) convertView.getTag();
	            }
			 holder.smsBody.setText(smsInfo.getSmsbody());
			 if(smsInfo.getName()!=null)
			 {
				 holder.smsName.setText(smsInfo.getName()+"("+smsInfo.getPhoneNumber()+")"); 				 
			 }else
			 {
				 holder.smsName.setText(smsInfo.getPhoneNumber());
			 }
			 holder.smsDate.setText(smsInfo.getDate());
			 
			 
	         return convertView;
		}
		
		class ViewHolder
		{
			TextView smsName;
			TextView smsBody;
			TextView smsDate;
		}
    }
