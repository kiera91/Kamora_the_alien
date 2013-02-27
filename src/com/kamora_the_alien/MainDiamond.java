package com.kamora_the_alien;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;

/** This is one of the classes that extends the main class Item.  As there are multiple collectible items throughout the game, it is more code
 * 	efficient to have a super class (Item) and sub-classes extending that super class where the correct variables can be passed in.  This is 
 * 	better than having separate individual classes for each collectible item that can be created. One item class could have been used and 
 * 	the image, dimensions and user data would need to be specified each time it was called, however this way reduces greatly the risk of human
 *  error as specific code is not being typed out fully each time.
 *  @author Kiera O'Reilly
 */
public class MainDiamond extends Item {

	/** This is the constructor for the MainDiamond class, all that is require is the line beginning with super.  This will pass all variables given
	 * 	to the main Item class and create that object. 
	 *  @param the_base
	 *			- The reference to the base class base_class
	 * @param posY 
	 * @param posX 
	 * @param image 
	 *  @param itemUXpos
	 *  		- The starting X position of the object.
	 *  @param itemUYpos
	 *  		- The starting Y position of the object.
	 */ 
	public MainDiamond(base_class the_base, String userData, String image, int sizeX, int sizeY, int posX, int posY) {
		super(the_base, userData,  image, sizeX, sizeY, posX, posY, 0);
		final LoopEntityModifier entityModifier = new LoopEntityModifier((IEntityModifier) new ParallelEntityModifier(new RotationModifier(6, 0, 360), new SequenceEntityModifier(new ScaleModifier(3, 1, 1.5f), new ScaleModifier(3, 1.5f, 1))));
		sItem.registerEntityModifier(entityModifier);
	}

}
