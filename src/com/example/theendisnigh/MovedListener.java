package com.example.theendisnigh;

import android.graphics.PointF;

public interface MovedListener 
{
	
        public void onMoved(PointF movement, float angle);
        public void onReleased();
        public void onCentred();
        
}