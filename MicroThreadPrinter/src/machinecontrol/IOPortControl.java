package machinecontrol;
import java.io.*;
import java.util.*;

import gnu.io.*;
public class IOPortControl implements SerialPortEventListener{
	private NRSerialPort serial;
	private BufferedReader ins;
	private DataOutputStream outs;
	private ArrayList<String> recievedLines = new ArrayList<>();
	private ArrayList<COMLineRecieved> listeners = new ArrayList<COMLineRecieved>();
    String port;
	public IOPortControl(String _port, int _baud){
		serial = new NRSerialPort(_port, _baud);
		port = _port;
		
	}
	public void configurePort(String _port, int _baud){
		serial = new NRSerialPort(_port, _baud);
		port = _port;
	}
	public static Set<String> getPorts(){
		return NRSerialPort.getAvailableSerialPorts();
	}
	public void addNewLineListener(COMLineRecieved _listener){
		listeners.add(_listener);
	}
	public void removeAllListeners(){
		listeners.clear();
	}
	/**
	 * Connect to the specified serial port
	 * @return true if the connection is successful or if it is already connected
	 */
	public boolean connectPort(){
		boolean connected = serial.connect();
		//serial.notifyOnDataAvailable(true);
		if (connected){
			try {
				serial.addEventListener(this);
			} catch (TooManyListenersException e) {
			
				e.printStackTrace();
			}
			
		ins = new BufferedReader(new InputStreamReader(serial.getInputStream()));
		outs = new DataOutputStream(serial.getOutputStream());	
		}
		return connected;
	}
	/**
	 * Disconnect the serial port
	 * @return true if the disconnection is successful 
	 */
	public boolean disconnectPort(){
		if (serial.isConnected()){
			serial.disconnect();
		}		
		return !serial.isConnected();
				
	}
	public void sendDataLine(String _dataLine){
		try {
			outs.writeChars(_dataLine+"\r\n");
			outs.flush();
			System.out.println(_dataLine);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isConnected(){
		return serial.isConnected();
	}
	public String readNextLine(){
		if (!recievedLines.isEmpty()){
			String outStr = recievedLines.get(0);
			
			System.out.println(outStr);
			recievedLines.remove(0);
			return outStr;
		}
		return null;
	}
	public boolean isNewAvail(){
		if (!recievedLines.isEmpty()){
			return true;
		}
		return false;
	}
	public String getPort(){
		return port;
	}
	@Override
	public void serialEvent(SerialPortEvent ev) {
		try {
			if (ins.ready()){
				while (ins.ready()){
					try {
						recievedLines.add(ins.readLine());
					} catch (IOException e) {
						
					}
					
					for (COMLineRecieved comLineRecieved : listeners) {
						comLineRecieved.newLineRecieved();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
