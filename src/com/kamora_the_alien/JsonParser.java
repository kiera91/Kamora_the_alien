package com.kamora_the_alien;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.CustomJsonNodeFactory;
import com.fasterxml.jackson.databind.ObjectWriter;

import android.content.res.AssetManager;
import android.os.Looper;

/** The JsonParser class is what creates each level. The level that is to be parsed is passed through,
 * 	and data is extracted from it and then passed on to methods which build up a level.  
 * 	*/
public class JsonParser{

	private JSONObject jObject;
	private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();
	private static final ObjectMapper MAPPER   = CustomJsonNodeFactory.getMapper();

	InputStream input;
	String text; 

	InputStream inschema;
	public base_class base;

	/* main character */
	public String mcImage = null;
	public JSONArray mcSize =null;
	public JSONArray mcPos = null;
	public double scale = 0;

	public int level;
	public JSONObject object = null;
	public JSONObject backgroundObject = null;
	public JSONObject collectableObject = null;
	public JSONObject harmfulObject = null;

	/* floor */
	public JSONArray floorSize = null;
	public String floorImage = null;
	public String enemyImage = null;
	public String music = null;
	public String levelType = null;
	public JSONArray levelSize = null;
	public JSONArray enemySize = null;
	public String frontImage = null;
	public JSONArray frontSize = null;
	public JSONArray enemyPos = null;


	public Integer nextLevel = null;
	public Integer endOfLevel = null;

	public JSONObject bonusWallObject;
	public JSONObject randomItemObject;
	private JSONObject stillObject;
	private JSONObject movingObject;

	public ArrayList<ArrayList> collectableArray = new ArrayList<ArrayList>();
	public ArrayList<ArrayList> harmfulArray = new ArrayList<ArrayList>();
	public ArrayList<ArrayList> bonusArray = new ArrayList<ArrayList>();
	public ArrayList<ArrayList> randomArray = new ArrayList<ArrayList>();
	public ArrayList<ArrayList> bg = new ArrayList<ArrayList>();
	public ArrayList<ArrayList> stillPlatforms = new ArrayList<ArrayList>();
	public ArrayList<ArrayList> movingPlatforms = new ArrayList<ArrayList>();
	private String enemyTimer;
	private JSONObject enemyParticles;
	private String left_control;
	private String right_control;
	private JSONObject harmfulParticles;
	private String linearDampling;
	private String velocity;


	/** This is the constructor, which is called when new game is pressed, or when a level is complete/restarted and a new one
	 * 	is going to be loaded.  It runs in a new thread.  It first takes the JSON schema that has been given, then uses the
	 * 	external library to compare the JSON file to load and the schema, putting results into a report which is printed.  
	 * 	Following that the parse method is called.
	 * */
	public JsonParser(base_class base_class, final String level){
		base = base_class;	
		new Thread(new Runnable(){

			@Override
			public void run() {
				Looper.prepare();
				try {
					AssetManager assetManager = base.getAssets();
					input = assetManager.open(level);
					inschema = assetManager.open("json_schema.json");

					final JsonNode jsonSchema = fromResource(inschema);
					final JsonNode levelJson = fromResource(input);

					final JsonSchemaFactory factory = JsonSchemaFactory.defaultFactory();

					final JsonSchema schema = factory.fromSchema(jsonSchema);

					ValidationReport report;

					report = schema.validate(levelJson);
					printReport(report);
					input = assetManager.open(level);
					int size = input.available();
					byte[] buffer = new byte[size];
					input.read(buffer);
					input.close();

					text = new String(buffer);	

				}catch (IOException e) {
					e.printStackTrace();
				}
				try {
					parse();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();


	}

	/** Method from the external library which prints the report. */
	protected static void printReport(final ValidationReport report)
			throws IOException
			{
		final boolean success = report.isSuccess();
		System.out.println("Validation " + (success ? "succeeded" : "failed"));

		if (!success) {
			System.out.println("---- BEGIN REPORT ----");
			System.out.println(WRITER.writeValueAsString(report.asJsonObject()));
			System.out.println("---- END REPORT ----");
		}
			}

	/** Method from the external library to get the schema. */
	public static JsonNode fromResource(InputStream inschema) throws IOException
	{

		if (inschema == null){
			throw new IOException("resource " + inschema + " not found");
		}


		final JsonNode ret;

		try {
			ret = MAPPER.readTree(inschema);
		} finally {
			inschema.close();
		}

		return ret;
	}

	/** The parse method is what works through the JSON file, extracts all the data and prepares to send it to the BaseLevel
	 * 	class.  It starts off by getting the JSONArray "levels", this is what all the data is contained in.  It has been 
	 * 	necessary to hard-code all of the elements.  There is a for loop in place which will loop through depending on how many
	 * 	objects are in the array.*/
	private void parse() throws Exception {
		jObject = new JSONObject(text);

		JSONArray levelArray = jObject.getJSONArray("levels");

		for(int n = 0; n < levelArray.length(); n++)
		{	
			object = levelArray.getJSONObject(n);
			Iterator<?> keys = object.keys();
			/* This goes through each key in the object and prints it out. */
			while( keys.hasNext() ){
				/* Turn the key into a string for comparison purposes. */
				String key = (String)keys.next();

				/* If the key is equal to level. */       
				if(key.equals("level") ){
					/* Get that JSON object. */
					JSONObject levelItem = object.getJSONObject("level");
					/* Store all of the data that is given about a level. */
					level = levelItem.getInt("number");
					levelType = levelItem.getString("type");
					levelSize = levelItem.getJSONArray("size");
					
					if(levelType.equals("accelerometer") || levelType.equals("accelerometer-velocity")){
						linearDampling = levelItem.getString("linear");
						velocity = levelItem.getString("velocity");
					}
				}
				else if(key.equals("floor")){
					JSONObject floor = object.getJSONObject("floor");
					floorImage = floor.getString("image_name");
					floorSize = floor.getJSONArray("image_size");
				}
				else if(key.equals("enemies")){
					JSONObject enemies = object.getJSONObject("enemies");
					enemyTimer = enemies.getString("timer");
					enemyImage = enemies.getString("image_name");
					enemySize = enemies.getJSONArray("image_size");
					enemyPos = enemies.getJSONArray("starting_point");

					if(!enemies.isNull("particleEffects")){
						enemyParticles = enemies.getJSONObject("particleEffects");
						JSONArray direction = enemyParticles.getJSONArray("direction");
						JSONArray velocity = enemyParticles.getJSONArray("velocity");
						JSONArray colour = enemyParticles.getJSONArray("colour");
						String image = enemyParticles.getString("image");
						Enemy.setParticleVars(image, direction, velocity, colour);
					}
				}
				else if(key.equals("background")){
					JSONArray background = object.getJSONArray("background");
					for(int backgroundLayer = 0; backgroundLayer < background.length(); backgroundLayer++)
					{
						/* Create an ArrayList to hold individual background layers. */
						ArrayList<Object> individualBG = new ArrayList<Object>();
						/* Initialize new background object. */
						backgroundObject = background.getJSONObject(backgroundLayer);

						Integer layer = backgroundObject.getInt("layer_number");
						individualBG.add(layer);

						String layerImage = backgroundObject.getString("image_name");
						individualBG.add(layerImage);

						JSONArray layerSize = backgroundObject.getJSONArray("image_size");
						individualBG.add(layerSize);

						JSONArray position = backgroundObject.getJSONArray("position");
						individualBG.add(position);

						JSONArray layerParallax = backgroundObject.getJSONArray("parallax");
						individualBG.add(layerParallax);

						bg.add(individualBG);
					}					
				}
				else if(key.equals("frontfloor")){
					JSONObject frontfloor = object.getJSONObject("frontfloor");
					frontImage = frontfloor.getString("image");
					frontSize = frontfloor.getJSONArray("image_size");
				}
				else if(key.equals("maincharacter")){
					JSONObject maincharacter = object.getJSONObject("maincharacter");
					mcImage = maincharacter.getString("image_name");
					mcSize = maincharacter.getJSONArray("image_size");
					mcPos = maincharacter.getJSONArray("position");
					scale = maincharacter.getDouble("scale");
				}
				else if(key.equals("collectableItem")){
					JSONArray collectable = object.getJSONArray("collectableItem");

					for(int collectableItem = 0; collectableItem < collectable.length();collectableItem++){
						collectableObject = collectable.getJSONObject(collectableItem);
						ArrayList<Object> individualCollectable = new ArrayList<Object>();

						String itemType = collectableObject.getString("user_data");
						individualCollectable.add(itemType);

						JSONArray collectablePosition = collectableObject.getJSONArray("position");
						individualCollectable.add(collectablePosition);

						String collectableImage = collectableObject.getString("image_name");
						individualCollectable.add(collectableImage);

						JSONArray collectableSize = collectableObject.getJSONArray("image_size");
						individualCollectable.add(collectableSize);

						double itemScore = collectableObject.getInt("item_score");
						individualCollectable.add(itemScore);

						collectableArray.add(individualCollectable);
					}
				}
				else if(key.equals("friendlyBullet")){
					JSONObject friendlyBullet = object.getJSONObject("friendlyBullet");

					String bulletImage = friendlyBullet.getString("image_name");
					JSONArray bulletSize = friendlyBullet.getJSONArray("image_size");
					JSONArray velocity = friendlyBullet.getJSONArray("velocity");

					base.theControls.setBulletVars(bulletImage, bulletSize, velocity);	
				}
				else if(key.equals("enemyBullet")){
					JSONObject enemyBullet = object.getJSONObject("enemyBullet");

					String bulletImage = enemyBullet.getString("image_name");
					JSONArray bulletSize = enemyBullet.getJSONArray("image_size");
					JSONArray velocity = enemyBullet.getJSONArray("velocity");

					Enemy.setBulletVars(bulletImage, bulletSize, velocity);	
				}
				else if(key.equals("harmfulObject")){
					JSONArray harmful = object.getJSONArray("harmfulObject");
					for(int harmfulItem = 0; harmfulItem < harmful.length();harmfulItem++){
						harmfulObject = harmful.getJSONObject(harmfulItem);
						ArrayList<Object> individualHarmful = new ArrayList<Object>();

						String objectType = harmfulObject.getString("user_data");
						individualHarmful.add(objectType);

						JSONArray harmfulPosition = harmfulObject.getJSONArray("position");
						individualHarmful.add(harmfulPosition);

						String harmfulImage = harmfulObject.getString("image_name");
						individualHarmful.add(harmfulImage);

						JSONArray harmfulSize = harmfulObject.getJSONArray("image_size");
						individualHarmful.add(harmfulSize);

						double animated = harmfulObject.getDouble("animated");
						individualHarmful.add(animated);

						harmfulArray.add(individualHarmful);
						
						if(!harmfulObject.isNull("particleEffects")){
							harmfulParticles = harmfulObject.getJSONObject("particleEffects");
							JSONArray direction = harmfulParticles.getJSONArray("direction");
							JSONArray velocity = harmfulParticles.getJSONArray("velocity");
							JSONArray colour = harmfulParticles.getJSONArray("colour");
							String image = harmfulParticles.getString("image");
							//Enemy.setParticleVars(image, direction, velocity, colour);
						}
					}
				}
				else if(key.equals("stillPlatform")){
					JSONArray stillPlatformSingle = object.getJSONArray("stillPlatform");
					for(int platforms = 0; platforms < stillPlatformSingle.length();platforms++){
						stillObject = stillPlatformSingle.getJSONObject(platforms);
						ArrayList<Object> individualStill = new ArrayList<Object>();

						String type = stillObject.getString("user_data");
						individualStill.add(type);

						JSONArray stillPosition = stillObject.getJSONArray("position");
						individualStill.add(stillPosition);

						String image = stillObject.getString("image_name");
						individualStill.add(image);

						JSONArray size = stillObject.getJSONArray("image_size");
						individualStill.add(size);

						stillPlatforms.add(individualStill);
					}	
				}
				else if(key.equals("movingPlatform")){
					JSONArray movingPlatformSingle = object.getJSONArray("movingPlatform");
					for(int mPlatforms = 0; mPlatforms < movingPlatformSingle.length();mPlatforms++){
						movingObject = movingPlatformSingle.getJSONObject(mPlatforms);
						ArrayList<Object> individualMoving = new ArrayList<Object>();

						String type = movingObject.getString("user_data");
						individualMoving.add(type);

						JSONArray stillPosition = movingObject.getJSONArray("position");
						individualMoving.add(stillPosition);

						String image = movingObject.getString("image_name");
						individualMoving.add(image);

						JSONArray size = movingObject.getJSONArray("image_size");
						individualMoving.add(size);

						stillPlatforms.add(individualMoving);
					}	
				}
				else if(key.equals("otherItems")){
					JSONArray randomItem = object.getJSONArray("otherItems");
					for(int randomItems = 0; randomItems < randomItem.length();randomItems++){
						randomItemObject = randomItem.getJSONObject(randomItems);
						ArrayList<Object> individualRandom = new ArrayList<Object>();

						String randomType = randomItemObject.getString("user_data");
						individualRandom.add(randomType);

						JSONArray randomPosition = randomItemObject.getJSONArray("position");
						individualRandom.add(randomPosition);

						String randomImage = randomItemObject.getString("image_name");
						individualRandom.add(randomImage);

						JSONArray randomSize = randomItemObject.getJSONArray("image_size");
						individualRandom.add(randomSize);

						randomArray.add(individualRandom);
					}
				}
				else if(key.equals("bonusWall")){
					JSONArray bonusWall = object.getJSONArray("bonusWall");
					for(int bonusWalls = 0; bonusWalls < bonusWall.length(); bonusWalls++){
						bonusWallObject = bonusWall.getJSONObject(bonusWalls);
						ArrayList<Object> individualWall = new ArrayList<Object>();


						String userData = bonusWallObject.getString("user_data");
						individualWall.add(userData);

						JSONArray harmfulPosition = bonusWallObject.getJSONArray("position");
						individualWall.add(harmfulPosition);

						String bonusWallImage = bonusWallObject.getString("image_name");
						individualWall.add(bonusWallImage);

						JSONArray harmfulSize = bonusWallObject.getJSONArray("image_size");
						individualWall.add(harmfulSize);

						double rotation = bonusWallObject.getDouble("rotation");
						individualWall.add(rotation);

						bonusArray.add(individualWall);
					}				
				}
				else if(key.equals("music")){
					music = object.getString("music");
				}
				else if(key.equals("end_of_level")){
					endOfLevel = object.getInt("end_of_level");
				}
				else if(key.equals("next_level")){
					nextLevel = object.getInt("next_level");
				}
			}
			/* theLevel object has got everything it needs, call the runLevelCode method. */
			runLevelCode();
		}
	}

	/** Once all the data has been stored about a level, this method is run.  It is essentially a method JUST calling other
	 * 	methods to set up the level.  This is needed because it is necessary to set up some things before others.  The order
	 * 	of how the JSON is parsed cannot be pre-determined.  An example of this is the floor and background need to be in place
	 * 	before the main character is added to the scene, as it relies on the both of them and would not work otherwise.  The
	 * 	method checks to ensure that the data being passed through is not null, again this would cause problems. When everything
	 * 	has been set up, the level is started. */
	public void runLevelCode() throws JSONException{
		BaseLevel theLevel = new BaseLevel(base);

		theLevel.setLevel(level, levelType, levelSize);

		theLevel.setBackground(bg);

		if(floorSize != null && floorImage != null){
			theLevel.setFloor(floorSize, floorImage);
		}	

		if(mcImage != null){
			theLevel.setMaincharacter(mcImage, mcSize, mcPos, scale);
		}

		if(frontImage != null && frontSize != null){
			theLevel.setFrontFloor(frontImage, frontSize);
		}

		if(bonusArray != null){
			theLevel.processObject(bonusArray);
		}	

		if(collectableArray != null){
			theLevel.processObject(collectableArray);
		}

		if(randomArray != null){
			theLevel.processObject(randomArray);
		}

		if(harmfulArray != null){
			theLevel.processObject(harmfulArray);
		}
		if(stillPlatforms != null ){
			theLevel.processObject(stillPlatforms);
		}
		if(movingPlatforms != null  ){
			theLevel.processObject(movingPlatforms);
		}
		if(enemyImage != null && enemySize != null && enemyPos != null){
			theLevel.setEnemies(enemyImage, enemySize, enemyPos, enemyTimer);
		}
		theLevel.loadLevel(endOfLevel, music, nextLevel, linearDampling, velocity);
	}
}
