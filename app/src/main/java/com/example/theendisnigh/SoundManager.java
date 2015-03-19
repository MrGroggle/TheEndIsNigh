package com.example.theendisnigh;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Harry on 19/03/2015.
 */
public final class SoundManager
{
	private Context m_context;
	private static SoundPool m_soundpool;
	private static Map<String, Integer> m_sounds = new HashMap<String, Integer>();
    public SoundManager(Context context)
    {
    	m_context = context;
    	m_soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
    	m_sounds.put("shotgun", m_soundpool.load(m_context, R.raw.shotgun, 1));
    }
    
    public static void playAudio(String id)
    {
    	m_soundpool.play(m_sounds.get(id).intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
    }

}
