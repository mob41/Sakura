package com.mob41.sakura.info;

import com.mob41.hkoweather.api.WeatherManager;
import com.mob41.hkoweather.api.WeatherReport;
import com.mob41.hkoweather.exception.InvaildStationException;
import com.mob41.kmbeta.api.ArrivalManager;
import com.mob41.kmbeta.api.ArrivalTime;
import com.mob41.kmbeta.api.MultiArrivalManager;
import com.mob41.kmbeta.exception.CouldNotLoadDatabaseException;
import com.mob41.kmbeta.exception.InvalidArrivalTargetException;
import com.mob41.kmbeta.exception.InvalidException;

public class InformFetcher {
	
	public static final String DEFAULT_WEATHER_STATION_CODE = "WTS";
	
	private WeatherManager wman = null;
	
	private MultiArrivalManager arrman = null;
	
	private static InformFetcher fetcher = null;
	
	public static void runFetcher(boolean fetchNow) throws Exception{
		fetcher = new InformFetcher(fetchNow);
	}
	
	public static InformFetcher getFetcher(){
		return fetcher;
	}
	
	public InformFetcher(boolean fetchNow) throws Exception{
		wman = new WeatherManager(DEFAULT_WEATHER_STATION_CODE);
		arrman = new MultiArrivalManager(100);
		if (fetchNow){
			fetchData();
		}
	}
	
	public void fetchData() throws Exception{
		wman.fetchWeatherReport();
		arrman.fetchAllData();
	}
	
//Weather-based
	
	public WeatherReport getFetchedWeatherReport(){
		return wman.getWeatherReport();
	}
	
	public WeatherManager getWeatherManager(){
		return wman;
	}
	
	public void changeWeatherStationCode(String station_code) throws InvaildStationException{
		wman = new WeatherManager(station_code);
	}
	
//Bus Arrival Time based
	
	public ArrivalManager getArrivalManager(int index){
		return arrman.getArrivalManagers().get(index);
	}
	
	public MultiArrivalManager getMultiArrivalManager(){
		return arrman;
	}
	
	public void addNewArrivalManager(String busno, String stop_code, int bound, int language) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		ArrivalManager man = new ArrivalManager(busno, stop_code, bound, language);
		this.arrman.addArrivalManager(man);
	}
	
	public String getArrivalTime_Formatted(int index) throws InvalidException{
		ArrivalManager man = arrman.getArrivalManagers().get(index);
		return man.getArrivalTime_Formatted();
	}
	
	public String getArrivalTimeRemaining_Formatted(int index){
		ArrivalManager man = arrman.getArrivalManagers().get(index);
		return man.getArrivalTimeRemaining_Formatted();
	}
	
//Quick-check Bus Arrival Time
	
	public ArrivalTime getSpecifiedArrivalTime(String busno, String stop_code, int bound, int language) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		ArrivalManager man = new ArrivalManager(busno, stop_code, bound, language);
		return man.getArrivalTime();
	}
	
	public String getSpecifiedArrivalRemainingTime_Formatted(String busno, String stop_code, int bound, int language) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		ArrivalManager man = new ArrivalManager(busno, stop_code, bound, language);
		return man.getArrivalTimeRemaining_Formatted();
	}
}
