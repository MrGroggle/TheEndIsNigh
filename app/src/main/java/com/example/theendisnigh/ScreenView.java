package com.example.theendisnigh;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

//@SuppressLint("WrongCall")//Needed to suppress the lint error
public class ScreenView extends SurfaceView implements SurfaceHolder.Callback
{
    class GameThread extends Thread {
        private SurfaceHolder m_surfaceHolder;
        private Handler handler;
        private Context context;
        private boolean running;
        private boolean paused;

        public GameThread(SurfaceHolder h, Context c, Handler hand) {
            this.m_surfaceHolder = h;
            this.context = c;
            this.handler = hand;
        }

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean run) {
            running = run;
        }

        public boolean isPaused()
        {
            return paused;
        }
        public void setPaused(boolean set)
        {
            paused = set;
        }

        @Override
        public void run()
        {
            boolean doPause = false;
            boolean doResume = true;
            //Remove conflict between the UI thread and the game thread.
            init();
            while (running)
            {
                Canvas c = null;
                try{
                    if(!paused){
                        c = m_surfaceHolder.lockCanvas(null);
                        synchronized (m_surfaceHolder)
                        {
                            if(doResume)
                            {
                                doPause = true;
                                doResume = false;
                            }
                            Update();
                            c.drawARGB(255, 0, 0, 0);
                            Draw(c);
                        }
                    }
                    else
                    {
                        if(doPause) {
                            doResume = true;
                            doPause = false;
                            c = m_surfaceHolder.lockCanvas(null);
                            synchronized (m_surfaceHolder) {

                                Paint p = new Paint();
                                p.setStyle(Paint.Style.FILL);
                                p.setColor(Color.BLACK);
                                p.setAlpha(125);
                                c.drawRect(0, 0, PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT, p);
                                p = null;
                            }
                        }
                        paused = m_player.m_shouldPause;
                    }
                }catch(Exception e)
                {
                    running = false;
                }
                finally{
                    if(c!= null)
                    {
                        m_surfaceHolder.unlockCanvasAndPost(c);
                    }
                }

            }
        }
    }
    private final int PLAY_AREA_WIDTH = 2560;
    private final int PLAY_AREA_HEIGHT = 2560;
    private final int MAX_PLAYER_BULLETS = 20;
    private final int MAX_ENEMIES = 50;

    private final int LEFT_COLLISION = 1;
    private final int TOP_COLLISION = 2;
    private final int RIGHT_COLLISION = 3;
    private final int BOTTOM_COLLISION = 4;
    private Bitmap m_background;
    private Quadtree m_quadTree;
	SurfaceHolder holder;
	GameThread thread;
	Paint paint = new Paint();

	//Width and height of screen
	private int m_width;
	private int m_height;

    //Width and height of the play area
    private int m_fieldWidth = PLAY_AREA_WIDTH;
    private int m_fieldHeight = PLAY_AREA_HEIGHT;

    private PointF m_fieldCentre = new PointF(PLAY_AREA_WIDTH/2,PLAY_AREA_HEIGHT/2);

	private int m_maxXTranslate;
	private int m_maxYTranslate;

    private TextView m_scoreUI;
	private JoystickView m_moveStick;
	private JoystickView m_fireStick;

	private Projectile[] m_playerProjectiles;

    //Temporary testing variable
    private Enemy[] m_enemyPool;
    private EnemySpawner m_spawner;

	private Player m_player;
	//Define the frame rate 
	//private final int FRAME_RATE = 10;

	public ScreenView(Context context, AttributeSet attrs) 
	{
		
		super(context, attrs);
		holder = getHolder();
		m_fieldWidth = PLAY_AREA_WIDTH;
        m_fieldHeight = PLAY_AREA_HEIGHT;
        holder.addCallback(this);
        thread = new GameThread(holder, context, new Handler()
        {
            @Override
            public void handleMessage(Message m)
            {
                //@Todo handle messages
            }
        });
        Bitmap background = loadBitmap(R.drawable.playareatest, context);
        m_background = Bitmap.createScaledBitmap(background, PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT, true);
        m_spawner = new EnemySpawner(10, m_fieldWidth, m_fieldHeight);

        XMLPullParserHandler parser = new XMLPullParserHandler();
        m_spawner.setEnemyConfigs(parser.parse(context.getResources().getXml(R.xml.zombiedata)));
	}
    private void init()
    {
        View v = (View)getParent();
        if(v != null)
        {
            m_moveStick = (JoystickView) v.findViewById(R.id.joystickViewMove);
            m_fireStick = (JoystickView) v.findViewById(R.id.joystickViewFire);
            m_scoreUI = (TextView) v.findViewById(R.id.scoreUpdate);
        }

        m_player = new Player(PLAY_AREA_WIDTH/2,PLAY_AREA_HEIGHT/2);
        m_player.setMovementSpeed(10f);
        m_playerProjectiles = new Projectile[MAX_PLAYER_BULLETS];
        m_enemyPool = new Enemy[MAX_ENEMIES];
        for(int i = 0; i < MAX_PLAYER_BULLETS; i++)
        {
            m_playerProjectiles[i] = new Projectile();
        }
        for(int i = 0; i < MAX_ENEMIES; i++)
        {
            m_enemyPool[i] = new Enemy(0, 0);
            m_enemyPool[i].setTarget(m_player);
        }

        //@TODO Create a Steering manager/ implement collision checks between enemies so they don't stack up
        m_moveStick.setMovedSubscriber(m_player);
        m_moveStick.setStickType(0);
        m_fireStick.setMovedSubscriber(m_player);
        m_fireStick.setStickType(1);
    }
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        m_width = xNew;
        m_height = yNew;

        m_quadTree = new Quadtree(0, new Rect(0, 0, xNew, yNew));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = Math.min(m_fieldWidth, MeasureSpec.getSize(widthMeasureSpec));
        int height = Math.min(m_fieldHeight, MeasureSpec.getSize(heightMeasureSpec));

        m_width = width;
        m_height = height;

        m_maxXTranslate = m_fieldWidth - width;
        m_maxYTranslate = m_fieldHeight - height;

        setMeasuredDimension(width, height);
        m_quadTree = new Quadtree(0, new Rect(0, 0, width, height));

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        start();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        stop();
    }
    public void stop()
    {
        thread.setRunning(false);
        try {
            thread.join();
        }catch (InterruptedException e)
        {

        }
    }
	public void pause()
	{
		thread.setPaused(true);
	}

	public void resume()
	{
		thread.setPaused(false);
	}
    public void start()
    {
        thread.setRunning(true);
        thread.start();
    }
    public void updateQuadTree()
    {
        m_quadTree.clear();
        m_quadTree.insert(m_player);
        for(int i = 0; i<MAX_PLAYER_BULLETS;i++)
        {
            if(m_playerProjectiles[i].m_isActive)
            {
                m_quadTree.insert(m_playerProjectiles[i]);
            }
        }
        for(int i=0; i<MAX_ENEMIES;i++)
        {
            if(m_enemyPool[i].m_isActive)
            {
                m_quadTree.insert(m_enemyPool[i]);
            }
        }
    }
    public void playerCollision()
    {
        List<Collidable> returnCollidables = new ArrayList<Collidable>();
        m_quadTree.retrieve(returnCollidables, m_player);

        for(int i = 0; i < returnCollidables.size(); i++)
        {
            if(returnCollidables.get(i) instanceof Enemy)
            {
                if(m_player.checkCollision(returnCollidables.get(i)))
                {
                    //final Handler handler = new Handler();
                    returnCollidables.get(i).m_isActive = false;
                    if(!m_player.playerHit()) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                m_player.m_isActive = true;
                            }
                        }, 3000); //Wait 5 seconds
                    }else
                    {
                        if(getContext() instanceof GameActivity)
                        {
                            ((GameActivity) getContext()).onDeath();
                        }
                        //Do something here to move to highscores

                    }
                }
            }
        }
    }
    private void bulletCollision()
    {
        List<Collidable> returnCollidables = new ArrayList<Collidable>();
        for(int i = 0; i < MAX_PLAYER_BULLETS; i++)
        {
            if(m_playerProjectiles[i].m_isActive)
            {
                m_quadTree.retrieve(returnCollidables, m_playerProjectiles[i]);

                for(int x = 0; x < returnCollidables.size(); x++)
                {
                    if(returnCollidables.get(x) instanceof Enemy && returnCollidables.get(x).m_isActive)
                    {
                        if(m_playerProjectiles[i].checkCollision(returnCollidables.get(x)))
                        {
                            if(((Enemy)returnCollidables.get(x)).checkDeadAfterHit())
                            {
                                m_player.m_currentScore += ((Enemy)returnCollidables.get(x)).m_score;
                            }
                            m_playerProjectiles[i].m_isActive = false;
                        }
                    }
                }
            }
        }

    }
	public void Update()
	{

        int check = checkEdge(m_player);
        if(check > 0)
        {
            if((check & (1 << TOP_COLLISION)) == (1 << TOP_COLLISION))
            {
                m_player.setPosition(m_player.m_position.x, m_player.m_radius/2);
                m_player.m_velocity.y =0f;
            }
            if( (check & (1 << BOTTOM_COLLISION)) == (1 << BOTTOM_COLLISION))
            {
                m_player.setPosition(m_player.m_position.x, m_fieldHeight - m_player.m_radius/2);
                m_player.m_velocity.y =0f;
            }
            if( (check & (1 << LEFT_COLLISION)) == (1 << LEFT_COLLISION))
            {
                m_player.setPosition(m_player.m_radius/2, m_player.m_position.y);
                m_player.m_velocity.x =0f;
            }
            if( (check & (1 << RIGHT_COLLISION)) == (1 << RIGHT_COLLISION))
            {
                m_player.setPosition(m_fieldWidth - m_player.m_radius/2, m_player.m_position.y);
                m_player.m_velocity.x =0f;
            }
        }else
        {
            m_player.update();
        }
        UpdateBullets();
        for(int i = 0; i < MAX_ENEMIES; i++)
        {
            m_enemyPool[i].update();
        }
        m_spawner.spawnEnemies(m_enemyPool);
        updateQuadTree();
        bulletCollision();
        playerCollision();
        if(m_player.m_shouldPause)
        {
            pause();
        }

	}
    private void UpdateBullets()
    {

        for(int i = 0; i < MAX_PLAYER_BULLETS; i++)
        {
            if(!m_playerProjectiles[i].m_isActive)
            {
                if(m_player.m_shouldCreateBullet)
                {
                    m_playerProjectiles[i].m_isActive = true;
                    m_playerProjectiles[i].m_position.set(m_player.getRadialPosition());
                    m_playerProjectiles[i].m_rotation = m_player.getAngle();
                    m_playerProjectiles[i].setMovementSpeed(20f);
                    m_player.m_shouldCreateBullet = false;
                    break;
                }
            }
            else
            {
                m_playerProjectiles[i].update();
                if(checkEdge(m_playerProjectiles[i]) > 0)   // Has collided with something
                {
                    m_playerProjectiles[i].m_killNextFrame = true;
                }
            }

        }
    }
	public void Draw(final Canvas c)
	{
        c.save();
        /*
            VIEW SPACE DRAWING
         */
        if(m_player.m_position.x > m_width/2)
        {
            m_fieldCentre.x = Math.min(m_player.m_position.x - (m_width/2), m_maxXTranslate);
            c.translate(-m_fieldCentre.x, 0);
        }
        if(m_player.m_position.y > m_height/2)
        {
            m_fieldCentre.y = Math.min(m_player.m_position.y - (m_height/2), m_maxYTranslate);
            c.translate(0, -m_fieldCentre.y);
        }
        /*
            WORLD SPACE DRAWING
        */
        c.drawBitmap(m_background, 0, 0, null);
        m_spawner.draw(paint,c);
        m_player.draw(paint, c);
        for(int i = 0; i<MAX_ENEMIES; i++)
        {
            m_enemyPool[i].draw(paint, c);
        }
        for(int i = 0; i< MAX_PLAYER_BULLETS; i++)
        {
            m_playerProjectiles[i].draw(paint, c);
        }
        post(new Runnable() {
            @Override
            public void run() {

                m_scoreUI.setText(Long.toString(m_player.m_currentScore));

            }
        });
        c.restore();
	}

    private int checkEdge(Collidable c)
    {
        //  Adjusted velocity is velocity with movement speed added
        int isOver = 0;
        if(c.m_position.x + c.getAdjustedVelocity().x > m_fieldCentre.x + m_width)
            isOver |= (1 << RIGHT_COLLISION);
        if(c.m_position.x + c.getAdjustedVelocity().x < m_fieldCentre.x)
            isOver |= (1 << LEFT_COLLISION);
        if(c.m_position.y + c.getAdjustedVelocity().y > m_fieldCentre.y + m_height)
            isOver |= (1 << BOTTOM_COLLISION);
        if(c.m_position.y + c.getAdjustedVelocity().y < m_fieldCentre.y)
            isOver |= (1 << TOP_COLLISION);
        return isOver;
    }

    public Bitmap loadBitmap(int resDrId, Context context)
    {
        return loadBitmap(resDrId, 1, context);
    }
    private static Bitmap loadBitmap(int resDrId, int sampleSize, Context context)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = sampleSize;

        try
        {
            return BitmapFactory.decodeResource(context.getResources(), resDrId, options);
        }
        catch (OutOfMemoryError e)
        {
            if( sampleSize == 4)
            {
                return null;
            }
            return loadBitmap(resDrId, sampleSize++, context);
        }
        catch(Exception e)
        {
            return null;
        }
    }
}
