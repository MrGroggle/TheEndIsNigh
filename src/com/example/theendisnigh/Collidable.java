package com.example.theendisnigh;

import android.graphics.PointF;

abstract class Collidable 
{
	
	protected float m_radius;
	protected PointF m_position;	//Position in space on the screen of the player
	protected float m_rotation;	//Angle in degrees where player is facing
	
	public Collidable()
	{
		m_position = new PointF(0f,0f);
		m_rotation = 0f;
	}
	
	public Collidable(float xPos, float yPos)
	{
		m_position.x = xPos;
		m_position.y = yPos;
		m_rotation = 0;
	}
	
	public void setPosition(float xPos, float yPos)
	{
		m_position.set(xPos, yPos);
	}
	
	public void setPosition(PointF pos)
	{
		m_position.set(pos);
	}
	
	public void updatePosition(float xPos, float yPos)
	{
		m_position.x += xPos;
		m_position.y += yPos;
	}
	
	public void setRotation(float newAngle)
	{
		m_rotation = newAngle;
	}
	
	public void updateRotation(float diff)
	{
		m_rotation += diff;
	}
	
	public void update()
	{
		if(m_rotation >= 360f)
		{
			m_rotation -=360f;
		}
		if(m_rotation < 0)
		{
			m_rotation +=360.f;
		}
	}
	
}
