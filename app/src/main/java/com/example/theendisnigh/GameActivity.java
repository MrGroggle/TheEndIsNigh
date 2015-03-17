package com.example.theendisnigh;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends Activity implements HighscoresDialogFragment.HighscoreDialogListener {
	
	private ScreenView sv;
    private HighscoresDialogFragment hf;

    private SharedPreferences gamePrefs;
    public static final String GAME_PREFS = "EndNighFile";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		/* Make sure that import android.view.Window;
		import android.view.WindowManager; have been added so 
		the below will work*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);//Set full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        //Get the height and width of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
         
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.game_layout);
        sv = (ScreenView) findViewById(R.id.screenView);
        gamePrefs = getSharedPreferences(GAME_PREFS, Context.MODE_PRIVATE);
	}
	@Override
    protected void onPause(){
        super.onPause();
        sv.pause();
    }
     
    @Override
    protected void onResume(){
        super.onResume();
        sv.resume();
    }

    protected void onDeath()
    {
        hf = new HighscoresDialogFragment();
        hf.show(getFragmentManager(), "HighscoresFragment");
    }

    private void setHighScore(String name)
    {

        long currentScore = sv.getPlayer().m_currentScore;
        if(currentScore > 0l)
        {
            SharedPreferences.Editor edit = gamePrefs.edit();
            String scores = gamePrefs.getString("highscores", "");

            if (scores.length() > 0)
            {
                List<Score> scoreStrings = new ArrayList<Score>();
                String[] exScores = scores.split("\\|");
                for(int i = 0; i < 10; i++)
                {
                    String[] parts = exScores[i].split(" - ");
                    scoreStrings.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }
                Score newScore = new Score(name, currentScore);
                scoreStrings.add(newScore);
                Collections.sort(scoreStrings);
                StringBuilder scoreBuild = new StringBuilder("");
                for(int s=0; s<scoreStrings.size(); s++){
                    if(s>=10) break;//only want ten
                    if(s>0) scoreBuild.append("|");//pipe separate the score strings
                    scoreBuild.append(scoreStrings.get(s).getScoreText());
                }
                //write to prefs
                edit.putString("highscores", scoreBuild.toString());
                edit.commit();
            }
            else
            {
                edit.putString("highscores", "" + name + " - " + currentScore);
                edit.commit();
            }
        }
    }
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        setHighScore(((HighscoresDialogFragment) dialog).getName());
        Intent intent = new Intent(getApplicationContext(), ScoresActivity.class);
        startActivity(intent);
    }
    public void onDialogNegativeClick(DialogFragment dialog)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
