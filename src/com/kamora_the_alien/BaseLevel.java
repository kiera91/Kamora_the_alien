package com.kamora_the_alien;

import java.util.ArrayList;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.json.JSONArray;
import org.json.JSONException;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/** This is the BaseLevel class, all data passed through from the level JSON files will get processed here.
 * 	@author Kiera O'Reilly
 * */
public class BaseLevel implements Runnable {

	/* Create the initial variables that are accessed all through the class. */
	public base_class base;
	private BitmapTextureAtlas frontF;
	private ITextureRegion frontFR;
	private BitmapTextureAtlas floor;
	private ITextureRegion floorR;
	private IUpdateHandler myHandler;
	public static ParallaxBackground2d ParallaxBackground;
	public Scene scene = new Scene();
	public int next = 0;
	static String typeOfLevel;

	/** 
	 * 	The constructor for the BaseLevel class.  It takes no parameters, but simply creates a new scene.  It is the first thing that is called when 
	 *  creating a new level.
	 */
	public BaseLevel(base_class my_base_class){
		base = my_base_class;		
		scene.attachChild(new Entity());
		scene.setOnAreaTouchTraversalFrontToBack();
		base.getEngine().setScene(scene);
	}

	/** 
	 * 	The loadLevel method is the last to be called when processing all the data from the JSON.  It sets up the final variables, including music
	 * 	and what level to load next.  In here, the Hud is set, adn the controls.  It checks to see if the level is of type bonus, and if it is, hide
	 * 	the controls and start the bonus level timer.
	 * 	@param endOfLevel 
	 * 			-the number of pixels the character can walk until the next level is loaded.
	 * 	@param music
	 * 		  	-the music file to be played throughout the level.
	 * 	@param nextLevel
	 * 			-the next JSON file to be processed. 
	 * @param linearDampling 
	 * @param velocity 
	 *  */
	public void loadLevel(final Integer endOfLevel, String music, final int nextLevel, String linearDampling, String velocity){

		
		next = nextLevel;
		if(base.isMuted == false){
			MusicManager.startMusic(music, true);
		}

		base.hud = new Hud(base);

		base.getEngine().getScene().setChildScene(base.theControls.my_control_variable);

		if(typeOfLevel.equals("accelerometer")){
			base.getEngine().getScene().getChildScene().setVisible(false);
			createTimerHandler();
			base.myGuy.getBody().setLinearDamping(Float.parseFloat(linearDampling));
		}else if(typeOfLevel.equals("accelerometer-velocity")){
			base.getEngine().getScene().getChildScene().setVisible(false);
			base.myGuy.getBody().setLinearDamping(Float.parseFloat(linearDampling));
			String[] velocityA = velocity.split(",");
			base.myGuy.getBody().setLinearVelocity(Float.parseFloat(velocityA[0]),Float.parseFloat(velocityA[1]));
		}


		/** Register the update handler on the world. TO-DO - check that this is needed along with the below */
		base.getEngine().getScene().registerUpdateHandler(base.mPhysicsWorld);

		myHandler = new IUpdateHandler(){
			@Override
			/** This is an update handler specific to this level, it checks the character X position in the level and once it's reached the specified
			 * number of pixels, it will then load the bonus level.  This update handler is also checking for collisions, it checks in the collision
			 * class.  If a collision is detected, it removes the body and sprite from the appropriate arrays.  It checks the score, and updates 
			 * after a collision has occurred.
			 * I also constantly check the position of the moving platform so that it is known when it has reached it's boundary and the code will
			 * be called to change it's direction.
			 *  */
			public void onUpdate(float pSecondsElapsed) {

				if(typeOfLevel.equals("main")){
					if (base.myGuy.getX() > endOfLevel){
						MusicManager.music.stop();
						scene.unregisterUpdateHandler(myHandler);
						base.loadLevel(nextLevel);
					}	
				}

				synchronized(base.toRemoveOnUpdate){
					for (int i = 0; i < base.toRemoveOnUpdate.size(); i++) {
						final Body body = base.toRemoveOnUpdate.get(0);
						body.setActive(false);
						base.toRemoveOnUpdate.remove(0);
					}
				}

				base.hud.scoreText.setText("Score: " + base.score);
			}



			@Override
			public void reset() {

			}


		};
		/** Register the above update handler */
		scene.registerUpdateHandler(myHandler);

		/** Set the current scene to this scene. */
		base.getEngine().setScene(scene);

	}

	/** This method will take the JSON arrays from the JSON parser, and then return an array of integers that can be accessed easily.
	 * 	@param size
	 * 			-JSONArray containing the size of the image.
	 * 	@param pos	
	 * 			-JSONArray containing the position of the image
	 * 	@param currentPos
	 * 			-int used to check what element of the position array we are on, if it contains more that one value.
	 * 	@param length
	 * 			-specifies the length of the position array.
	 *  */
	public int[] getPosAndSize(JSONArray size, JSONArray pos, int currentPos, int length){

		int[] data;
		data = new int[4];
		int sizeX = 0;
		int sizeY = 0 ;
		int posX = 0;
		int posY = 0;
		if(currentPos >= 0 && length >= 1){
			try {
				String is = pos.get(currentPos).toString();
				is = is.substring(1,is.length()-1);
				String[] sizeArray = is.split(",");
				posX = Integer.parseInt(sizeArray[0]);
				data[2] = posX;
				posY = Integer.parseInt(sizeArray[1]);
				data[3]= posY;


			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			sizeX = size.getInt(0);
			data[0] = sizeX;
			sizeY = size.getInt(1);
			data[1] = sizeY;
			if(length == 0){
				posX = pos.getInt(0);
				data[2] = posX;
				posY = pos.getInt(1);
				data[3] = posY;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return data;

	}

	/** Method which will load a texture 
	 * 	@param textureAtlas
	 * 		- 
	 * 	@param textureRegion
	 * 		-
	 * 	@param size
	 * 		- Size of the image to be used
	 * 	@param image
	 * 		- the image to be used
	 * */
	public ITextureRegion loadTextures(BitmapTextureAtlas textureAtlas, ITextureRegion textureRegion, JSONArray Size, String image){

		int sizeX = 0;
		int sizeY = 0;
		try {
			sizeX = Size.getInt(0);
			sizeY = Size.getInt(1);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		textureAtlas = new BitmapTextureAtlas(base.getTextureManager(),sizeX, sizeY, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, base, image , 0, 0) ;
		textureAtlas.load();

		return textureRegion;

	}

	/** This method is called at the beginning of the setting up stage, it sets a few important variables about the level
	 *  such as the level number and the level boundaries.  
	 * 	@param level
	 * 			- the level number
	 * 	@param levelType
	 * 			- specifies whether the level is a bonus or normal
	 * 	@param size
	 * 			- the size of the level boundaries.
	 * 		
	 * */
	public void setLevel(int level, String levelType, JSONArray size) throws JSONException{
		base.level = level;	
		int width = size.getInt(0);
		base.LANDSCAPE_WIDTH = width;
		base.LANDSCAPE_HEIGHT = size.getInt(1);
		typeOfLevel = levelType;
		base.collision = new Collision(base);
		if(typeOfLevel.equals("accelerometer") || typeOfLevel.equals("accelerometer")){
			base.getEngine().enableAccelerationSensor(base,base);
		}
	}	

	/** This is a standard method which will process various object from the JSON which all have the same attributes.  It is passed an arraylist of arraylists
	 * 	which are then looped through to extract all the data needed.  When this is complete there are some checks in place to see which class needs to 
	 * 	be called.
	 * 	@param object
	 * 		- the arraylist passed through from the JSON parser containing data about an element of the game.
	 * */
	public void processObject(ArrayList<ArrayList> object){
		double extra = 0;

		for(int i = 0; i < object.size(); i++){
			String userData = object.get(i).get(0).toString();
			JSONArray pos = (JSONArray) object.get(i).get(1);
			String image = object.get(i).get(2).toString();
			JSONArray size = (JSONArray) object.get(i).get(3);
			if(object.get(i).size() > 4){
				extra = (Double) object.get(i).get(4);	
			}

			for(int j = 0; j < pos.length(); j++){
				int[] posSize = getPosAndSize(size,  pos, j, pos.length());
				if(userData.equals("main_diamond") || userData.equals("bonus_diamond")){
					new MainDiamond(base, userData, image, posSize[0], posSize[1], posSize[2], posSize[3]);

				}
				else if(userData.equals("moving")){
					new MovingPlatform(base, userData, image, posSize[0], posSize[1], posSize[2], posSize[3]);
				}
				else{
					new Item(base, userData, image, posSize[0], posSize[1], posSize[2], posSize[3], extra);
				}
			}

		}
	}

	/** The setFloor method is called if the game has a floor, it will set it to the correct height and repeat it across the level.  An invisible
	 * 	physical floor is put in which will mean the character cannot go below it.  The physics world is registered and user data of the floor. 
	 * 	Invisible rectangles are placed at either end of the level preventing the character from walking off screen.
	 * 	@param floorSize
	 * 			- the size of the floor image
	 * 	@param image
	 * 			- the image to be used for the floor
	 *  */
	public void setFloor(JSONArray floorSize, String image){
		floorR = loadTextures(floor, floorR, floorSize, image);
		for (int i = 0;i<40;i++){
			Sprite floorSprite = new Sprite(i*2048, base.CAMERA_HEIGHT-300, floorR.deepCopy(), base.getVertexBufferObjectManager() );
			base.getEngine().getScene().attachChild(floorSprite);		
		}

		FixtureDef floorFixtureDef = PhysicsFactory.createFixtureDef(1000f, 0f, 0.95f);
		Rectangle  shFloor = new Rectangle(0, base.CAMERA_HEIGHT-90, base.LANDSCAPE_WIDTH, 0, base.getVertexBufferObjectManager());  

		Body bfloorBody = PhysicsFactory.createBoxBody(base.mPhysicsWorld, shFloor, BodyType.StaticBody, floorFixtureDef);
		bfloorBody.setUserData("floor");
		base.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(shFloor, bfloorBody, true, true));

		Rectangle shFront = new Rectangle(0,0,0, base.LANDSCAPE_HEIGHT, base.getVertexBufferObjectManager());			
		PhysicsFactory.createBoxBody(base.mPhysicsWorld, shFront, BodyType.StaticBody, floorFixtureDef).setUserData("edge");
		Rectangle shEnd = new Rectangle(base.LANDSCAPE_WIDTH,0,0,base.LANDSCAPE_HEIGHT, base.getVertexBufferObjectManager() );
		PhysicsFactory.createBoxBody(base.mPhysicsWorld, shEnd, BodyType.StaticBody, floorFixtureDef).setUserData("edge");

	}

	/** This can probs somehow be included in the main set floor. */
	public void setFrontFloor(String frontImage, JSONArray frontSize){
		frontFR = loadTextures(frontF,frontFR, frontSize, frontImage);

		for (int i = 0;i<40;i++){ 
			Sprite crystalSprite= new Sprite(i*2048, base.CAMERA_HEIGHT-200, frontFR,base.getVertexBufferObjectManager());		
			base.getEngine().getScene().attachChild(crystalSprite);
		}
	}

	/** Set enemies method will set the first enemy in it's fixed position, then start the thread which will then randomly spawn enemies
	 * at a set interval.
	 * 	@param enemyImage
	 * 			- image used for the enemy
	 * 	@param enemySize
	 * 			- size of the enemy image
	 * 	@param enemyPos
	 * 			- starting point of the first enemy
	 * @param enemyParticles 
	 * */
	public void setEnemies(String enemyImage, JSONArray enemySize, JSONArray enemyPos, String enemyTimer){
		int[] posSize = getPosAndSize(enemySize, enemyPos, 0, 0);
		Enemy testEnemy = new Enemy(base, enemyImage, posSize[0], posSize[1], posSize[2], posSize[3], enemyTimer );
		testEnemy.startEnemies(base, enemyImage, posSize[0], posSize[1], posSize[2], posSize[3]);
	}

	/** The set background class takes an arraylist of arraylists much like the random object method.  It loops through each of the background layers 
	 * 	adding them to the scene.
	 * 	@param bg 
	 * 			- arraylist passed through from JSON parser
	 * */
	public void setBackground(ArrayList<ArrayList> bg) {

		ParallaxBackground = new ParallaxBackground2d(0, 0, 0);
		for(int i = 0; i < bg.size(); i++){
			String image = bg.get(i).get(1).toString();
			JSONArray size = (JSONArray) bg.get(i).get(2);
			JSONArray pos = (JSONArray) bg.get(i).get(3);
			JSONArray parallax = (JSONArray) bg.get(i).get(4);

			int[] posSize = getPosAndSize(size, pos, i, 0);

			try {
				new Background(base, image, posSize[0], posSize[1], posSize[2], posSize[3], parallax.get(0).toString(), parallax.get(1).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		base.getEngine().getScene().setBackground(ParallaxBackground);

	}

	/** Method which deals with setting up the main character.
	 * 	@param mcImage
	 * 			- image to be used for main character
	 * 	@param mcSize
	 * 			- size of the image to be used for the main character
	 * 	@param mcPos
	 * 			- starting postition of main character
	 * 	@param scale
	 * 			- scale to set the main character to
	 * */
	public void setMaincharacter(String mcImage, JSONArray mcSize, JSONArray mcPos, double scale){
		int[] charData = getPosAndSize(mcSize, mcPos, 0, 0);
		base.myGuy = new maincharacter(base, mcImage, charData, scale);	
	}


	/** The timer handler will be set off as soon as the user begins the bonus level.  It will give the user 25 seconds to collect as many diamonds as
	 * 	possible before the time runs out. */
	public void createTimerHandler()
	{

		float mEffectDamageDelay = 15f;
		base.getEngine().registerUpdateHandler(new TimerHandler(mEffectDamageDelay, new ITimerCallback()
		{          
			/** After the delay, stop the acceleration sensor, unregister the bonus handler and call the load level method for level 2.
			 */
			public void onTimePassed(final TimerHandler pTimerHandler)
			{  	
				base.getEngine().disableAccelerationSensor(base);
				base.mPhysicsWorld.setGravity(base.gravityAll);
				base.getEngine().getScene().unregisterUpdateHandler(myHandler);
				base.loadLevel(next);
			}
		}));
	}

	@Override
	public void run() {

	}



}
