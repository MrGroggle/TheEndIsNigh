package com.example.theendisnigh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View
{
	//Well is the holder for the stick (couldn't think of a better name)
	private final Paint m_stickPaint;
	private final Paint m_wellPaint;
		
	private final int BAD_POINTER = -1;
	private int m_pointerID = BAD_POINTER;
	private MovedListener m_listener;
	
	private int m_stickRadius;
	private int m_wellRadius;
	private int m_movementLimit;
	
	//Point vals for the stick
	private PointF m_viewDims = new PointF(0f, 0f);			//View dimension
	private PointF m_viewCentre = new PointF(0f, 0f);		//Centre of view
	private PointF m_stick = new PointF(0f, 0f);			//Position of the stick (in view space)
	private PointF m_touch = new PointF(0f, 0f);			//Touch point (in view space)
	//private PointF m_offset = new PointF(0f, 0f);			//Offset from Screen space to view space
	private PointF m_adjustedCoords = new PointF(0f,0f);	//Change from view space to cartesian co-ordinates
	private PointF m_output = new PointF(0f,0f);
	private PointF m_bounds = new PointF(0f,0f);
	
	private float m_stickAngle = 0f;						//Used for firing direction
	
	private int m_padding;
	public JoystickView(Context context)
	{
		super(context);
		
		m_wellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		m_wellPaint.setStrokeWidth(1);
		m_wellPaint.setARGB(128,64,64,64);
		
		m_stickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		m_stickPaint.setStrokeWidth(1);
		m_stickPaint.setARGB(128,128,128,128);
		
		m_padding = 10;
		
	}
	public JoystickView(Context context, AttributeSet attr)
	{
		super(context,attr);
		
		m_wellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		m_wellPaint.setStrokeWidth(1);
		m_wellPaint.setARGB(128,64,64,64);
		
		m_stickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		m_stickPaint.setStrokeWidth(1);
		m_stickPaint.setARGB(128,128,128,128);
		
		m_padding = 10;
	}

	public void setMovedListener(MovedListener listener)
	{
		m_listener = listener;
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		
		super.onLayout(changed, left, top, right, bottom);
		float smallestDimension = Math.min(getMeasuredWidth(), getMeasuredHeight());
		
		m_viewDims.set(smallestDimension, smallestDimension);
		m_viewCentre.set(smallestDimension/2f, smallestDimension/2f);
		
		m_wellRadius = (int)m_viewDims.x/2 - m_padding;
		m_stickRadius = (int)(smallestDimension * 0.25f);
		m_movementLimit = (int)Math.min(m_viewCentre.x, m_viewCentre.y) - m_stickRadius;
		
		
	}
	@Override
    protected void onDraw(Canvas canvas) 
	{
        canvas.save();
        //Draw the well
        canvas.drawCircle(m_viewCentre.x, m_viewCentre.y, m_wellRadius, m_wellPaint);

        //Draw the stick
        m_stick.x = m_touch.x + m_viewCentre.x;
        m_stick.y = m_touch.y + m_viewCentre.y;
        canvas.drawCircle(m_stick.x, m_stick.y, m_stickRadius, m_stickPaint);

        canvas.restore();
    }
	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) 
		{
			case MotionEvent.ACTION_MOVE: 
			{

				Log.d("Input", "ACTION_MOVE");
				return handleMove(event);
			}     
			//All pointers up
			case MotionEvent.ACTION_CANCEL: 		
			case MotionEvent.ACTION_UP: 
			{
				
				if (m_pointerID != BAD_POINTER)
				{
					Log.d("Input", "ACTION_UP");
					//Add code for moving handle to centre?
					setPointerID(BAD_POINTER);
				}
				break;
				
			}
			//Handle non original pointer up 
			case MotionEvent.ACTION_POINTER_UP: 
			{
				if (m_pointerID != BAD_POINTER)
				{
					
					//Get pointer index by bitshifting down the index mask and action bits (from android developer site)
					final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
					final int pointerId = event.getPointerId(pointerIndex);
					if (pointerId == this.m_pointerID) 
					{
						//Add code for moving handle to centre?
						setPointerID(BAD_POINTER);
						return true;
					}
					
				}
				break;
			}
			//First pointer down
			case MotionEvent.ACTION_DOWN: {
				if (m_pointerID == BAD_POINTER)
				{
					
					float x = event.getX();
					if (x >= 0 && x < m_viewDims.x)
					{						
						setPointerID(event.getPointerId(0));
						Log.d("Input", "ACTION_DOWN: " + x);
						return true;
					}
					
				}
				break;
			}
			//Subsequent pointers down
			case MotionEvent.ACTION_POINTER_DOWN:
			{
				if (m_pointerID == BAD_POINTER)
				{
					
					//Get pointer index by bitshifting down the index mask and action bits (from android developer site)
					final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
					final int pointerId = event.getPointerId(pointerIndex);
					float x = event.getX(pointerId);
					if (x >= 0 && x < m_viewDims.x)
					{
						//Set pointer ID
						setPointerID(pointerId);
						return true;
					}
					
				}
				break;
			}
		}
		return false;
	}
	
	public void setPointerID(int newID)
	{
		this.m_pointerID = newID;
	}
	public int getPointerID()
	{
		return this.m_pointerID;
	}
	
	private boolean handleMove(MotionEvent ev) 
	{
        if ( m_pointerID != BAD_POINTER ) 
        {
            final int pointer = ev.findPointerIndex(m_pointerID);
            
            float x = ev.getX(pointer);
            float y = ev.getY(pointer);
            m_touch.set(x-m_viewCentre.x, y-m_viewCentre.y);
            limitMovement();
            calcOutputCoordinates();
            notifyListener();
            invalidate();
            
            return true;
        }
        return false;
	}
	// Constrain touch within a circle
    private void limitMovement() 
    {
        float tX = m_touch.x;
        float tY = m_touch.y;
        float touchRadius = (float)Math.sqrt((tX*tX) + (tY*tY));
        if (touchRadius > m_movementLimit)
        {
        	m_touch.set((tX/touchRadius) * m_movementLimit, (tY/touchRadius) * m_movementLimit);
        }
    }
    
    private void calcOutputCoordinates()
    {
    	    	
        //Convert from view space to Cartesian coordinates
    	m_adjustedCoords.x = m_touch.x / m_wellRadius;	//Add movement range here if required
    	m_adjustedCoords.y = m_touch.y / m_wellRadius;        
        m_stickAngle = (float)Math.atan2(m_adjustedCoords.x, m_adjustedCoords.y);

        //TODO BUILD INTO OPTIONS MENU
        /*if ( !m_invertY )
        {
        	m_adjustedCoords.y  *= -1;
        }*/
        
        m_adjustedCoords.y  *= -1;	//Invert Y-Axis
                
    	m_output.x = m_adjustedCoords.x;
    	m_output.y = m_adjustedCoords.y;
        
    }
    
    private void notifyListener()
    {
    	if(m_listener != null)
    	{
    		boolean boundsX = Math.abs(m_touch.x - m_bounds.x) >= 0.05f;	//Make sure we don't send events if the stick has barely moved
    		boolean boundsY = Math.abs(m_touch.y - m_bounds.y) >= 0.05f;
    		
    		if(boundsX || boundsY)
    		{
    			this.m_bounds.set(m_touch);
    			m_listener.onMoved(m_output, m_stickAngle);
    		}
    	}
    }
}
