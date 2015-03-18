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

        private static MutatorType[] values = null;
        public static MutatorType fromInt(int i) {
            if(MutatorType.values == null) {
                MutatorType.values = MutatorType.values();
            }
            return MutatorType.values[i];
        }
    }

    private static long m_totalTime = 0L;
    private static long m_pulseTime = 0L;

    private long m_lastTime;
    private long m_duration;
    private long m_pulse = 0L;
    private int m_damage;
    private boolean m_canMove;
    private int m_colour;
    private Bitmap m_mutatorImage;
    private Bitmap m_fireImage;
    private Bitmap m_iceImage;
    private Bitmap m_poisonImage;
    private MutatorType m_type;
    private int m_pulseCounter;
    private int m_numberOfPulses;

    public Mutator(MutatorType type)
    {
        setType(type);
    }

    public void setFromConfig(MutatorConfig config)
    {
        m_mutatorImage = config.m_mutatorImage;
        m_damage = config.m_damage;
        m_radius = config.m_radius;
        m_canMove = config.m_canMove;
        m_duration = config.m_duration;
        m_pulse = config.m_pulse;
        m_numberOfPulses = (int)Math.floor(config.m_duration / config.m_pulse);
    }
    public void setType(MutatorType type)
    {
        m_type = type;
        m_isActive = false;
        switch(type) {
            case FIRE:
                m_duration = 5000L;
                m_damage = 2;
                m_radius = 100;
                m_canMove = true;
                m_colour = Color.argb(125, 255, 0, 0);
                m_mutatorImage = (m_fireImage != null ? Bitmap.createScaledBitmap(m_fireImage, (int)m_radius*2, (int)m_radius*2, true) : null);
                break;
            case POISON_SOURCE:
                m_duration = 10000L;
                m_pulse = 3300L;
                m_damage = 0;
                m_radius = 20;
                m_canMove = true;
                m_colour = Color.argb(125, 0, 255, 0);
                m_mutatorImage = (m_poisonImage != null ? Bitmap.createScaledBitmap(m_poisonImage, (int)m_radius*2, (int)m_radius*2, true) : null);
                break;
            case POISON:
                m_duration = 7500L;
                m_damage = 3;
                m_radius = 100;
                m_canMove = false;
                m_colour = Color.argb(125, 0, 255, 0);
                m_mutatorImage = (m_poisonImage != null ? Bitmap.createScaledBitmap(m_poisonImage, (int)m_radius*2, (int)m_radius*2, true) : null);
                break;
            case FREEZE:
                m_duration = 1200L;
                m_damage = 0;
                m_radius = 250;
                m_canMove = false;
                m_colour = Color.argb(125, 0, 255, 255);
                m_mutatorImage = (m_iceImage != null ? Bitmap.createScaledBitmap(m_iceImage, (int)m_radius*2, (int)m_radius*2, true) : null);
                break;
            case SHIELD:
                m_radius = 50;
                m_duration = 3000L;
                m_damage = 10;
                m_canMove = true;
                m_colour = Color.argb(125, 0, 0, 255);
                m_mutatorImage = null;
                break;
            default:
                m_duration = 0L;
                m_damage = 0;
                m_colour = Color.argb(0,0,0,0);
                m_mutatorImage = null;
                break;
        }

    }
    public void activate()
    {
        m_isActive = true;
    }
    public boolean updateMutator()
    {
        boolean generateMutator = false;

        long currTime = System.currentTimeMillis();
        if(m_isActive) {

            m_pulseTime += (currTime - m_lastTime);
            if (m_pulseTime >= m_pulse) {
                m_totalTime += m_pulseTime;
                m_pulseTime = 0L;
                if (m_type == MutatorType.POISON_SOURCE) {
                    generateMutator = true;
                    m_pulseCounter++;
                    if(m_pulseCounter > m_numberOfPulses)
                    {
                        m_isActive = false;
                    }
                }
                if (m_totalTime > m_duration) {
                    m_totalTime = 0L;
                    m_isActive = false;
                }
            }
        }
        m_lastTime = currTime;
        return generateMutator;
    }

    public void draw(Paint p, Canvas c)
    {
        if(m_isActive) {
            p.setStrokeWidth(3);
            p.setColor(m_colour);
            p.setAlpha(125);
            c.save();
            if(m_mutatorImage != null)
            {
                c.drawBitmap(m_mutatorImage, m_position.x - m_mutatorImage.getWidth()/2, m_position.y-m_mutatorImage.getHeight()/2, null);
            }
            else
            {
                c.drawCircle(m_position.x, m_position.y, m_radius, p);
            }
            //c.drawCircle(m_position.x, m_position.y, m_radius, p);
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

    public void setMutatorImage(MutatorType m, Bitmap s)
    {
        switch (m)
        {
            case FIRE:
                m_fireImage = s;
                return;
            case FREEZE:
                m_iceImage = s;
                return;
            case POISON:
            case POISON_SOURCE:
                m_poisonImage = s;
                return;
            default:
                return;
        }
    }

}
