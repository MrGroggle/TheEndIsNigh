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
    private MutatorConfig m_config;
    private Bitmap m_currentImage;
    private Timer m_timer;

    PlayerPickup(float xPos, float yPos, MutatorConfig config)
    {
        super(xPos, yPos);
        m_radius = 25;
        m_isActive = false;
        setFromConfig(config);
        m_timer = new Timer();
    }
    public void setFromConfig(MutatorConfig config)
    {
        m_currentImage = config.m_pickupImage;
        m_config = config;
    }

    public void onPickup(Player p)
    {
        if(!p.getMutator().m_isActive) {
            p.setMutatorFromConfig(m_config);
            m_isActive = false;
        }
    }

    public void update()
    {
        if(m_isActive) {
            m_timer.startTimer();
            if (m_timer.getStartTimerMillis() >= 10000.0) {
                m_isActive = false;
                m_timer.stopTimer();
            }
        }

    }

    public void draw(Paint p, Canvas c)
    {
        if(m_isActive) {
            p.setStrokeWidth(3);
            c.save();
            c.drawBitmap(m_currentImage, m_position.x - m_currentImage.getWidth()/2, m_position.y - m_currentImage.getHeight()/2, null);
            c.restore();
        }
    }
}
