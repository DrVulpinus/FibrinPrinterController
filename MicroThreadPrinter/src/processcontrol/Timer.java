package processcontrol;

import javax.swing.JTextField;

public class Timer extends Thread{
	private long startTime;
	private long stopTime;
	private JTextField tf;
	private boolean running = false;
public Timer(){
	
}
public Timer(JTextField _tf){
	this.tf = _tf;
}
public void startTimer(){
	this.startTime =  System.currentTimeMillis();
	this.running = true;
	this.start();
}
public void startCountDownTimer(long _seconds){
	this.startTime = System.currentTimeMillis() + (_seconds*1000);
	this.running = true;
	this.start();
}
public void stopTimer(){
	this.stopTime = System.currentTimeMillis();
	this.running = false;
	//this.interrupt();
}
public void resetTimer(){
	this.startTime = 0;
}
public long getTimeMillis(){
	return Math.abs(System.currentTimeMillis() - this.startTime);
}
public String getTimeString(){
	String outTime = "";
	long thisTime = getTimeMillis()/1000;
	if (!this.running){
		thisTime = (this.stopTime-this.startTime)/1000;
	}	
	long hours = thisTime/3600;
	long minutes = (thisTime%3600)/60;
	long seconds = (thisTime%3600)%60;
	String strHours = String.valueOf(hours);
	String strMinutes = String.valueOf(minutes);
	String strSeconds = String.valueOf(seconds);
	if (hours < 10){
		strHours = "0" + strHours;
	}
	if (minutes < 10){
		strMinutes = "0" + strMinutes;
	}
	if (seconds < 10){
		strSeconds = "0" + strSeconds;
	}
	outTime = strHours + ":" + strMinutes + ":" + strSeconds;
	return outTime;
}

@Override
public void run(){
	while(true){
	if (this.tf != null){
		this.tf.setText(getTimeString());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.isInterrupted()){
			return;
		}
	}
	}
}
}
