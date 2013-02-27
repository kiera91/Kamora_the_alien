package com.kamora_the_alien;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;

/** This is the maincharacter class, this is where the main character that is used in each of the levels is created.  
 * 	@author Kiera O'Reilly
 * */
public class maincharacter {

	/** Set the class variables */
	public base_class base;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mGuyTextureRegion;
	private FixtureDef mainCharFixtureDef;
	private MassData defaultMassData;
	public AnimatedSprite mainGuy;
	static Body bCharBody;
	

	/** This is the maincharacter constructor, passed through are:
	 *  @param the_base
	 *			- The reference to the base class base_class
	 *  @param characterXPos
	 *  		- The starting X position of the character.
	 *  @param characterYPos
	 *  		- The starting Y position of the character.
	 *  @param scale
	 *  		- The scale that the character needs to be.  This differs between main levels and bonus levels as the character needs to be smaller.
	 *  
	 *  Here the textures are loaded into memory.  The sprite for the maincharacter is created.  It is an animated sprite so that animations can occur when the character is walking, 
	 *  standing or jumping.  Physics are created, these will determine the density, elasticity and friction one the character.  
	 *  When the maincharacter class is called, the scale variable is passed through, this means a new class for smallMainCharacter does not need to be created.  The scale is set before the 
	 *  body is created so that the body is made to the same scale as the sprite.
	 *  FixedRotation is also set to true so that the maincharacter is unable to "fall over" and rotate 90 degrees or more when jumping etc.
	 *  The defaultMassData must be set on the character body so that it is able to move with the given physics.  With different images sizes, objects will react differently to the physics
	 *  so this needs to be set, rather than having to change the physics.
	 *  UserData is added to the character so that when a collision occurs it can specifically be checked for.
	 * */
	public maincharacter(base_class the_base, String image, int[] data, double scale) {

		base = the_base;
		
		mBitmapTextureAtlas = new BitmapTextureAtlas(base.getTextureManager(),data[0], data[1], TextureOptions.BILINEAR);
		mGuyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, base, image, 0, 0, 6, 3);
		mBitmapTextureAtlas.load();
		
		
		
		mainGuy = new AnimatedSprite(data[2], data[3], mGuyTextureRegion.deepCopy(), base.getVertexBufferObjectManager());
		mainCharFixtureDef = PhysicsFactory.createFixtureDef(0.8f, 0f, 0.4f, false);
		
		mainGuy.setScale((float) scale);
		mainCharFixtureDef = PhysicsFactory.createFixtureDef(0.8f, 0f, 0.4f, false);
		bCharBody = PhysicsFactory.createBoxBody(base.mPhysicsWorld, mainGuy, BodyType.DynamicBody, mainCharFixtureDef);
		bCharBody.setFixedRotation(true);
		
		/* TO-DO - check my javadoc about this is right */
		defaultMassData = bCharBody.getMassData();
		defaultMassData.mass = 2;
		bCharBody.setMassData(defaultMassData);
		base.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mainGuy, bCharBody, true, true));
		bCharBody.setUserData("player");
		base.getEngine().getScene().attachChild(mainGuy);	

	}

	/** This method returns the main character body. */
	public Body getBody() {
		return bCharBody;
	}

	/** This method returns the sprite of the main character. */
	public AnimatedSprite getSprite() {
		return mainGuy;
	}

	/* TO-DO - check if this is needed */
	public float getY() {
		return mainGuy.getY();
	}

	/** This method returns the X position of the main character. */
	public float getX() {
		return mainGuy.getX();
	}
	
	/** This method returns the character life. */
	public int getLife(){
		return base.life;
	}

	
}

