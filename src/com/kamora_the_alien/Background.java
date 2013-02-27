package com.kamora_the_alien;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kamora_the_alien.ParallaxBackground2d.Parallax2dEntity;

/** This is the Background class, background images get passed in from BaseLevel and are set up.
 * 	@author Kiera O'Reilly
 * */
public class Background {

	public base_class base;


	private BitmapTextureAtlas backgroundTextureAtlas;
	private TextureRegion backgroundTextureRegion;


	/** The constructor for the background class is where the background is created.  It takes parameters from the BaseLevel class, which are received from
	 *  the JSON file for each level..  It loads the textures in first.  Then, a check is done to see what kind of level I am dealing with, and if
	 *  it is bonus, I set the walls, as the level uses the accelerometer.  Otherwise I add the background and it sets the parallax background.
	 * @param base_class 
	 * @param image 
	 * 			-image passsed through from the JSON parser to be part of the background
	 * @param sizeY 
	 * 			- Y size of the image
	 * @param sizeX 
	 * 			- X size of the image
	 * @param posX
	 * 			- X pos of the image
	 * @param posY
	 * 			- Y pos of the image
	 * @param pX
	 * 			- Parrallax 
	 * @param pY
	 * 			- Parralax
	 * */
	public Background(base_class my_base_class, String image, int sizeX, int sizeY, int posX, int posY, String pX, String pY){
		base = my_base_class;

		float parallaxX = Float.parseFloat(pX);
		float parallaxY = Float.parseFloat(pY);		

		/* Here I am loading in the background, I define the size and the image name and load it in to memory */
		backgroundTextureAtlas = new BitmapTextureAtlas(base.getTextureManager(),sizeX,sizeY, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas, base, image, 0, 0);
		backgroundTextureAtlas.load();

		if(BaseLevel.typeOfLevel.equals("bonus")){
			/* This is the code to create a wall around the edge of the phone screen  */
			final Rectangle ground = new Rectangle(0, base.CAMERA_HEIGHT - 35, base.CAMERA_WIDTH, 0, base.vertexBufferObjectManager);
			final Rectangle roof = new Rectangle(0, 35, base.CAMERA_WIDTH, 0, base.vertexBufferObjectManager);
			final Rectangle left = new Rectangle(40, 0, 0, base.CAMERA_HEIGHT, base.vertexBufferObjectManager);
			final Rectangle right = new Rectangle(base.CAMERA_WIDTH - 40, 0, 0, base.CAMERA_HEIGHT, base.vertexBufferObjectManager);

			ground.setUserData("wall");
			roof.setUserData("wall");
			left.setUserData("wall");
			right.setUserData("wall");

			/* set the wall and then attach it to a scene */
			final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
			PhysicsFactory.createBoxBody(base.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef).setUserData("wall");
			PhysicsFactory.createBoxBody(base.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef).setUserData("wall");
			PhysicsFactory.createBoxBody(base.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef).setUserData("wall");
			PhysicsFactory.createBoxBody(base.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef).setUserData("wall");



			base.getEngine().getScene().attachChild(ground);
			base.getEngine().getScene().attachChild(roof);
			base.getEngine().getScene().attachChild(left);
			base.getEngine().getScene().attachChild(right);
		}


		BaseLevel.ParallaxBackground.attachParallaxEntity(new Parallax2dEntity(parallaxX, parallaxY, new Sprite(posX, posY, backgroundTextureRegion, base.getVertexBufferObjectManager()),false,false));




	}



}
