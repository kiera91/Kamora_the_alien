package com.kamora_the_alien;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.DialogInterface;
import android.content.SharedPreferences;

/** 
 * This is the settings class.  It contains various preferences for the game, e.g. sound muting and playing. It extends the
 * ActionBarClass, as this needs to be in all the menu pages. As well as this it uses Android's SharedPreferences, which write
 * the data to a file which relates to this application.  A toggle is used for the sound preference, which has a listener 
 * attached to it.  Checks are made to ensure that it is set to the correct value.
 * 
 * */
public class Settings extends ActionBarClass{

	/* Set up initial variables. */
	private ToggleButton musicToggle;
	public SharedPreferences sharedPref;
	public SharedPreferences.Editor prefEditor;
	public String musicPref;
	private String latestLevel;
	public URL downloadURL = null;

	@Override
	public void onPause(){
		super.onPause();
		if(MusicManager.music.isPlaying())
			MusicManager.music.pause();
	}
	
	/** onCreate is called when this activity starts, it sets the layout to be the settings page, then creates a variable 
	 * 	relating to the toggle created in that layout.  A check is done to see what the value of the toggle should be and it 
	 * 	is set accordingly.
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		musicToggle = (ToggleButton)findViewById(R.id.toggleButton1);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		sharedPref = this.getSharedPreferences("com.kamora_the_alien", MODE_PRIVATE);
		prefEditor = sharedPref.edit();
		latestLevel = sharedPref.getString("levelDownloaded", null);
		setMusic();
		if(latestLevel == null)
			latestLevel = "standard";


		/* Create a button and add an onClick listener to it.  When it is click the code is run to connect to the university
		 * web drive to check for any updates. */
		final Button button = (Button) findViewById(R.id.updateButton);
		button.setOnClickListener(new View.OnClickListener() {

			private String newLevels;

			public void onClick(View v) {
				/* 
				 * http://adilmukarram.wordpress.com/2011/01/29/sending-and-receiving-data-from-a-php-web-application/
				 *  */
				String url = "http://itsuite.it.brighton.ac.uk/ko48/fyp/checkUpdates.php";

				/* Send a request to the PHP script to see if it will return any results. */
				try {
					/* Set up the HTTP post and HTTP client. */
					final HttpPost httppost = new HttpPost(url);
					final HttpClient httpclient = new DefaultHttpClient();
					latestLevel = sharedPref.getString("levelDownloaded", null);
					
					/* Set up the information (latest level downloaded) to the PHP script. */
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("level", latestLevel));

					/* Set the data with the HTTP header. */
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					/* Set up the reponse string. */
					final ResponseHandler<String> responseHandler = new BasicResponseHandler();

					/** This thread has been created due to the fact that in Android versions 2.3+ it is not possible to 
					 * 	perform code talking to the network in the same thread as the main thread.
					 * */
					new Thread(new Runnable() {
						public void run() {
							try {
								/* Execute the request. */
								newLevels =  httpclient.execute(httppost, responseHandler);
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} 
						}
					}).start();

					/* Important to sleep so the above code can be executed before continuing. */
					Thread.sleep(500);

					/* http://www.androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification */
					/* If the returned string is not empty and is set, go into the following code. */
					if(newLevels != null && !newLevels.equals("")){
						/* http://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-in-android */
						
						/* Let the user know if there are updates, and chose whether to download them.  When check for updates is clicked, if updates are available, 
						 *	the dialog box will appear, if the user hits download now, the acceptDownload method is called. 
						 * */
						DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogBox, int button) {
								switch (button){
								case DialogInterface.BUTTON_POSITIVE:
									try {
										acceptDownload(newLevels);
									} catch (InterruptedException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}	
									break;

								case DialogInterface.BUTTON_NEGATIVE:
									break;
								}
							}
						};

						AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
						builder.setMessage("There are available downloads, do you wish to proceed?").setPositiveButton("Download Now", dialogClickListener)
						.setNegativeButton("I'll do it later.", dialogClickListener).show();

					}
					else{
						Toast.makeText(getApplicationContext(), "No new updates!", Toast.LENGTH_SHORT).show();									

					}
				}catch(Exception e){
					System.out.println("Exception: " + e);
					e.printStackTrace();
				}
			}
		});
	}

	/** The acceptDownload method is called if the user hits the "postive" button from the dialog box.  It takes a parameter of the newLevel string returned from the PHP
	 * 	script and splits it.  A for loop is the begun, which will go through each result from the string split and providing that entry is not equal to a ',', will run
	 * 	the code to download that particular file. 
	 * */
	public void acceptDownload(String newLevel) throws InterruptedException, IOException{
		String[] result = newLevel.split(",");
		for(int i = 0; i < result.length; i++){
			if(!result[i].equals(",")){
				try {
					/* Create new URL to get the file from. */
					downloadURL = new URL( "http://itsuite.it.brighton.ac.uk/ko48/fyp/json/" + result[i] + ".txt");

					/* Create a connection to the downloadURL. */
					final HttpURLConnection urlConnection = (HttpURLConnection) downloadURL.openConnection();

					/* Set requestMethod as GET as we want to receive data. */
					urlConnection.setRequestMethod("GET");

					/* Again the connection setup needs to be executed in a separate thread from main. */
					new Thread(new Runnable() {
						public void run() {
							try {
								urlConnection.connect();
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} 
						}
					}).start();

					/* Sleep to give thread time to execute.*/
					Thread.sleep(500);
					
					/* Set up directory path where files will be downloaded.  If specified folder doesn't exist, create it. */
					String dirPath = getApplicationContext().getFilesDir().getParent() + "/downloaded";
					File f = new File(dirPath);
					if(!f.exists()){
						f.mkdir();
					}
					
					/* Set the name and location to save the file we are writing to. */
					File file = new File(dirPath, (result[i] + ".json"));
					final FileOutputStream fileOutput = new FileOutputStream(file);

					/* Again next section deals with network, needs to be in a separate thread, can't put all code in this method in a thread otherwise some values
					 * would have to be final, making it not work. */
					new Thread(new Runnable() {
						public void run() {
							try {
								/* Set up input stream. */
								InputStream inputStream = urlConnection.getInputStream();
								urlConnection.getContentLength();
								
								/* Create a buffer. */
								byte[] buffer = new byte[1024];
								int bufferLength = 0;

								/* Loop through the input steam and store it in the buffer. */
								while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
									/* Write the data in the buffer to the file. */
									fileOutput.write(buffer, 0, bufferLength);

								}
								/* Close the output stream. */
								fileOutput.close();

							} catch (ClientProtocolException e) {
								e.printStackTrace();

							} catch (IOException e) {
								e.printStackTrace();

							} 
						}
					}).start();

					Toast.makeText(getApplicationContext(), "Files Downloaded", Toast.LENGTH_SHORT).show();									
					/* Update the latest downloaded level. */
					prefEditor.putString("levelDownloaded", result[result.length - 1]).commit();

				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} 
			}
		}
	}


	/** This method is related to the toggle button that is on the page, when it is clicked the following code will be run
	 * 	it is to check whether it is on/off then write the appropriate data to the SharedPreferences.	
	 *  */
	public void onToggleClicked(View view) {
		boolean toggled = ((ToggleButton) view).isChecked();

		if (toggled) {
			prefEditor.putString("sound", "0");
			Toast.makeText(this, "music on" , Toast.LENGTH_SHORT).show();
		} else {
			prefEditor.putString("sound", "1");
			Toast.makeText(this, "music off" , Toast.LENGTH_SHORT).show();
		}
		prefEditor.commit();	    	
		setMusic();
	}

	/** 
	 * 	After the music toggle has changed, the setMusic method is called.  It gets what the value should be, and sets the
	 * 	toggled checked/unchecked accordingly.  As well as this it pauses and starts the music within the settings class.
	 * */
	public void setMusic(){
		musicPref = sharedPref.getString("sound", null);

		if(musicPref != null && musicPref.equals("0")){
			musicToggle.setChecked(true);
			MusicManager.music.start();
			System.out.println("music start");
		}else if(musicPref != null && musicPref.equals("1")){
			musicToggle.setChecked(false);
			if(MusicManager.music != null && MusicManager.music.isPlaying())
				MusicManager.music.pause();
			System.out.println("music not on");

		}else{
			musicToggle.setChecked(true);
			MusicManager.music.start();

		}
	}
}
