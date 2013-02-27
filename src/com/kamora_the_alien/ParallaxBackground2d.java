
package com.kamora_the_alien;

import java.util.ArrayList;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.opengl.util.GLState;


/**
 * @author Oldskool73
 * 
 * Parallax background that scrolls in both X and/or Y directions.
 * 
 * Usage:
 * 
 * ...x & y free scrolling tiled background...
 * mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.2f,-0.2f, new Sprite(0, 0, this.mParallaxLayerStars)));
 * 
 * ...side scroller repeating strip...
 * mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.4f, 0.0f, new Sprite(0, 100, this.mParallaxLayerHills),true,false));
 *
 * ...vertical scroller repeating strip...
 * mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.0f,-0.4f, new Sprite(100, 0, this.mParallaxLayerHills),false,true));
 *
 * ...non repeating positioned item...
 * mParallaxBackground.addParallaxEntity(new ParallaxBackground2d.ParallaxBackground2dEntity(-0.4f,-0.4f, new Sprite(100, 100, this.mParallaxLayerSun),false,false,true));
 * 
 * 
 * 
 *Heavily modified by Richard Phipps to work with AndEngine GLES2
 * 
 * 
 */
public class ParallaxBackground2d extends Background {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final ArrayList<Parallax2dEntity> mParallaxEntities = new ArrayList<Parallax2dEntity>();
	private int mParallaxEntityCount;

	protected float mParallaxValueX;
	protected float mParallaxValueY;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ParallaxBackground2d(float pRed, float pGreen, float pBlue) {
		super(pRed, pGreen, pBlue);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setParallaxValue(final float pParallaxValueX, final float pParallaxValueY) {
		this.mParallaxValueX = pParallaxValueX;
		this.mParallaxValueY = pParallaxValueY;
	}
	
	public void offsetParallaxValue(final float pParallaxValueX, final float pParallaxValueY) {
		this.mParallaxValueX += pParallaxValueX;
		this.mParallaxValueY += pParallaxValueY;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	@Override
	public void onDraw(final GLState pGLState , final Camera pCamera) {
		super.onDraw(pGLState, pCamera);

		final float parallaxValueX = this.mParallaxValueX;
		final float parallaxValueY = this.mParallaxValueY;
		final ArrayList<Parallax2dEntity> parallaxEntities = this.mParallaxEntities;

		for(int i = 0; i < this.mParallaxEntityCount; i++) {
			parallaxEntities.get(i).onDraw(pGLState, parallaxValueX, parallaxValueY, pCamera);
		}
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	public void attachParallaxEntity(final Parallax2dEntity pParallaxEntity) {
		this.mParallaxEntities.add(pParallaxEntity);
		this.mParallaxEntityCount++;
	}

	public boolean detachParallaxEntity(final Parallax2dEntity pParallaxEntity) {
		final boolean success = this.mParallaxEntities.remove(pParallaxEntity);
		if (success == true) {
			this.mParallaxEntityCount--;
		}
		return success;
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static class Parallax2dEntity {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		final float mParallaxFactorX;
		final float mParallaxFactorY;
		final Boolean mRepeatX;
		final Boolean mRepeatY;		
		final IAreaShape mShape;
		final Boolean mShouldCull;

		// ===========================================================
		// Constructors
		// ===========================================================

		// add a repeating x & y texture fill
		public Parallax2dEntity(final float pParallaxFactorX, final float pParallaxFactorY, final IAreaShape pShape) {
			this.mParallaxFactorX = pParallaxFactorX;
			this.mParallaxFactorY = pParallaxFactorY;
			this.mRepeatX = true;
			this.mRepeatY = true;
			this.mShouldCull = false;
			this.mShape = pShape;
		}

		// add an x or y only repeating strip
		public Parallax2dEntity(final float pParallaxFactorX, final float pParallaxFactorY, final IAreaShape pShape, final Boolean pRepeatX, final Boolean pRepeatY) {
			this.mParallaxFactorX = pParallaxFactorX;
			this.mParallaxFactorY = pParallaxFactorY;
			this.mRepeatX = pRepeatX;
			this.mRepeatY = pRepeatY;
			this.mShouldCull = false;
			this.mShape = pShape;
		}			
		
		// add an x or y only repeating strip or non repeating feature that may be culled when off screen
		public Parallax2dEntity(final float pParallaxFactorX, final float pParallaxFactorY, final IAreaShape pShape, final Boolean pRepeatX, final Boolean pRepeatY, final Boolean pShouldCull) {
			this.mParallaxFactorX = pParallaxFactorX;
			this.mParallaxFactorY = pParallaxFactorY;
			this.mRepeatX = pRepeatX;
			this.mRepeatY = pRepeatY;
			this.mShouldCull = (pRepeatX && pRepeatY)? false : pShouldCull;
			this.mShape = pShape;
		}

		
		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public void onDraw(final GLState pGLState, final float pParallaxValueX, final float pParallaxValueY, final Camera pCamera) {
			pGLState.pushModelViewGLMatrix();
			{
				final float cameraWidth = pCamera.getWidth();
				final float cameraHeight = pCamera.getHeight();
				final float shapeWidthScaled = this.mShape.getWidthScaled();
				final float shapeHeightScaled = this.mShape.getHeightScaled();

				//reposition
				float baseOffsetX = (pParallaxValueX * this.mParallaxFactorX);
				if (this.mRepeatX) {
					baseOffsetX = baseOffsetX % shapeWidthScaled;
					while(baseOffsetX > 0) {
						baseOffsetX -= shapeWidthScaled;
					}
				}			
				float baseOffsetY = (pParallaxValueY * this.mParallaxFactorY);
				if (this.mRepeatY) {
					baseOffsetY = baseOffsetY % shapeHeightScaled;
					while(baseOffsetY > 0) {
						baseOffsetY -= shapeHeightScaled;
					}				
				}
				
				//optionally screen cull non repeating items
				Boolean culled = false;
				if (mShouldCull) {
					if (!this.mRepeatX) {
						if ((baseOffsetY + (shapeHeightScaled*2) < 0) || (baseOffsetY > cameraHeight)) {
							culled = true;
						}
					}	
					if (!this.mRepeatY) {
						if ((baseOffsetX + (shapeWidthScaled*2) < 0) || (baseOffsetX > cameraWidth)) {
							culled = true;
						}
					}
				}
				
				if (!culled) {
					//draw
					pGLState.translateModelViewGLMatrixf(baseOffsetX, baseOffsetY, 0);
					float currentMaxX = baseOffsetX;
					float currentMaxY = baseOffsetY;
					do {														//rows
						this.mShape.onDraw(pGLState, pCamera);
						if (this.mRepeatY) {
							currentMaxY = baseOffsetY;							
							do {												//columns
								pGLState.translateModelViewGLMatrixf(0, shapeHeightScaled, 0);
								currentMaxY += shapeHeightScaled;						
								this.mShape.onDraw(pGLState, pCamera);
							} while(currentMaxY < cameraHeight);				//end columns
							pGLState.translateModelViewGLMatrixf(0, -currentMaxY + baseOffsetY, 0);					
						} 
						pGLState.translateModelViewGLMatrixf(shapeWidthScaled, 0, 0);
						currentMaxX += shapeWidthScaled;
					} while (this.mRepeatX && currentMaxX < cameraWidth);		//end rows
				}
			}
			pGLState.popModelViewGLMatrix();
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}


