package com.example.theendisnigh;

import java.util.concurrent.TimeUnit;

/**
 * Created by sempleharry on 19/03/2015.
 */
public class Timer {

    private long previousTimeNano;

    private long startTime;
    private boolean isTimerStarted;

    public Timer()
    {
        previousTimeNano = System.nanoTime();
        isTimerStarted = false;
    }

    public double getElapsedMillis()
    {
        long currentTime = System.nanoTime();
        double returnTime = TimeUnit.MILLISECONDS.convert((currentTime - previousTimeNano), TimeUnit.NANOSECONDS);
        previousTimeNano = currentTime;
        return returnTime;
    }
    public void startTimer()
    {
        if(!isTimerStarted)
        {
            startTime = System.nanoTime();
            isTimerStarted = true;
        }
    }

    public double getStartTimerMillis()
    {
        if(isTimerStarted)
        {
            long currentTime = System.nanoTime();
            return TimeUnit.MILLISECONDS.convert((currentTime - startTime), TimeUnit.NANOSECONDS);

        }else
        {
            return -1;
        }
    }
    public void stopTimer()
    {
        startTime = 0L;
        isTimerStarted = false;
    }


}
