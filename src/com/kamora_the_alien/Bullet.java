package com.kamora_the_alien;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;


/** This is the bullet class, this is where the bullets are created when the player uses the shoot control.  Or when the enemy
 * 	is triggered to fire at the main player.  
 * 	@author Kiera O'Reilly
 * */
public class Bullet {

	public base_class base;
	private BitmapTextureAtlas tBullet;
	private TextureRegion trBullet;
	private Sprite sBullet;
	private FixtureDef mainBulletFixtureDef;
	private Body bBulletBody;
	private Vector2 mVelocity;
	
	/** The constructor for the bullet class is what creates the bullet.  It loads the textures in first.  Then I have put a check in to see which way
	 * 	the main character is facing so that can set the correct start position and direction of the bullets.
	 * 	I set the user data on the bullet so it can be checked for in the collision class.  
	 * 	A timer handler is attached to the sprite and after 1 second, which is the time that it takes for the bullet to be off screen, the bullet is 
	 * 	removed.
	 * @param velocityY 
	 * 			- Y velocity of the bullet
	 * @param velocityX 
	 * 			- X velocity of the bullet
	 * @param sizeY 
	 * 			- Y size of the bullet image
	 * @param sizeX 
	 * 			- X size of the bullet image
	 * @param userData
	 * 			- user data to be set to the bullet
	 * @param image
	 * 			- image to use for the bullet
	 * @param direction
	 * 			- which direction do we need to shoot in
	 * @param charSprite
	 * 			- which character is shooting
	 * */
	public Bullet(base_class the_base, String userData, String image, int sizeX, int sizeY, String velocityX, String velocityY, String direction, Sprite charSprite){

		base = the_base;	

		tBullet = new BitmapTextureAtlas(base.getTextureManager(),sizeX, sizeY, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		trBullet = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tBullet, base, image , 0, 0);
		tBullet.load();
		float shootXPos = 0;
		float shootYPos = 0;

		/* This if statement set is performed to see if the main character is shooting, if he is facing the other way, change the start position
		 *  of the bullet.  However if the direction is equal to left, it means it is the enemy shooting and depending on whether the player is 
		 *  further ahead of the enemy, it will set where to start the enemy bullet from, with the velocity passed through from the JSON. */
		if(userData.equals("friendly_bullet")){
			if(base.myGuy.getSprite().isFlippedHorizontal() == false){
				mVelocity = Vector2Pool.obtain(Float.parseFloat(velocityX), Float.parseFloat(velocityY));    
				shootXPos = charSprite.getX() + base.myGuy.getSprite().getWidth();
				Vector2Pool.recycle(mVelocity);
			}
			else{
				mVelocity = Vector2Pool.obtain(Float.parseFloat(velocityX)*-1, Float.parseFloat(velocityY));  
				shootXPos = charSprite.getX() -5;
				Vector2Pool.recycle(mVelocity);
			}
		}
		else{
			if(direction.equals("left")){
				mVelocity = Vector2Pool.obtain(Float.parseFloat(velocityX)*-1, Float.parseFloat(velocityY));  
				shootXPos = charSprite.getX() -5;
				Vector2Pool.recycle(mVelocity);
			}
			else{				
				mVelocity = Vector2Pool.obtain(Float.parseFloat(velocityX), Float.parseFloat(velocityY));    
				shootXPos = charSprite.getX() + charSprite.getWidth();
				Vector2Pool.recycle(mVelocity);
			}

		}
		shootYPos = charSprite.getY()+30;

		sBullet = new Sprite (shootXPos, shootYPos, trBullet, base.getVertexBufferObjectManager());

		mainBulletFixtureDef = PhysicsFactory.createFixtureDef(1f, 0f, 0.5f);		
		bBulletBody = PhysicsFactory.createBoxBody(base.mPhysicsWorld, this.sBullet, BodyType.KinematicBody, mainBulletFixtureDef);
		base.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(sBullet, bBulletBody, true, true));

		bBulletBody.setUserData(userData);
		base.bulletArray.add(this);
		base.getEngine().getScene().attachChild(sBullet);	


		getBody().setLinearVelocity(mVelocity);

		/** This is an update handler attached to all bullets that are created.  It's spawn time is 0.5 seconds. After that a method is called to remove
		 * the bullet from the scene. */
		this.sBullet.registerUpdateHandler(new TimerHandler(0.5f,false, new ITimerCallback()
		{        
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				base.runOnUpdateThread(new Runnable() {

					@Override
					public void run() {
						removeTheBullet();
					}


				});


			}
		}));


	}


	/** Thie method returns the body of the bullet. */
	public Body getBody(){
		return bBulletBody;
	}

	/** This method returns the sprite of the bullet. */
	public Sprite getSprite(){
		return sBullet;
	}

	

	/** This method is called when the bullet needs to be removed.  It looks through the array of bullets to find the bullet, removes the physics connection, it
	 *  also ignores any updates from the sprite, sets it to invisible, then detaches it from the scene.  We add the body to the toRemoveOnUpdate arraylist
	 *  for the level, as if we destroyed it here, there would be a physics crash.  We finally remove the bullet from the bulletArray. 
	 *  */
	public synchronized boolean removeTheBullet() {
		if (base.bulletArray.indexOf(this) != -1) {
			final PhysicsConnector blockPhysicsConnector = base.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(this.sBullet);
			base.mPhysicsWorld.unregisterPhysicsConnector(blockPhysicsConnector);
			this.getSprite().setIgnoreUpdate(true);
			this.getSprite().setVisible(false);
			base.getEngine().getScene().detachChild(this.sBullet);
			base.toRemoveOnUpdate.add(this.getBody());
			base.bulletArray.remove(base.bulletArray.indexOf(this));
		}
		return true;
	}



}
