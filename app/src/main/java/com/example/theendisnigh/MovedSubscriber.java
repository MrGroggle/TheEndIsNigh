package com.example.theendisnigh;

import android.graphics.PointF;

public interface MovedSubscriber 
{
	
        public void onMoved(PointF movement, float angle);
        public void onShoot(PointF movement, float angle);
        public void onReleased();
        public void onCentred(int pointerID);
        
}