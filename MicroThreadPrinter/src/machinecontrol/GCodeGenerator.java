package machinecontrol;

import java.util.ArrayList;



public class GCodeGenerator {
	private ArrayList<GCode> gCodeProg = new ArrayList<GCode>();
	
	public GCodeGenerator(){
		
	}
	public void clearCodes(){
		gCodeProg.clear();
	}
	public void newRapidMove(float x, float y, float z){
		gCodeProg.add(new GCode('G', 0, new GCodeParam('X', x),new GCodeParam('Y', y),new GCodeParam('Z', z)));
	}
	public void newFeedMove(float x, float y, float z){
		//if (!Float.isFinite(z)){
			//System.err.println("Do not move Z");
			//gCodeProg.add(new GCode('G', 1, new GCodeParam('X', x),new GCodeParam('Y', y)));
		//}
		//else {
			gCodeProg.add(new GCode('G', 1, new GCodeParam('X', x),new GCodeParam('Y', y),new GCodeParam('Z', z)));
	//	}
	}
	public void newArcCW(float startX, float startY, float endX, float endY, float centerX, float centerY){
		float offsetX = startX - centerX;
		float offsetY = startY - centerY;
		gCodeProg.add(new GCode('G', 3, new GCodeParam('X', endX), new GCodeParam('Y', endY), new GCodeParam('I', offsetX), new GCodeParam('J',offsetY)));
	}
	public void newArcCCW(float startX, float startY, float endX, float endY, float centerX, float centerY){
		float offsetX = startX - centerX;
		float offsetY = startY - centerY;
		gCodeProg.add(new GCode('G', 2, new GCodeParam('X', endX), new GCodeParam('Y', endY), new GCodeParam('I', offsetX), new GCodeParam('J',offsetY)));
	}
	public void setNewFeedRate(float feedRate){
		gCodeProg.add(new GCode('F',(int)feedRate));
	}
	public void newDwellCode(float dwellTime){
		gCodeProg.add(new GCode('G',4,new GCodeParam('P', dwellTime)));
	}
	public ArrayList<GCode> getGCodes(){
		return gCodeProg;
	}
	public void printCodes(){
		for (GCode gCode : gCodeProg) {
			System.out.println(gCode.toString());
		}
	}
	
}
