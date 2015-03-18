package com.example.theendisnigh;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harry on 11/02/2015.
 */
public class Quadtree
{

    private int m_level;
    private List<Collidable> m_objects;
    private Rect m_bounds;
    private Quadtree[] m_nodes;

    public Quadtree(int level, Rect bounds)
    {
        m_level = level;
        m_objects = new ArrayList<Collidable>();
        m_bounds = bounds;
        m_nodes = new Quadtree[4];
    }

    public void setBounds(Rect bounds)
    {
        m_bounds = bounds;
    }


    public void clear()
    {
        m_objects.clear();
        for(int i = 0; i < m_nodes.length; i++)
        {
            if(m_nodes[i] != null)
            {
                m_nodes[i].clear();
                m_nodes[i] = null;
            }
        }
    }

    private void split()
    {
        int x = m_bounds.left;
        int y = m_bounds.top;
        int x1 = m_bounds.right;
        int y1 = m_bounds.bottom;

        m_nodes[0] = new Quadtree(m_level+1, new Rect(m_bounds.centerX(), y, x1, m_bounds.centerY()));
        m_nodes[1] = new Quadtree(m_level+1, new Rect(x, y, m_bounds.centerX(), m_bounds.centerY()));
        m_nodes[2] = new Quadtree(m_level+1, new Rect(x, m_bounds.centerY(), m_bounds.centerX(), y1));
        m_nodes[3] = new Quadtree(m_level+1, new Rect(m_bounds.centerX(), m_bounds.centerY(), x1, y1));

    }
    private int getIndex(Collidable c)
    {
        float top = c.m_position.y-c.m_radius;
        float bottom = c.m_position.y+c.m_radius;
        float left = c.m_position.x-c.m_radius;
        float right = c.m_position.x+c.m_radius;
        int index = -1;
        boolean topCheck = (top < m_bounds.centerY() && bottom < m_bounds.centerY());
        boolean bottomCheck = (top > m_bounds.centerY());

        if(left < m_bounds.centerX() && right < m_bounds.centerX())
        {
            if(topCheck)
                index = 1;
            else if(bottomCheck)
                index = 2;
        }
        else if(left > m_bounds.centerX())
        {
            if(topCheck)
                index = 0;
            else if (bottomCheck)
                index = 3;
        }
        return index;
    }

    public void insert(Collidable c)
    {
        if(m_nodes[0] != null)
        {
            int index = getIndex(c);

            if(index != -1)
            {
                m_nodes[index].insert(c);
                return;
            }
        }

        m_objects.add(c);
        int MAX_OBJECTS = 10;
        int MAX_LEVELS = 5;
        if(m_objects.size() > MAX_OBJECTS && m_level < MAX_LEVELS)
        {
            if(m_nodes[0] == null)
            {
                split();
            }
            int i = 0;
            while (i < m_objects.size())
            {
                int index = getIndex(m_objects.get(i));
                if(index != -1)
                {
                    m_nodes[index].insert(m_objects.remove(i));

                }
                else
                {
                    i++;
                }
            }
        }
    }
    public List retrieve(List returnObjects, Collidable c)
    {
        int index = getIndex(c);
        if(index != -1 && m_nodes[0] != null)
        {
            m_nodes[index].retrieve(returnObjects, c);
        }
        returnObjects.addAll(m_objects);
        return returnObjects;
    }
}
