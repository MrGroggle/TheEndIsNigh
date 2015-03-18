package com.example.theendisnigh;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Harry on 11/02/2015.
 */
public class EnemySpawner
{
    private final long SPAWN_PERIOD = 750L; // Adjust to suit timing. We could alter this depending on what weapons the player has
    private long lastTime = System.currentTimeMillis() - SPAWN_PERIOD;
    private float interpTime = 0f;
    private int m_mutations = 0;
    private final int NUM_SPAWN_POINTS = 20;
    private List<Vector2F> m_spawnLocations;
    private ArrayList<EnemyConfig> m_enemySetup;
    private int m_fieldWidth;
    private int m_fieldHeight;
    public EnemySpawner()
    {
        m_spawnLocations = new ArrayList<Vector2F>();
    }
    public EnemySpawner(int xWidth, int yWidth)
    {
        m_fieldWidth = xWidth + 20;
        m_fieldHeight = yWidth + 20;
        m_spawnLocations = new ArrayList<Vector2F>();
        setEnemySpawns();

    }
    public void setEnemyConfigs(ArrayList<EnemyConfig> configs)
    {
        m_enemySetup = configs;
    }
    private void setEnemySpawns()
    {
        int perimeter = m_fieldWidth * 2 + m_fieldHeight * 2;
        float pointSeparation = perimeter / NUM_SPAWN_POINTS;

        for(int i = 0; i < NUM_SPAWN_POINTS; i++)
        {
            Vector2F point = new Vector2F(m_fieldWidth/2, -10.f);   //Start from middle top
            //Start from top left
            float pointOnLine = pointSeparation * i;
            if((pointOnLine -= m_fieldWidth/2) > 0)
            {
                if ((pointOnLine -= m_fieldHeight) > 0)
                {
                    if ((pointOnLine -= m_fieldWidth) > 0)
                    {
                        if ((pointOnLine -= m_fieldHeight) > 0)
                        {
                            point.x = pointOnLine;
                        }
                        else
                        {
                            point.y = -10.f - pointOnLine;
                            point.x = -10.f;
                        }
                    }
                    else
                    {
                        point.y = m_fieldHeight + 10.f;
                        point.x = -10.f - pointOnLine;
                    }
                }
                else
                {
                    point.x = m_fieldWidth + 10.f;
                    point.y = m_fieldHeight + 10.f + pointOnLine;
                }
            }
            else
            {
                point.x = m_fieldWidth + 10.f + pointOnLine;
            }

            m_spawnLocations.add(point);

        }

    }
    public void spawnEnemies(Enemy[] c)
    {
        //m_currentCount = c.length;

        long currTime = System.currentTimeMillis();

        if((currTime - lastTime) >= SPAWN_PERIOD)
        {
            interpolateWeight();
            for(Enemy col : c)
            {
                if(!col.m_isActive)
                {
                    col.setFromConfig(getRandomEnemy());
                    Vector2F point = m_spawnLocations.get(new Random().nextInt(NUM_SPAWN_POINTS));
                    col.m_position.x = point.x;
                    col.m_position.y = point.y;
                    col.m_isActive = true;
                    lastTime = currTime;
                    return;
                }
            }
        }

    }

    public void setFieldDimensions(int w, int h)
    {
        m_fieldWidth = w;
        m_fieldHeight = h;
    }

    private EnemyConfig getRandomEnemy()
    {
        int maxWeight = 0;
        for(int i = 0; i < m_enemySetup.size(); i++)
        {
            maxWeight += m_enemySetup.get(i).m_weight;
        }
        if(maxWeight != 0)
        {
            int number = new Random().nextInt(maxWeight);
            int weightSum = 0;

            for(int i = 0; i < m_enemySetup.size(); i++)
            {
                weightSum += m_enemySetup.get(i).m_weight;
                if(number <= weightSum)
                {
                    return m_enemySetup.get(i);
                }
            }
        }
        return null;
    }

    private void interpolateWeight()
    {
        float time = System.currentTimeMillis() - lastTime;
        long MUTATE_TIMER = 1000L * 30L * 5L;
        interpTime += time / MUTATE_TIMER;
        if(interpTime < 1) {
            for (int i = 0; i < m_enemySetup.size(); i++) {
                m_enemySetup.get(i).m_weight = Math.round(m_enemySetup.get(i).m_startWeight + interpTime * (m_enemySetup.get(i).m_endWeight - m_enemySetup.get(i).m_startWeight));
            }
        }
        else
        {
            m_mutations++;
            interpTime = 0f;
        }
    }

}
