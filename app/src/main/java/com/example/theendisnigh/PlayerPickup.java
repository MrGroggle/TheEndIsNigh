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
    private Mutator m_mutator;

    PlayerPickup(float xPos, float yPos, Mutator.MutatorType type)
    {
        super(xPos, yPos);
        m_radius = 10;
        m_mutator = new Mutator(type);
        m_isActive = false;
        m_type = type;
    }
    public void setMutator(Mutator.MutatorType type)
    {
        m_mutator.setType(type);
        m_type = type;
    }

    public void onPickup(Player p)
    {
        if(p.getMutator() == null) {
            m_isActive = false;
            p.addMutator(m_mutator);


            int newType = new Random().nextInt(Mutator.MutatorType.COUNT.ordinal());
            if (newType < Mutator.MutatorType.COUNT.ordinal()) {
                setMutator(Mutator.MutatorType.fromInt(newType));
            }
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
        else if(m_type == Mutator.MutatorType.POISON)
        {
            return Color.GREEN;
        }
        else if(m_type == Mutator.MutatorType.FREEZE)
        {
            return Color.BLUE;
        }
        else
        {
            return Color.BLACK;
        }
    }
}
