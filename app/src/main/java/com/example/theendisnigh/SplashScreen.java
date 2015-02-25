package com.example.theendisnigh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Harry on 23/02/2015.
 */
public class SplashScreen extends Activity
{
    private long m_millis = 0;
    private final long SPLASH_TIME = 2000;
    private boolean m_isActive = true;
    private boolean m_paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread thread = new Thread()
        {
            public void run()
            {
                try {
                    while(m_isActive && m_millis < SPLASH_TIME)
                    {
                        if(!m_paused)
                        {
                            m_millis+=100;

                        }
                        sleep(100);
                    }
                }catch (Exception e) {}
                finally {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
}
