package com.cpumon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.example.cpumonitor.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class CPUMon extends Activity {

	private Thread t;
	String[] data;
	double[] prevData;
	double sum1, sum2, sum3, psum;
	double totPercentage, widgetPercentage;
	TextView userlbl, systemlbl, otherlbl, totallbl, idlelbl;
	Button startBtn;
	CPUMonScreen widget;
	boolean paused = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpumon);

		userlbl = (TextView) findViewById(R.id.userLbl);
		systemlbl = (TextView) findViewById(R.id.systemLbl);
		otherlbl = (TextView) findViewById(R.id.otherLbl);
		totallbl = (TextView) findViewById(R.id.totalLbl);
		idlelbl = (TextView) findViewById(R.id.idleLbl);
		widget = (CPUMonScreen) findViewById(R.id.usageScreen);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("onPause", "Paused");
        widget.clearLines();
		t.interrupt();
	}

	@Override
	protected void onResume() {
		super.onResume();
		prevData = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		paused = false;

		t = new Threading();
		t.start();
		Log.i("onResume", "Resumed");
	}

	class Threading extends Thread {
		
		@Override
		public void interrupt() {
			super.interrupt();
			paused = true;
		}
		
		@Override
		public void run() {
			try {
				updateLabels();
			} catch (Throwable e) {
				Log.e(null, "Oops - thread ended unexpectedly.", e);
			}
		}
		
		public void readStats() {
	    	String line = "";
	    	try {
	    	    BufferedReader mounts = new BufferedReader(new FileReader("/proc/stat"));
	
	    	    if ((line = mounts.readLine()) != null) {
	    	        line = mounts.readLine();
	    	    }
	    	}
	    	catch (FileNotFoundException e) {
	    	    Log.d("Cannot find /proc/stat...", null);
	    	}
	    	catch (IOException e) {
	    	    Log.d("Ran into problems reading /proc/stat...", null);
	    	}
	    	
	    	data = line.split(" ");
	    }
	
		// runs in separate thread
		public void updateLabels() throws Exception {
			double widgetSum, pWidgetSum;
			for ( ; ; ) {
	    		Thread.sleep(500);
	    		if (paused) return;
	    		
		    	readStats();
		    	
		    	// convert current data values
		    	Double i1 = Double.valueOf(data[1]);
		    	Double i2 = Double.valueOf(data[2]);
		    	Double i3 = Double.valueOf(data[3]);
		    	Double i4 = Double.valueOf(data[4]);
		    	Double i5 = Double.valueOf(data[5]);
		    	Double i6 = Double.valueOf(data[6]);
		    	Double i7 = Double.valueOf(data[7]);
		    	sum1 = i1 + i2;
		    	sum2 = i5 + i6 + i7;
		    	sum3 = i1 + i2 + i3 + i5 + i6 + i7;
		    	widgetSum = i1 + i2 + i3;
		    	
		    	// convert previous data values
		    	Double p1 = Double.valueOf(prevData[1]);
		    	Double p2 = Double.valueOf(prevData[2]);
		    	Double p3 = Double.valueOf(prevData[3]);
		    	Double p4 = Double.valueOf(prevData[4]);
		    	Double p5 = Double.valueOf(prevData[5]);
		    	Double p6 = Double.valueOf(prevData[6]);
		    	Double p7 = Double.valueOf(prevData[7]);
		    	psum = p1 + p2 + p3 + p5 + p6 + p7;		
		    	pWidgetSum = p1 + p2 + p3;
		    	
		    	totPercentage = (double)Math.round(((sum3 - psum) / ((sum3 + i4) - (psum + p4))) * 1000) / 10;
		    	widgetPercentage = (double)Math.round(((widgetSum - pWidgetSum) / ((widgetSum + i4) - (pWidgetSum + p4))) * 1000) / 10;
		    	prevData = new double[] { 0, i1, i2, i3, i4, i5, i6, i7 };

	    		userlbl.post(new Runnable() {
	
					@Override
					public void run() {
		    			userlbl.setText("User: " + (int)sum1);
		            	systemlbl.setText("System: " + data[3]);
		            	otherlbl.setText("Other: " + (int)sum2);
		            	totallbl.setText("Total Work: " + (int)sum3 + " (" + totPercentage + "%)");
		            	idlelbl.setText("Idle: " + data[4]);
				    	widget.setSize((int)((widgetPercentage / 100) * 300));
					}
	        	});
	    	}
		}
	}
}
