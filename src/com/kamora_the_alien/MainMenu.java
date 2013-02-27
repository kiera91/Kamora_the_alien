package com.kamora_the_alien;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;



public class MainMenu implements IOnSceneTouchListener, IClickDetectorListener {

	protected static int PADDING = 50;

	private base_class base;

	private BitmapTextureAtlas mMenuTextureAtlas;        
	private ITextureRegion mMenuLeftTextureRegion;
	private ITextureRegion mMenuRightTextureRegion;

	private Sprite menuleft;
	private Sprite menuright;
	public int itemNo;
	private String iItemClicked;

	public int totalLength;

	private List<TextureRegion> columns = new ArrayList<TextureRegion>();

	private ClickDetector mClickDetector;

	private JSONObject menuItemObject;
	private String menuItemImage;
	private int menuItemNo;

	private String type;

	public MainMenu(base_class my_base_class ){
		base = my_base_class;
		if(base.isMuted == false){
			MusicManager.startMusic("menu", true);
		}else{
			// Set vars
			MusicManager.startMusic("menu", false);

		}

	}

	protected void loadMenu(JSONArray pageArray) throws JSONException {
		// Paths
		final Scene scene = new Scene();

		int spriteX = PADDING;
		int spriteY = PADDING;

		scene.attachChild(new Entity());
		scene.setOnAreaTouchTraversalFrontToBack();
		base.getEngine().setScene(scene);
		totalLength = ((pageArray.length()) * 260);

		//Images for the menu
		for(int objects = 0; objects < pageArray.length(); objects++){

			for (int inner = 0; inner < pageArray.length(); inner++){
				menuItemObject = pageArray.getJSONObject(inner);
				Iterator<?> objectKeys = menuItemObject.keys();
				while( objectKeys.hasNext() ){
					String objectKey = (String)objectKeys.next();
					if(objectKey.equals("image") ){
						menuItemImage = menuItemObject.getString("image");
					}
					else if(objectKey.equals("item")){
						menuItemNo = menuItemObject.getInt("item");
					}
					else if(objectKey.equals("type")){
						type = menuItemObject.getString("type");

					}
				}

				if(objects == menuItemNo){
					BitmapTextureAtlas mMenuBitmapTextureAtlas = new BitmapTextureAtlas(base.getTextureManager(), 256,256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
					ITextureRegion mMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBitmapTextureAtlas, base, menuItemImage, 0, 0);

					base.getEngine().getTextureManager().loadTexture(mMenuBitmapTextureAtlas);
					columns.add((TextureRegion) mMenuTextureRegion);

					//On Touch, save the clicked item in case it's a click and not a scroll.
					final String itemToLoad = type;
					Sprite sprite = new Sprite(spriteX,spriteY,(ITextureRegion)columns.get(objects), base.getVertexBufferObjectManager()){

						public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
							iItemClicked = itemToLoad;
							return false;
						}        			 
					};        		 


					base.getEngine().getScene().attachChild(sprite);        		 
					base.getEngine().getScene().registerTouchArea(sprite);        		 

					spriteX += 20 + PADDING+sprite.getWidth();

					break;
				}

			}
		}

		//Textures for menu arrows
		mMenuTextureAtlas = new BitmapTextureAtlas(base.getTextureManager(), 64,64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlas mMenuTextureAtlas1 = new BitmapTextureAtlas(base.getTextureManager(), 64,64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		mMenuLeftTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTextureAtlas, base, "menu_left.png", 0, 0);
		mMenuRightTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTextureAtlas1, base, "menu_right.png",0, 0);
		mMenuTextureAtlas.load();
		mMenuTextureAtlas1.load();

		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setTouchAreaBindingOnActionMoveEnabled(true);
		scene.setOnSceneTouchListenerBindingOnActionDownEnabled(true);

		CreateMenuBoxes();

		this.mClickDetector = new ClickDetector(this);
		base.currentScene = scene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		this.mClickDetector.onTouchEvent(pSceneTouchEvent);
		return true;
	}


	private void CreateMenuBoxes() {
		menuleft = new Sprite(0, base.CAMERA_HEIGHT-80, mMenuLeftTextureRegion, base.getVertexBufferObjectManager()){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction()==TouchEvent.ACTION_UP){



					base.MainCamera.setCenter(base.MainCamera.getCenterX()-300,base.MainCamera.getCenterY());

					checkPos();
				}
				return true;
			}
		};
		menuright = new Sprite(base.MainCamera.getCenterX()+base.CAMERA_WIDTH/2-64, base.CAMERA_HEIGHT-80, mMenuRightTextureRegion, base.vertexBufferObjectManager){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction()==TouchEvent.ACTION_UP){

					base.MainCamera.setCenter(base.MainCamera.getCenterX()+300, base.MainCamera.getCenterY());
					checkPos();

				}
				return true;
			}
		};
		base.getEngine().getScene().registerTouchArea(menuright);
		base.getEngine().getScene().registerTouchArea(menuleft);
		base.getEngine().getScene().attachChild(menuright);
		menuleft.setVisible(false);
		base.getEngine().getScene().attachChild(menuleft);

	}


	public void checkPos(){
		if(base.MainCamera.getCenterX()<=400)
			menuleft.setVisible(false);
		else
			menuleft.setVisible(true);

		if(base.MainCamera.getCenterX()>=totalLength){
			menuright.setVisible(false);
			base.getEngine().getScene().unregisterTouchArea(menuright);
		}	
		else{
			base.getEngine().getScene().registerTouchArea(menuright);
			menuright.setVisible(true);
			//set the arrows for left and right
			menuright.setPosition(base.MainCamera.getCenterX()+base.CAMERA_WIDTH/2-menuright.getWidth(),menuright.getY());
		}


		menuleft.setPosition(base.MainCamera.getCenterX()-base.CAMERA_WIDTH/2,menuleft.getY());

	}



	@Override
	public void onClick(ClickDetector pClickDetector, int pPointerID,
			float pSceneX, float pSceneY) {
		load(iItemClicked);

	}

	public void load(final Object iItemClicked2){

		if(iItemClicked2 != null && iItemClicked2.equals("newgame")){
			//base.menuMusic.pause();
			base.levelParser("level1.json");
		}
		else if(iItemClicked2 != null && iItemClicked2.equals("continue")){
			try {
				base.startGame("continue");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(iItemClicked2 != null && iItemClicked2.equals("load")){
			try {
				base.startGame("load");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(iItemClicked2 != null && iItemClicked2.equals("help")){
			Intent i = new Intent(base, HelpPages.class);
			base.startActivity(i);
		}
		else if(iItemClicked2 != null && iItemClicked2.equals("settings")){
			Intent i = new Intent(base, Settings.class);
			base.startActivity(i);

		}
		else if(iItemClicked2 != null && iItemClicked2.equals("exit")){
			android.os.Process.killProcess(android.os.Process.myPid());	
		}

	}

}
