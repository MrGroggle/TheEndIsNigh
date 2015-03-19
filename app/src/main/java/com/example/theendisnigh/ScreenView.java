package com.example.theendisnigh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

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
            running = true;
            paused = false;
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
            if(!isPaused())
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
    private final int PLAY_AREA_WIDTH = 1280;
    private final int PLAY_AREA_HEIGHT = 1280;
    private final int MAX_PLAYER_BULLETS = 20;
    private final int MAX_ENEMIES = 50;
    private final int MAX_PICKUPS = 10;

    private final int LEFT_COLLISION = 1;
    private final int TOP_COLLISION = 2;
    private final int RIGHT_COLLISION = 3;
    private final int BOTTOM_COLLISION = 4;
    private Bitmap m_background;

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
    private TextView m_livesUI;
	private JoystickView m_moveStick;
	private JoystickView m_fireStick;
	public SoundManager m_soundManager;

	private Projectile[] m_playerProjectiles;
    private Enemy[] m_enemyPool;
    private EnemySpawner m_spawner;
    private MutateHandler m_mutateHandler;

    private PlayerPickup[] m_playerPickups;

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
        thread.setRunning(true);
        Bitmap background = loadBitmap(R.drawable.labbackground, context);
        m_background = Bitmap.createScaledBitmap(background, PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT, true);
        m_spawner = new EnemySpawner(m_fieldWidth, m_fieldHeight);
        m_mutateHandler = new MutateHandler();

        XMLPullParserHandler parser = new XMLPullParserHandler(context);
        m_spawner.setEnemyConfigs(parser.parseEnemyConfigs(context.getResources().getXml(R.xml.zombiedata)));
        m_mutateHandler.setMutatorConfigs(parser.parseMutatorConfigs(context.getResources().getXml(R.xml.mutatordata)));
        m_soundManager = new SoundManager(context);
	}

    private void init()
    {
        View v = (View)getParent();
        if(v != null)
        {
            m_moveStick = (JoystickView) v.findViewById(R.id.joystickViewMove);
            m_fireStick = (JoystickView) v.findViewById(R.id.joystickViewFire);
            m_scoreUI = (TextView) v.findViewById(R.id.scoreUpdate);
            m_livesUI = (TextView) v.findViewById(R.id.livesUpdate);

        }

        m_player = new Player(PLAY_AREA_WIDTH/2,PLAY_AREA_HEIGHT/2, m_mutateHandler.getMutator(Mutator.MutatorType.POISON), m_mutateHandler.getMutator(Mutator.MutatorType.SHIELD));
        m_player.setSprite(loadBitmap(R.drawable.player, getContext()));
        m_player.setMovementSpeed(10f);
        //m_player.setMutatorSprites(Mutator.MutatorType.FIRE, loadBitmap(R.drawable.firecircle,getContext()));
        //m_player.setMutatorSprites(Mutator.MutatorType.FREEZE, loadBitmap(R.drawable.icecircle,getContext()));
        //m_player.setMutatorSprites(Mutator.MutatorType.POISON, loadBitmap(R.drawable.poisoncircle,getContext()));
        m_playerProjectiles = new Projectile[MAX_PLAYER_BULLETS];
        m_enemyPool = new Enemy[MAX_ENEMIES];
        m_playerPickups = new PlayerPickup[MAX_PICKUPS];

        for(int i = 0; i < MAX_PLAYER_BULLETS; i++)
        {
            m_playerProjectiles[i] = new Projectile();
            m_playerProjectiles[i].setSprite(loadBitmap(R.drawable.bullet, getContext()));
        }

        for(int i = 0; i < MAX_ENEMIES; i++)
        {
            m_enemyPool[i] = new Enemy(0, 0);
            m_enemyPool[i].setTarget(m_player);
            m_enemyPool[i].setImage(loadBitmap(R.drawable.zombietwo, getContext()));
        }

        for(int i = 0; i < MAX_PICKUPS; i++) {
            m_playerPickups[i] = new PlayerPickup(0, 0, m_mutateHandler.getRandomMutator());
        }

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

    }
    public Player getPlayer()
    {
        return m_player;
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
        if(!thread.isRunning())
        {
            thread = new GameThread(getHolder(), getContext(), new Handler());
            thread.setRunning(true);
            thread.setPaused(true);
            thread.start();
        }
        else {
            thread.start();
        }
    }

    public void playerCollision()
    {
        for(int i = 0; i < MAX_ENEMIES; i++)
        {
            if(m_enemyPool[i].m_isActive) {
                if (m_player.checkCollision(m_enemyPool[i])) {
                    m_enemyPool[i].m_isActive = false;
                    if (!m_player.hasInvulnerability()) {
                        if (!m_player.playerHit()) {
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                m_player.setMutatorFromConfig(m_mutateHandler.getMutator(Mutator.MutatorType.SHIELD));
                                m_player.m_isActive = true;
                                }
                            }, 3000); //Wait 3 seconds
                        } else {
                            if (getContext() instanceof GameActivity) {
                                ((GameActivity) getContext()).onDeath();
                                thread.setRunning(false);
                            }
                            //Do something here to move to highscores
                        }
                    }
                }
            }
        }
        for(int i = 0; i < MAX_PICKUPS; i++)
        {
            if(m_playerPickups[i].m_isActive) {
                if (m_player.checkCollision(m_playerPickups[i])) {
                    m_playerPickups[i].onPickup(m_player);
                }
            }
        }
    }

    private void bulletCollision()
    {
        //List<Collidable> returnCollidables = new ArrayList<Collidable>();
        for(int i = 0; i < MAX_PLAYER_BULLETS; i++)
        {
            if(m_playerProjectiles[i].m_isActive)
            {
                for(int x = 0; x < MAX_ENEMIES; x++)
                {
                    if(m_enemyPool[x].m_isActive)
                    {
                        if(m_playerProjectiles[i].checkCollision(m_enemyPool[x]))
                        {
                            if(m_enemyPool[x].checkDeadAfterHit(10, true))
                            {
                                m_player.m_currentScore += m_enemyPool[x].m_score;
                                generatePickup(m_enemyPool[x]);
                            }
                            m_playerProjectiles[i].m_isActive = false;
                        }
                    }
                }
            }
        }
    }
    private void poisonCollision()
    {
        for(int i = 0; i < m_player.m_poisonCollide.length; i++)
        {
            if(m_player.m_poisonCollide[i].m_isActive)
            {
                for(int x = 0; x < MAX_ENEMIES; x++)
                {
                    if(m_enemyPool[x].m_isActive)
                    {
                        if(m_player.m_poisonCollide[i].checkCollision(m_enemyPool[x]))
                        {
                            if(m_enemyPool[x].checkDeadAfterDebuff(m_player.m_poisonCollide[i].getDamage(), true))
                            {
                                m_player.m_currentScore += m_enemyPool[x].m_score;
                                generatePickup(m_enemyPool[x]);
                            }
                        }else
                        {
                            m_enemyPool[x].slowEnemy(false);
                        }
                    }
                }
            }
        }
    }

    private void mutatorCollision()
    {
        if(m_player.getMutator().m_isActive) {

            for (int i = 0; i < MAX_ENEMIES; i++) {
                if (m_enemyPool[i].m_isActive) {
                    if (m_player.getMutator().checkCollision(m_enemyPool[i])) {
                        switch (m_player.getMutator().getType()) {
                            case FIRE:
                                m_enemyPool[i].setFireDamage(m_player.getMutator().getDamage());
                                break;
                            case FREEZE:
                                m_enemyPool[i].setFrozen();
                                break;
                            case SHIELD:
                                if (m_enemyPool[i].checkDeadAfterHit(m_player.getMutator().getDamage(), true)) {
                                    m_player.m_currentScore += m_enemyPool[i].m_score;
                                    generatePickup(m_enemyPool[i]);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    private void generatePickup(Enemy e)
    {
        int chance = new Random().nextInt(10);
        if(chance < 3)
        {
            for(PlayerPickup p : m_playerPickups)
            {
                if(!p.m_isActive)
                {
                    p.setFromConfig(m_mutateHandler.getRandomMutator());
                    p.m_isActive = true;
                    p.setPosition(e.m_position.x, e.m_position.y);
                    return;
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
        bulletCollision();
        poisonCollision();
        mutatorCollision();
        if(m_player.m_isActive) //We're invincible with a shield on)
            playerCollision();
        if(m_player.m_shouldPause)
            thread.setPaused(true);

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
        m_player.draw(paint, c);
        for(int i = 0; i<MAX_ENEMIES; i++)
        {
            m_enemyPool[i].draw(paint, c);
        }
        for(int i = 0; i< MAX_PLAYER_BULLETS; i++)
        {
            m_playerProjectiles[i].draw(paint, c);
        }
        for(PlayerPickup p : m_playerPickups)
        {
            p.update();
            p.draw(paint, c);
        }
        post(new Runnable() {
            @Override
            public void run() {
                m_scoreUI.setText(Long.toString(m_player.m_currentScore));
                m_livesUI.setText(Integer.toString(m_player.getHealth()));
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

    public static Bitmap loadBitmap(int resDrId, Context context)
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
