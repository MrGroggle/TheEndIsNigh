package com.example.theendisnigh;

/**
 * Created by Harry on 16/03/2015.
 */

public class Mutator extends Collidable {
    public enum MutatorType {
        FIRE,
        POISON,
        FREEZE
    }

    public final long m_duration;
    public final int m_damage;
    public final String m_ability;
    public Mutator(MutatorType type)
    {
        switch(type) {
            case FIRE:
                m_duration = 5000L;
                m_damage = 1;
                m_ability = "fireFunction";
                break;
            case POISON:
                m_duration = 10000L;
                m_damage = 2;
                m_ability = "poisonFunction";
                break;
            case FREEZE:
                m_duration = 5000L;
                m_damage = 5;
                m_ability = "freezeFunction";
                break;
            default:
                m_duration = 0L;
                m_damage = 0;
                m_ability = "noFunction";
                break;
        }
    }

    public void fireFunction()
    {

    }
    public void poisonFunction()
    {

    }
    public void freezeFunction()
    {

    }
    public void noFunction()
    {

    }

}
