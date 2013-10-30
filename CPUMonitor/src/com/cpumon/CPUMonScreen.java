package com.cpumon;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CPUMonScreen extends View {
	int size, start, curPoint = -1, nextPoint;
	boolean toggle = true;
	Paint paint = new Paint();
	ArrayDeque<Integer> lines = new ArrayDeque<Integer>();
	
	public CPUMonScreen(Context context, AttributeSet set) {
        super(context, set);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//draw monitor screen
    	paint.setColor(Color.BLACK);
    	canvas.drawRect(0, 0, 400, 300, paint);
    	paint.setColor(Color.GREEN);
    	canvas.drawLine(0, 0, 400, 0, paint);
    	canvas.drawLine(0, 75, 400, 75, paint);
    	canvas.drawLine(0, 150, 400, 150, paint);
    	canvas.drawLine(0, 225, 400, 225, paint);
    	canvas.drawLine(0, 300, 400, 300, paint);
    	
    	//draw horizontal bar
    	paint.setColor(Color.BLACK);
    	canvas.drawRect(415, 0, 445, 300, paint);
    	paint.setColor(Color.YELLOW);
    	canvas.drawRect(415, 300 - size, 445, 300, paint);

    	drawVerticalLines(canvas);
    	drawCPUPoints(canvas);
	}

	private void drawCPUPoints(Canvas canvas) {
		paint.setColor(Color.YELLOW);
		
		int curPoint = 0, nextPoint;
		Integer[] cur = lines.toArray(new Integer[20]);
		
		start = 400 - (20 * lines.size());
		
		if (lines.size() > 1) {
			for (int i = 0; i < lines.size() - 1; ++i) {
				try {
					curPoint = cur[i];
					nextPoint = cur[i+1];
					canvas.drawLine(start, 300 - curPoint, start + 20, 300 - nextPoint, paint);
					start += 20;
				} catch (NoSuchElementException e) {
					Log.e("drawCPUPoints", "no such element");
				}
				catch (Throwable e) {
					Log.e("drawCPUPoints", "Something went wrong drawing lines", e);
				}
			}
			canvas.drawLine(start, 300 - curPoint, start, 300 - curPoint, paint);
		}
	}

	private void drawVerticalLines(Canvas canvas) {
		paint.setColor(Color.GRAY);
		
		start = (toggle) ? 0 : 20;
		
		for (int i = 0; i < 10; ++i) {
			canvas.drawLine(start, 0, start, 300, paint);
			start += 40;
		}
		
		toggle = (toggle) ? false : true;
	}
    
    public void clearLines() {
        lines.clear();
    }

	public void setSize(int total) {
		size = total;
		lines.addLast(total);
		if (lines.size() > 20) { lines.removeFirst(); }
		invalidate();
	}
}
