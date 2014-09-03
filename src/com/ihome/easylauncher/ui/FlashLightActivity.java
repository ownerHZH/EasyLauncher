package com.ihome.easylauncher.ui;

import com.ihome.easylauncher.R;
import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FlashLightActivity extends Activity {

	Context context;
	LinearLayout linContent;
	ImageView ivFlashLight;
	boolean turn=false;
	 private Camera mCamera;
     private Camera.Parameters parameters;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context=FlashLightActivity.this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash_light);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		linContent=(LinearLayout) findViewById(R.id.linContent);
		ivFlashLight=(ImageView) findViewById(R.id.ivFlashLight);
		
		mCamera = Camera.open();
        parameters = mCamera.getParameters();
		
		ivFlashLight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!turn)
				{
					linContent.setBackgroundColor(color.white);
					linContent.setBackgroundColor(android.graphics.Color.parseColor("#ffffff"));
					linContent.setBackgroundColor(Color.rgb(255, 255, 255));
					 
					ivFlashLight.setImageResource(R.drawable.flashlight_on_2);	
					openLight();
				}else
				{
					linContent.setBackgroundColor(color.black);
					linContent.setBackgroundColor(android.graphics.Color.parseColor("#000000"));
					linContent.setBackgroundColor(Color.rgb(0, 0, 0));
					ivFlashLight.setImageResource(R.drawable.flashlight_off_1);	
					closeLight();
				}
				turn=!turn;			  
			}
		});
	}
	
	 /**
     * 打开手电
     * @author owner
     */
     private void openLight()
     {
         parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
         mCamera.setParameters(parameters);
         mCamera.startPreview();
     }
     
     /**
      * 关闭手电
      * @author owner
      */
      private void closeLight()
      {
          parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
          mCamera.setParameters(parameters);
      }

      @Override
      protected void onDestroy()
      {
          mCamera.release();
          super.onDestroy();
      }
      
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_flash_light, menu);
		return false;
	}

}
