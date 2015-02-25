package com.example.theendisnigh;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class Player extends Collidable implements MovedSubscriber
{

    private final long SHOOT_PERIOD = 200L; // Adjust to suit timing. We could alter this depending on what weapons the player has
    private long lastTime = System.currentTimeMillis() - SHOOT_PERIOD;
    public boolean m_shouldCreateBullet = false;
    public boolean m_killedByEnemy = false;
    private Vector2F m_startPos = new Vector2F(0,0);
    private int m_health = 3;
    public long m_currentScore = 0l;
    //private final Handler m_handler = new Handler();
	public Player()
	{
		super();
	}
    public boolean playerHit()
    {
        m_isActive = false;
        m_health--;
        m_velocity.x = 0;
        m_velocity.y = 0;
        if(m_health > 0) {
            return false;
        }
        else
        {
            return true;
        }

    }
	public Player(float xPos, float yPos)
	{
		super(xPos, yPos);
        m_startPos.x = xPos;
        m_startPos.y = yPos;
		m_radius = 20f;
        MOVEMENT_SPEED = 10f;
        m_isActive = true;
	}
	public void onMoved(PointF movement, float angle)
	{
        if(m_isActive) {
            super.setVelocity(movement.x, movement.y);
        }
        //@TODO Need to find a good way to set the walking direction
	}
	public void onShoot(PointF movement, float angle)
	{
		if(movement.length() > 0.1f && m_isActive)
		{
			super.setRotation(angle);
            long currTime = System.currentTimeMillis();

            if((currTime - lastTime) >= SHOOT_PERIOD)
            {
                lastTime = currTime;
                m_shouldCreateBullet = true;
            }
		}

	}
	
	public void onReleased()
	{
		
	}
	
	public void onCentred()
	{
		
	}
	public void draw(Paint p, Canvas c)
	{
		if(m_isActive) {
            p.setStrokeWidth(3);
            p.setColor(Color.MAGENTA);
            if (m_killedByEnemy)
                p.setColor(Color.BLACK);
            c.save();
            c.rotate((float) Math.toDegrees(m_rotation), m_position.x, m_position.y);
            c.drawRect(m_position.x - 10, m_position.y - 10, m_position.x + 10, m_position.y + 10, p);
            p.setStrokeWidth(1);
            p.setColor(Color.DKGRAY);
            //Math.PI/180.0 *
            //c.drawCircle(m_position.x + m_radius * (float)Math.cos(m_rotation), m_position.y + m_radius * (float)Math.sin(m_rotation), 5, p);
            c.drawCircle(m_position.x + m_radius, m_position.y + m_radius, 5, p);
            c.restore();
        }
	}
	
	public void update()
	{
		super.update();
		if(Math.abs(m_velocity.x) < 0.1f)
			m_velocity.x = 0f;
		if(Math.abs(m_velocity.y) < 0.1f)
			m_velocity.y = 0f;
			
		updatePosition(m_velocity.x * MOVEMENT_SPEED, m_velocity.y * MOVEMENT_SPEED);
	}

    public PointF getRadialPosition()
    {
        return new PointF(m_position.x + m_radius * (float)Math.cos(m_rotation),m_position.y + m_radius * (float)Math.sin(m_rotation));
    }
    public float getAngle()
    {
        return m_rotation;
    }

	
	
	
}
