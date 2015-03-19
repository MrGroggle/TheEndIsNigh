package com.example.theendisnigh;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

import java.util.concurrent.TimeUnit;

/**
 * Created by Harry on 06/02/2015.
 */

//@TODO: Implement separation behaviour on these so they don't all bunch together.

public class Enemy extends Collidable
{
    //private Sprite m_sprite;
    private Bitmap m_image;
    private Bitmap m_scaledImage;
    private Collidable m_target;
    private ColorFilter m_colour;
    public int m_score = 100;

    private float m_maxSpeed = 1f;
    private float m_maxForce = 0.1f;
    private float m_mass = 5f;
    private int m_health = 1;

    private boolean m_slowed = false;
    private boolean m_onFire = false;
    private boolean m_frozen = false;

    private int m_fireDamage = 0;
    private Timer m_timer;
    //private long lastTime = System.nanoTime()/1000 - 200L;

    public Enemy()
    {
        super();
    }
    public Enemy(float xPos, float yPos)
    {
        super(xPos, yPos);
        m_radius = 20f;
        m_isActive = false;
        m_timer = new Timer();
    }

    public void update()
    {
        if(m_isActive && !m_frozen)
        {
            super.update();
            moveToTarget();

            m_rotation = (float)(Math.atan2(m_velocity.x, m_velocity.y * -1) + 1.5 * Math.PI);
        }
        if(m_isActive && m_onFire)
        {
            if(m_frozen)
            {
                m_frozen = false;
            }
            if(checkDeadAfterDebuff(m_fireDamage, false)) {
                if (m_target instanceof Player) {
                    ((Player) m_target).m_currentScore += m_score;
                    m_isActive = false;
                }
            }
        }
    }
    public void setImage(Bitmap s)
    {
        m_image = s;
    }
    public void setFromConfig(EnemyConfig e)
    {
        m_mass = e.m_mass;
        m_maxSpeed = e.m_speed;
        m_maxForce = m_maxSpeed/10;
        m_score = e.m_score;
        m_radius = e.m_radius;
        m_scaledImage = Bitmap.createScaledBitmap(m_image, (int)m_radius*2, (int)m_radius*2, true);
        m_colour = new LightingColorFilter(e.m_paintTEMP, 1);
        m_health = e.m_hp;
        m_slowed = false;
        m_onFire = false;
        m_frozen = false;
    }

    private Vector2F getBehaviour()
    {
        if(m_target.m_isActive && !m_onFire)
        {
            return seek();
        }else
        {
            return flee();
        }
    }

    public void moveToTarget()
    {
        //Use Atan2 here to calculate angle to the player and set rotation
        //Seek behaviour

        Vector2F desiredVelocity = getBehaviour();
        desiredVelocity.trunc(m_maxForce);
        desiredVelocity.div(m_mass);
        desiredVelocity.add(m_velocity);
        desiredVelocity.trunc(m_maxSpeed);
        m_velocity.x = desiredVelocity.x;
        m_velocity.y = desiredVelocity.y;

        updatePosition(m_velocity.x * MOVEMENT_SPEED * (m_slowed ? 0.5f : 1.f), m_velocity.y * MOVEMENT_SPEED * (m_slowed ? 0.3f : 1.f));
    }
    private Vector2F seek()
    {
        Vector2F seekVelocity = new Vector2F(m_target.m_position.x - m_position.x, m_target.m_position.y - m_position.y);
        seekVelocity.normalise();
        seekVelocity.mul(m_maxSpeed);
        seekVelocity.sub(m_velocity);
        return seekVelocity;
    }
    private Vector2F flee()
    {
        Vector2F fleeVelocity = seek();
        fleeVelocity.negate();
        return fleeVelocity;
    }
    public void draw(Paint p, Canvas c)
    {
        if(m_isActive)
        {
            p.setStrokeWidth(3);

            p.setColorFilter(m_colour);
            c.save();
            c.rotate((float) Math.toDegrees(m_rotation), m_position.x, m_position.y);
            c.drawBitmap(m_scaledImage, m_position.x-m_image.getWidth()/2, m_position.y-m_image.getHeight()/2, p);
            c.restore();
        }
    }
    public boolean checkDeadAfterDebuff(int damage, boolean shouldSlow)
    {
        m_timer.startTimer();
        if(m_timer.getStartTimerMillis() >= 500.0)
        {
            //lastTime = currTime;
            m_health-=damage;
            slowEnemy(shouldSlow);
            if(m_health <= 0)
            {
                m_isActive = false;
            }
            m_timer.stopTimer();
        }
        return !m_isActive;
    }

    public boolean checkDeadAfterHit(int damage, boolean shouldSlow)
    {
        m_health-=damage;
        slowEnemy(shouldSlow);
        if(m_health <= 0)
        {
            m_isActive = false;
        }
        return !m_isActive;

    }
    public void setTarget(Collidable c)
    {
        m_target = c;
    }
    public void slowEnemy(boolean slow)
    {
        m_slowed = slow;
    }
    public void setFireDamage(int damage)
    {
        m_onFire = true;
        m_velocity.x = 0f;
        m_velocity.y = 0f;
        m_fireDamage = damage;
    }
    public void setFrozen()
    {
        m_frozen = true;
    }

}
