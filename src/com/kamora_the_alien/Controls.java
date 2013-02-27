package com.kamora_the_alien;

import android.opengl.GLES20;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.json.JSONArray;
import org.json.JSONException;
import com.badlogic.gdx.math.Vector2;


/** This is the controls class.  It is highly important as the main controls are set up in here.
 * 	It also houses the code which will move and animate the main character when the controls buttons are pressed.
 * @author Kiera O'Reilly
 * */
public class Controls {

	/** Declare class variables */
	public base_class base;
	public DigitalOnScreenControl my_control_variable;
	public DigitalOnScreenControl my_control_variable2;

	private BitmapTextureAtlas onscreen_control_base;
	private ITextureRegion mOnScreenControlBaseTextureRegion;

	private BitmapTextureAtlas onscreen_control_knob;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	private BitmapTextureAtlas onscreen_control_right;
	private ITextureRegion mOnScreenControlBaseTextureRegion_right;


	private BitmapTextureAtlas onscreen_control_knob_right;
	private ITextureRegion mOnScreenControlKnobTextureRegion_right;

	boolean isJumping = false;
	boolean isShooting = false;
	boolean running;

	boolean isTouching = false;	

	public static String image = "";
	public static Integer sizeX ;
	public static Integer sizeY ;
	public static String velocityX;
	public static String velocityY;


	/** */
	public void createTimerEnemyHandler(float delay)
	{

		float mEffectEnemyDelay = delay;
		base.getEngine().registerUpdateHandler(new TimerHandler(mEffectEnemyDelay, new ITimerCallback()
		{          
			/** After the delay, move the character body back by 15 pixels to give it the effect of jumping back, the text is removed from
			 *  the screen and to stop a constant collision with the spikes, it then calls the setHealth method.
			 */
			public void onTimePassed(final TimerHandler pTimerHandler)
			{  	
				isJumping = false;
			}
		}));
	}

	public void createTimerBulletHandler(float delay)
	{

		float mEffectEnemyDelay = delay;
		base.getEngine().registerUpdateHandler(new TimerHandler(mEffectEnemyDelay, new ITimerCallback()
		{          
			/** After the delay, move the character body back by 15 pixels to give it the effect of jumping back, the text is removed from
			 *  the screen and to stop a constant collision with the spikes, it then calls the setHealth method.
			 */
			public void onTimePassed(final TimerHandler pTimerHandler)
			{  	
				isShooting = false;
			}
		}));
	}


	/** Here in the controls constructor, all the textures that will be used for the controls are then loaded in and a control is created.  It will be placed on the left hand side of the
	 *  screen and will deal with left and right X axis movements.  When a button is pressed, we apply force to a sprite in that particular direction.  This
	 *  is done for the right hand side control as well, which will control the jumping.
	 *  As well as setting the controls, an IF statement is used to overcome an issue that arose where the animations when walking would only start once I
	 *  had stopped pressing the button.  The IF statement takes a boolean value which is initialised to false, if it is false, we can animate, so the
	 *  animations are set, and the variable is then put back to true.  When the walking animation is not going, the standing still animation is started.
	 *	When the jump button is pressed, it is necessary, for good effect, to move the parallax background along with the main character to make it look
	 *	like a realistic jump.  When the character jumps, it sets a variable to true and starts the run() timer method, which disables the jump button to
	 *	prevent infinite jumping.
	 *	For each control variable that is made, some properties are given to it, then the second control variable attaches to the first to make the child scene.
	 *	@param the_base
	 *           - The reference to the base class base_class
	 * @param right_control 
	 * @param left_control 
	 */
	public Controls(base_class the_base, String left_control, String right_control) {

		base = the_base;

		onscreen_control_base = new BitmapTextureAtlas(base.getTextureManager(),128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(onscreen_control_base, base, left_control, 0, 0);
		onscreen_control_base.load();

		onscreen_control_knob = new BitmapTextureAtlas(base.getTextureManager(),1, 1, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(onscreen_control_knob, base, "onscreen_control_knob.png", 0, 0);
		onscreen_control_knob.load();  


		onscreen_control_right = new BitmapTextureAtlas(base.getTextureManager(),128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mOnScreenControlBaseTextureRegion_right = BitmapTextureAtlasTextureRegionFactory.createFromAsset(onscreen_control_right, base, right_control, 0, 0);
		onscreen_control_right.load();		  

		onscreen_control_knob_right = new BitmapTextureAtlas(base.getTextureManager(),1, 1, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mOnScreenControlKnobTextureRegion_right = BitmapTextureAtlasTextureRegionFactory.createFromAsset(onscreen_control_knob_right, base, "onscreen_control_knob.png", 0, 0);
		onscreen_control_knob_right.load();


		onscreen_control_base = new BitmapTextureAtlas(base.getTextureManager(),128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		onscreen_control_right = new BitmapTextureAtlas(base.getTextureManager(),128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);


		/** Here we create a control, this one is going to be placed on the left hand side */
		my_control_variable = new DigitalOnScreenControl(0, base.CAMERA_HEIGHT - mOnScreenControlBaseTextureRegion.getHeight(),base.MainCamera, mOnScreenControlBaseTextureRegion, mOnScreenControlKnobTextureRegion, 0.1f, base.getVertexBufferObjectManager(), new IOnScreenControlListener() {


			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				/** If we press the D Pad right we get a value of 1  */   
				if (pValueX==1) {
					/** For force to be applied to the sprite, we use Vector2, which takes an X and Y value for the
          	    	 force in the right direction */
					Vector2  myVector2 = Vector2Pool.obtain(4.35f, 0f);
					base.myGuy.getBody().applyLinearImpulse(myVector2, base.myGuy.getBody().getWorldCenter());
					Vector2Pool.recycle(myVector2);


				}

				/** When we press the left D pad button, we get a value of -1*/
				if (pValueX==-1) {
					/** For force to be applied to the sprite, we use Vector2, which takes an X and Y value for the
         	    	 force in the left direction - note how a minus number is used here */

					Vector2  myVector2 = Vector2Pool.obtain(-4.35f, 0f);
					base.myGuy.getBody().applyLinearImpulse(myVector2, base.myGuy.getBody().getWorldCenter());
					Vector2Pool.recycle(myVector2);
				} 	

				/** This is an IF statement that has been put in because of an issue where the animations when walking would
				 * only start once I had stopped pressing the button.  This here uses a boolean value which is initialized to 
				 * FALSE, to say when to do the animations.  When it is not doing the walking animation,we set isTouching to FALSE
				 * and run the standing still animation.
				 * */ 
				if (pValueX == 1){
					base.myGuy.getSprite().setFlippedHorizontal(false);
					if (isTouching == false && isJumping != true){
						base.myGuy.getSprite().animate(new long[] {100, 100, 100 }, new int[] { 12, 13, 12 },9999);
						isTouching = true;	
					}
				}
				else if(pValueX == -1 ){
					base.myGuy.getSprite().setFlippedHorizontal(true);
					if (isTouching == false && isJumping != true){
						base.myGuy.getSprite().animate(new long[] {100, 100, 100 }, new int[] { 12, 13, 12 },9999);

						isTouching = true;	
					}
				}
				else{
					if(isTouching == true){
						base.myGuy.getSprite().animate(new long[] {8000, 150, 150 }, new int[] { 6, 7, 8 },9999);
						isTouching = false;
					}
				}


			}



		});



		/** This is the second control variable which will be placed on the right hand side. */ 
		my_control_variable2 = new DigitalOnScreenControl(640, base.CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion_right.getHeight(),base.MainCamera, this.mOnScreenControlBaseTextureRegion_right, this.mOnScreenControlKnobTextureRegion_right, 0.01f, base.getVertexBufferObjectManager(), new IOnScreenControlListener() {

			/** When this is pressed, we are jumping, we have to set the camera in the correct place and move the parallax background
			 * as well so that it looks like we are jumping.  I have set an animation here for when jump is pressed.  Again we use the vector2
			 * to mmove the character.  I also call the jumping method as otherwise you can jump continuously, I disable the jump button for 1500
			 * milliseconds.
			 * */
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {				
				
				if(BaseLevel.typeOfLevel.equals("main")){
					base.MainCamera.setCenter(base.myGuy.getX(), base.myGuy.getY()-10);

				}else{
					base.MainCamera.setCenter(0,  base.CAMERA_HEIGHT/2);
				}


				BaseLevel.ParallaxBackground.setParallaxValue(base.myGuy.getX()-395,-base.myGuy.getY()+420);

				if (pValueY==-1 && isJumping==false) {

					base.myGuy.getSprite().animate(new long[] {100, 100, 100,100,100,100 }, new int[] { 0,1,2,3,4,5 },1);	
					Vector2 vector1 = Vector2Pool.obtain(0, -30.35f);
					base.myGuy.getBody().applyLinearImpulse(vector1,base.myGuy.getBody().getWorldCenter());
					Vector2Pool.recycle(vector1);

					isJumping=true;
					createTimerEnemyHandler(1.5f);

				}

				if(pValueX==1 && isShooting != true){
					new Bullet(base, "friendly_bullet", image, sizeX, sizeY, velocityX, velocityY, "", base.myGuy.getSprite());

					isShooting= true;
					createTimerBulletHandler(1f);
				}
			}	 
		}	);	


		/** For each control variable give it some properties.  TO-DO some of this probs doesn't need to be in there so get rid of it. */
		my_control_variable.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		my_control_variable.setColor(1f, 1f, 1f, 0.5f);
		my_control_variable.getControlBase().setScaleCenter(0, 128);
		my_control_variable.getControlBase().setScale(1.25f);
		my_control_variable.getControlKnob().setScale(1.25f);
		my_control_variable.refreshControlKnobPosition();

		my_control_variable2.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		my_control_variable2.setColor(1f, 1f, 1f, 0.5f);
		my_control_variable2.getControlBase().setScaleCenter(0, 128);
		my_control_variable2.getControlBase().setScale(1.25f);
		my_control_variable2.getControlKnob().setScale(1.25f);
		my_control_variable2.refreshControlKnobPosition();


		/** Set the second controls as a child scene of the first. TO-DO - check that this is right. */
		my_control_variable.setChildScene(my_control_variable2);

	}

	/** This method is called from the JSONParser class to store the main charcter bullet data so that it can be used when
	 * 	a bullet is created.
	 * */
	public void setBulletVars(String bulletImage, JSONArray bulletSize, JSONArray velocity){
		image = bulletImage;

		try {
			sizeX = (Integer) bulletSize.get(0);
			sizeY = (Integer) bulletSize.get(1);
			velocityX = velocity.get(0).toString();
			velocityY = velocity.get(1).toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
