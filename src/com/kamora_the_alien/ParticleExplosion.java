package com.kamora_the_alien;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.json.JSONArray;
import org.json.JSONException;

import android.opengl.GLES20;

/** The particle explosion class creates particles after an event (enemy destroyed) in the game.  The most important settings
 * 	are specified from the JSON file for that level. This code has been taken from the Andengine 'cool' particlesystem 
 * 	example.https://github.com/nicolasgramlich/AndEngineExamples/blob/GLES2/src/org/andengine/examples/ParticleSystemCoolExample.java  */
public class ParticleExplosion {

	public base_class base;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mParticleTextureRegion;
	private IUpdateHandler myHandler;


	/** Constructor for the particle explosion class, takes the position to set it in as a parameter.   
	 * @param particleImage 
	 * @param particleColour 
	 * @param particleVelocity 
	 * @param particleDirection 
	 * @throws JSONException */
	public ParticleExplosion(base_class the_base, String particleImage, float xPos, float yPos, JSONArray particleDirection, JSONArray particleVelocity, JSONArray particleColour) throws JSONException{
		
		base = the_base;
		mBitmapTextureAtlas = new BitmapTextureAtlas(base.getTextureManager(), 32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mParticleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, base, particleImage, 0, 0);

		mBitmapTextureAtlas.load();

		final SpriteParticleSystem particleSystem = new SpriteParticleSystem(new PointParticleEmitter(xPos, yPos), 6, 10, 200, mParticleTextureRegion, base.getVertexBufferObjectManager());
		particleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE));
		
		/* SPEED */
		particleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(particleVelocity.getInt(0), particleVelocity.getInt(1), particleVelocity.getInt(2), particleVelocity.getInt(3)));
		
		/* DIRECTION */
		particleSystem.addParticleInitializer(new AccelerationParticleInitializer<Sprite>(particleDirection.getInt(0), particleDirection.getInt(1)));
		
		/* ROTATION OF PARTICLES */
		particleSystem.addParticleInitializer(new RotationParticleInitializer<Sprite>(0.0f, 360.0f));
	
		/* STARTING COLOUR */
		//particleSystem.addParticleInitializer(new ColorParticleInitializer<Sprite>(Float.parseFloat((String) particleColour.get(0)), Float.parseFloat((String) particleColour.get(1)), Float.parseFloat((String) particleColour.get(2))));
	
		/* DISAPPEAR AFTER HOW LONG */
		particleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(6f));

		/* SIZE */
		particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, 5, 0.5f, 2.0f));
		
		particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(2.5f, 3.5f, 1.0f, 0.0f));

		base.getEngine().getScene().attachChild(particleSystem);
		createTimerHandler(particleSystem);

	}

	public void createTimerHandler(final SpriteParticleSystem particleSystem)
	{

		float mEffectDamageDelay = 3f;
		base.getEngine().registerUpdateHandler(new TimerHandler(mEffectDamageDelay, new ITimerCallback()
		{          
			/** After the delay, stop the acceleration sensor, unregister the bonus handler and call the load level method for level 2.
			 */
			public void onTimePassed(final TimerHandler pTimerHandler)
			{  	
				
				base.getEngine().getScene().unregisterUpdateHandler(myHandler);
				particleSystem.setParticlesSpawnEnabled(false);


			}
		}));


	}
}

