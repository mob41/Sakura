package com.github.mob41.sakura.err.old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.mob41.sakura.ToastMessage;

public class ErrorHandler implements Runnable{
	
	private static final Logger logger = LogManager.getLogger(ErrorHandler.class.getName());
	
	public boolean error = false;
	private int erroraccept = 5;
	private boolean running = false;
	private boolean checking = false;
	private static ErrorHandler errthread;
	private ActionListener checkerror = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!checking){
				checking = true;
				if (erroraccept <= 0){
					error = true;
					stop();
					timer.stop();
					checking = false;
					return;
				}
				checking = false;
			}
		}
		
	};
	private Timer timer = new Timer(10000, checkerror);
	
	public static void launch(){
		errthread = new ErrorHandler();
		errthread.start();
		errthread.resetErrors();
	}
	
	public static ErrorHandler getThread(){
		return errthread;
	}
	public boolean isRunning(){
		return running;
	}
	
	@Override
	public void run(){
		start();
	}
	
	public void resetErrors(){
		logger.info("All errors resetted.");
		error = false;
		erroraccept = 5;
	}
	
	public void rejectError(){
		erroraccept--;
		logger.error("1 error rejected. Remaining: " + erroraccept);
	}
	
	public void start(){
		if (!running){
			running = true;
			resetErrors();
			timer.start();
		}
	}
	
	public void stop(){
		if (running){
			running = false;
			timer.stop();
		}
	}
	
	public void restart(){
		stop();
		start();
	}
}
