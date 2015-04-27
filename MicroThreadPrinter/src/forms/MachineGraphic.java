package forms;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;
import processcontrol.PathCreator;
import machinecontrol.GCode;

public class MachineGraphic extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7262504781097113916L;
	double bedX = 50;
	double bedY = 150;
	PathCreator pC;
	
	private ArrayList<int[]> extPoints = new ArrayList<int[]>();
	public MachineGraphic(PathCreator _pC){
		pC = _pC;
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		bedX = pC.getxSize();
		bedY = pC.getySize();
		System.out.println("Repainting");
		
		g2.setStroke(new BasicStroke(5));
		g2.setColor(Color.LIGHT_GRAY);
		g2.fill(getBedRectangle());
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.ORANGE);
		g2.fill(getHomeBar());
		g2.fill(getStretchBar());
		g2.setColor(Color.BLUE);
		g2.fill(getStretchedBar());
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.GREEN);
		extPoints.clear();
		for (GCode code : pC.getExtCodes()) {
			
			if (code.isMoveCode()){
				extPoints.add(getPixelFromPosition(
						(int)(code.getParameters().get(0).getValue()), 
						(int)(code.getParameters().get(1).getValue())));
			}
		}
		
		for (int i = 1; i < extPoints.size(); i++) {
			int[] startPoint = extPoints.get(i-1);
			int[] endPoint = extPoints.get(i);
			
			g.drawLine(startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
		}		
		//TODO: Animate the stretch shoe when stretching is active?
		
	}
	/**
	 * Finds the best size ratio for converting the bed positions into pixel coordinates while still maintaining 
	 * the aspect ratio of the bed
	 * @return The determined size ratio
	 */
	public double sizeRatio(){
		double heightRatio = (getSize().getHeight()-10)/bedY;
		double widthRatio = (getSize().getWidth()-10)/bedX;
		double finalRatio = widthRatio; //Default the final ratio to the width ratio
		if (heightRatio <= widthRatio){
			finalRatio = heightRatio;//If the height ratio is actually smaller, then change the value
		}
		return finalRatio;
	}
	public Rectangle getHomeBar(){
		Rectangle out = new Rectangle();
		double barThickPix = (pC.getBarThickness()*sizeRatio());
		out.setSize(getBedRectangle().width, (int)barThickPix);
		out.setLocation(getBedRectangle().x, getBedRectangle().y);
		return out;
	}
	
	public Rectangle getStretchBar(){
		Rectangle out = new Rectangle();
		double barThickPix = (pC.getBarThickness()*sizeRatio());
		out.setSize(getBedRectangle().width, (int)barThickPix);
		int bar2Pos = getPixelFromPosition(0, (int) pC.getStretchBarStart())[1];
		out.setLocation(getBedRectangle().x, bar2Pos);
		return out;
	}
	public Rectangle getStretchedBar(){
		Rectangle out = new Rectangle();
		double barThickPix = (pC.getBarThickness()*sizeRatio());
		out.setSize(getBedRectangle().width, (int)barThickPix);
		int bar2Pos = getPixelFromPosition(0, (int) pC.getStretchBarEnd())[1];
		out.setLocation(getBedRectangle().x, bar2Pos);
		return out;
	}
	
	public Rectangle getBedRectangle(){
		Rectangle output = new Rectangle();
		//For the sake of making the graphic easy to understand, it is important that we maintain the correct aspect ratio
		//This is done by dividing the available width and height by the widths and heights for the given bed area
		//and using the smaller of those quotients to determine the correct sizes for the graphic
		
		
		output.setSize((int)(bedX*sizeRatio()),(int)(bedY*sizeRatio()));//Set the final size of the bed Rectangle
		int xMargin = (int)((getSize().getWidth() - output.getWidth())/2);//Find the remaining room left in the X axis and then divide that by 2 to get the margin size
		int yMargin = (int)((getSize().getHeight() - output.getHeight())/2);//Find the remaining room left in the Y axis and then divide that by 2 to get the margin size
		
		output.setLocation(xMargin, yMargin);//Set the location so that the bed will be centered in the margins
		//System.out.println(output.toString());
		return output;
	}
	
	/**
	 * This method returns an array of the format [x,y] where the values "x" and "y" correspond to the absolute
	 * position in the JPanel of a position given in terms of the dimensions of the physical bed
	 * @param _positionX The bed position in the X axis
	 * @param _positionY The bed position in the Y axis
	 * @return An array of the format [x,y] where "x" is the x position, and "y" is the y position
	 */
	public int[] getPixelFromPosition(int _positionX, int _positionY){
		int output[] = {0,0};
		double xOut = _positionX * sizeRatio();
		double yOut = _positionY * sizeRatio();
		
		xOut += getBedRectangle().getLocation().getX();
		yOut += getBedRectangle().getLocation().getY();
		
		output[0] = (int)xOut;
		output[1] = (int)yOut;
		//System.out.println("Original Point here: (" + _positionX + "," + _positionY + ")");
		//System.out.println("Point here: (" + output[0] + "," + output[1] + ")");
		return output;
	}
	
}
