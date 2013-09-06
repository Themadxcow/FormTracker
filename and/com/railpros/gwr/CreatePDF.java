//
//  Android PDF Writer
//  http://coderesearchlabs.com/androidpdfwriter
//
//  by Javier Santo Domingo (j-a-s-d@coderesearchlabs.com)
//

package com.railpros.gwr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Set;

import com.railpros.gwr.R;

//import crl.android.pdfwriter.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

public class CreatePDF extends Activity {
	private Bitmap sub1;
	private Bitmap sub2;
	private Bitmap sub3;
	private Bitmap sub4;
	private Bitmap logo;
	private SharedPreferences prefs;
	private int x=1;
	private String[] data = new String[getIntent().getStringArrayExtra("DUMP").length];
	ArrayList<String> DATA = new ArrayList<String>();
	
	private String GenForm() {
		Resources res = getResources();
		AssetManager asset = getAssets();
		
		PDFWriter mPDFWriter = new PDFWriter(PaperSize.A4_WIDTH, PaperSize.A4_HEIGHT);
		//buildAsset();
			try {
				logo = BitmapFactory.decodeStream(asset.open("kcslogo.bmp"));
			sub1 = BitmapFactory.decodeStream(asset.open("kcsh1.bmp"));
			sub2 = BitmapFactory.decodeStream(asset.open("kcsh2.bmp"));
			sub3 = BitmapFactory.decodeStream(asset.open("kcsh3.bmp"));
			sub4 = BitmapFactory.decodeStream(asset.open("kcsh4.bmp"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Bitmap CSIG = BitmapFactory.decodeByteArray(
				getIntent().getByteArrayExtra("CSIG"), 0,
				getIntent().getByteArrayExtra("CSIG").length);
		Bitmap FSIG = BitmapFactory.decodeByteArray(
				getIntent().getByteArrayExtra("FSIG"), 0,
				getIntent().getByteArrayExtra("FSIG").length);	
        mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA_OBLIQUE);
        //Log.d("DATAin x", String.valueOf(DATA.get(x).toString()));
        //Log.d("DATAin", String.valueOf(x));
        //Log.d("DATAin", String.valueOf(DATA.toString()));
        mPDFWriter.addText(460, 760, 10, "Fax: 866.762.7619");
        mPDFWriter.addText(449, 745, 10, "Phone: 877.315.0513");
        mPDFWriter.addText(396, 731, 10, "1 Ada, Suite 200 Irvine, CA 92618");
        mPDFWriter.addImage(65, 745, logo);
        
        mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA_BOLD);
        mPDFWriter.addText(65, 730, 16, "KCS DAILY WORK REPORT");
        
        mPDFWriter.addRectangle(65, 700, 485, 15);
        mPDFWriter.addImage(65, 700, sub1);
        	mPDFWriter.addText(65, 677, 10, res.getString(R.string.date));
        		mPDFWriter.addLine(100, 677, 296, 677); 
        			mPDFWriter.addText(100, 678, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(295, 677, 10, res.getString(R.string.flagman));
	        	mPDFWriter.addLine(357, 677, 550, 677);
	        		mPDFWriter.addText(357, 678, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(65, 653, 10, res.getString(R.string.project_name));
	        	mPDFWriter.addLine(145, 653, 296, 653);
	        		mPDFWriter.addText(145, 654, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(295, 653, 10, res.getString(R.string.job_num));
	        	mPDFWriter.addLine(357, 653, 550, 653);
	        		mPDFWriter.addText(357, 654, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(65, 628, 10, res.getString(R.string.project_loc));
	        	mPDFWriter.addLine(158, 628, 550, 628);
	        		mPDFWriter.addText(158, 629, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(295, 603, 10, res.getString(R.string.cname));
		        mPDFWriter.addLine(423, 603, 550, 603);
		        	mPDFWriter.addText(423, 603, 14, DATA.get(x++).toString());
		        	
		        mPDFWriter.addText(65, 603, 10, res.getString(R.string.fstart));
		        	mPDFWriter.addLine(184, 603, 296, 603);
		        		mPDFWriter.addText(184, 604, 14, DATA.get(x++).toString());
		        mPDFWriter.addText(65, 579, 10, res.getString(R.string.fonsite));
		        	mPDFWriter.addLine(184, 579, 296, 579);
		        		mPDFWriter.addText(184, 580, 14, DATA.get(x++).toString());
		        mPDFWriter.addText(65, 555, 10, res.getString(R.string.fleave));
	        		mPDFWriter.addLine(184, 555, 296, 555);
	        			mPDFWriter.addText(184, 556, 14, DATA.get(x++).toString());
	        	mPDFWriter.addText(65, 531, 10, res.getString(R.string.fend));
		        	mPDFWriter.addLine(184, 531, 296, 531);
		        		mPDFWriter.addText(184, 532, 14, DATA.get(x++).toString());
		        	
		        mPDFWriter.addText(295, 579, 10, res.getString(R.string.cstart));
	        		mPDFWriter.addLine(423, 579, 550, 579);
	        			mPDFWriter.addText(423, 580, 14, DATA.get(x++).toString());
		        mPDFWriter.addText(295, 555, 10, res.getString(R.string.cend));
	        		mPDFWriter.addLine(423, 555, 550, 555);
	        			mPDFWriter.addText(423, 556, 14, DATA.get(x++).toString());
		        mPDFWriter.addText(295, 531, 10, res.getString(R.string.total));
	        		mPDFWriter.addLine(423, 531, 550, 531);
	        			//mPDFWriter.addText(100, 678, 14, DATA[x++++].toString());
        
	        
	        		mPDFWriter.addImage(65, 506,sub2);  		
	        mPDFWriter.addText(65, 488, 10, res.getString(R.string.subdiv));
	        	mPDFWriter.addLine(140, 488, 296, 488);
	        		mPDFWriter.addText(140, 489, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(296, 488, 10, res.getString(R.string.road));
	        	mPDFWriter.addLine(378, 488, 550, 488);
	        		mPDFWriter.addText(378, 489, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(65, 464, 10, res.getString(R.string.milepost));
        		mPDFWriter.addLine(184, 464, 550, 464);
        			mPDFWriter.addText(184, 465, 14, DATA.get(x++).toString());
        			mPDFWriter.addText(264, 465, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(65, 441, 10, res.getString(R.string.prot));
        		mPDFWriter.addLine(140, 441, 550, 441);
        			mPDFWriter.addText(140, 442, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(65, 417, 10, res.getString(R.string.other));
        		mPDFWriter.addLine(122, 417, 550, 417);
        			mPDFWriter.addText(122, 418, 14, DATA.get(x++).toString());
        
        		mPDFWriter.addImage(65, 374,sub3);	 
	        mPDFWriter.addText(75, 364, 10, res.getString(R.string.authority));
	        	mPDFWriter.addText(75, 350, 10, res.getString(R.string.number));
	        		mPDFWriter.addText(75, 336, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(135, 364, 10, res.getString(R.string.dispatcher));
	        	mPDFWriter.addText(135, 350, 10, res.getString(R.string.initials));
	        		mPDFWriter.addText(135, 336, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(222, 350, 10, res.getString(R.string.worktime));
	        	mPDFWriter.addText(202, 336, 12, DATA.get(x++).toString());
	        		mPDFWriter.addText(232, 336, 12, DATA.get(x++).toString());
	        mPDFWriter.addText(307, 350, 10, res.getString(R.string.clear));
	        	mPDFWriter.addText(307, 336, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(463, 350, 10, res.getString(R.string.comment));
	        	mPDFWriter.addText(463, 336, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(373, 350, 10, res.getString(R.string.total));
        		
        		mPDFWriter.addLine(65, 374, 550, 374);
        		mPDFWriter.addLine(65, 346, 550, 346);
        		mPDFWriter.addLine(65, 332, 550, 332);
        
        		mPDFWriter.addLine(65, 374, 65, 332);
        		mPDFWriter.addLine(126, 374, 126, 332);
        		mPDFWriter.addLine(187, 374, 187, 332);
        		mPDFWriter.addLine(296, 374, 296, 332);
        		mPDFWriter.addLine(366, 374, 366, 332);
        		mPDFWriter.addLine(434, 374, 434, 332);
        		mPDFWriter.addLine(550, 374, 550, 332);
        		
        		
        		mPDFWriter.addImage(65, 257, sub4);	
	        mPDFWriter.addText(80, 243, 10, res.getString(R.string.engine));
	        	mPDFWriter.addText(80, 215, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(197, 243, 10, res.getString(R.string.expected));
	        	mPDFWriter.addText(197, 215, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(292, 243, 10, res.getString(R.string.passed));
	        	mPDFWriter.addText(292, 215, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(369, 243, 10, res.getString(R.string.direction));
	        	mPDFWriter.addText(369, 215, 14, DATA.get(x++).toString());
	        mPDFWriter.addText(464, 243, 10, res.getString(R.string.comment));
	        	mPDFWriter.addText(464, 215, 14, DATA.get(x++).toString());
	        
		        mPDFWriter.addLine(65, 257, 550, 257);
		        mPDFWriter.addLine(65, 229, 550, 229);
		        mPDFWriter.addLine(65, 214, 550, 214);
		        
		        mPDFWriter.addLine(65, 257, 65, 214);
		        mPDFWriter.addLine(176, 257, 176, 214);
		        mPDFWriter.addLine(267, 257, 267, 214);
		        mPDFWriter.addLine(356, 257, 356, 214);
		        mPDFWriter.addLine(428, 257, 428, 214);
		        mPDFWriter.addLine(550, 257, 550, 214);
	    		
        
        mPDFWriter.addText(65, 119, 10, res.getString(R.string.fsig));
        //mPDFWriter.addText(65, 95, 10, res.getString(R.string.date));
        mPDFWriter.addText(298, 119, 10, res.getString(R.string.csig));
        //mPDFWriter.addText(360, 95, 10, res.getString(R.string.date));

        mPDFWriter.addImage(65, 33, FSIG);
        	//mPDFWriter.addLine(176, 118, 357, 118);
        //mPDFWriter.addLine(398, 118, 550, 118);
        	mPDFWriter.addText(176, 119, 14, DATA.get(x++).toString());
        mPDFWriter.addImage(298, 33, CSIG);
        	//mPDFWriter.addLine(176, 35, 357, 95);
        //mPDFWriter.addLine(398, 95, 550, 95);
        	mPDFWriter.addText(409, 119, 14,  DATA.get(x).toString());
         
        
        String form = mPDFWriter.asString();
        
        return form;
	}
	
	private void outputToFile(String FILENAME, String pdfContent, String encoding) {
        File newFile = new File(prefs.getString("PATH", Environment.getExternalStorageDirectory()+"/") + FILENAME+".pdf");
		//File newFile = new File(Environment.getExternalStorageDirectory()+"/" + FILENAME+"_v"+Indentifiers.md5.hashCode()+".txt");
        File pFile = new File(prefs.getString("PATH", Environment.getExternalStorageDirectory()+"/") + FILENAME+".txt");
        StringBuilder sb = new StringBuilder();
       for(String s:DATA){
    	   sb.append(s).append("/n");
       }
       
       //Set<String> set =prefs.getStringSet("OLDFORM", null);
       //set.add(sb.toString());
       prefs.edit().putString("OLDFORM", sb.toString());
        try {
            newFile.createNewFile();
            pFile.createNewFile();
            try {
                FileWriter writer = new FileWriter(pFile);
                writer.append(data.toString());
                writer.flush();
                writer.close();
            } catch(FileNotFoundException e) {
            	Log.e("TXTFail", String.valueOf(pFile.getAbsolutePath()));
            }
            try {
            	Intent intent = new Intent();
            	FileOutputStream railFile = new FileOutputStream(newFile);
            	railFile.write(pdfContent.getBytes(encoding));
            	railFile.close();
            	intent.putExtra("formdata", pdfContent.getBytes(encoding));
                setResult(1, intent);
                finish();
            } catch(FileNotFoundException e) {
            	Log.e("PDFpath", String.valueOf(newFile.getAbsolutePath()));
            }
        } catch(IOException e) {
        	Log.e("NOTFOUND", FILENAME);
        }
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //Data = new String[getIntent().getStringArrayExtra("DUMP").length];
        data=getIntent().getStringArrayExtra("DUMP");
        for(String abc:getIntent().getStringArrayExtra("DUMP")){
        	DATA.add(abc);
        }
        Log.d("DATAout", String.valueOf(DATA.size()));
        String pdfcontent = GenForm();
        outputToFile(DATA.get(0),pdfcontent,"ISO-8859-1");
    }
}