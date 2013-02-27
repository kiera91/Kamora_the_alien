package com.kamora_the_alien;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.res.AssetManager;
import android.os.Looper;

/** The JSONPageParser class is similar to that of the JSONParser in that it uses the schema and takes data out of a json to 
 * 	create a page.  This is how the main menu is created. */
public class JSONPageParser {

	private JSONObject jObject;
	InputStream input;
	String text; 

	InputStream inschema;
	public base_class base;
	public JSONObject object = null;
	private JSONObject slideObject;
	private ArrayList<ArrayList> slides = new ArrayList<ArrayList>();

	/** Run the menu.json file against the schema and parse. */
	public JSONPageParser(base_class base_class, final String page){
		base = base_class;	
		new Thread(new Runnable(){

			@Override
			public void run() {
				Looper.prepare();
				try {
					AssetManager assetManager = base.getAssets();
					input = assetManager.open(page);
					inschema = assetManager.open("json_schema.json");

					input = assetManager.open(page);
					int size = input.available();
					byte[] buffer = new byte[size];
					input.read(buffer);
					input.close();

					text = new String(buffer);	

				}catch (IOException e) {
					e.printStackTrace();
				}
				try {
					parse(page);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();

	}


	/** */
	protected void parse(String page) throws JSONException {
		jObject = new JSONObject(text);
		String name = page.split("\\.")[0];
		JSONArray pageArray = jObject.getJSONArray(name);

		if(name.equals("menu")){
			new MainMenu(base).loadMenu(pageArray);
		}
		else if(name.equals("game_settings")){
			for(int n = 0; n < pageArray.length(); n++)
			{	
				object = pageArray.getJSONObject(n);
				Iterator<?> keys = object.keys();
				/* This goes through each key in the object and prints it out. */
				while( keys.hasNext() ){
					/* Turn the key into a string for comparison purposes. */
					String key = (String)keys.next();

					/* If the key is equal to level. */       
					if(key.equals("gravity") ){
						double gravity = object.getDouble("gravity");
						base.gravity = gravity;
					}
					else if(key.equals("controls")){
						JSONObject controls = object.getJSONObject("controls");
						String left_control = controls.getString("left_control");
						JSONArray left_size = controls.getJSONArray("left_size");
						String right_control = controls.getString("right_control");
						JSONArray right_size = controls.getJSONArray("right_size");
						
						base.left_control = left_control;
						base.right_control = right_control;
					}
				}
			}
		}


	}
}
