package com.kamora_the_alien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.widget.Toast;

/** This is the base class for the game.  It extends BaseGameActivity, an Andengine class.  It implements IAccelerationListener, another Andengine class </br>
 * which allows now for the use of the accelerometer within the game. Andengine is a game engine that makes it easier to create a game.  It deals with the  </br>
 * openGL side of creating animated sprites, and so on.  </br>
 * The base_class is linked with nearly all my other classes, it is used as a central reference point, it contains variables that need to be used across the game  </br>
 * and methods that handle the loading and unloading of levels. </br>
 * I override some methods from the BaseGameActivity which sets up some initial settings for the game.</br>
 * */
public class base_class extends BaseGameActivity implements IAccelerationListener {

	public Engine mEngine;

	public PhysicsWorld mPhysicsWorld;

	/* Create a new BoundCamera.  This type of camera can move. */
	public BoundCamera MainCamera;
	/*Set Camera height and width variables - 
	this is the size of my phones pixel resolution - It is also the ratio of the screen size */
	public final int CAMERA_WIDTH = 800;
	public final int CAMERA_HEIGHT = 480;
	/* The Landscape variables will set the size of the level, this is set in the JSON file and can be changed.  The camera is now bound to these dimensions.
	 * It will not stop the character from moving outside them.
	 */
	public int LANDSCAPE_WIDTH = 8600;
	public int LANDSCAPE_HEIGHT = 960;

	/* Classes */
	public maincharacter myGuy;
	public Item Item;
	public lifeDisplay lifeDisplay;
	public MovingPlatform movingPlatform;
	public RockU rocku;
	public MainDiamond MainDiamond;
	public JsonParser JsonParser;
	public Hud hud;

	public Scene currentScene;
	public Controls theControls;
	public Collision collision;
	public Database database;


	public VertexBufferObjectManager vertexBufferObjectManager;

	/* Create synchronized arrays for items bullets and enemies.  This will stop concurrent modification errors. */
	public ArrayList<Item> removeItems = new ArrayList<Item>(); 
	public List<Bullet> bulletArray = Collections.synchronizedList(new ArrayList<Bullet>());
	public List<Enemy> enemyArray = Collections.synchronizedList(new ArrayList<Enemy>());
	public List<Body> toRemoveOnUpdate = Collections.synchronizedList(new ArrayList<Body>());

	public Font mFont;
	public Text elapsedText;

	public int level = 0;

	private float mGravityX;
	private float mGravityY;
	final Vector2 gravityAll = Vector2Pool.obtain(0, SensorManager.GRAVITY_JUPITER);
	public boolean isMuted = false;

	public String dbLevel;
	public String[] dPos;
	public int dbScore;
	public int dbHealth;
	public int score = 0;
	public int life = 6;

	private boolean mHasWindowFocused;
	public SharedPreferences prefs;
	public String musicPref;

	private Editor prefEditor;

	public double gravity;

	public String left_control;
	public String right_control;

	/** This method returns a FixedStepEngine - the engine that the game will run on.  I have set the game to run at 60
	 * 	frames per second.  The engine options are the default from Andengine's BaseGameActivity. */
	@Override
	public Engine onCreateEngine(final EngineOptions pEngineOptions) {
		return new FixedStepEngine(pEngineOptions, 60);
	}

	/** 
	 * 	onPause is an Android method which I am overrding.  Code in here will be run when the application is not at the front
	 * 	of the phones activity stack - focus has been taken away from the application (a call or the user presses the home
	 *  button).  It is used for the control of pausing the music and saving the games state by calling saveState 
	 *  in the database class.  It is important to save the game sa the state will soon be wiped from the phones memory.
	 **/
	@Override
	public void onPause(){
		super.onPause();
		if(MusicManager.music != null && MusicManager.music.isPlaying()){
			MusicManager.music.pause();
		}
		if(level != 0){
			String pos = myGuy.getX() + " " + myGuy.getY();
			database.saveState("state",level, pos, score, myGuy.getLife(), "");
			MusicManager.music.pause();
		}
	}

	/** 
	 * 	onResume is called when the application is brought back into focus, it checks whether or not the music should be muted
	 * 	and sets the variables appropriately.
	 * */
	@Override
	public void onResume(){
		super.onResume();
		if(musicPref != null)
			musicPref = prefs.getString("sound", null);

		if(musicPref != null && musicPref.equals("0")){
			isMuted = false;
		}
		else if(musicPref != null && musicPref.equals("1")){
			isMuted = true;
		}	
	}

	/** This method is in place to stop any music the app is playing when the power button is pressed. This code was taken from the old BaseGameActivity, found
	 * here http://code.google.com/p/andengine/source/browse/src/org/anddev/andengine/ui/activity/BaseGameActivity.java?r=710678c21e723cdbf7d229d464dd4d5427ee87cb
	 * I have adapted it to suit my needs. I have used it as onResume doesn't work how I would like it to.  This method stops music being played on the lock screen. */
	@Override
	public void onWindowFocusChanged(final boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);
		System.out.println("window");
		if(pHasWindowFocus){ 
			if(!this.mHasWindowFocused) {
				if(isMuted == false && level > 0){
					MusicManager.startMusic(MusicManager.musicPlaying, true);
				}
				else if(MusicManager.music != null && isMuted == false){
					MusicManager.music.start();
				}
			}
			this.mHasWindowFocused = true;
		} else {
			this.mHasWindowFocused = false;
		}
	}

	/** This Andengine method returns the engine options for the game.  In here the MainCamera is set up, including the height,
	 *	and width of the camera and the landscape.  The orientation of the game is also set, in this case it is landscape, and
	 *	it is fixed in that.  Multitouch and music is also turned on.   
	 **/
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.MainCamera = new BoundCamera(0, 0, CAMERA_WIDTH , CAMERA_HEIGHT , 0, LANDSCAPE_WIDTH, -480, LANDSCAPE_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.MainCamera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		engineOptions.getAudioOptions().setNeedsSound(true);

		return engineOptions;
	}

	/** 
	 * 	This method creates some initial resources, such as the Box2D physics world, that is used for collision detection etc.
	 * 	As well as that, it sets up font, image paths, the controls and the databases.  It is only necessary to do these things 
	 * 	once, and they need to be globally accessible.
	 * */
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		new JSONPageParser(this,"game_settings.json");
		prefs = getSharedPreferences("com.kamora_the_alien", MODE_PRIVATE);
		prefEditor = prefs.edit();

		if(prefs != null){

			musicPref = prefs.getString("sound", null);
			if(musicPref != null && musicPref.equals("0")){
				isMuted = false;

			}
			else if(musicPref != null && musicPref.equals("1")){
				isMuted = true;
			}
		}

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, (float) gravity), false);
		mPhysicsWorld.setAutoClearForces(true);
		vertexBufferObjectManager = this.getVertexBufferObjectManager();

		mFont = FontFactory.create(getFontManager(), getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, Color.rgb(184, 134, 11));	
		mFont.load();		
		theControls = new Controls(this, left_control, right_control);
		database = new Database(this);
		String levels = database.levelDownloaded();
		if(levels != null){
			prefEditor.putString("levelDownloaded", levels);
		}
		

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	/** This method creates the initial scene for the application.  My initial scene is going to be the menu screen.  So the
	 * 	menu() method is called which will set up that scene, then it is returned. 
	 * */
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		menu();

		pOnCreateSceneCallback.onCreateSceneFinished(currentScene);

	}
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	};

	/** Method which calls the page parser class for the menu page. */
	public void menu(){
		new JSONPageParser(this,"menu.json");
	}

	/** Method which is called to load new levels.
	 * 	@param level 
	 * 			- String containing the next JSON file to be loaded.
	 * */
	public void levelParser(String level){

		new JsonParser(this, level);
	}

	/** This method is called at the end of each level, it goes through unloading all the physics bodies to prevent Box2D
	 * 	crashes.  It clears each of the ArrayLists, followed by child scenes, detaches all children and resets the scene.
	 * 	A new instances of controls is created and the PhysicsWorld is also reset.  A Java garbage collection is also run.
	 * 	A check is run to see whether the characters life is 0 (indicating that the level is being reset due to life loss)
	 * 	if this is the case, the life is reset to 6.  Then the level to load is passed through to the levelParser method.
	 * 	@param loadLevel
	 * 			int containing the number of the level to load.	
	 * */
	public void loadLevel(int loadLevel){
		database.unlocked(this.level);
		Iterator<Body> allMyBodies = mPhysicsWorld.getBodies();
		while (allMyBodies.hasNext()) {
			try {
				final Body myCurrentBody = allMyBodies.next();

				mPhysicsWorld.destroyBody(myCurrentBody);

			} catch (Exception e) {
			}
		}
		removeItems.clear();
		bulletArray.clear();
		enemyArray.clear();
		toRemoveOnUpdate.clear();
		this.getEngine().getScene().clearTouchAreas();
		this.getEngine().getScene().clearChildScene();
		this.getEngine().getScene().detachChildren();
		this.getEngine().getScene().clearEntityModifiers();
		this.getEngine().getScene().reset();

		theControls = new Controls(this, left_control, right_control);


		this.getEngine().getScene().detachSelf();
		this.getEngine().getScene().dispose();
		this.mPhysicsWorld.clearForces();
		this.mPhysicsWorld.clearPhysicsConnectors();
		this.mPhysicsWorld.reset();

		System.gc();
		if (life <= 0){
			life = 6;
			lifeDisplay.heart1.animate(new long[] { 1000000 }, new int[] { 0 }, 1000000);
			lifeDisplay.heart2.animate(new long[] { 1000000 }, new int[] { 0 }, 1000000);
			lifeDisplay.heart3.animate(new long[] { 1000000 }, new int[] { 0 }, 1000000);

		}

		String nextLevel = "level" + loadLevel + ".json";
		levelParser(nextLevel);

	}


	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {}


	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		this.mGravityX = pAccelerationData.getX();
		this.mGravityY = pAccelerationData.getY();

		final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, this.mGravityY);
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);

	}

	public void startGame(String pressed) throws InterruptedException {
		Cursor returned;
		if(pressed.equals("load")){
			returned = database.exists("save");
		}
		else{
			returned = database.exists("state");
		}
		if(returned != null){
			if(returned.moveToFirst()){
				do{
					dbLevel = returned.getString(0);
					String dbPos = returned.getString(1);
					dPos = dbPos.split(" ");
					dbScore = returned.getInt(2);
					dbHealth = returned.getInt(3);

				}while (returned.moveToNext());
			}
			String next= "level" + dbLevel + ".json";
			new JsonParser(this, next);
			Thread.sleep(2000);
			setSave(dPos, dbScore, dbHealth);
		}
		else{
			toastText("You don't have a game stored!");
		}	
	}

	public void toastText(final String text){
		this.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(base_class.this, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
	private void setSave(String[] dPos2, int dbScore2, int dbHealth2) {
		try{
			final float halfWidth = myGuy.getSprite().getWidth() / 2;
			final float halfHeight = myGuy.getSprite().getHeight() / 2;
			final Vector2 v2 = Vector2Pool.obtain(( Float.parseFloat(dPos2[0]) + halfWidth) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, ( Float.parseFloat(dPos2[1]) + halfHeight) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT );
			myGuy.getBody().setTransform(v2, myGuy.getBody().getAngle());
			Vector2Pool.recycle(v2);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}


}