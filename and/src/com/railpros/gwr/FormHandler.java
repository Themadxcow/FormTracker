package com.railpros.gwr;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;



public class FormHandler extends Activity implements OnClickListener, OnFocusChangeListener,TextWatcher  {
	private ImageButton restore;
	private ImageButton newrep;
	private ImageButton fsig;
	private ImageButton csig;
	
	private Button submit;
	private Button train;
	private Button workinfo;
	private Button workwindow;
	private Button projectinfo;
	
	private ArrayList<EditText> fields = new ArrayList<EditText>();
	//private ArrayList<EditText> fielddata = new ArrayList<EditText>();

	private TextView cdate;
	private TextView fdate;
	private TextView total;
	
	private ImageView fsignature;
	private ImageView csignature;
	private Switch email;
	private int year;
	private int month;
	private int day;
	
	private ViewGroup mainLayout;
	//private ViewGroup parent;
	private LayoutInflater inflater;
	private SharedPreferences prefs;
	private View trainform;
	private View projectform;
	private View workform;
	private View windowform;
	private View load;
	private View sigcap;
	private EditText blank;
	
	private Bitmap fraw;
	private Bitmap craw;
	private int t, i, ii, iii;
	
	
	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.newform);
	        t=29; i=12; ii=18; iii=24;
	        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	        findAllViewsById();
	        prefs.edit().putInt("STATE", 1).apply();
	        fsig.setOnClickListener(this);
	        csig.setOnClickListener(this);
	        newrep.setOnClickListener(this);
	        restore.setOnClickListener(this);
	        train.setOnClickListener(this);
	        projectinfo.setOnClickListener(this);
	        workinfo.setOnClickListener(this);
	        workwindow.setOnClickListener(this);
	        submit.setOnClickListener(this);
	        setTextSaver();
		}
	
	@Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.train:
            	removeOverlay();
        		setOverlay(Integer.parseInt(train.getTag().toString()));
        		setTextSaver();
                break;
            case R.id.projectinfo: 
            	removeOverlay();
        		setOverlay(Integer.parseInt(projectinfo.getTag().toString()));
        		setTextSaver();
                break;
            case R.id.workinfo: 
            	removeOverlay();
        		setOverlay(Integer.parseInt(workinfo.getTag().toString()));
        		setTextSaver();
                break;
            case R.id.windowinfo:
            	removeOverlay();
        		setOverlay(Integer.parseInt(workwindow.getTag().toString()));
        		setTextSaver();
                break;
            case R.id.submitdata:
            	Boolean check = validate();
            	if(check){
            		gatherData();
            	}
            	break;
            case R.id.newrep: 
            		clearForm();
                break;
            case R.id.restore: 
            		restoreForm();
                break;
            case R.id.csigcap:
        		Intent getcsig = new Intent(FormHandler.this,SigCapture.class);
        		startActivityForResult(getcsig, 1);
                break;
            case R.id.fsigcap: 
            	Intent getfsig = new Intent(getApplicationContext(),SigCapture.class);
    			startActivityForResult(getfsig, 0);
                break;
        }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1 && requestCode ==0) {
			fsig.setImageResource(android.R.drawable.ic_lock_idle_lock);
			fraw = BitmapFactory.decodeByteArray(
					data.getByteArrayExtra("byteArray"), 0,
					data.getByteArrayExtra("byteArray").length);
			fsignature.setImageBitmap(fraw);
			fdate.setText(String.valueOf(day+"."+month+"."+year));
			return;
		} if(resultCode == 1 && requestCode ==1) {
			csig.setImageResource(android.R.drawable.ic_lock_idle_lock);
			craw = BitmapFactory.decodeByteArray(
					data.getByteArrayExtra("byteArray"), 0,
					data.getByteArrayExtra("byteArray").length);
			csignature.setImageBitmap(craw);
			cdate.setText(String.valueOf(day+"."+month+"."+year));
			return;
		} if(resultCode == 1 && requestCode ==3) {
			sendData();
		}
	}
	
	@Override
	public void onFocusChange(View v, boolean b) {
		if(fields.indexOf((EditText) findViewById(v.getId()))<=0){
			Log.e("INDEXERROR",String.valueOf(fields.size()));
			return;
		}
		int i=fields.indexOf((EditText) findViewById(v.getId()));

		if(!b){
			if(fields.get(i).getText().length()<=0){
				//if (fielddata.get(i)!=blank){
				//	fielddata.remove(i);
				//}
			//} else {
				//if (fielddata.get(i)!=blank){
				//	fielddata.set(i,fields.get(i));
					Log.d("ARRAYFINALIZE", prefs.getString(fields.get(i).getTag().toString(),"nil"));
				//}
				prefs.edit().putString(fields.get(i).getTag().toString(), fields.get(i).getText().toString()).apply();
				restore.setEnabled(true);
			}
		}
	}
	
	private void gatherData(){
		int x=0;
		String[] data = new String[fields.size()+3];
		data[x++] = (fields.get(1).getText().toString()+fields.get(2).getText().toString()+fields.get(0).getText().toString()).replace(" ", "");
		for(EditText i : fields){
			data[x++] =  i.getText().toString();
		}
		data[x++] = fdate.getText().toString();
		data[x++] = cdate.getText().toString();
		Intent pdf = new Intent(FormHandler.this,CreatePDF.class);
		pdf.putExtra("DUMP", data);
		ByteArrayOutputStream fs = packageImage(fraw);
		ByteArrayOutputStream cs = packageImage(craw);
		pdf.putExtra("FSIG", fs.toByteArray());
		pdf.putExtra("CSIG", cs.toByteArray());
		startActivityForResult(pdf, 3); 	
	}
	
	private ByteArrayOutputStream packageImage(Bitmap bit){
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		Bitmap img=bit.copy(Bitmap.Config.ARGB_8888, true);
		int [] allpixels = new int [ img.getHeight()*img.getWidth()];
		img.getPixels(allpixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
		for(int i=0;i<allpixels.length;i++){
			//if(Color.alpha(allpixels[i]) != 0) {
				//allpixels[i] = (allpixels[i] & (0 << 24));
				if(allpixels[i] < Color.RED) {
					allpixels[i] =Color.BLACK;
				}else{
					allpixels[i]=Color.WHITE;
				}
		}
		img.setPixels(allpixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight()); 
		img =Bitmap.createScaledBitmap(img, 230, 84, false);
		img.compress(Bitmap.CompressFormat.PNG, 100, result);
		return result;
	}
	
	private void sendData(){
		File file = new File(prefs.getString("PATH", "/")+(fields.get(1).getText().toString()+fields.get(2).getText().toString()+fields.get(0).getText().toString()).replace(" ", "")+".pdf");
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"dhiatt89@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "KCS: " + fields.get(1).getText() + " " + day + "." + month + "." + year);
		i.putExtra(Intent.EXTRA_TEXT   , "**Automated email report**\n\nMD5 Checksum: "+Indentifiers.md5);
		i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		i.setType("text/richtext");
		if(email.isChecked()){
			prefs.edit().putBoolean("EMAIL", true).apply();
			i.putExtra(Intent.EXTRA_CC, "self");
		} else {
			prefs.edit().putBoolean("EMAIL", false).apply();
		}
		
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(FormHandler.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
		file.delete();
		finish();
	}

	private void findAllViewsById() {
		blank = (EditText) findViewById(R.id.blank);
		int y;
		for(y=0;y<t;y++){
			fields.add(blank);
		} y=0;
	//	fielddata.addAll(fields);
		mainLayout = (ViewGroup)findViewById(R.id.newform_cont);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		trainform = inflater.inflate(R.layout.formtrain, mainLayout, false);
		projectform = inflater.inflate(R.layout.formproject, mainLayout, false);
		workform = inflater.inflate(R.layout.formwork, mainLayout, false);
		windowform = inflater.inflate(R.layout.formwindow, mainLayout, false);
		//sigcap = inflater.inflate(R.layout.sigcap, mainLayout, false);
		load = inflater.inflate(R.layout.loader, mainLayout, false);
		mainLayout.addView(projectform, 2);
		
		train = (Button) findViewById(R.id.train);
		workinfo = (Button) findViewById(R.id.workinfo);
		workwindow = (Button) findViewById(R.id.windowinfo);
		projectinfo = (Button) findViewById(R.id.projectinfo);
		fsignature = (ImageView) findViewById(R.id.fsignature);
		csignature = (ImageView) findViewById(R.id.csignature);
		projectinfo.setEnabled(false);
		
		submit = (Button) findViewById(R.id.submitdata);
		restore = (ImageButton) findViewById(R.id.restore);
		newrep = (ImageButton) findViewById(R.id.newrep);
		fsig = (ImageButton) findViewById(R.id.fsigcap);
		csig = (ImageButton) findViewById(R.id.csigcap);
		
		fields.set(y++,(EditText)findViewById(R.id.dateView));
		fields.set(y++,(EditText)findViewById(R.id.flagman));
		fields.set(y++,(EditText)findViewById(R.id.project));
		fields.set(y++,(EditText)findViewById(R.id.job));
		fields.set(y++,(EditText)findViewById(R.id.location));
		fields.set(y++,(EditText)findViewById(R.id.cname));
		fields.set(y++,(EditText)findViewById(R.id.fstart));
		fields.set(y++,(EditText)findViewById(R.id.fsite));
		fields.set(y++,(EditText)findViewById(R.id.fleave));
		fields.set(y++,(EditText)findViewById(R.id.fend));
		fields.set(y++,(EditText)findViewById(R.id.cstart));
		fields.set(y++,(EditText)findViewById(R.id.cend));
		cdate = (TextView) findViewById(R.id.cdateView);
		fdate = (TextView) findViewById(R.id.fdateView);
		email = (Switch) findViewById(R.id.email);
		if(this.getCallingActivity().toString()=="FileDialog"){
			Log.d("ACTIVE=", this.getCallingActivity().toString());
			submit.setText("Resubmit");
		}else{
			final Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			fields.get(0).setText(String.valueOf(day+"."+month+"."+year));
			email.setChecked(prefs.getBoolean("EMAIL", false));
		}
	}
	
	private Boolean validate(){
		Boolean a,b,c;a=false;b=false;c=false;
		int y;
		for(y=0;y<t;y++){
			if(y<i){
				if(fields.get(y).getText().length()<=0){
					fields.get(y).requestFocus();
					Log.w("VAL", fields.get(y).toString()+" "+y);
					Toast.makeText(FormHandler.this, fields.get(y).getTag().toString()+" is a required field.", Toast.LENGTH_SHORT).show();
					return false;
				} 
			}if(y>=i&&y<ii){
				if(fields.get(y)==blank && a==true){
					removeOverlay();
					setOverlay(Integer.parseInt(workinfo.getTag().toString()));
					setTextSaver();
					Log.d("Loga", fields.get(y).getText().toString());
					Toast.makeText(FormHandler.this, "Incomplete data has been entered in Work Zone Information", Toast.LENGTH_SHORT).show();
					return false;
				}if(fields.get(y)!=blank){
					a=true;
				}
			}if(y>=ii&&y<iii){
				if(fields.get(y)==blank && b==true){
					removeOverlay();
					setOverlay(Integer.parseInt(workwindow.getTag().toString()));
					setTextSaver();
					Log.d("Logb", fields.get(y).getText().toString());
					Toast.makeText(FormHandler.this, "Incomplete data has been entered in Work Window Information", Toast.LENGTH_SHORT).show();
					return false;
				}if(fields.get(y)!=blank){
					b=true;
				}
			}if(y>iii){
				if(fields.get(y)==blank && c==true){
					removeOverlay();
					setOverlay(Integer.parseInt(train.getTag().toString()));
					setTextSaver();
					Toast.makeText(FormHandler.this, "Incomplete data has been entered in Train Activiy", Toast.LENGTH_SHORT).show();
					Log.d("Logc", fields.get(y).getText().toString());
					return false;
				}if(fields.get(y)!=blank){
					c=true;
				}
			}
			
		}
        if(cdate.getText().length() >=4 && fdate.getText().length() >=4){
        	return true;
        }
        Toast.makeText(FormHandler.this, "Both signatures are required.", Toast.LENGTH_SHORT).show();
    	return false;
	}
	
	
	private int removeOverlay() {
		switch(prefs.getInt("STATE", 1)){
		case 1: mainLayout.removeView(projectform);
				projectinfo.setEnabled(true);
				return 1;
		case 2: mainLayout.removeView(workform);
				workinfo.setEnabled(true);
				return 2;
		case 3: mainLayout.removeView(windowform);
				workwindow.setEnabled(true);
				return 3;
		case 4: mainLayout.removeView(trainform);
				train.setEnabled(true);
				return 4;
		}
		//mainLayout.removeView(sigcap);
		return 5;
	}
	
	protected void setOverlay(int x){
		int y=0;
		restore.setEnabled(false);
		switch(x){
			case 1:	mainLayout.addView(projectform, 1);
					projectinfo.setEnabled(false);
					prefs.edit().putInt("STATE", 1).apply();
					for(y=1;y<6;y++){
						if(prefs.getString(fields.get(y).getTag().toString(), null)!=null){
								restore.setEnabled(true);
								break;
							}
						}
						return;
			case 2:mainLayout.addView(workform, 1);
					workinfo.setEnabled(false);
					prefs.edit().putInt("STATE", 2).apply();
					if(fields.get(i)==blank){
						y=i;
						fields.set(y++, (EditText) findViewById(R.id.subdivision));
						fields.set(y++,(EditText) findViewById(R.id.road));
						fields.set(y++,(EditText) findViewById(R.id.milepost));
						fields.set(y++,(EditText) findViewById(R.id.milepostend));
						fields.set(y++,(EditText) findViewById(R.id.prot));
						fields.set(y++,(EditText) findViewById(R.id.other));
					}
					for(y=i;y<ii;y++){
						if(prefs.getString(fields.get(y).getTag().toString(), null)!=null){
								restore.setEnabled(true);
								break;
							}
						}
						return;
			case 3:mainLayout.addView(windowform, 1);
					workwindow.setEnabled(false);
					prefs.edit().putInt("STATE", 3).apply();
					if(fields.get(ii)==blank){
						y=ii;
						fields.set(y++, (EditText) findViewById(R.id.auth));
						fields.set(y++,(EditText) findViewById(R.id.dispatch));
						fields.set(y++,(EditText) findViewById(R.id.worktime));
						fields.set(y++,(EditText) findViewById(R.id.clear));
						fields.set(y++,(EditText) findViewById(R.id.worktimeend));
						fields.set(y++,(EditText) findViewById(R.id.wComment));
					}
					for(y=ii;y<iii;y++){
						if(prefs.getString(fields.get(y).getTag().toString(), null)!=null){
								restore.setEnabled(true);
								break;
							}
						}
						return;
			case 4:mainLayout.addView(trainform, 1);
					train.setEnabled(false);
					prefs.edit().putInt("STATE", 4).apply();
					if(fields.get(iii)==blank){
						y=iii;
						fields.set(y++,(EditText) findViewById(R.id.engine));
						fields.set(y++,(EditText) findViewById(R.id.expect));
						fields.set(y++,(EditText) findViewById(R.id.passed));
						fields.set(y++,(EditText) findViewById(R.id.direction));
						fields.set(y++,(EditText) findViewById(R.id.comment));
					}
					for(y=iii;y<t;y++){
					if(prefs.getString(fields.get(y).getTag().toString(), null)!=null){
							restore.setEnabled(true);
							break;
						}
					}
					return;
		}
		return;
	}
	
	private void clearForm(){
		int y,s=0;
		switch(prefs.getInt("STATE", 1)){
			case 1: y=0; t=i; break;
			case 2: y=i; s=ii; break;
			case 3: y=ii; s=iii; break;
			case 4: y=iii; s=t; break;
			default: return;
		}
		for(y=y;y<s;y++){
			fields.get(y).setText("");
		}
	}
	
	private void restoreForm(){
		int y,s=0;
		switch(prefs.getInt("STATE", 1)){
			case 1: y=1; s=6; break;
			case 2: y=i; s=ii; break;
			case 3: y=ii; s=iii; break;
			case 4: y=iii; s=t; break;
			default: return;
		}
		for(y=y;y<s;y++){
			fields.get(y).setText(String.valueOf(prefs.getString(fields.get(y).getTag().toString(), "")));
		}
	}
	
	private void setTextSaver(){
		int y,s = 0;
		switch(prefs.getInt("STATE", 1)){
			case 1: y=1; s=i; break;
			case 2: y=i; s=ii; break;
			case 3: y=ii; s=iii; break;
			case 4: y=iii; s=t; break;
			default: return;
		}
		for(y=y;y<s;y++){
		//	if(y>5&&y<12){
		//		fields.get(y).addTextChangedListener(this);
		//		Log.d("TXSet",fields.get(y).getTag().toString());
		//	}else{
			fields.get(y).setOnFocusChangeListener(this);
			Log.d("TXSet",String.valueOf(y));
		//	}
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		int y = 0,x = 0;
		switch(prefs.getInt("STATE", 1)){
		case 1:if(fields.get(10).getText().equals(null)){
			y=0;
		}if(fields.get(11).getText().equals(null)){
			x=0;
		}
			total.setText(String.valueOf(x+y));
				return;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
}



