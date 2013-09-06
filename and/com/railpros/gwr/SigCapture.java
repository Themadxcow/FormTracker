package com.railpros.gwr;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
 
import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
public class SigCapture extends Activity  {
    signature mSignature;
    Paint paint;
    ViewGroup mainlayout;
    LinearLayout mContent;
    ImageButton clear;
    ImageButton save;
    
    //private LocationManager loc;
    float minx;
    float miny;
    float maxx;
    float maxy;
    Path scrib;
    ArrayList<Float> histX = new ArrayList<Float>();
    ArrayList<Float> histY = new ArrayList<Float>();

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sigcap);
        //loc = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //loc.
        mainlayout = (ViewGroup) findViewById(R.id.sigcanvas);
        clear = (ImageButton) findViewById(R.id.clear);
        save = (ImageButton) findViewById(R.id.save);
        
        
        minx=999;
        maxx=0;
        maxy=0;
        miny=999;
        save.setEnabled(false);
        //clear.setEnabled(prefs.getBoolean("SIGSTATE", false));
        mContent = (LinearLayout) findViewById(R.id.sign);
        mSignature = new signature(this, null);
        mContent.addView(mSignature);
        save.setOnClickListener(onButtonClick);
		clear.setOnClickListener(onButtonClick);
    }
 
    ImageButton.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == clear) {
                mSignature.clear();
            } else if (v == save) {
                mSignature.save();
            }
        }
    };
 
    public class signature extends View {
        static final float STROKE_WIDTH = 8f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();
        
        float lastTouchX;
        float lastTouchY;
        final RectF dirtyRect = new RectF();
 
        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.SQUARE);
            paint.setStrokeWidth(STROKE_WIDTH);
            paint.setStrokeMiter(HALF_STROKE_WIDTH);
            paint.setDither(true);
            paint.setARGB(255, 0, 0, 0);
            
        }
 
        public void clear() {
            path.reset();
            invalidate();
            histX.clear();
            histY.clear();
            save.setEnabled(false);
            clear.setEnabled(false);
        }
 

		public void save() {
			DisplayMetrics metrics = getResources().getDisplayMetrics();
        	for(float x : histX){
        		if(x<minx){
        			minx=x;
        		} 
        		if(x>maxx){
        			maxx=x;
        		}
        	}
        	for(float y : histY){
        		if(y<miny){
        			miny=y;
        		} 
        		if(y>maxy){
        			maxy=y;
        		}
        	}
        	maxx=Math.min(maxx, metrics.widthPixels);
        	maxy=Math.min(maxy, metrics.heightPixels-50);
        	minx=Math.max(minx, 1);
        	miny=Math.max(miny, 1);
        	int w = ((int) Math.ceil(maxx-minx));
        	int h = ((int) Math.ceil(maxy-miny));

        	Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(),
        			mContent.getHeight(), Bitmap.Config.ARGB_8888);
        	Canvas canvas = new Canvas(returnedBitmap);
        	Drawable bgDrawable = mContent.getBackground();

            if (bgDrawable != null) 
                bgDrawable.draw(canvas);  
            else 
            	canvas.drawColor(Color.TRANSPARENT);
            mContent.draw(canvas);

            returnedBitmap=Bitmap.createBitmap(returnedBitmap, (int)minx, (int)miny, (int)w, (int)h);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            returnedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bs);
            
            Intent intent = new Intent();
            intent.putExtra("byteArray", bs.toByteArray());
            setResult(1, intent);
            finish();
        }
 
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
 
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            histX.add(eventX);
            histY.add(eventY);
            save.setEnabled(true);
            clear.setEnabled(true);
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;
 
            case MotionEvent.ACTION_MOVE:
 
	            case MotionEvent.ACTION_UP:
	                resetDirtyRect(eventX, eventY);
	                int historySize = event.getHistorySize();
	                for (int i = 0; i < historySize; i++) {
	                    float historicalX = event.getHistoricalX(i);
	                    float historicalY = event.getHistoricalY(i);
	                    histX.add(historicalX);
	                    histY.add(historicalY);
	                    path.lineTo(historicalX, historicalY);
	                }
	                path.lineTo(eventX, eventY);
	                break;
	            }
//            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
//                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
//                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
//                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
            invalidate((int) (dirtyRect.left - STROKE_WIDTH),
                  (int) (dirtyRect.top - STROKE_WIDTH),
                  (int) (dirtyRect.right + STROKE_WIDTH),
                  (int) (dirtyRect.bottom + STROKE_WIDTH));
            lastTouchX = eventX;
            lastTouchY = eventY;
            return true;
        }
 
        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}