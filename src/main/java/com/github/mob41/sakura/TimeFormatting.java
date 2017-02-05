package com.github.mob41.sakura;

import java.util.Calendar;

public class TimeFormatting {

	private static int hr;
	private static int min;
	private static int sec;
	private static int AM_PM;
	private static int dy;
	private static int mon;
	private static int yr;
	
	public static String getFormattedTime(boolean hour24){
		Calendar cal = Calendar.getInstance();
		if (hour24){
			hr = cal.get(Calendar.HOUR_OF_DAY);
			min = cal.get(Calendar.MINUTE);
			sec = cal.get(Calendar.SECOND);
		}
		else
		{
			hr = cal.get(Calendar.HOUR);
			min = cal.get(Calendar.MINUTE);
			sec = cal.get(Calendar.SECOND);
		}
		
		AM_PM = cal.get(Calendar.AM_PM);
		
		String AMPM = AM_PM == Calendar.AM ? "AM" : "PM";
		AMPM = hour24 ? "" : AMPM;
		String hour = hr < 10 ? "0" + hr : Integer.toString(hr);
		String minute = min < 10 ? "0" + min : Integer.toString(min);
		String second = sec < 10 ? "0" + sec : Integer.toString(sec);
		
		String output = hour + " : " + minute + " : " + second + " " + AMPM;
		return output;
	}
	
	public static String getLoggingTime(){
		Calendar cal = Calendar.getInstance();
		hr = cal.get(Calendar.HOUR_OF_DAY);
		min = cal.get(Calendar.MINUTE);
		sec = cal.get(Calendar.SECOND);
		
		dy = cal.get(Calendar.DAY_OF_MONTH);
		mon = cal.get(Calendar.MONTH) + 1;
		yr = cal.get(Calendar.YEAR);
		
		String day = dy < 10 ? "0" + dy : Integer.toString(dy);
		String month = mon < 10 ? "0" + mon  : Integer.toString(mon);
		String year = yr < 10 ? "0" + yr  : Integer.toString(yr);
		
		//String hour = hr < 10 ? "0" + hr : Integer.toString(hr);
		String minute = min < 10 ? "0" + min : Integer.toString(min);
		String second = sec < 10 ? "0" + sec : Integer.toString(sec);
		
		String output = day + month + year + "-" + hr + minute + second;
		return output;
	}
	
	public static String getFormattedDate(){
		Calendar cal = Calendar.getInstance();
		dy = cal.get(Calendar.DAY_OF_MONTH);
		mon = cal.get(Calendar.MONTH) + 1;
		yr = cal.get(Calendar.YEAR);
		
		String day = dy < 10 ? "0" + dy : Integer.toString(dy);
		String month = mon < 10 ? "0" + mon  : Integer.toString(mon);
		String year = yr < 10 ? "0" + yr  : Integer.toString(yr);
		
		String output = day + " - " + month + " - " + year;
		return output;
	}
}
