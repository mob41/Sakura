package com.github.mob41.sakura.ann;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;

import javax.swing.Timer;

public class AnnounceThread implements Runnable {

	private boolean isRunning = false;
	private Timer timer;
	private static AnnounceThread runnable;
	private static Thread thread;
	
	private ActionListener checkAnnounce = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			checkAnnounceNow();
		}
		
	};
	
	public static void startThread(){
		runnable = new AnnounceThread();
		thread = new Thread(runnable);
		thread.setName("AnnounceThread");
		thread.start();
	}
	
	public static AnnounceThread getRunnable(){
		return runnable;
	}
	
	public static Thread getThread(){
		return thread;
	}
	
	public void checkAnnounceNow(){
		List<String[]> data = AnnounceMem.getAllData();
		String[] announce;
		Calendar cal = Calendar.getInstance();
		Calendar tar;
		for (int i = 0; i < data.size(); i++){
			announce = data.get(i);
			tar = Calendar.getInstance();
			tar.setTimeInMillis(Long.parseLong((String) announce[6]));
			if (cal.after(tar)){
				AnnounceMem.removeAnnounceByUID((String) announce[1]);
			}
		}
	}
	
	@Override
	public void run() {
		start();
	}
	
	public void start(){
		if (!isRunning){
			timer = new Timer(1000, checkAnnounce);
			timer.start();
		}
	}
	
	public void stop(){
		if (isRunning){
			
		}
	}

}
