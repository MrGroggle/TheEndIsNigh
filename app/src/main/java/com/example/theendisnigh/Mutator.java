package com.example.theendisnigh;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Harry on 16/03/2015.
 */

public class Mutator extends Collidable {
    public enum MutatorType {
        FIRE,
        POISON_SOURCE,
        FREEZE,
        SHIELD,

        COUNT,
        POISON;
    }

    private Timer m_timer;
    private double m_duration;
    private double m_pulse = 0.0;
    private int m_damage;
    private boolean m_canMove;
    private boolean m_canRotate = false;
    private Bitmap m_mutatorImage;
    private MutatorType m_type;
    private int m_pulseCounter = 1;

    public Mutator(MutatorConfig config)
    {
        m_timer = new Timer();
        m_rotation = 270.f;
        setFromConfig(config);
    }

    public void setFromConfig(MutatorConfig config)
    {
        m_type = config.m_type;
        m_mutatorImage = config.m_mutatorImage;
        m_damage = config.m_damage;
        m_radius = config.m_radius;
        m_canMove = config.m_canMove;
        m_canRotate = config.m_canRotate;
        m_duration = config.m_duration;
        m_pulse = config.m_pulse;
    }

    public boolean updateMutator()
    {
        boolean generateMutator = false;

        if(m_isActive) {
            if(m_canRotate) {
                updateRotation(0.5f);
                super.update();
            }
            m_timer.startTimer();
            if (m_timer.getStartTimerMillis() >= m_pulse * m_pulseCounter) {
                m_pulseCounter++;
                if (m_type == MutatorType.POISON_SOURCE)
                {
                    generateMutator = true;
                }
            }
            if (m_timer.getStartTimerMillis() >= m_duration) {
                m_timer.stopTimer();
                m_isActive = false;
                m_pulseCounter = 0;
            }
        }else
        {
            m_timer.stopTimer();
        }
        return generateMutator;
    }

    public void draw(Paint p, Canvas c)
    {
        if(m_isActive) {
            p.setStrokeWidth(3);
            p.setColor(Color.BLUE);
            p.setAlpha(125);
            c.save();
            c.rotate((float) Math.toDegrees(m_rotation), m_position.x, m_position.y);
            if(m_mutatorImage != null)
            {
                c.drawBitmap(m_mutatorImage, m_position.x - m_mutatorImage.getWidth()/2, m_position.y-m_mutatorImage.getHeight()/2, null);
            }
            else
            {
                c.drawCircle(m_position.x, m_position.y, m_radius, p);
            }
            c.restore();
            p.setAlpha(255);
        }
    }

    public void updatePos(Collidable p)
    {
        if(m_canMove && m_isActive)
        {
            setPosition(p.m_position.x, p.m_position.y);
        }
    }

    public int getDamage()
    {
        return m_damage;
    }
    public MutatorType getType()
    {
        return m_type;
    }
}
