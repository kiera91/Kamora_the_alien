package com.kamora_the_alien;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** The Database class is what handles any database communication, it extends the SQLiteOpenHelper class, as Android has an
 * 	SQLite database already.	
 * */
public class Database extends SQLiteOpenHelper {

	static final String db_name = "kamora";
	static final String table_highscore = "highscoreTable";
	static final String field_highscore = "highscore";
	static final String table_unlocked = "unlocked";
	static final String field_unlocked = "levelUnlocked";
	static final String field_downloaded = "downloaded";
	static final String table_state = "state";
	static final String table_save = "save";
	static final String field_level = "current_level";
	static final String field_charpos = "character_position";
	static final String field_score = "score";
	static final String field_health = "health";
	static final String field_enemies = "enemies";


	public Database(Context context) {
		super(context, db_name, null, 1);
	}

	/** When on create is run, tables are created which will be used throughout the game, if they do not already exist. */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL("CREATE TABLE IF NOT EXISTS "+table_highscore+" (" + field_highscore + " TEXT , " + field_downloaded + " TEXT" + ")");
			db.execSQL("CREATE TABLE IF NOT EXISTS "+table_unlocked+" (" + field_unlocked + " INTEGER" + ")");
			db.execSQL("CREATE TABLE IF NOT EXISTS "+table_state+" (" + field_level + " INTEGER , " + field_charpos + " TEXT, " + field_score + " INTEGER, " + field_health + " INTEGER, " + field_enemies + " TEXT " + ")");
			db.execSQL("CREATE TABLE IF NOT EXISTS "+table_save+" (" + field_level + " INTEGER , " + field_charpos + " TEXT, " + field_score + " INTEGER, " + field_health + " INTEGER, " + field_enemies + " TEXT " + ")");	
		
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+table_highscore);
		db.execSQL("DROP TABLE IF EXISTS "+table_unlocked);
		db.execSQL("DROP TABLE IF EXISTS "+table_state);
		db.execSQL("DROP TABLE IF EXISTS "+table_save);
		onCreate(db);
	}


	/** When the save game button is pressed in the pause menu, this method is run.  It takes the lvel, position of the character
	 * 	score etc and stores it in the database table. */
	public void saveState(String table, int level, String pos, int score, int health, String enemies){

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(table, null, null);
		ContentValues cv = new ContentValues();
		cv.put(field_level, level);
		cv.put(field_charpos, pos);
		cv.put(field_score, score);
		cv.put(field_health, health);
		cv.put(field_enemies, enemies);


		db.insert(table, null, cv);
		db.close();

	}

	/** When going to load a game, a query is run to see if that database has any data in it, and is returned. */
	public Cursor exists(String table) {
		SQLiteDatabase db = this.getWritableDatabase();

		String sql = "SELECT * FROM " + table + " "; 
		Cursor cursor = db.rawQuery(sql, null); 

		if(cursor.moveToFirst()){
			db.close();
			return cursor;
		}else{
			db.close();
			return null;
		}
	}

	/** Once a level has been completed, add it to the highest level unlocked table for level select. */
	public void unlocked(int level) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(table_unlocked, null, null);	
		ContentValues cv = new ContentValues();
		cv.put(field_unlocked, level);
		
		db.insert(table_unlocked, null, cv);
		db.close();


	}
	
	public String levelDownloaded(){
		String value = null;
		SQLiteDatabase db = this.getWritableDatabase();

		String sql = "SELECT " + field_downloaded + " FROM " + table_highscore + " "; 
		Cursor cursor = db.rawQuery(sql, null); 
	
		if(cursor.moveToFirst()){
			value = cursor.getString(0);
		}
		db.close();
		return value;
	}

}
