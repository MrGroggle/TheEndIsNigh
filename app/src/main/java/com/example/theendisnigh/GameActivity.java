package com.example.theendisnigh;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends ActionBarActivity {
	
	private ScreenView sv;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		/* Make sure that import android.view.Window;
		import android.view.WindowManager; have been added so 
		the below will work*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);//Set full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        //Get the height and width of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
         
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.game_layout);
        
        sv = (ScreenView) findViewById(R.id.screenView);
	}
	@Override
    protected void onPause(){
        super.onPause();
        sv.pause();
    }
     
    @Override
    protected void onResume(){
        super.onResume();
        sv.resume();
    }

}
