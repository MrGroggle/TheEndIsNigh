package com.example.theendisnigh;

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

    private long m_lastTime;
    private long m_duration;
    private long m_pulse = 0L;
    private int m_damage;
    private boolean m_canMove;
    private int m_colour;
    private MutatorType m_type;

    public Mutator(MutatorType type)
    {
        setType(type);
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
                break;
            case POISON_SOURCE:
                m_duration = 10000L;
                m_pulse = 3300L;
                m_damage = 0;
                m_radius = 20;
                m_canMove = true;
                m_colour = Color.argb(125, 0, 255, 0);
                break;
            case POISON:
                m_duration = 7500L;
                m_damage = 1;
                m_radius = 100;
                m_canMove = false;
                m_colour = Color.argb(125, 0, 255, 0);
                break;
            case FREEZE:
                m_duration = 500L;
                m_damage = 5;
                m_radius = 250;
                m_canMove = false;
                m_colour = Color.argb(125, 0, 255, 255);
                break;
            case SHIELD:
                m_radius = 50;
                m_duration = 3000L;
                m_damage = 10;
                m_canMove = true;
                m_colour = Color.argb(125, 0, 0, 255);
                break;
            default:
                m_duration = 0L;
                m_damage = 0;
                m_colour = Color.argb(0,0,0,0);
                break;
        }

    }
    public void activate()
    {
        m_isActive = true;
        m_lastTime = System.currentTimeMillis();
    }
    public boolean updateMutator()
    {
        boolean generatePoison = false;
        if(m_isActive) {
            long currTime = System.currentTimeMillis();
            if ((currTime - m_lastTime) >= m_pulse) {
                m_totalTime += (currTime - m_lastTime);

                m_lastTime = currTime;
                if (m_type == MutatorType.POISON_SOURCE) {
                    generatePoison = true;
                }
                if (m_totalTime > m_duration) {
                    m_totalTime = 0L;
                    m_isActive = false;
                }
            }
        }
        return generatePoison;
    }

    public void draw(Paint p, Canvas c)
    {
        if(m_isActive) {
            p.setStrokeWidth(3);
            p.setColor(m_colour);
            p.setAlpha(125);
            c.save();
            c.drawCircle(m_position.x, m_position.y, m_radius, p);
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
