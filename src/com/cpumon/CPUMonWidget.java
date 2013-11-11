package com.cpumon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CPUMonWidget extends View{
	int size;
	
	public CPUMonWidget(Context context, AttributeSet set) {
        super(context, set);
	}

	@Override
	protected void onDraw(Canvas canvas) {
    	Paint paint = new Paint();
    	paint.setColor(Color.BLACK);
    	canvas.drawRect(0, 30, 300, 60, paint);
    	paint.setColor(Color.RED);
    	canvas.drawRect(0, 30, size, 60, paint);
	}

	public void setSize(int total) {
		this.size = total;
		invalidate();
	}

}
