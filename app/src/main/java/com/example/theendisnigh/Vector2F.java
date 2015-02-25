package com.example.theendisnigh;

import android.graphics.PointF;

/**
 * Created by Harry on 10/02/2015.
 */
public class Vector2F
{
    public float x;
    public float y;

    Vector2F(){}
    Vector2F(float val)
    {
        x = val;
        y = val;
    }
    Vector2F(float xVal, float yVal)
    {
        x = xVal;
        y = yVal;
    }
    public void add(Vector2F val)
    {
        x+=val.x;
        y+=val.y;
    }
    public void add(PointF val)
    {
        x+=val.x;
        y+=val.y;
    }
    public void add(float val)
    {
        x+=val;
        y+=val;
    }
    public void sub(Vector2F val)
    {
        x-=val.x;
        y-=val.y;
    }
    public void sub(PointF val)
    {
        x-=val.x;
        y-=val.y;
    }
    public void sub(float val)
    {
        x-=val;
        y-=val;
    }
    public void mul(Vector2F val)
    {
        x*=val.x;
        y*=val.y;
    }
    public void mul(PointF val)
    {
        x*=val.x;
        y*=val.y;
    }
    public void mul(float val)
    {
        x*=val;
        y*=val;
    }
    public void div(Vector2F val)
    {
        if(val.x != 0f)
            x/=val.x;
        if(val.y != 0f)
            y/=val.y;
    }
    public void div(PointF val)
    {
        if(val.x != 0f)
            x/=val.x;
        if(val.y != 0f)
            y/=val.y;
    }
    public void div(float val)
    {
        if(val != 0f)
        {
            x/=val;
            y/=val;
        }
    }

    public float dot(Vector2F val)
    {
        return x*val.x + y*val.y;
    }
    public float dot(PointF val)
    {
        return x*val.x + y*val.y;
    }
    public float length()
    {
        return (float)Math.sqrt(x*x + y*y);
    }
    public void normalise()
    {
        if(length()!= 0)
        {
            x /= length();
            y /= length();
        }
    }
    public void trunc(float limit)
    {
        if(x < 0)
            x = Math.max(x, -limit);
        else
            x = Math.min(x, limit);
        if(y < 0)
            y = Math.max(y, -limit);
        else
            y = Math.min(y, limit);
    }
    public void negate()
    {
        x = -x;
        y = -y;
    }



}
