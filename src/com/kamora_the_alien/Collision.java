package com.kamora_the_alien;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.text.Text;
import org.json.JSONException;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/** This is the collision class, there is a contact listener in here which is constantly checking for collisions.  This class makes use of the userdata
 * 	that I have been setting on various objects.  The collision class will be used in all levels where collision is needed.  I create an arraylist
 * 	base.toRemoveOnUpdate which is where all bodies are stored that have been used in the game and now need to be removed, e.g when I collect an item, it will
 * 	be moved from the removeItems array into the base.toRemoveOnUpdate array as it is no longer needed and this will save memory, and stop the physics breaking.
 * @author Kiera O'Reilly */
public class Collision {

	/** Declare variables in the class.  I've created an arraylist to hold all the bodies in that are ready to be removed from the game. */
	public base_class base;
	private Body CurrentBodyTarget;

	public TimerHandler waitTime;
	public boolean isHit = false;
	private Body CurrentBodyEnemy;

	/** This is the collision class, it checks for all collisions in a ContactListener that is created. When a contact occurs, the method beginContact()
	 * 	is called, it gets the bodies of the objects that are colliding and then calls upon the isColliding method passing the two bodies into it.
	 *  @param the_base
	 *			- The reference to the base class base_class
	 */	
	public Collision(base_class my_base_class) {

		base = my_base_class;

		/**  Create a new contact listener */
		base.mPhysicsWorld.setContactListener(new ContactListener() {

			/** The contact method, set the fixtures of what has collided and set the bodies as BodyA and BodyB
			 *  then call the isColling() method sending the bodies as parameters.
			 */
			@Override
			public void beginContact(Contact contact) {
				final Fixture fixtureA = contact.getFixtureA();
				final Body BodyA = fixtureA.getBody();

				final Fixture fixtureB = contact.getFixtureB();
				final Body BodyB = fixtureB.getBody();
				CurrentBodyTarget = null;
				try {
					isColliding(BodyA, BodyB);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}		


		});

	}


	/** This is an Andengine timer, which is called when the character collides with spikes.  I have chosen to use Andengine timers here as
	 *  opposed to Java timers because Andengine timers stop the physics from happening, causing less issues.
	 */
	public void createTimerMoveHandler(final Text ouch)
	{

		float mEffectDamageDelay = 0.3f;
		base.getEngine().registerUpdateHandler(new TimerHandler(mEffectDamageDelay, new ITimerCallback()
		{          
			/** After the delay, move the character body back by 15 pixels to give it the effect of jumping back, the text is removed from
			 *  the screen and to stop a constant collision with the spikes, it then calls the setHealth method.
			 */
			public void onTimePassed(final TimerHandler pTimerHandler)
			{  	
				base.myGuy.getBody().setLinearVelocity(-15, 0);
				base.getEngine().getScene().detachChild(ouch);
			}
		}));
	}

	/** This Andengine timer is used when the player has run out of life.  It waits with a delay of 2 seconds, and then reloads the current level. */
	public void createTimerDamageHandler()
	{    
		float mEffectDamageDelay = 2;
		base.getEngine().registerUpdateHandler(new TimerHandler(mEffectDamageDelay, new ITimerCallback()
		{                      
			public void onTimePassed(final TimerHandler pTimerHandler)
			{  
				isHit = true;
				/** TO-DO - this is hardcoded to load level one, should be current level possibly? */
				base.loadLevel(1);

			}
		}));
	}

	/** This is the isColliding method, it takes two parameters BodyA and Body B. Some Text is created which will be displayed when you 
	 *	collide with a spike/lose all your life.  There are then a set of IF statements which check for collisions between the player and
	 *	various objects (spikes, collectible items etc).
	 *	In the instance of a player/spike collision, a life is deducted and if the users is then out of lives, the DamageHandler method is 
	 *	called which reloads level1.  If the player has still got life, it attaches the "Ouch" text to the screen and calls the MoveHandler
	 *	timer which will move the player back.
	 *	The second set is checking if the player has collided with the main diamond. If it has set the target as the diamond
	 *	and the attacker of the item the player.
	 *	The for loop then goes through the arraylist of removeItems, it checks if the colliding body is equal to the body of the current item 
	 *	in the array, if it is it stores it in a deletable body, removes the item from the array, then adds the body to the remove on update array.
	 * @throws JSONException 
	 */ 
	public boolean isColliding(Body BodyA, Body BodyB) throws JSONException{

		Text dead = new Text(base.myGuy.getX()-100,200, base.mFont, "TOO BAD, YOU'RE OUT OF LIVES", base.getVertexBufferObjectManager());
		Text ouch = new Text(base.myGuy.getX()-100,200, base.mFont, "MIND THE SPIKES!", base.getVertexBufferObjectManager());

		/** Player and spike */
		if((BodyA.getUserData().equals("player") && BodyB.getUserData().equals("spikes") && isHit == false)){

			base.life = base.life - 1;
			base.lifeDisplay.setHealth(base.life, base.lifeDisplay.heart1,  base.lifeDisplay.heart2,  base.lifeDisplay.heart3);

			if(base.myGuy.getLife() == 0){
				base.getEngine().getScene().attachChild(dead);
				base.score = 0;
				createTimerDamageHandler();
			}else{
				base.getEngine().getScene().attachChild(ouch);					
				createTimerMoveHandler(ouch);
			}
		}
		else if ((BodyA.getUserData().equals("spikes") && BodyB.getUserData().equals("player")) && isHit == false){

			base.life = base.life - 1;
			base.lifeDisplay.setHealth(base.life, base.lifeDisplay.heart1,  base.lifeDisplay.heart2,  base.lifeDisplay.heart3);

			if(base.myGuy.getLife() == 0){
				base.currentScene.attachChild(dead);
				base.score = 0;
				createTimerDamageHandler();


			}else{
				base.currentScene.attachChild(ouch);
				createTimerMoveHandler(ouch);

			}
		}


		/** Enemy bullet and player */
		if((BodyA.getUserData().equals("enemy_bullet")) && BodyB.getUserData().equals("player")){
			CurrentBodyTarget = BodyA;

			base.life = base.life - 1;
			base.lifeDisplay.setHealth(base.life, base.lifeDisplay.heart1, base.lifeDisplay.heart2, base.lifeDisplay.heart3);

			if(base.myGuy.getLife() == 0){
				base.score = 0;
				createTimerDamageHandler();
			}
			else{
				for (int i = 0; i < base.bulletArray.size(); i++) {	
					if (CurrentBodyTarget == base.bulletArray.get(i).getBody()) {
						base.bulletArray.get(i).removeTheBullet();
						break;
					}
				}	
			}
		}else if((BodyA.getUserData().equals("player")) && BodyB.getUserData().equals("enemy_bullet")){
			CurrentBodyTarget = BodyB;

			base.life = base.life - 1;
			base.lifeDisplay.setHealth(base.life, base.lifeDisplay.heart1,  base.lifeDisplay.heart2,  base.lifeDisplay.heart3);

			if(base.myGuy.getLife() == 0){
				base.score = 0;
				createTimerDamageHandler();
			}
			else{
				for (int i = 0; i < base.bulletArray.size(); i++) {	
					if (CurrentBodyTarget == base.bulletArray.get(i).getBody()) {
						base.bulletArray.get(i).removeTheBullet();
						break;
					}
				}	
			}
		}

		/** Player bullet and enemy */
		if ((BodyA.getUserData().equals("friendly_bullet") && BodyB.getUserData().equals("enemy"))) {
			CurrentBodyTarget = BodyA;
			CurrentBodyEnemy = BodyB;
			for (int i = 0; i < base.enemyArray.size(); i++) {
				if (CurrentBodyEnemy == base.enemyArray.get(i).getBody()) {
					base.enemyArray.get(i).enemyLife--;
					if (base.enemyArray.get(i).getLife() <= 0) {
						base.enemyArray.get(i).removeEnemy();
						break;
					}
				}
			}                        
			for (int j = 0; j < base.bulletArray.size(); j++) {
				if (CurrentBodyTarget == base.bulletArray.get(j).getBody()) {
					base.bulletArray.get(j).removeTheBullet();
					break;
				}
			}                        
		}
		else if ((BodyA.getUserData().equals("enemy") && BodyB.getUserData().equals("friendly_bullet"))) {
			CurrentBodyTarget = BodyB;
			CurrentBodyEnemy = BodyA;
			for (int i = 0; i < base.enemyArray.size(); i++) {
				if (CurrentBodyEnemy == base.enemyArray.get(i).getBody()) {
					base.enemyArray.get(i).enemyLife--;
					if (base.enemyArray.get(i).getLife() <= 0) {
						base.enemyArray.get(i).removeEnemy();
						break;
					}
				}
			}
			for (int j = 0; j < base.bulletArray.size(); j++) {
				if (CurrentBodyTarget == base.bulletArray.get(j).getBody()) {
					base.bulletArray.get(j).removeTheBullet();
					break;
				}
			}
		}

		/** Player and enemy */
		if((BodyA.getUserData().equals("player") && BodyB.getUserData().equals("enemy") && isHit == false)){
			base.life = base.life - 1;
			base.lifeDisplay.setHealth(base.life, base.lifeDisplay.heart1,  base.lifeDisplay.heart2,  base.lifeDisplay.heart3);

			if(base.myGuy.getLife() == 0){
				base.score = 0;
				createTimerDamageHandler();
			}else{
				createTimerMoveHandler(ouch);
			}
		}
		else if((BodyA.getUserData().equals("enemy") && BodyB.getUserData().equals("player") && isHit == false)){
			base.life = base.life - 1;
			base.lifeDisplay.setHealth(base.life, base.lifeDisplay.heart1,  base.lifeDisplay.heart2,  base.lifeDisplay.heart3);

			if(base.myGuy.getLife() == 0){
				base.score = 0;
				createTimerDamageHandler();
			}else{
				createTimerMoveHandler(ouch);
			}
		}

		/** Player and life */
		if((BodyA.getUserData().equals("player") && BodyB.getUserData().equals("heart"))){
			CurrentBodyTarget = BodyB;

			if(base.myGuy.getLife() <=5){
				base.life = base.life +1;

				base.lifeDisplay.setHealth(base.life, base.lifeDisplay.heart1,  base.lifeDisplay.heart2,  base.lifeDisplay.heart3);


				for (int i = 0; i < base.removeItems.size(); i++) {				
					if (CurrentBodyTarget == base.removeItems.get(i).getBody()) {
						base.removeItems.get(i).removeItem();

					}
				}
			}else{
				base.toastText("Your life is full!");
			}
			return true;
		}else if((BodyB.getUserData().equals( "player") && BodyA.getUserData().equals("heart"))){
			CurrentBodyTarget = BodyA;
			if(base.myGuy.getLife() <=5){
				base.life = base.life +1;
				base.lifeDisplay.setHealth(base.life, base.lifeDisplay.heart1,  base.lifeDisplay.heart2,  base.lifeDisplay.heart3);

				for (int i = 0; i < base.removeItems.size(); i++) {				
					if (CurrentBodyTarget == base.removeItems.get(i).getBody()) {
						base.removeItems.get(i).removeItem();
					}
				}
			}
			else{
				base.toastText("Your life is full!");
			}

			return true;
		}

		/** Player and main diamond TO-DO: sort it out like the bullet. */
		if((BodyA.getUserData().equals( "player") && BodyB.getUserData().equals("main_diamond"))){
			System.out.println("diamond");
			CurrentBodyTarget = BodyB;
			for (int i = 0; i < base.removeItems.size(); i++) {				
				if (CurrentBodyTarget == base.removeItems.get(i).getBody()) {
					base.removeItems.get(i).removeItem();
					base.score++;
				}

			}
			return true;
		}
		else if((BodyB.getUserData().equals( "player") && BodyA.getUserData().equals("main_diamond"))){
			System.out.println("diamond");

			CurrentBodyTarget = BodyA;
			for (int i = 0; i < base.removeItems.size(); i++) {				
				if (CurrentBodyTarget == base.removeItems.get(i).getBody()) {
					base.removeItems.get(i).removeItem();
					base.score++;
				}

			}
			return true;
		}

		/** Player and bonus diamond */
		if((BodyA.getUserData().equals("player") && BodyB.getUserData().equals("bonus_diamond"))){
			CurrentBodyTarget = BodyB;
			for (int i = 0; i < base.removeItems.size(); i++) {
				if (CurrentBodyTarget == (base.removeItems.get(i)).getBody()) {
					base.removeItems.get(i).removeItem();
					base.score = base.score + 10;					

				}
			}	

			return true;
		}
		else if((BodyB.getUserData().equals("player") && BodyA.getUserData().equals("bonus_diamond"))){
			CurrentBodyTarget = BodyA;
			for (int i = 0; i < base.removeItems.size(); i++) {
				if (CurrentBodyTarget == (base.removeItems.get(i)).getBody()) {
					base.removeItems.get(i).removeItem();
					base.score = base.score + 10;					
					break;
				}
			}	

			return true;
		}
		else{
			return false;
		}
	}



}

