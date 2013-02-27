package com.kamora_the_alien;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.math.MathUtils;
import org.json.JSONArray;
import org.json.JSONException;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/** This is the enemy class, this is where the enemy characters in the level are created.  
 * 	@author Kiera O'Reilly
 * */
public class Enemy {

	/** Set the class variables */
	public base_class base;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TextureRegion mGuyTextureRegion;
	private FixtureDef mainCharFixtureDef;
	public Sprite enemySprite;
	public Body bCharBody;
	public TimerHandler spriteTimerHandler;
	public boolean enemyShooting = false;
	public int enemyLife;
	protected static String bulletImage;
	protected static int bSizeX;
	protected static int bSizeY;
	protected static String velocityX;
	protected static String velocityY;
	private static JSONArray particleDirection;
	private static JSONArray particleVelocity;
	private static JSONArray particleColour;
	private static String particleImage;
	public String enemyTimer = null;

	/** This is the enemy constructor, passed through are:
	 *  @param the_base
	 *			- The reference to the base class base_class
	 * @param enemyTimer 
	 *  @param enemyXpos
	 *  		- The starting X position of the enemy.
	 *  @param enemyYpos
	 *  		- The starting Y position of the enemy.
	 *
	 *  
	 *  Here the textures are loaded into memory.  The sprite for the enemy is created. Physics are created, these will determine the density, elasticity
	 *  and friction on the emeny character.  
	 *  When the enemy class is initially called, I specify the first position.  Then however I call the startEnemies() method, which then spawns enemies
	 *  randomly every 10 seconds.
	 *  I set the enemy life so that they are not removed immediately and it's more difficult for the user.
	 *  UserData is added to the enemy so that when a collision occurs it can specifically be checked for.
	 *  An update handler is also attached to the enemy sprite which will then perform some basic AI when in a certain range of the players character.
	 * */
	public Enemy(base_class the_base, String image, int sizeX, int sizeY, float xPos, float yPos, String timer) {

		base = the_base;

		enemyTimer = timer;
		
		mBitmapTextureAtlas = new BitmapTextureAtlas(base.getTextureManager(),sizeX, sizeX, TextureOptions.BILINEAR);
		mGuyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, base, image, 0, 0);
		mBitmapTextureAtlas.load();

		enemySprite = new Sprite(xPos, yPos, mGuyTextureRegion, base.getVertexBufferObjectManager());	

		mainCharFixtureDef = PhysicsFactory.createFixtureDef(0.8f, 0f, 0.4f, false);
		bCharBody = PhysicsFactory.createBoxBody(base.mPhysicsWorld, this.enemySprite, BodyType.DynamicBody, mainCharFixtureDef);
		bCharBody.setFixedRotation(true);


		/* TO-DO - check my javadoc about this is right */
		base.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(enemySprite, bCharBody, true, true));

		enemyLife = 2;
		bCharBody.setUserData("enemy");
		base.enemyArray.add(this);

		base.getEngine().getScene().attachChild(enemySprite);	

		this.enemySprite.registerUpdateHandler(new IUpdateHandler(){
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(base.myGuy.getSprite().getX() >= enemySprite.getX()){
					enemySprite.setFlippedHorizontal(true);
				}
				else{
					enemySprite.setFlippedHorizontal(false);
				}

				if(base.myGuy.getSprite().getX() >= enemySprite.getX()-300){
					if(base.myGuy.getSprite().getX() >= enemySprite.getX()){
						if(enemyShooting == false){
							new Bullet(base, "enemy_bullet", bulletImage, bSizeX, bSizeY, velocityX, velocityY, "right", enemySprite);
							enemyShooting = true;
							createTimerEnemyHandler();
						}
					}
					else{
						if(enemyShooting == false){
							new Bullet(base, "enemy_bullet", bulletImage, bSizeX, bSizeY, velocityX, velocityY, "left", enemySprite);
							enemyShooting = true;
							createTimerEnemyHandler();
						}
					}

				}
			}

			@Override
			public void reset() {

			}
		});
	}

	/** This method returns the enemy body. */
	public Body getBody() {
		return bCharBody;
	}

	/** This method returns the sprite of the enemy. */
	public Sprite getSprite() {
		return this.enemySprite;

	}

	/** This method returns the X position of the enemy. */
	public  float getX() {
		return enemySprite.getX();
	}

	/** This method returns the life of the enemy object. */
	public int getLife(){
		return this.enemyLife;

	}

	/** This method attaches an update handler to the scene, then after a timer of 10 seconds, puts a sprite in a random
	 * 	postition within the coordinates that I have specified.
	 * @param j 
	 * @param i 
	 * @param enemyPos 
	 * @param enemySize 
	 * @param enemyImage 
	 * @param base2 
	 * */
	public void startEnemies(base_class the_base, final String eImage, final int sizeX, final int sizeY, float xPos, float yPos){
		base.getEngine().getScene().registerUpdateHandler(spriteTimerHandler = new TimerHandler(10f,true, new ITimerCallback()
		{        
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				spriteTimerHandler.reset();

				//Random Position Generator
				final float xPos = MathUtils.random(100, 4100);
				final float yPos = MathUtils.random(90, 100);
				new Enemy(base, eImage, sizeX, sizeY, xPos, yPos,enemyTimer);

			}
		}));

	}

	/** The removeEnemy() method removes the specific sprite form the scene. 
	 * @throws JSONException */
	public synchronized boolean removeEnemy() throws JSONException {
		if (base.enemyArray.indexOf(this) != -1) {
			if(particleImage != null)
				new ParticleExplosion(base, particleImage, this.getX(), this.getSprite().getY(), particleDirection, particleVelocity, particleColour);

			final PhysicsConnector blockPhysicsConnector = base.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(this.enemySprite);
			base.mPhysicsWorld.unregisterPhysicsConnector(blockPhysicsConnector);
			this.getSprite().setIgnoreUpdate(true);
			this.getSprite().setVisible(false);
			base.getEngine().getScene().detachChild(this.enemySprite);
			base.toRemoveOnUpdate.add(this.getBody());
			base.enemyArray.remove(base.enemyArray.indexOf(this));
		}
		return true;
	}


	/** This method is called from the JSONParser, like with the main character bullet data. */
	public static void setBulletVars(String bImage, JSONArray bulletSize,
			JSONArray velocity) {
		bulletImage = bImage;
		try {
			bSizeX = (Integer) bulletSize.get(0);
			bSizeY = (Integer) bulletSize.get(1);
			velocityX = (String) velocity.get(0);
			velocityY = (String) velocity.get(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	

	public static void setParticleVars(String image, JSONArray direction, JSONArray velocity, JSONArray colour) throws JSONException{
		particleDirection = direction;
		particleVelocity = velocity;
		particleColour = colour;
		particleImage = image;
	}
	
	
	public void createTimerEnemyHandler()
	{

		float mEffectDamageDelay = Float.parseFloat(enemyTimer);
		base.getEngine().registerUpdateHandler(new TimerHandler(mEffectDamageDelay, new ITimerCallback()
		{          
			/** After the delay, move the character body back by 15 pixels to give it the effect of jumping back, the text is removed from
			 *  the screen and to stop a constant collision with the spikes, it then calls the setHealth method.
			 */
			public void onTimePassed(final TimerHandler pTimerHandler)
			{  	
				enemyShooting = false;
			}
		}));
	}



}

