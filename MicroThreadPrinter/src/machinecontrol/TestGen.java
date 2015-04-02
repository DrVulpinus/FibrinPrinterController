package machinecontrol;

import java.awt.Canvas;

import processcontrol.PathCreator;
import processcontrol.PathCreator.Point;

public class TestGen  {

	public static void main(String[] args) {
		PathCreator pC = new PathCreator();
		
		pC.setParams(100,300,5,5,200,5,5,0,0,10);
		pC.doCalculations();
		pC.printCodes();
		
	}

}
