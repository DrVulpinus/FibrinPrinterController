package machinecontrol;

import java.util.ArrayList;

public class GCode {
private char letter;
private int number;
private ArrayList<GCodeParam> parameters = new ArrayList<GCodeParam>();
public GCode(char _letter, int _number){
	letter = _letter;
	number = _number;
}
public GCode(char _letter, int _number, GCodeParam... param){
	letter = _letter;
	number = _number;
	for (GCodeParam gCodeParam : param) {
		parameters.add(gCodeParam);
	};
}
public char getLetter(){
	return letter;
}
public int getNumber(){
	return number;
}
public ArrayList<GCodeParam> getParameters(){
	return parameters;
}
public boolean isMoveCode(){
	if(getLetter() == 'G'){
		if (getNumber() == 1 || getNumber() == 0){
			return true;
		}
	}
	return false;
}
@Override
public String toString(){
	String outCode = "";
	outCode += letter;
	outCode += number;
	outCode += " ";
	for (GCodeParam gCodeParam : parameters) {
		outCode += gCodeParam.getParam();
		outCode += gCodeParam.getValue();
		outCode += " ";
	}
	//outCode += ";";
	return outCode;
}
	
}
