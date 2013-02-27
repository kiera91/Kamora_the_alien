package com.kamora_the_alien;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

/** The Hud class will create the Hud and everything that will be attached to it and set it to the main camera.  The Hud will be used across all classes to
 * 	display the character's life score and pause button.
 *	@Author Kiera O'Reilly 
 * */
public class Hud {

	public base_class base;
	public  Text scoreText;
	public BitmapTextureAtlas bPause;
	public ITextureRegion brPause;
	public static HUD hud;
	private PauseMenu pause;

	/** In the constructor for the Hud class, a new HUD is created, this will be used to attach various things to (pause button/life etc). The PauseMenu
	 * class is then called along with the lifeDisplay class to create my hearts. Then the sprite for the pause button, and add into it some code that
	 *  is run if the sprite it touched.  When touched calls the createMenuScene() method inside the pause class and it sets the child scene created
	 *  by this method as the menu scene. The pause button is attached to the HUD and the touch area of the pause sprite is registered.
	 *  Create some new text which will display the score and attach it to the HUD, set it in the top left of the screen. 
	 *  @param the_base
	 *           - The reference to the base class base_class
	 *  */
	public Hud(base_class the_base){
		base = the_base;

		hud = new HUD();
		pause = new PauseMenu(base);

		base.lifeDisplay = new lifeDisplay(base, 650,0);

		bPause = new BitmapTextureAtlas(base.getTextureManager(),50, 50, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		brPause = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bPause, base, "pause.png", 0, 0);
		bPause.load();

		Sprite sPause = new Sprite(300, 0, brPause, base.vertexBufferObjectManager){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction()==TouchEvent.ACTION_UP){
					if(base.myGuy.getSprite().isAnimationRunning()){
						base.myGuy.getSprite().stopAnimation(0);
						
					}
				//	base.music.pause();
					base.getEngine().getScene().clearChildScene();
					pause.createMenuScene();
					base.getEngine().getScene().setChildScene(pause.mMenuScene, false, true, true);
					
					base.myGuy.getSprite().animate(new long[] {8000, 150, 150 }, new int[] { 6, 7, 8 },9999);
				}
				return true;
			}
		};

		hud.attachChild(sPause);
		hud.registerTouchArea(sPause);	 

		scoreText = new Text(0,0, base.mFont, "Score: " + base.score, 10 ,base.getVertexBufferObjectManager());
		hud.attachChild(scoreText);
		base.MainCamera.setHUD(Hud.hud);

	}
}
