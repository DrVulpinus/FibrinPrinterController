package processcontrol;

import java.util.ArrayList;

import machinecontrol.GCode;
import machinecontrol.GCodeGenerator;

/**
 * This class does all of the calculations to determine the path for the extruder
 * @author Richard
 *
 */
public class PathCreator {
	GCodeGenerator genExt = new GCodeGenerator();
	GCodeGenerator genInit = new GCodeGenerator();
	GCodeGenerator genStretch = new GCodeGenerator();
	/*
	 * These are all of input parameters for G-Code Generation
	 */
	float xSize = 0; //The total width of the extrusion area, including side margins
	float ySize = 0; //The total length of the bed
	float sideMargins = 0; //The minimum distance of the first and last thread should be from either side of the bounding box
	float extrusionRate = 0; //The rate at which the pump should be run
	float extrusionLength = 0; //The length of the threads minus the end bars
	float feedRate = 0; //The feedrate of the extrusion head in mm/s
	float threadSpacing = 0; //The spacing in mm between each thread
	float threadStartPauseTime = 0; //The time to spend stopped to make a "blob" at the end/start of each thread
	long polymerizationTime = 0; //The time to spend polymerizing at the end of extrusion
	float barThickness = 0;  //The thickness of the bars at either end of the travel
	int numThreads = 0; //The number of threads to be produced
	float stretchRate = 0;
	float stretchPercent = 0;
	/*
	 * These are the internally determined variables that are use for path generation 
	 */
	float finalWidth = 0;
	
	
	public PathCreator(){
		
	}
	/**
	 * 
	 * @param _xSize
	 * @param _ySize
	 * @param _sideMargins
	 * @param _extrusionRate
	 * @param _extrusionLength
	 * @param _feedRate
	 * @param _threadSpacing
	 * @param _threadStartPauseTime
	 * @param _polymerizationTime
	 * @param _barThickness
	 */
	public void setParams(float _xSize, float _ySize, float _sideMargins, float _extrusionRate, float _extrusionLength, float _feedRate, float _threadSpacing, float _threadStartPauseTime, long _polymerizationTime, float _barThickness, float _numThreads, float _stretchPercent, float _stretchRate){
		xSize = _xSize;
		ySize = _ySize;
		sideMargins = _sideMargins;
		extrusionRate = _extrusionRate;
		extrusionLength = _extrusionLength;
		feedRate = _feedRate;
		threadSpacing = _threadSpacing;
		threadStartPauseTime = _threadStartPauseTime;
		polymerizationTime = _polymerizationTime;
		barThickness = _barThickness;
		numThreads = Float.floatToIntBits(_numThreads);
		stretchRate = _stretchRate;
		stretchPercent = _stretchPercent;
		
	}
	/**
	 * 
	 * @param _xSize
	 * @param _ySize
	 * @param _sideMargins
	 * @param _extrusionRate
	 * @param _extrusionLength
	 * @param _feedRate
	 * @param _threadSpacing
	 * @param _threadStartPauseTime
	 * @param _polymerizationTime
	 * @param _barThickness
	 */
	public void setParams(int _xSize, int _ySize, int _sideMargins, int _extrusionRate, int _extrusionLength, int _feedRate, int _threadSpacing, int _threadStartPauseTime, long _polymerizationTime, int _barThickness, int _numThreads, int _stretchPercent, int _stretchRate){
		xSize = _xSize;
		ySize = _ySize;
		sideMargins = _sideMargins;
		extrusionRate = _extrusionRate;
		extrusionLength = _extrusionLength;
		feedRate = _feedRate;
		threadSpacing = _threadSpacing;
		threadStartPauseTime = _threadStartPauseTime;
		polymerizationTime = _polymerizationTime;
		barThickness = _barThickness;
		numThreads = _numThreads;
		stretchRate = _stretchRate;
		stretchPercent = _stretchPercent;
	}
	public float getxSize() {
		return xSize;
	}
	public void setxSize(float _xSize) {
		xSize = _xSize;
	}
	public float getySize() {
		return ySize;
	}
	public void setySize(float _ySize) {
		ySize = _ySize;
	}
	public float getBarThickness(){
		return barThickness;
	}
	public float getStretchBarStart(){
		return barThickness + extrusionLength;
	}
	public void checkValues(){
		System.out.println("X-Size: " + xSize);
		System.out.println("Y-Size: " + ySize);
		System.out.println("Side Margins: " + sideMargins);
		System.out.println("Extrusion Rate: " + extrusionRate);
		System.out.println("Extrusion Length: " + extrusionLength);
		System.out.println("Feed Rate: " + feedRate);
		System.out.println("Thread Spacing: " + threadSpacing);
		System.out.println("Thread Start Pause Time: " + threadStartPauseTime);
		System.out.println("Polymerization Time: " + polymerizationTime);
		System.out.println("Bar Thickness: " + barThickness);
		System.out.println("# of Threads: " + numThreads);
		System.out.println("Stretch Rate: " + stretchRate);
		System.out.println("Stretch Percent: " + stretchPercent);
	}
	
	
	public void doCalculations(){
		genExt.clearCodes();
		genInit.clearCodes();
		genStretch.clearCodes();
		genExt.setNewFeedRate(feedRate);
		finalWidth = xSize - (2*sideMargins);
		genExt.newRapidMove(sideMargins, 0, 0);
		float bar1StartY = barThickness;
		float bar1EndY = -3;
		float bar2StartY = barThickness + extrusionLength;
		float bar2EndY = bar2StartY+ barThickness +3;
		float currentX = sideMargins;
		float xStep = threadSpacing/2;
		int currThreads = 0;
		
		while (currThreads < numThreads) {
			genExt.newFeedMove(currentX, bar2StartY, 0);
			currentX += xStep;
			genExt.newFeedMove(currentX, bar2EndY, 0);
			currThreads++;
			if (currThreads >= numThreads){
				break;
			}
			currentX += xStep;
			genExt.newFeedMove(currentX, bar2StartY, 0);
			genExt.newFeedMove(currentX, bar1StartY, 0);
			currentX += xStep;
			genExt.newFeedMove(currentX, bar1EndY, 0);
			currThreads++;
			if (currThreads >= numThreads){
				break;
			}
			currentX += xStep;
			genExt.newFeedMove(currentX, bar1StartY, 0);
		}
		genExt.newRapidMove(xSize, ySize, extrusionLength);
		//genExt.newRapidMove(0, 0, 0);
		//System.out.println(currThreads);
		
		
		
		//Now calculate the initial stuff
		genInit.setNewFeedRate(400);
		//TODO find out the exact distance that this needs to move out
		genInit.newFeedMove(0, 0, extrusionLength);
		//TODO Input Offsets here, may need to add more steps for reliability
		genInit.newRapidMove(0, 0, -75);
		
		//Now do the stretching
		genStretch.setNewFeedRate(stretchRate);
		//TODO may need more offset here
		float stretchDist = (extrusionLength * stretchPercent)/100;
		stretchDist += barThickness;
		genStretch.newFeedMove(xSize, ySize, stretchDist);
		System.out.println(getExtCodes().size());
	}
	public ArrayList<GCode> getExtCodes(){
		return genExt.getGCodes();
	}
	public GCodeGenerator getExtGenerator(){
		return genExt;
	}
	public ArrayList<GCode> getInitCodes(){
		return genInit.getGCodes();
	}
	public GCodeGenerator getInitGenerator(){
		return genInit;
	}
	public ArrayList<GCode> getStretchCodes(){
		return genStretch.getGCodes();
	}
	public GCodeGenerator getStretchGenerator(){
		return genStretch;
	}
	public long getPolyTime(){
		return polymerizationTime;
	}
	public long getPolyTimeMillis(){
		return polymerizationTime*1000;
	}
	public float getExtrusionLength() {
		return extrusionLength;
	}
	public float getStretchPercent() {
		return stretchPercent;
	}
	public float getStretchBarEnd(){
		return barThickness + ((extrusionLength*stretchPercent)/100);
	}
	public void printCodes(){
		genExt.printCodes();
	}
	public class Point {
		private float x;
		private float y;
		public Point(float _x, float _y){
			x = _x;
			y = _y;
		}
		public float getX(){
			return x;
		}
		public float getY(){
			return y;
		}
		@Override
		public String toString(){
			String outStr ="";
			outStr += "x: ";
			outStr += x;
			outStr += " y: ";
			outStr += y;
			return outStr;
		}
	}

}
