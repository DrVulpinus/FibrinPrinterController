package processcontrol;

public class ProcessParam {
	private String name;
	private double value;
	private String strValue = null;
	public ProcessParam(String _name, double _value){
		name = _name;
		value = _value;
	}
	public ProcessParam(String _name, String _value){
		name = _name;
		strValue = _value;
	}
	@Override
	public String toString(){
		String outStr = name;
		outStr += ": ";
		if (strValue == null){
			outStr += Double.toString(value);
		}
		else{
			outStr += strValue;
		}
		return outStr;
	}
}
