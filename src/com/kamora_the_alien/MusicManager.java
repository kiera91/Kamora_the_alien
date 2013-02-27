package com.kamora_the_alien;

import android.content.Context;
import android.media.MediaPlayer;

/** This is the MusicManager class.  It acts as a Singleton class, as there must only be one instance.  It controls the 
 * 	starting and stopping of the music playing in the app.*/
public class MusicManager{

	static MediaPlayer music;
	static int musicID;
	static int musicPos;
	static String musicPlaying;
	static Context context;
	static String previousMusic;

	/** 
	 *	The startMusic method searches for the resource that has been passed through as a parameter, and starts the music.
	 *	This method is called from various classes around the application where music is needed. 
	 *	@param musicString
	 *		- String containing the music file to search for
	 *	@param play
	 *		- Boolean value which will be true if the music should play or false if simply the music should be set up to play
	 *			at a later date (if muted is true, we still want to set it up so that if it is unmuted, music will play).
	 * */
	public static void startMusic(String musicString, Boolean play){
		musicPlaying = musicString;
		musicID = context.getResources().getIdentifier(musicString, "raw", context.getPackageName());
		if(musicID != 0){
			music = MediaPlayer.create(context, musicID);
			if(play){
				music.start();
				music.setLooping(true);
			}
		}
	}

	/** The pauseMusic method saves the current position, then pauses the music, this is then used when restarting the music
	 * 	so it continues from the same place.
	 * */
	public static void pauseMusic(){
		musicPos = music.getCurrentPosition();
		music.pause();
	}


}