package com.kamora_the_alien;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/** 
 * 	The ActionBarClass extends SherlockActivity.  ActionBar in Android was only introduced in version 3.0.  However an external
 * 	library has been created which allows to have an ActionBar in the lower versions of Android, and will still work on the
 * 	later versions.  Any page that wants to include an ActionBar will extend this class.  There is a layout created which holds
 * 	the buttons that will be in the ActionBar.  Code has also been written to handle the actions that need to be performed for
 * 	each button.
 * 	@author Kiera O'Reilly
 * */
public class ActionBarClass extends SherlockActivity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	/** This is the method which deals with menu items being pressed.  It turns the action to a string and compares it to
	 * 	the possibilities.  If "Go Back" is pressed, the current activity is finished.  If "About" is pressed, a dialog box
	 * 	is created, and the about layout is the content that will be displayed.  A button is created to close the dialog box.*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.toString().equals("Go Back")) {
			finish();
		}
		else if(item.toString().equals("About")){
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.about);
			dialog.setCancelable(true);
			Button button = (Button) dialog.findViewById(R.id.button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();

		}
		return super.onOptionsItemSelected(item);
	}

	/** This is called when the ActionBar class is called, it takes the actionbar XML and turns it into a menu. */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.layout.actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
