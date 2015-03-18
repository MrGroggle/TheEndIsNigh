package com.example.theendisnigh;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class Player extends Collidable implements MovedSubscriber
{
    public boolean m_shouldCreateBullet = false;
    public boolean m_shouldPause = false;
    public long m_currentScore = 0l;
    public Mutator[] m_poisonCollide;
    private final int NUM_COLLIDERS = 3;
    private boolean m_moving = false;
    private boolean m_shooting = false;
    private Sprite m_playerSprite;
    private Bitmap m_playerImage;
    private int m_health = 3;
    private final long SHOOT_PERIOD = 200L; // Adjust to suit timing. We could alter this depending on what weapons the player has
    private long lastTime = System.currentTimeMillis() - SHOOT_PERIOD;
    private Mutator m_currentMutator;

	public Player()
	{
		super();
	}

	public Player(float xPos, float yPos)
	{
		super(xPos, yPos);
        Vector2F m_startPos = new Vector2F(0, 0);
        m_startPos.x = xPos;
        m_startPos.y = yPos;
		m_radius = 32f;
        MOVEMENT_SPEED = 10f;
        m_isActive = true;
        m_poisonCollide = new Mutator[NUM_COLLIDERS];
        for(int i = 0; i < NUM_COLLIDERS; i++)
        {
            m_poisonCollide[i] = new Mutator(Mutator.MutatorType.POISON);
            m_poisonCollide[i].m_isActive = false;
        }
        m_currentMutator = new Mutator(Mutator.MutatorType.SHIELD);
        m_currentMutator.activate();
	}

    public void setSprite(Bitmap s)
    {
        m_playerImage = Bitmap.createScaledBitmap(s, (int)m_radius*2, (int)m_radius*2, true);
    }

    public boolean playerHit()
    {
        m_isActive = false;
        m_health--;
        m_velocity.x = 0;
        m_velocity.y = 0;
        return m_health <= 0;
    }
    public int getHealth()
    {
        return m_health;
    }
	public void onMoved(PointF movement, float angle)
	{
        m_shouldPause = false;
        m_moving = true;
        if(m_isActive) {
            super.setVelocity(movement.x, movement.y);
        }
        if(!m_shooting)
            super.setRotation(angle);
	}

	public void onShoot(PointF movement, float angle)
	{
        m_shouldPause = false;
        m_shooting = true;
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

	public void onReleased(){}
	
	public void onCentred(int type)
	{
        if(type == 0)
            m_moving = false;
        else if(type == 1)
            m_shooting = false;

        if(!m_moving && !m_shooting && m_isActive)
            m_shouldPause = true;
	}

	public void draw(Paint p, Canvas c)
	{
        for(int i = 0; i < NUM_COLLIDERS; i++)
        {
            if(m_poisonCollide[i].m_isActive)
            {
                m_poisonCollide[i].draw(p,c);
            }
        }
		if(m_isActive)
        {
            if(m_currentMutator.m_isActive)
            {
                m_currentMutator.draw(p,c);
            }
            c.save();
            c.rotate((float) Math.toDegrees(m_rotation), m_position.x, m_position.y);
            c.drawBitmap(m_playerImage, m_position.x-m_playerImage.getWidth()/2, m_position.y-m_playerImage.getHeight()/2, null);
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
        for(int i = 0; i < NUM_COLLIDERS; i++)
        {
            if(m_poisonCollide[i].m_isActive)
            {
                m_poisonCollide[i].updateMutator();
            }
        }
        if(m_currentMutator.m_isActive)
        {
            m_currentMutator.updatePos(this);
            if(m_currentMutator.updateMutator())
            {
                for(int i = 0; i < NUM_COLLIDERS; i++)
                {
                    if(!m_poisonCollide[i].m_isActive)
                    {
                        m_poisonCollide[i].setPosition(m_position.x, m_position.y);
                        m_poisonCollide[i].activate();
                        break;
                    }
                }
            }
        }
	}

    public PointF getRadialPosition()
    {
        return new PointF(m_position.x + m_radius * (float)Math.cos(m_rotation),m_position.y + m_radius * (float)Math.sin(m_rotation));
    }
    public float getAngle()
    {
        return m_rotation;
    }
    public void setPlayerMutator(Mutator.MutatorType m)
    {
        m_currentMutator.setType(m);
        m_currentMutator.activate();
        m_currentMutator.setPosition(m_position.x, m_position.y);
    }
    public Mutator getMutator()
    {
        return m_currentMutator;
    }

    public boolean hasActiveShield()
    {
        return m_currentMutator.m_isActive && m_currentMutator.getType() == Mutator.MutatorType.SHIELD;
    }
}
