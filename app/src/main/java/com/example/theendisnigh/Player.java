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
    private int m_lives = 3;
    private int m_bonusLives = 1;
    private final double SHOOT_PERIOD = 200.0; // Adjust to suit timing. We could alter this depending on what weapons the player has
    //private long lastTime = System.nanoTime()/1000 - SHOOT_PERIOD;
    private Mutator m_currentMutator;
    private Timer m_timer;

	public Player()
	{
		super();
	}

	public Player(float xPos, float yPos, MutatorConfig poisonConfig, MutatorConfig shieldConfig)
	{
		super(xPos, yPos);
        Vector2F m_startPos = new Vector2F(0, 0);
        m_startPos.x = xPos;
        m_startPos.y = yPos;
		m_radius = 32f;
        MOVEMENT_SPEED = 10f;
        m_isActive = true;
        m_timer = new Timer();
        m_poisonCollide = new Mutator[NUM_COLLIDERS];
        for(int i = 0; i < NUM_COLLIDERS; i++)
        {
            m_poisonCollide[i] = new Mutator(poisonConfig);
            m_poisonCollide[i].m_isActive = false;
        }
        m_currentMutator = new Mutator(shieldConfig);
        m_currentMutator.m_isActive = true;
	}

    public void setSprite(Bitmap s)
    {
        m_playerImage = Bitmap.createScaledBitmap(s, (int)m_radius*2, (int)m_radius*2, true);
    }
    public boolean playerHit()
    {
        m_isActive = false;
        m_lives--;
        m_velocity.x = 0;
        m_velocity.y = 0;
        m_currentMutator.m_isActive = false;
        if(m_lives <= 0)
        {
            return true;
        }
        return false;
    }
    public int getHealth()
    {
        return m_lives;
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
            m_timer.startTimer();
            if(m_timer.getStartTimerMillis() >= SHOOT_PERIOD)
            {
                m_timer.stopTimer();
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
                        m_poisonCollide[i].m_isActive = true;
                        break;
                    }
                }
            }
        }
        checkBonusLives();
	}
    private void checkBonusLives()
    {
        if(m_currentScore >= 10000 * m_bonusLives)
        {
            m_bonusLives++;
            m_lives++;
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

    public void setMutatorFromConfig(MutatorConfig config)
    {
        m_currentMutator.setFromConfig(config);
        m_currentMutator.m_isActive = true;
        m_currentMutator.setPosition(m_position.x, m_position.y);
    }
    public Mutator getMutator()
    {
        return m_currentMutator;
    }

    public boolean hasInvulnerability()
    {
        return m_currentMutator.m_isActive && (m_currentMutator.getType() == Mutator.MutatorType.SHIELD || m_currentMutator.getType() == Mutator.MutatorType.FIRE);
    }
}
