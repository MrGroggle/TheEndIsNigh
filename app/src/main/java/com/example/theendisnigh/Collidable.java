package com.example.theendisnigh;

import android.graphics.PointF;

abstract class Collidable 
{
	protected float MOVEMENT_SPEED;
	protected float m_radius;
	protected PointF m_position = new PointF(0f,0f);	//Position in space on the screen of the player
	protected float m_rotation;	//Angle in degrees where player is facing
	protected PointF m_velocity = new PointF(0f,0f);
    protected boolean m_isActive;
	
	public Collidable()
	{
		m_rotation = 0f;
        MOVEMENT_SPEED = 1f;
	}
	public Collidable(float xPos, float yPos)
	{
		m_position.x = xPos;
		m_position.y = yPos;
		m_rotation = 0;
        MOVEMENT_SPEED = 1f;
	}
	
	public void setPosition(float xPos, float yPos)
	{
		m_position.set(xPos, yPos);
	}
	public void setMovementSpeed(float newSpeed)
    {
        MOVEMENT_SPEED = newSpeed;
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
	
	public void setVelocity(float xSpeed, float ySpeed)
	{
		m_velocity.x = xSpeed;
		m_velocity.y = ySpeed;
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

    public boolean checkCollision(Collidable c)
    {
        float xPos = (this.m_position.x - c.m_position.x) * (this.m_position.x - c.m_position.x);
        float yPos = (this.m_position.y - c.m_position.y) * (this.m_position.y - c.m_position.y);
        float rad = (this.m_radius + c.m_radius) * (this.m_radius + c.m_radius);
        return (xPos + yPos <= rad);
    }

    public PointF getAdjustedVelocity()
    {
        return new PointF(m_velocity.x * MOVEMENT_SPEED, m_velocity.y * MOVEMENT_SPEED);
    }
}
