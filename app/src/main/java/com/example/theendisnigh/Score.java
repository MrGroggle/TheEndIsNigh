package com.example.theendisnigh;

/**
 * Created by Harry on 26/02/2015.
 */
public class Score implements Comparable<Score>
{
    private String m_name;
    private long m_score;

    public Score(String name, long score)
    {
        m_name = name;
        m_score = score;
    }

    public int compareTo(Score sc){
        //return 0 if equal
        //1 if passed greater than this
        //-1 if this greater than passed
        return sc.m_score>m_score? 1 : sc.m_score<m_score? -1 : 0;
    }
    public String getScoreText()
    {
        return m_name+" - "+m_score;
    }
}
