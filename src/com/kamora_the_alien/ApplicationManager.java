package com.kamora_the_alien;

import android.app.Application;

/** This is the ApplicationManager class.  It extends Application, which means that this class is the application.  It
 * 	the application context through to the MusicManager so that it is globally accessible.	
 * */
public class ApplicationManager extends Application {
	
	@Override
	public void onCreate(){
		MusicManager.context = this;
	}

}
