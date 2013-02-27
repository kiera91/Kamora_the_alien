package com.kamora_the_alien;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/** This is the generic item class, it is only called upon from within it's subclasses e.g. MainDiamond.  There are no hardcoded values in here, everything
 * 	needed to create the texture and sprite are passed in.  The item class should never be called on it's own.
 * 	@author Kiera O'Reilly
 * */
public class Item {

	public base_class base;
	private BitmapTextureAtlas item;
	private ITextureRegion titem;
	Sprite sItem;
	private FixtureDef mainItemFixtureDef;
	private Body bitemBody;
	private AnimatedSprite sAItem;
	private ITiledTextureRegion tAitem;

	/** This is the constructor for the Item class, it takes lot of parameters as it is generic.  In here, the LoopEntityModifier is set as I want all my 
	 * 	collectible items to be rotating.  The textures are then loaded into the engine and a sprite created with those textures applied.  Physics are created
	 * 	for the item, as we need to be able to collide with it.  We create a static body so that it is not affected by gravity.  UserData that is passed into
	 * 	this method is applied so we can test for the specific collisions.   
	 * 	 @param the_base
	 *			- The reference to the base class base_class
	 * 	 @param string
	 *  		- The starting X position of the object.
	 *   @param image
	 *  		- The starting Y position of the object.
	 *   @param sizeX
	 *   		- a string to hold the user data for this item, e.g "main_diamond".
	 *   @param sizeY
	 *   		- a string to hold the image name for the item
	 * @param extra 
	 *   @param xDim
	 *   		- an int to hold the X dimension of the image
	 *   @param yDim
	 *   		- an int to hold the Y dimension of the image.	
	 * */
	public Item(base_class the_base,String userData, String image, int sizeX, int sizeY, int posX, int posY, double extra) {
		this.base = the_base;

		item = new BitmapTextureAtlas(base.getTextureManager(),sizeX, sizeY, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		if(userData.equals("lava")){
			tAitem = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(item, base, image, 0, 0, 15, 1);
			item.load();
			sAItem = new AnimatedSprite(posX, posY, tAitem.deepCopy(), base.getVertexBufferObjectManager());
			sAItem.animate(new long[] {200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200 }, new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14 },9999);
		}else{
			titem = BitmapTextureAtlasTextureRegionFactory.createFromAsset(item, base, image, 0, 0);
			item.load();
			sItem = new Sprite (posX, posY,titem, base.getVertexBufferObjectManager());
		}
		/** Set the physics on the item, make it a static body so that it is not affected by gravity - TO-DO CHECK THIS IS RIGHT
		 * TO-DO CHECK NEED FIXTUREDEF AND CHECK PHYSICS CONNECTION
		 *  */
		mainItemFixtureDef = PhysicsFactory.createFixtureDef(1f, 0f, 0.5f);	
		if(sItem == null){
			bitemBody = PhysicsFactory.createBoxBody(base.mPhysicsWorld, this.sAItem, BodyType.StaticBody, mainItemFixtureDef);
			base.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(sAItem, bitemBody, true, true));
		}else{
			bitemBody = PhysicsFactory.createBoxBody(base.mPhysicsWorld, this.sItem, BodyType.StaticBody, mainItemFixtureDef);
			base.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(sItem, bitemBody, true, true));
		}


		bitemBody.setUserData((String) userData);
		base.removeItems.add(this);

		if(extra != 0.0 ){
			if(userData.equals("wall")){	
				bitemBody.setTransform(bitemBody.getWorldCenter(), (float) Math.toRadians(extra));
				sItem.setRotation((float) extra);

			}
		}

		if(sItem == null){
			base.getEngine().getScene().attachChild(this.sAItem);	

		}else{
			base.getEngine().getScene().attachChild(this.getSprite());	
		}
		/** TO-DO CHECK IF THIS IS NEEDED */
		//base.getEngine().getScene().registerTouchArea(sItem);


	}
	/* TO-DO - check if these are needed for this class */
	/** This method returns the body of the item */
	public Body getBody() {
		return bitemBody;
	}

	/** This method returns the sprite of the item */
	public Sprite getSprite() {
		return sItem;
	}

	/** This method returns the Y position of the item */
	public float getY() {
		return sItem.getY();
	}

	/** This method returns the X position of the item */
	public float getX() {
		return sItem.getX();
	}

	public Body getUserData() {
		return null;
	}

	public synchronized boolean removeItem() {
		if (base.removeItems.indexOf(this) != -1) {
			final PhysicsConnector blockPhysicsConnector = base.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(this.sItem);
			base.mPhysicsWorld.unregisterPhysicsConnector(blockPhysicsConnector);
			this.getSprite().setIgnoreUpdate(true);
			this.getSprite().setVisible(false);
			base.getEngine().getScene().detachChild(this.sItem);
			base.toRemoveOnUpdate.add(this.getBody());
			base.removeItems.remove(base.removeItems.indexOf(this));
		}
		return true;
	}

}
