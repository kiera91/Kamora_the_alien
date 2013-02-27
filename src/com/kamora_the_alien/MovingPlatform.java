package com.kamora_the_alien;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/** This MovingPlatform class has been created entirely for the purpose of creating a moving platform object. This class extends SmallPlatform because
 * the object that will be moved is exactly the same as the SmallPlatform (i.e same image/size etc).  BodyType differs but this is set correctly in this
 * class.  
 * @author Kiera O'Reilly
 * */
public class MovingPlatform extends Item{

	public base_class base;
	private int origX;

	/** This is the MovingPlatform constructor.  The origX variable is set, which means that if more than one moving platform is wanted, it will be 
	 * it's own object and not be affected by hardcoded values.  The extensions in this class are setting the bodytype to Kinematic, and the vector
	 * code and update handler to make it move correctly.  In the update handler the method onupdate is executed and it checks the X position of the 
	 * body, if it is equal to a certain limit, it swaps the direction. 
	 * 	@param the_base
	 *           - The reference to the base class base_class
	 *   @param platformXPos
	 *   		- float variable that defines the X starting position
	 *   @param platformYPos
	 *   		- float variable that defines the Y starting position
	 * */
	public MovingPlatform(base_class the_base, String type, String image, int sizeX, int sizeY, int posX, int posY) {
		super(the_base, type, image, sizeX, sizeY, posX, posY, 0); 
		base = the_base;
		
		origX = posX;

		getBody().setType(BodyType.KinematicBody);
		Vector2 mVelocity = Vector2Pool.obtain(2f, 0f);      
		getBody().setLinearVelocity(mVelocity);
		Vector2Pool.recycle(mVelocity); 
	
		getSprite().registerUpdateHandler(new IUpdateHandler(){

			

			@Override
			public void onUpdate(float pSecondsElapsed) {

				getPosition();	
				
			}
			private void getPosition() {
				if (getX() <= origX ){
					/** This code sets the direction that the platform will move in to the left and then we set this velocity
					 *  to the body.
					 */
					Vector2 mVelocity = Vector2Pool.obtain(2f, 0f);     
					getBody().setLinearVelocity(mVelocity);
					Vector2Pool.recycle(mVelocity);

				}
				else if (getX() >= origX+800){

					/** This code sets the direction that the platform will move in to the right and then we set this velocity
					 *  to the body.
					 */
					Vector2 mVelocity = Vector2Pool.obtain(-2f, 0f);   
					
					getBody().setLinearVelocity(mVelocity);
					Vector2Pool.recycle(mVelocity);

				}				
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}

		});
		

	}
}
