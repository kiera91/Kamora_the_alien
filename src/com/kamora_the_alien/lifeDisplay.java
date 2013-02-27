package com.kamora_the_alien;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;

/** The lifeDisplay class is what creates the life display in each of the levels, it is used as an indication to the user as to how much life they have
 *	remaining.
 * @author Kiera O'Reilly
 * */
public class lifeDisplay {

	public base_class base;
	private BitmapTextureAtlas mBitmapTextureAtlas;

	private TiledTextureRegion mHeartTextureRegion1;
	private TiledTextureRegion mHeartTextureRegion2;
	private TiledTextureRegion mHeartTextureRegion3;
	public AnimatedSprite heart1;
	public AnimatedSprite heart2;
	public AnimatedSprite heart3;
	public int count = 0;

	/** Here is the constructor of the lifeDisplay class, in here, the textures are loaded.  Three texture regions are created, one for each
	 * of the hearts that will be displayed.  Animated sprites are used for the hearts as they will need to switch through their frames depending 
	 * on how much life the user has.  Each heart is attached to the hud. 
	 * 	@param the_base
	 *           - The reference to the base class base_class
	 *   @param heartXPos
	 *   		- float variable that defines the X starting position of the heart
	 *   @param heartYPos
	 *   		- float variable that defines the Y starting position of the heart
	 */

	public lifeDisplay(base_class the_base,float heartXPos,float heartYPos) {
		base = the_base;

		mBitmapTextureAtlas = new BitmapTextureAtlas(base.getTextureManager(),150, 50, TextureOptions.BILINEAR);	
		mHeartTextureRegion1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, base, "heart_tiled.png", 0, 0, 3, 1);	
		mHeartTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, base, "heart_tiled.png", 0, 0, 3, 1);	
		mHeartTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, base, "heart_tiled.png", 0, 0, 3, 1);	
		mBitmapTextureAtlas.load();

		heart1 = new AnimatedSprite(heartXPos, heartYPos, mHeartTextureRegion1.deepCopy(), base.getVertexBufferObjectManager());
		heart2 = new AnimatedSprite(heartXPos+50, heartYPos, mHeartTextureRegion2.deepCopy(), base.getVertexBufferObjectManager());
		heart3 = new AnimatedSprite(heartXPos+100, heartYPos, mHeartTextureRegion3.deepCopy(), base.getVertexBufferObjectManager());

		Hud.hud.attachChild(heart1);
		Hud.hud.attachChild(heart2);
		Hud.hud.attachChild(heart3);


	}

	/** This method will set the health depending on the characters life.  The switch statement checks how much life there 
	 *  is and displays the correct amount of life.
	 */
	public void setHealth(int health,AnimatedSprite heart1, AnimatedSprite heart2, AnimatedSprite heart3){
		switch(health){
		case 6:
			heart3.animate(new long[] { 1000000 }, new int[] { 0 }, 1000000);
			heart2.animate(new long[] { 1000000 }, new int[] { 0 }, 1000000);
			heart1.animate(new long[] { 1000000 }, new int[] { 0 }, 1000000);
			break;
		case 5:
			heart3.animate(new long[] { 1000000 }, new int[] { 1 }, 1000000);
			break;
		case 4:
			heart3.animate(new long[] { 1000000 }, new int[] { 2 }, 1000000);
			heart2.animate(new long[] { 1000000 }, new int[] { 0 }, 1000000);
			break;
		case 3:
			heart2.animate(new long[] { 1000000 }, new int[] { 1 }, 1000000);
			break;
		case 2:
			heart2.animate(new long[] { 1000000 }, new int[] { 2 }, 1000000);
			heart1.animate(new long[] { 1000000 }, new int[] { 0 }, 1000000);
			break;
		case 1:
			heart1.animate(new long[] { 1000000 }, new int[] { 1 }, 1000000);
			break;
		case 0:
			heart1.animate(new long[] { 1000000 }, new int[] { 2 }, 1000000);
			break;
		}
	}
	
	
	public void checkScore(int score){
		if((count + score) == 100){
			count = 0;
			base.life = 6;
			setHealth(6, heart1, heart2, heart3);
		}
		else{
			count = count + 1;
		}
		
	}
}
