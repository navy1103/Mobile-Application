package com.appsrox.remindme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ChartView extends View {
	
//	private static final String TAG = "ChartView";
	
	private String[] arr;
	
	private Paint linePaint;
	private Paint textPaint;

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		arr = getResources().getStringArray(R.array.spinner2_arr);
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
        // Must manually scale the desired text size to match screen density
		textPaint.setTextSize(12 * getResources().getDisplayMetrics().density);
		textPaint.setColor(0xFF000000);		
		
		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setColor(0xFF000000);
		linePaint.setStrokeWidth(0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int len = arr.length;
		int w = getWidth();
		int h = getHeight() / (len-1);
		int a = (int)textPaint.ascent();
		
		for (int i=1; i<len; i++) {
			canvas.drawText(arr[i], 0, i*h , textPaint);
			//canvas.drawLine(0, i*h, w, i*h, linePaint);			
		}		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		return super.onSaveInstanceState();
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
	}

}
