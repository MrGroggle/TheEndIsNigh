package com.example.theendisnigh;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

//@SuppressLint("WrongCall")//Needed to suppress the lint error
public class ScreenView extends SurfaceView implements Runnable
{
	SurfaceHolder holder;
	private boolean ok = false;
	Thread t = null;
	Paint paint = new Paint();

	//Width and height of screen
	private int m_width;
	private int m_height;
	private int m_xPosition = 0;
	private int m_yPosition = 0;

	//Define the frame rate 
	private final int FRAME_RATE = 10;

	public ScreenView(Context context, int w, int h) 
	{
		super(context);
		holder = getHolder();        
		
		this.m_width = w;
		this.m_height = h;
		SetScreenDims(w,h);
	}  
	public ScreenView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		holder = getHolder();
	}  


	protected void onDraw(Canvas canvas) 
	{
		Canvas c = holder.lockCanvas(); //Lock canvas, paint canvas, unlock canvas
		canvas.drawARGB(0, 0, 0, 0); //Add a background colour
		Update();
		Draw(canvas);	
		holder.unlockCanvasAndPost(c);
	}

	@Override
	public void run() 
	{
		//Remove conflict between the UI thread and the game thread.
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

		while (ok == true)
		{
			//perform canvas drawing
			if(!holder.getSurface().isValid())
			{//if surface is not valid
				continue;//skip anything below it
			}			
			this.invalidate();
			
		}
	}

	public void Pause()
	{
		ok = false;
		while(true)
		{
			try
			{
				t.join();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			break;
		}
		t = null;
	}

	public void Resume()
	{
		ok = true;
		t = new Thread(this);
		t.start();
	}

	public void Update()
	{


	}
	public void SetScreenDims(int w, int h)
	{		

	}
	public void Draw(final Canvas c)
	{

	}

	public void UpdatePos(float xPos,float yPos)
	{


	}

}
