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
        POISON,
        FREEZE,
        SHIELD,

        COUNT;

        private static MutatorType[] values = null;
        public static MutatorType fromInt(int i) {
            if(MutatorType.values == null) {
                MutatorType.values = MutatorType.values();
            }
            return MutatorType.values[i];
        }
    }

    private static long m_totalTime = 0L;
    private long m_duration;
    private long m_lastTime = System.currentTimeMillis() - m_duration;
    private long m_pulse = 0L;
    private int m_damage;
    private boolean m_canMove;
    public boolean m_isExpired = false;
    private MutatorType m_type;

    public Mutator(MutatorType type)
    {
        setType(type);
    }
    public void setType(MutatorType type)
    {
        m_type = type;
        m_isActive = true;
        switch(type) {
            case FIRE:
                m_duration = 5000L;
                m_damage = 1;
                m_radius = 50;
                m_canMove = true;
                break;
            case POISON:
                m_duration = 10000L;
                m_pulse = 3300L;
                m_damage = 2;
                m_radius = 50;
                m_canMove = false;
                break;
            case FREEZE:
                m_duration = 5000L;
                m_damage = 5;
                m_radius = 50;
                m_canMove = false;
                break;
            case SHIELD:
                m_radius = 50;
                m_duration = 2000L;
                m_damage = 10;
                m_canMove = true;
                break;
            default:
                m_duration = 0L;
                m_damage = 0;
                break;
        }
    }
    public void updateMutator()
    {
        if(m_isActive) {
            long currTime = System.currentTimeMillis();
            if ((currTime - m_lastTime) >= m_pulse) {
                m_lastTime = currTime;
                m_totalTime += (currTime - m_lastTime);

                if (m_type == MutatorType.POISON) {
                    addPoisonArea();
                }
                if (m_totalTime > m_duration) {
                    m_totalTime = 0L;
                    m_isActive = false;
                }
            }
        }

    }
    public void addPoisonArea()
    {

    }

    public void draw(Paint p, Canvas c)
    {
        if(m_isActive) {
            p.setStrokeWidth(3);
            p.setColor(Color.MAGENTA);
            p.setAlpha(125);
            c.drawCircle(m_position.x, m_position.y, m_radius, p);
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

}
