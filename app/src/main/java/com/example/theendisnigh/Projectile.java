package com.example.theendisnigh;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Harry on 04/02/2015.
 */
public class Projectile extends Collidable
{
    private Bitmap m_bulletImage;
    public boolean m_killNextFrame = false;
    Projectile()
    {
        super();
        m_isActive = false;
        m_radius = 10f;
    }

    Projectile(float xPos, float yPos)
    {
        super(xPos, yPos);
        m_rotation = 0f;
        m_isActive = false;
        m_radius = 10.f;
    }

    Projectile(float xPos, float yPos, float angle)
    {
        super(xPos, yPos);
        m_rotation = angle;
        m_isActive = false;
        m_radius = 10f;
    }

    public void setSprite(Bitmap s)
    {
        m_bulletImage = Bitmap.createScaledBitmap(s, (int)m_radius*2, (int)m_radius*2, true);
    }

    public void update()
    {
        super.update();
        doThrust();
    }
    public void draw(Paint p, Canvas c)
    {
        if(m_isActive)
        {
            p.setStrokeWidth(1);
            p.setColor(Color.GRAY);
            c.save();
            c.rotate((float)Math.toDegrees(m_rotation), m_position.x, m_position.y);
            //c.drawRect(m_position.x-5, m_position.y-5, m_position.x+5, m_position.y+5, p);
            c.drawBitmap(m_bulletImage, m_position.x - m_bulletImage.getWidth()/2, m_position.y - m_bulletImage.getHeight()/2, null);
            c.restore();
        }

        if(m_killNextFrame)
        {
            m_isActive = false;
            m_killNextFrame = false;
        }
    }
    private void doThrust()
    {
        m_velocity.x = (float)Math.cos(m_rotation);
        m_velocity.y = (float)Math.sin(m_rotation);
        updatePosition(m_velocity.x * MOVEMENT_SPEED, m_velocity.y * MOVEMENT_SPEED);
    }
}
