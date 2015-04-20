package machinecontrol;

import java.util.ArrayList;

public class COMOutLines extends ArrayList<String>{
	private ArrayList<ArrayAddListener> listeners = new ArrayList<ArrayAddListener>();
	

	
@Override
public boolean add(String e){
	boolean out = super.add(e);
	for (ArrayAddListener arrayAddListener : listeners) {
		arrayAddListener.itemAdded();
	}
	return out;
}

public boolean add(GCode _code){	
	boolean out = super.add(_code.toString());
	for (ArrayAddListener arrayAddListener : listeners) {
		arrayAddListener.itemAdded();
	}
	return out;
}
public void addForImmediateSend(GCode _code){	
	super.add(0, _code.toString());
	for (ArrayAddListener arrayAddListener : listeners) {
		arrayAddListener.itemAdded();
	}
}
public void addForImmediateSend(String _e){	
	super.add(0, _e);
	for (ArrayAddListener arrayAddListener : listeners) {
		arrayAddListener.itemAdded();
	}	
}




public void addAddListener(ArrayAddListener _listener){
	listeners.add(_listener);
}
}
