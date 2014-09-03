package com.ihome.easylauncher.ui;

import com.ihome.easylauncher.R;
import com.ihome.easylauncher.R.layout;
import com.ihome.easylauncher.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsDetailActivity extends Activity {

	Context context;
	String url="http://guonei.news.baidu.com/";
	WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		context=NewsDetailActivity.this;
		Intent rI=getIntent();
		if(rI!=null)
		{
			url=rI.getStringExtra("url");
		}
		
		setContentView(R.layout.activity_news_detail);
		webView=(WebView) findViewById(R.id.wvNewsDetail);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient(){       
            public boolean shouldOverrideUrlLoading(WebView view, String url) {       
                view.loadUrl(url);       
                return true;       
            }       
        }); 
		webView.loadUrl(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_news_detail, menu);
		return false;
	}

}
