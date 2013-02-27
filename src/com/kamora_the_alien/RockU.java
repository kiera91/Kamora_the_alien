package com.kamora_the_alien;

/** This RockU class has been created entirely for the purpose of creating a U shaped object for my bonus level.  It calls three instances
 * of Bonus_rocks and the correct positions have been set through trial and error so that they form a U shape that the main character can fit inside of.  
 * @author Kiera O'Reilly
 * 
 * TO DO = ROTATION */
public class RockU {

	public base_class base;

	/** This is the constructor for the RockU class.  When the RockU method is called, a position and a variable for rotation is passed in.  If the rotation variable is equal to 0, 
	 *  three instances of Bonus_rocks are called in, and then one is transformed to make the base of the U, rotation of the sprite and
	 *  the body is done separately.  If the rotation variable is set to 90, two of the spites and bodies are rotated.  There is only code
	 *  for a 90 degree rotation which is all that was needed, but it would be simple to extend this further.
	 *  @param the_base
	 *			- The reference to the base class base_class
	 *  @param rockUXpos
	 *  		- The starting X position of the object.
	 *  @param rockUYpos
	 *  		- The starting Y position of the object.
	 *  @param rotation
	 *  		- the number of degrees to rotate - currently only 90 and 0 are allowed.          
	 */
	public RockU(base_class the_base, float rockUXPos,float rockUYPos, float rotation){
		base = the_base;

		if(rotation == 0){

			//new Bonus_rocks(base, rockUXPos, rockUYPos);
			//new Bonus_rocks(base, rockUXPos+70, rockUYPos);
			//Bonus_rocks BU = new Bonus_rocks(base, rockUXPos+35, rockUYPos+40);

		//	BU.getBody().setTransform(BU.getBody().getWorldCenter(), (float) 1.57079633);
			//BU.getSprite().setRotation(90);
		}
		else if(rotation == 90){
			//Bonus_rocks LU = new Bonus_rocks(base, rockUXPos+70, rockUYPos+80);
			//Bonus_rocks RU = new Bonus_rocks(base, rockUXPos+70, rockUYPos);
			//new Bonus_rocks(base, rockUXPos+35, rockUYPos+40);
			//LU.getBody().setTransform(LU.getBody().getWorldCenter(), (float) 1.57079633);
			//LU.getSprite().setRotation(90);
			//RU.getBody().setTransform(RU.getBody().getWorldCenter(), (float) 1.57079633);
			//RU.getSprite().setRotation(90);
		}


	}

}
