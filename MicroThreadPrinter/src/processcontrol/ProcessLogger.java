package processcontrol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import application.SettingsManager;

public class ProcessLogger {
	private ArrayList<ProcessParam> parameters = new ArrayList<ProcessParam>();
	private Date startTime;
	private Date endTime;
	private String processName = "";
	private String processDescription = "";
	private SettingsManager prefs;
	public ProcessLogger(SettingsManager _prefs){
		prefs = _prefs;
		processName = startTime.toString();
	}
	public ProcessLogger(SettingsManager _prefs, String _processName){
		prefs = _prefs;
		processName = _processName;
	}
	public ProcessLogger(SettingsManager _prefs, String _processName, String _processDescription){
		prefs = _prefs;
		processName = _processName;
		processDescription = _processDescription;
	}
	public void addParam(ProcessParam _param){
		parameters.add(_param);
	}
	public void addParam(String _name, double _value){
		addParam(new ProcessParam(_name, _value));
	}
	/**
	 * Records the start time of the process using the specified date and time value
	 * @param _startTime the time to be recorded as the start time
	 */
	public void recordStartTime(Date _startTime){
		startTime = _startTime;
	}
	/**
	 * Records the start time of the process as the time at which this method is called
	 */
	public void recordStartTime(){
		recordStartTime(Calendar.getInstance().getTime());
	}
	
	/**
	 * Records the end time of the process using the specified date and time value
	 * @param _endTime the time to be recorded as the end time
	 */
	public void recordEndTime(Date _endTime){
		endTime = _endTime;
	}
	
	/**
	 * Records the end time of the process as the time at which this method is called
	 */
	public void recordEndTime(){
		recordEndTime(Calendar.getInstance().getTime());
	}
	public static String newLineChar(){
		return "\r\n";
	}
	
	/**
	 * This method takes all of the information supplied and actually generates the output log file
	 * @throws IOException 
	 */
	public void generateLogFile() throws IOException{
		String path = prefs.getLogFileDir();
		path += "\\";
		path += processName;
		path += ".txt";
		System.out.println(path);
		File logFile = new File(path);
		if (!logFile.exists()){
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{//If that file already exists, add a timestamp to avoid overwrites
			processName += startTime.toString();
			path = prefs.getLogFileDir();
			path += "\\";
			path += processName;
			path += ".txt";
			System.out.println(path);
			logFile = new File(path);
			if (!logFile.exists()){
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		@SuppressWarnings("resource")
		FileWriter writer = new FileWriter(logFile);
		writer.write("Process Name: " + processName + newLineChar());
		writer.write("Process Description: " + processDescription + newLineChar());
		writer.write("Start Time: " + startTime.toString() + newLineChar());
		writer.write("End Time: " + endTime.toString() + newLineChar());
		writer.write("Process Parameters: "+ newLineChar());
		for (ProcessParam processParam : parameters) {
			writer.write(processParam.toString() + newLineChar());
		}
		writer.close();
		
	}

}
