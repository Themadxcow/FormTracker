package com.railpros.gwr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public abstract class PDFLoader extends FormHandler {
	private SharedPreferences prefs;
	
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	        prefs.edit().putBoolean("firstrun", true).apply();
	        setContentView(R.layout.main);
	        //prefs = getSharedPreferences("FORM", Context.MODE_PRIVATE);
	}
}

