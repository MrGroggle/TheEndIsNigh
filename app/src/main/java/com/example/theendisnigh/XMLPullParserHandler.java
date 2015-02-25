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
    private ArrayList<EnemyConfig> m_configs = new ArrayList<EnemyConfig>();
    private EnemyConfig m_tempConfig;
    private String m_tempText;


    public ArrayList<EnemyConfig> parse(XmlResourceParser xp)
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
                            m_tempConfig = new EnemyConfig();
                        }
                        break;
                    case XmlResourceParser.TEXT:
                        m_tempText = xp.getText();
                        break;
                    case XmlResourceParser.END_TAG:
                        if(tagname.equalsIgnoreCase("zombie"))
                        {
                            m_configs.add(m_tempConfig);
                        }
                        else if(tagname.equalsIgnoreCase("id"))
                        {
                            m_tempConfig.m_id = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("tag"))
                        {
                            m_tempConfig.m_tag = m_tempText;
                        }
                        else if(tagname.equalsIgnoreCase("radius"))
                        {
                            m_tempConfig.m_radius = Float.parseFloat(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("mass"))
                        {
                            m_tempConfig.m_mass = Float.parseFloat(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("speed"))
                        {
                            m_tempConfig.m_speed = Float.parseFloat(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("hitpoints"))
                        {
                            m_tempConfig.m_hp = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("score"))
                        {
                            m_tempConfig.m_score = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("startweight"))
                        {
                            m_tempConfig.m_startWeight = Integer.parseInt(m_tempText);
                            m_tempConfig.m_weight = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("endweight"))
                        {
                            m_tempConfig.m_endWeight = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("pointvalue"))
                        {
                            m_tempConfig.m_pointVal = Integer.parseInt(m_tempText);
                        }
                        else if(tagname.equalsIgnoreCase("colour"))
                        {
                            m_tempConfig.m_paintTEMP = Color.parseColor(m_tempText);
                        }
                        break;
                    default:
                        break;
                }
                eventType = xp.next();
            }
        }catch (XmlPullParserException e) { e.printStackTrace();}
        catch (IOException e){e.printStackTrace();}

        return m_configs;
    }
}
