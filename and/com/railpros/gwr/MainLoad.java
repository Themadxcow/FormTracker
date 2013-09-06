package com.railpros.gwr;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.Log;

public class MainLoad extends Activity {
	private SharedPreferences prefs;
	private Button newform;
	private Button oldform;
	//private String LOG;
	
//	private Button feedback;
	
	
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	        prefs.edit().putBoolean("firstrun", true).apply();
	        setContentView(R.layout.main);
	        newform = (Button) findViewById(R.id.newform);
			oldform = (Button) findViewById(R.id.prev_form);
	        //prefs = getSharedPreferences("FORM", Context.MODE_PRIVATE);
	        findAllViewsById();
	        
	        
	        newform.setOnClickListener(new OnClickListener(){
	        	@Override
				public void onClick(View v) {
	        		Intent intentform = new Intent(getApplicationContext(),FormHandler.class);
	        		startActivity(intentform);
	        	}
	        });
	        
	      /*  oldform.setOnClickListener(new OnClickListener(){
	        	@Override
				public void onClick(View v) {
	        		Intent intentDialog = new Intent(MainLoad.this, FileDialog.class);
	        		intentDialog.putExtra(FileDialog.START_PATH, prefs.getString("PATH", "/tmp"));
	        		intentDialog.putExtra(FileDialog.CAN_SELECT_DIR, false);
	        		//intentDialog.putExtra(FileDialog.FORMAT_FILTER, "pdf");
	        		startActivity(intentDialog);
	        	}
	        }); */
	}
	        
	private void findAllViewsById() {
		boolean firstrun = prefs.getBoolean("firstrun", true);
	    if (firstrun){
	    	//prefs = getSharedPreferences("FORM", Context.MODE_PRIVATE);
	    	if(prefs.edit().putInt("STATE", 1).commit()){
	    		if(prefs.edit().putBoolean("EMAIL", false).commit()){
	    			if(prefs.edit().putInt("STATE", 1).commit()){
	    				if(prefs.edit().putString("PATH", Environment.getExternalStorageDirectory() +"/.RPFS/").commit()) {
	    					//if(prefs.edit().putStringSet("OLDFORM", set).commit()) {
	    						prefs.edit().putBoolean("firstrun", false).commit();
	    					//}
	    				}
	    			}
	    		}
	    	}
	    	File path = new File(prefs.getString("PATH", "/tmp/"));
	    	File xpath = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.railpros.gwr/");
	    	try {
		        path.mkdirs();
		        xpath.mkdirs();
		    } catch(SecurityException e) {
		        Log.e("LOG", "unable to write to device " + e.toString());
		    }
	    }
		// feedback = (Button) findViewById(R.id.feedback);
	}
}

