package application;

import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class SettingsManager {
	private static final String PUMP_COM_PORT = "pump_com_port";
	private static final String GRBL_COM_PORT = "grbl_com_port";
	private static final String LOG_FILES = "log_files";
	private static final String LOG_FILE_DIR = "log_file_dir";
	private static final String SYRINGE_UNITS = "syringe_units";
	private static final String SYRINGE_DIA = "syringe_dia";
	private static final String BED_X = "bed_x";
	private static final String BED_Y = "bed_y";
	private static final String BAR_THICKNESS = "bar_thickness";
	private static final String PERFORM_EXTRUSION = "perform_extrusion";
	private static final String SIDE_MARGINS = "side_margins";
	private static final String EXTRUSION_RATE = "extrusion_rate";
	private static final String FEEDRATE = "feedrate";
	private static final String THREAD_SPACING = "thread_spacing";
	private static final String THREAD_START_PAUSE_TIME= "tspt";
	private static final String POLY_TIME = "poly_time";
	private static final String THREAD_LENGTH = "thread_length";
	private static final String NUM_THREADS = "num_threads";
	private static final String PERFORM_STRETCH = "perform_stretch";
	private static final String STRETCH_RATE = "stretch_rate";
	private static final String STRETCH_PERCENT = "stretch_percent";
	private static final String DIVIDER_WIDTH = "divider_width";
	private static final String WINDOW_WIDTH = "window_width";
	private static final String WINDOW_HEIGHT = "window_height";
	Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	public void addPreferenceChangeListener(PreferenceChangeListener _pcl){
		prefs.addPreferenceChangeListener(_pcl);
	}
	
	public int getDividerWidth(){
		return prefs.getInt(DIVIDER_WIDTH, 100);
	}
	public int getWindowWidth(){
		return prefs.getInt(WINDOW_WIDTH, 500);
	}
	public int getWindowHeight(){
		return prefs.getInt(WINDOW_HEIGHT, 500);
	}
	public String getSyringeUnits(){
		return prefs.get(SYRINGE_UNITS, "ul/m");
	}
	public String getSyringeDia(){
		return prefs.get(SYRINGE_DIA, "00.00");
	}
	public boolean getLogFiles(){
		return prefs.getBoolean(LOG_FILES, false);
	}
	public String getLogFileDir(){
		return prefs.get(LOG_FILE_DIR, "");
	}
	public String getPumpPort(){
		return prefs.get(PUMP_COM_PORT, "COM1");
	}
	public String getGrblPort(){
		return prefs.get(GRBL_COM_PORT, "COM1");
	}
	public int getBarThickness(){
		return prefs.getInt(BAR_THICKNESS, 25);
	}
	public int getBedX(){
		return prefs.getInt(BED_X, 150);
	}
	public int getBedY(){
		return prefs.getInt(BED_Y, 310);
	}
	public int getExtrusionRate(){
		return prefs.getInt(EXTRUSION_RATE, 3);
	}
	public int getFeedrate(){
		return prefs.getInt(FEEDRATE, 1000);
	}
	public int getNumThreads(){
		return prefs.getInt(NUM_THREADS, 10);
	}
	public boolean getPerformExtrusion(){
		return prefs.getBoolean(PERFORM_EXTRUSION, true);
	}
	public boolean getPerformStretch(){
		return prefs.getBoolean(PERFORM_STRETCH, true);
	}
	public String getPolyTime(){
		return prefs.get(POLY_TIME, "00:00:00");
	}
	public int getSideMargins(){
		return prefs.getInt(SIDE_MARGINS, 5);
	}
	public int getStretchPercent(){
		return prefs.getInt(STRETCH_PERCENT, 300);
	}
	public int getStretchRate(){
		return prefs.getInt(STRETCH_RATE, 100);
	}
	public int getThreadLength(){
		return prefs.getInt(THREAD_LENGTH, 80);
	}
	public int getThreadSpacing(){
		return prefs.getInt(THREAD_SPACING, 10);
	}
	public int getTSPT(){
		return prefs.getInt(THREAD_START_PAUSE_TIME, 0);
	}
	
	
	
	
	public void setSyringeUnits(String _syringeUnits){
		prefs.put(SYRINGE_UNITS, _syringeUnits);
	}
	public void setSyringeDia(String _syringeDia){
		prefs.put(SYRINGE_DIA, _syringeDia);
	}
	public void setLogFiles(boolean _logFiles){
		prefs.putBoolean(LOG_FILES, _logFiles);
	}
	public void setLogFileDir(String _logFileDir){
		prefs.put(LOG_FILE_DIR, _logFileDir);
	}
	public void setPumpPort(String _port){
		prefs.put(PUMP_COM_PORT, _port);
	}
	public void setGrblPort(String _port){
		prefs.put(GRBL_COM_PORT, _port);
	}
	public void setBarThickness(int _thickness){
		prefs.putInt(BAR_THICKNESS, _thickness);
	}
	public void setBedX(int _val){
		prefs.putInt(BED_X, _val);
	}
	public void setBedY(int _val){
		prefs.putInt(BED_Y, _val);
	}
	public void setExtrusionRate(int _val){
		prefs.putInt(EXTRUSION_RATE, _val);
	}
	public void setFeedrate(int _val){
		prefs.putInt(FEEDRATE, _val);
	}
	public void setNumThreads(int _val){
		prefs.putInt(NUM_THREADS, _val);
	}
	public void setPreformExtrusion(boolean _val){
		prefs.putBoolean(PERFORM_EXTRUSION, _val);
	}
	public void setPerformStretch(boolean _val){
		prefs.putBoolean(PERFORM_STRETCH, _val);
	}
	public void setPolyTime(String _val){
		prefs.put(POLY_TIME, _val);
	}
	public void setSideMargins(int _val){
		prefs.putInt(SIDE_MARGINS, _val);
	}
	public void setStretchPercent(int _val){
		prefs.putInt(STRETCH_PERCENT, _val);
	}
	public void setStretchRate(int _val){
		prefs.putInt(STRETCH_RATE, _val);
	}
	public void setThreadLength(int _val){
		prefs.putInt(THREAD_LENGTH, _val);
	}
	public void setThreadSpacing(int _val){
		prefs.putInt(THREAD_SPACING, _val);
	}
	public void setTSPT(int _val){
		prefs.putInt(THREAD_START_PAUSE_TIME, _val);
	}
	public void setDividerWidth(int _val){
		prefs.putInt(DIVIDER_WIDTH, _val);
	}
	public void setWindowWidth(int _val){
		prefs.putInt(WINDOW_WIDTH,_val);
	}
	public void setWindowHeight(int _val){
		prefs.putInt(WINDOW_HEIGHT,_val);
	}
	
}
