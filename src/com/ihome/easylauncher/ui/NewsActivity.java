package com.ihome.easylauncher.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ihome.adapter.NewsAdapter;
import com.ihome.entity.NewsEntity;
import com.ihome.utils.HttpUtil;
import com.ihome.easylauncher.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NewsActivity extends Activity implements OnRefreshListener2<ListView> {

	PullToRefreshListView lvNews;
	List<NewsEntity> newsEntity=new ArrayList<NewsEntity>();
	NewsAdapter adapter;
	Context context;
	int page=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		context=NewsActivity.this;
		setContentView(R.layout.activity_news);
		lvNews=(PullToRefreshListView) findViewById(R.id.lvNews);
		adapter=new NewsAdapter(context, newsEntity);
		lvNews.setAdapter(adapter);
		lvNews.setMode(Mode.BOTH);
		lvNews.getLoadingLayoutProxy().setPullLabel("下拉刷新");
		lvNews.getLoadingLayoutProxy().setLastUpdatedLabel("正在玩命加载...");
		lvNews.getLoadingLayoutProxy().setRefreshingLabel("加载中...");
		lvNews.getLoadingLayoutProxy().setReleaseLabel("放开加载");
		getNewsListString(page);//获取第一页的数据
		
		lvNews.setOnItemClickListener(itemClicklistener);
		lvNews.setOnRefreshListener(this);
	}
	
	private void getNewsListString(int page)
	{
		String urlString="http://news.baidu.com/ns?word=%E5%9B%BD%E5%86%85&tn=newsfcu&from=news&cl="+page+"&rn=20&ct=0";
		HttpUtil.get(urlString,new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				    String respString="";
					try {
						respString=new String(arg2,"gbk");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					formateEntity(respString);
			}						
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Log.e("失败 arg0==", ""+arg0);
			}
		});
	}
	
	private void formateEntity(String respString) {
		newsEntity.clear();
		String[] dateString=respString.split("<div");
		if(dateString!=null&&dateString.length>=2)
		{
			String realStr=dateString[2];
			if(realStr!=null&&realStr!="")
			{
				String[] perP=realStr.split("<br>");
				if(perP!=null&&perP.length>0)
				{
					for(int i=0;i<perP.length;i++)
					{
						String dcs=perP[i].trim();
						if(dcs!=null&&!dcs.trim().equals(""))
						{							
							Document doc = Jsoup.parse(dcs);//解析HTML字符串返回一个Document实现
							Element link = doc.select("a").first();//查找第一个a元素
							String href=link.attr("href");
							String atext=link.text();
							link = doc.select("span").first();//查找第一个span元素
							String spantext=link.text();
							NewsEntity en=new NewsEntity(href,atext,spantext);
							newsEntity.add(en);
						}						
					}						
				}
			}
		}
		adapter.notifyDataSetChanged();
		lvNews.onRefreshComplete();
	}
	
	private OnItemClickListener itemClicklistener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//新闻详细点击事件
			Intent i=new Intent();
			i.putExtra("url", newsEntity.get(position).getUrl());
			i.setClass(context, NewsDetailActivity.class);
			startActivity(i);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_news, menu);
		return false;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> arg0) {
		page=0;
		getNewsListString(0);
		//Log.e("PullDown页数--", page+"");
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> arg0) {
		page++;
		getNewsListString(page);
		//Log.e("PullUp页数--", page+"");
	}

}
