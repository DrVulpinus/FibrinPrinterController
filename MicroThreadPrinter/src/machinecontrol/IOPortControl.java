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
	public boolean ok = false;
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
		if (listeners.size() > 0){
			System.out.println("Too Many Listeners");
			return;
		}
		//recievedLines.clear();
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
			ok = false;
			outs.writeChars(_dataLine);
			System.out.println("Send: " +_dataLine);
			outs.writeChars(" \n");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println(_dataLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected(){
		return serial.isConnected();
	}
	public String readNextLine(){
		if (!recievedLines.isEmpty()){
			String outStr = recievedLines.get(0);
			
			//System.out.println(outStr);
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
						String inline = ins.readLine();
						if (listeners.size() >0){							
								recievedLines.add(inline);
								//System.out.println("Recieve: " + inline);							
						}
					} catch (IOException e) {
						
					}
					
					for (COMLineRecieved comLineRecieved : listeners) {
						comLineRecieved.newLineRecieved();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
