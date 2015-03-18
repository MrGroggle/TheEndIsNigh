package com.example.theendisnigh;

import android.content.res.XmlResourceParser;
import android.graphics.Color;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Harry on 23/02/2015.
 */
public class XMLPullParserHandler
{
    private ArrayList<EnemyConfig> enemyConfigs = new ArrayList<EnemyConfig>();
    private ArrayList<MutatorConfig> mutatorConfigs = new ArrayList<MutatorConfig>();
    private EnemyConfig m_enemyConfig;
    private MutatorConfig m_mutatorConfig;
    private String m_tempText;


    public ArrayList<EnemyConfig> parseEnemyConfigs(XmlResourceParser xp)
    {
        try{
            /*XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);*/

            int eventType = xp.getEventType();
            while(eventType != XmlResourceParser.END_DOCUMENT)
            {
                String tagname = xp.getName();
                switch (eventType)
                {
                    case XmlResourceParser.START_TAG:
                        if(tagname.equalsIgnoreCase("zombie"))
                        {
                            m_enemyConfig = new EnemyConfig();
                        }
                        break;
                    case XmlResourceParser.TEXT:
                        m_tempText = xp.getText();
                        break;
                    case XmlResourceParser.END_TAG:
                        if(tagname.equalsIgnoreCase("zombie"))
                        {
                            enemyConfigs.add(m_enemyConfig);
                        }
                        else if(tagname.equalsIgnoreCase("id"))
                        {
                            m_enemyConfig.m_id = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("tag"))
                        {
                            m_enemyConfig.m_tag = m_tempText;
                        }
                        else if(tagname.equalsIgnoreCase("radius"))
                        {
                            m_enemyConfig.m_radius = Float.parseFloat(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("mass"))
                        {
                            m_enemyConfig.m_mass = Float.parseFloat(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("speed"))
                        {
                            m_enemyConfig.m_speed = Float.parseFloat(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("hitpoints"))
                        {
                            m_enemyConfig.m_hp = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("score"))
                        {
                            m_enemyConfig.m_score = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("startweight"))
                        {
                            m_enemyConfig.m_startWeight = Integer.parseInt(m_tempText);
                            m_enemyConfig.m_weight = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("endweight"))
                        {
                            m_enemyConfig.m_endWeight = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("pointvalue"))
                        {
                            m_enemyConfig.m_pointVal = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("colour"))
                        {
                            m_enemyConfig.m_paintTEMP = Color.parseColor(m_tempText);
                        }
                        break;
                    default:
                        break;
                }
                eventType = xp.next();
            }
        }catch (XmlPullParserException e) { e.printStackTrace();}
        catch (IOException e){e.printStackTrace();}

        return enemyConfigs;
    }
    public ArrayList<MutatorConfig> parseMutatorConfigs(XmlResourceParser xp)
    {
        try{
            /*XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);*/

            int eventType = xp.getEventType();
            while(eventType != XmlResourceParser.END_DOCUMENT)
            {
                String tagname = xp.getName();
                switch (eventType)
                {
                    case XmlResourceParser.START_TAG:
                        if(tagname.equalsIgnoreCase("mutator"))
                        {
                            m_mutatorConfig = new MutatorConfig();
                        }
                        break;
                    case XmlResourceParser.TEXT:
                        m_tempText = xp.getText();
                        break;
                    case XmlResourceParser.END_TAG:
                        if(tagname.equalsIgnoreCase("mutator"))
                        {
                            mutatorConfigs.add(m_mutatorConfig);
                        }
                        else if(tagname.equalsIgnoreCase("id"))
                        {
                            //m_mutatorConfig.m_mutatorImage = Integer.parseInt(m_tempText);
                        }
                    default:
                        break;
                }
                eventType = xp.next();
            }
        }catch (XmlPullParserException e) { e.printStackTrace();}
        catch (IOException e){e.printStackTrace();}

        return mutatorConfigs;
    }
}
