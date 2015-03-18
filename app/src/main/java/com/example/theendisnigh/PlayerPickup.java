package com.example.theendisnigh;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Harry on 17/03/2015.
 */
public class PlayerPickup extends Collidable
{
    private Mutator.MutatorType m_type;

    PlayerPickup(float xPos, float yPos, Mutator.MutatorType type)
    {
        super(xPos, yPos);
        m_radius = 15;
        m_isActive = false;
        m_type = type;
    }
    public void setMutator(Mutator.MutatorType type)
    {
        m_type = type;
    }

    public void onPickup(Player p)
    {
        if(!p.getMutator().m_isActive) {
            p.setPlayerMutator(m_type);
            int newType = new Random().nextInt(Mutator.MutatorType.COUNT.ordinal());
            setMutator(Mutator.MutatorType.fromInt(newType));
            m_isActive = false;
        }
    }

    public void draw(Paint p, Canvas c)
    {
        if(m_isActive) {
            p.setStrokeWidth(3);
            p.setColor(getColour());
            c.save();
            c.drawCircle(m_position.x, m_position.y, m_radius, p);
            c.restore();
        }
    }

    private int getColour()
    {
        if(m_type == Mutator.MutatorType.FIRE)
        {
            return Color.RED;
        }
        else if(m_type == Mutator.MutatorType.POISON_SOURCE)
        {
            return Color.GREEN;
        }
        else if(m_type == Mutator.MutatorType.FREEZE)
        {
            return Color.CYAN;
        }
        else if(m_type == Mutator.MutatorType.SHIELD)
        {
            return Color.BLUE;
        }
        return Color.BLACK;
    }
}
