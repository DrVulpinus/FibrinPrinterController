package processcontrol;

public class Timer {
	private long startTime;
	private long stopTime;
	
	
public Timer(){
	
}
public void startTimer(){
	startTime =  System.currentTimeMillis();
}
public void startCountDownTimer(long _seconds){
	startTime = System.currentTimeMillis() + (_seconds*1000);
}
public void stopTimer(){
	stopTime = System.currentTimeMillis();
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
	long hours = thisTime/3600;
	long minutes = (thisTime%3600)/60;
	long seconds = (thisTime%3600)%60;
	outTime = hours + ":" + minutes + ":" + seconds;
	return outTime;
}
}
