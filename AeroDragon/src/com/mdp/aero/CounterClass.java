package com.mdp.aero;

import java.util.concurrent.TimeUnit;

import android.os.CountDownTimer;

public class CounterClass extends CountDownTimer { 
	
	private String time;
	
	public CounterClass(long millisInFuture, long countDownInterval) { 
		super(millisInFuture, countDownInterval); 
		} 
	
	@Override 
	public void onTick(long millisUntilFinished) { 
		long millis = millisUntilFinished; 
		String hms = String.format(
				"%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), 
				TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))); 
				time = hms;
		} 
	
	public String getTime(){
		return time;
	}

	@Override
	public void onFinish() {

	}
	
	} 