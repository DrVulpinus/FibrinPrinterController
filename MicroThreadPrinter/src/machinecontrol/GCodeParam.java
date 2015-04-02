package machinecontrol;

public class GCodeParam{
	private char param;
	private float value;
	public GCodeParam(char _param, float _value){
		param = _param;
		value = _value;
	}
	public char getParam() {
		return param;
	}
	public void setParam(char _param) {
		this.param = _param;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float _value) {
		this.value = _value;
	}
}
