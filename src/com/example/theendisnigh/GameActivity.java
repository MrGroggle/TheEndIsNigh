package com.example.theendisnigh;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends ActionBarActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
	}
	
	/* Import MotionEvent must be added
	public boolean onTouchEvent(MotionEvent event) {
        
        int eventaction = event.getAction();
 
        switch (eventaction) {
            case MotionEvent.ACTION_DOWN: 
                // finger touches the screen
                break;
 
            case MotionEvent.ACTION_MOVE:
                // finger moves on the screen
                xPos = event.getX();
                yPos = event.getY();
                break;
 
            case MotionEvent.ACTION_UP:   
                // finger leaves the screen
                break;
        }
 
        // tell the system that we handled the event and no further processing is required
        sv.UpdatePos(xPos, yPos);
        return true; 
    }
	
	@Override
    protected void onPause(){
        super.onPause();
        sv.Pause();
    }
     
    @Override
    protected void onResume(){
        super.onResume();
        sv.Resume();
    }*/

}
