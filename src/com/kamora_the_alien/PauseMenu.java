package com.kamora_the_alien;

import javax.microedition.khronos.opengles.GL10;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;


/** 
 * This is the Pause menu class.  Here I create a new scene for the pause menu, which is called upon when the user presses the pause
 * button in the game.  This class extends an Andengine class IOnMenuItemClickListener.
 *	@author Kiera O'Reilly
 */
public class PauseMenu implements IOnMenuItemClickListener {

	public base_class base;

	MenuScene mMenuScene;

	private BitmapTextureAtlas mMenuTexture;
	private BitmapTextureAtlas mMenuTexture1;
	private BitmapTextureAtlas mMenuTexture2;
	private BitmapTextureAtlas mMenuTexture3;


	protected ITextureRegion mMenuResetTextureRegion;
	protected ITextureRegion mMenuQuitTextureRegion;
	protected ITextureRegion mMenuContTextureRegion;
	protected ITextureRegion mMenuSaveTextureRegion;


	protected static final int MENU_RESET = 0;
	protected static final int MENU_QUIT = MENU_RESET + 1;
	protected static final int MENU_CONTINUE = MENU_QUIT + 1;
	protected static final int MENU_SAVE = MENU_CONTINUE + 1;


	/** This is the constructor for the pause menu.  In here I load in all the textures for the menu buttons to the engine.
	 * @param the_base
	 *			- The reference to the base class base_class
	 */
	public PauseMenu(base_class the_base){

		base = the_base;

		mMenuTexture = new BitmapTextureAtlas(base.getTextureManager(),200,70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mMenuContTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTexture, base, "menu_continue.png", 0, 0);
		mMenuTexture.load();

		mMenuTexture1 = new BitmapTextureAtlas(base.getTextureManager(),200, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mMenuResetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTexture1, base, "menu_reset.png", 0, 0);
		mMenuTexture1.load();

		mMenuTexture2 = new BitmapTextureAtlas(base.getTextureManager(),200, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTexture2, base, "menu_quit.png", 0, 0);
		mMenuTexture2.load();

		mMenuTexture3 = new BitmapTextureAtlas(base.getTextureManager(),200, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mMenuSaveTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTexture3, base, "menu_save.png", 0, 0);
		mMenuTexture3.load();
	}

	/** This method is called when the user hits the pause button in the HUD.  A new scene is created and sprites are created and
	 *  textures are added to it.  Then, setBlendFunction is used on each one TO-DO - find out what this does.  The menu item is then
	 *  added to the menu scene.
	 *  Once this is done the method buildAnimations() is run TO-DO - check what this does.  Followed by setBackgroundEnabled() which will
	 *  disable the background so the player cannot be moved etc.  Finally a Listener is added to the menu items.
	 */
	protected void createMenuScene() {
		this.mMenuScene = new MenuScene(base.MainCamera);

		if(!base.isMuted){
			MusicManager.music.pause();
			MusicManager.previousMusic = MusicManager.musicPlaying;
			MusicManager.startMusic("menu",true);
		}

		final SpriteMenuItem contMenuItem = new SpriteMenuItem(MENU_CONTINUE, mMenuContTextureRegion, base.vertexBufferObjectManager);
		contMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(contMenuItem);

		final SpriteMenuItem saveMenuItem = new SpriteMenuItem(MENU_SAVE, mMenuSaveTextureRegion, base.vertexBufferObjectManager);
		saveMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(saveMenuItem);

		final SpriteMenuItem resetMenuItem = new SpriteMenuItem(MENU_RESET, mMenuResetTextureRegion, base.vertexBufferObjectManager);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(resetMenuItem);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, mMenuQuitTextureRegion, base.vertexBufferObjectManager);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(quitMenuItem);

		mMenuScene.buildAnimations();

		mMenuScene.setBackgroundEnabled(false);

		mMenuScene.setOnMenuItemClickListener(this);
	}

	/** This code is run when an item is clicked.  It is a simple switch statement that checks for which button was pressed.
	 *  If the continue button is pressed, the current child scene is cleared (the menu) and reset.  The child scene is then set back to being
	 *  the controls.
	 *  If the reset button is pressed we reset the currentScene after the menu scene has been cleared.
	 *  If the quit button is pressed, the app is closed.
	 *  
	 */
	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
		case MENU_CONTINUE:
			base.getEngine().getScene().clearChildScene();
			mMenuScene.reset();
			base.theControls = new Controls(base, base.left_control, base.right_control);
			base.getEngine().getScene().setChildScene(base.theControls.my_control_variable);	
			if(!base.isMuted)
				MusicManager.startMusic(MusicManager.previousMusic, true);
			return true; 
		case MENU_SAVE:
			String pos = base.myGuy.getX() + " " + base.myGuy.getY();
			base.database.saveState("save", base.level, pos, base.score, base.myGuy.getLife(), "");
			base.toastText("Game Saved!");
			return true;
		case MENU_RESET:
			base.getEngine().getScene().reset();
			/* Remove the menu and reset it. */
			base.currentScene.clearChildScene();
			mMenuScene.reset();
			base.score = 0;
			base.loadLevel(base.level);

			return true;
		case MENU_QUIT:
			android.os.Process.killProcess(android.os.Process.myPid());
		default:
			return false;
		}
	}


}