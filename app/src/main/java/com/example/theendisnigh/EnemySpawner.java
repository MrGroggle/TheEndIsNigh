package com.example.theendisnigh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Harry on 11/02/2015.
 */
public class EnemySpawner
{
    private final double SPAWN_PERIOD = 750.0; // Adjust to suit timing. We could alter this depending on what weapons the player has
    //private long lastTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) - SPAWN_PERIOD;
    private float interpTime = 0f;
    private int m_mutations = 0;
    private final int NUM_SPAWN_POINTS = 20;
    private List<Vector2F> m_spawnLocations;
    private ArrayList<EnemyConfig> m_enemySetup;
    private int m_fieldWidth;
    private int m_fieldHeight;
    private Timer m_timer;
    private Timer m_interpTimer;
    public EnemySpawner()
    {
        m_spawnLocations = new ArrayList<>();
    }
    public EnemySpawner(int xWidth, int yWidth)
    {
        m_fieldWidth = xWidth + 40;
        m_fieldHeight = yWidth + 40;
        m_spawnLocations = new ArrayList<Vector2F>();
        m_timer = new Timer();
        m_interpTimer = new Timer();
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

        //long currTime = System.nanoTime()/1000;
        m_timer.startTimer();
        if(m_timer.getStartTimerMillis() >= SPAWN_PERIOD)
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
                    m_timer.stopTimer();
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
        //float time = System.nanoTime()/1000 - lastTime;
        float MUTATE_TIMER = 1000 * 30 * 5;
        m_interpTimer.startTimer();
        interpTime = (float)m_interpTimer.getStartTimerMillis() / MUTATE_TIMER;
        if(interpTime < MUTATE_TIMER) {
            for (int i = 0; i < m_enemySetup.size(); i++) {
                m_enemySetup.get(i).m_weight = Math.round(m_enemySetup.get(i).m_startWeight + interpTime * (m_enemySetup.get(i).m_endWeight - m_enemySetup.get(i).m_startWeight));
            }
        }
        else
        {
            m_mutations++;
            m_interpTimer.stopTimer();
        }
    }

}
