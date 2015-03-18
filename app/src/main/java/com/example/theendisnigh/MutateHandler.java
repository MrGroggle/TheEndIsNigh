package com.example.theendisnigh;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Harry on 18/03/2015.
 */
public class MutateHandler
{
    private ArrayList<MutatorConfig> m_mutatorSettings;

    public void setMutatorConfigs(ArrayList<MutatorConfig> configs)
    {
        m_mutatorSettings = configs;
    }
    public void spawnMutator(PlayerPickup[] p, Enemy e)
    {
        for(PlayerPickup pickup : p)
        {
            if(!pickup.m_isActive)
            {
                //pickup.setFromConfig(getRandomMutator());
                pickup.m_position.x = e.m_position.x;
                pickup.m_position.y = e.m_position.y;
                pickup.m_isActive = true;
                return;
            }
        }
    }
    private MutatorConfig getRandomMutator()
    {
        int maxWeight = 0;
        for(int i = 0; i < m_mutatorSettings.size(); i++)
        {
            maxWeight += m_mutatorSettings.get(i).m_weighting;
        }
        if(maxWeight != 0)
        {
            int number = new Random().nextInt(maxWeight);
            int weightSum = 0;

            for(int i = 0; i < m_mutatorSettings.size(); i++)
            {
                weightSum += m_mutatorSettings.get(i).m_weighting;
                if(number <= weightSum)
                {
                    return m_mutatorSettings.get(i);
                }
            }
        }
        return null;
    }
}
