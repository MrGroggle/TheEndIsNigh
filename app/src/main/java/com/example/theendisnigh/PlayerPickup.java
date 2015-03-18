package com.example.theendisnigh;

import android.graphics.Bitmap;
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
    private MutatorConfig m_config;
    private Bitmap m_currentImage, m_poison, m_fire, m_freeze, m_shield;

    PlayerPickup(float xPos, float yPos, Mutator.MutatorType type)
    {
        super(xPos, yPos);
        m_radius = 15;
        m_isActive = false;
        m_type = type;
    }
    public void setFromConfig(MutatorConfig config)
    {
        m_currentImage = config.m_pickupImage;
        m_config = config;

    }
    public void setMutator(Mutator.MutatorType type)
    {
        m_type = type;
        switch (type)
        {
            case SHIELD:
                m_currentImage = m_shield;
                break;
            case FREEZE:
                m_currentImage = m_freeze;
                break;
            case FIRE:
                m_currentImage = m_fire;
                break;
            case POISON:
            case POISON_SOURCE:
                m_currentImage = m_poison;
                break;
            default:
                break;
        }
    }

    public void onPickup(Player p)
    {
        if(!p.getMutator().m_isActive) {
            //p.setPlayerMutator(m_type);
            p.setMutatorFromConfig(m_config);
            m_isActive = false;
        }
    }

    public void draw(Paint p, Canvas c)
    {
        if(m_isActive) {
            p.setStrokeWidth(3);
            c.save();
            c.drawBitmap(m_currentImage, m_position.x - m_currentImage.getWidth()/2, m_position.y - m_currentImage.getHeight()/2, null);
            //c.drawCircle(m_position.x, m_position.y, m_radius, p);
            c.restore();
        }
    }
    public void setImage(Mutator.MutatorType m, Bitmap s)
    {
        switch (m)
        {
            case SHIELD:
                m_shield = s;
                break;
            case FREEZE:
                m_freeze = s;
                break;
            case FIRE:
                m_fire = s;
                break;
            case POISON:
            case POISON_SOURCE:
                m_poison = s;
                break;
            default:
                break;
        }
    }
}
