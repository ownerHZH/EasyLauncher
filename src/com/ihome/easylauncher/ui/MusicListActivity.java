package com.ihome.easylauncher.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ihome.adapter.MusicAdapter;
import com.ihome.entity.MusicEntity;
import com.ihome.easylauncher.R;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Albums;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MusicListActivity extends Activity {

	Context context;
	ListView lvListView;
	Button btnButton;
	List<MusicEntity> musicEntityList=new ArrayList<MusicEntity>();
	MusicAdapter adapter;
	boolean isAlbum=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context=MusicListActivity.this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);
		lvListView=(ListView) findViewById(R.id.lvList);
		btnButton=(Button) findViewById(R.id.btnRefresh);
		
		adapter=new MusicAdapter(context, musicEntityList);
		lvListView.setAdapter(adapter);
		
		btnButton.setOnClickListener(relistclick);
		
		lvListView.setOnItemClickListener(listitemclick);
		
		getAlbums();//获取列表
	}
	
	private OnItemClickListener listitemclick=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MusicEntity me=musicEntityList.get(position);
			if(isAlbum)//是专辑就执行
			{
				getMedias(me.getId());
			}else
			{
				//打开音乐播放器
				String mediaUri = me.getUrl();  
                String type = me.getType();  
                Uri data = Uri.fromFile(new File(mediaUri));  
                Intent intent = new Intent(Intent.ACTION_VIEW);  
                intent.setDataAndType(data, type);  
                startActivity(intent);
			}
		}
	};
	
	private OnClickListener relistclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			getAlbums();
		}		
	};
	
	//获取某个Albums下对应的medias  
    public void getMedias(String albumId){  
    	isAlbum=false;
		musicEntityList.clear();
        String[] columns = new String[]{  
                Audio.Media._ID,  
                Audio.Media.DATA,  
                Audio.Media.DISPLAY_NAME,  
                Audio.Media.MIME_TYPE  
        };  
        String selection = Audio.Media.ALBUM_ID + "=?";  
        String[] selectionArgs = new String[]{  
                albumId+""  
        };   
        Cursor cursor = getContentResolver().query(Audio.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, Audio.Media.TITLE);
        if(cursor!=null)
        {
        	while(cursor.moveToNext())
        	{
        		String id=cursor.getString(0);
        		String data=cursor.getString(1);
        		String name=cursor.getString(2);
        		String type=cursor.getString(3);
        		MusicEntity me=new MusicEntity(id, name, data, type);
        		musicEntityList.add(me);
        	}
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
	
	private void getAlbums() {
		isAlbum=true;
		musicEntityList.clear();
		 String[] columns = new String[]{  
	                Albums._ID,  
	                Albums.ALBUM  
	        };
		Cursor cursor = getContentResolver().query(Albums.EXTERNAL_CONTENT_URI, columns, null, null, Albums.DEFAULT_SORT_ORDER);
		if(cursor!=null)
		{
			while(cursor.moveToNext())
			{
				String id=cursor.getString(0);
				String album=cursor.getString(1);
				MusicEntity me=new MusicEntity(id,album);
				musicEntityList.add(me);
			}
		}
		cursor.close();
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_music_list, menu);
		return false;
	}

}
