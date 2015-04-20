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
	tf = _tf;
}
public void startTimer(){
	startTime =  System.currentTimeMillis();
	running = true;
	this.start();
}
public void startCountDownTimer(long _seconds){
	startTime = System.currentTimeMillis() + (_seconds*1000);
	running = true;
	this.start();
}
public void stopTimer(){
	stopTime = System.currentTimeMillis();
	running = false;
	//this.interrupt();
}
public void resetTimer(){
	startTime = 0;
}
public long getTimeMillis(){
	return Math.abs(System.currentTimeMillis() - startTime);
}
public String getTimeString(){
	String outTime = "";
	long thisTime = getTimeMillis()/1000;
	if (!running){
		thisTime = (stopTime-startTime)/1000;
	}	
	long hours = thisTime/3600;
	long minutes = (thisTime%3600)/60;
	long seconds = (thisTime%3600)%60;
	outTime = hours + ":" + minutes + ":" + seconds;
	return outTime;
}

@Override
public void run(){
	while(true){
	if (tf != null){
		tf.setText(getTimeString());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (this.isInterrupted()){
			return;
		}
	}
	}
}
}
