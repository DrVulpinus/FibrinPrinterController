package machinecontrol;

import java.lang.Thread.State;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class GrblControl implements COMLineRecieved, ArrayAddListener{
	private MachineState machineState;
	private COMOutLines sendLines = new COMOutLines(); //New Commands are put here, each time a new command is added it sends it, and will continue sending commands until there are none left
	
	private IOPortControl grblPort;
	private boolean isCOMReady = false;
	private boolean allowMoreStatus = true;
	public GrblControl(String _port){
		grblPort = new IOPortControl(_port, 115200);
		sendLines.addAddListener(this);
	}
	public GrblControl(){
		for (String port : IOPortControl.getPorts()) {
			grblPort = new IOPortControl(port, 115200);
			if (verifySettings()){
				if( checkForGrbl()){
					return;
				}
			}
		}
		if (grblPort == null){
			try {
				throw new Exception("There is no Grbl Attached!");
			} catch (Exception e) {
				
				JOptionPane.showMessageDialog(null,e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				
			}
			
		}
		
	}
	public boolean isBufferEmpty(){
		return isCOMReady;
	}
	public void addNewGCode(GCode _code){
		sendLines.add(_code);
	}
	public String getPort(){
		return grblPort.getPort();
	}
	public boolean verifySettings(){
		if (!grblPort.connectPort()){
			return false;
		}
		if (!grblPort.disconnectPort()){
			return false;
		}
		return true;
	}
	public boolean connect(){
		if (grblPort.connectPort()){
			/*if(sr.getState() == State.NEW){
				sr.start();
			}*/
			grblPort.addNewLineListener(this);
			return true;
		}
		return false;
		
	}
	public boolean disconnect(){
		return grblPort.disconnectPort();
	}
	/**
	 * This method sends a reset command to the grbl board forcing all 
	 * operations to stop and everything with need to be started from the beginning
	 */
	public void emergencyStop(){
		if (grblPort.isConnected()){
			grblPort.sendDataLine("\u0018");
		}
	}
	
	
	public boolean checkForGrbl(){
		grblPort.removeAllListeners();
		grblPort.connectPort();
		if (grblPort.isConnected()){
		grblPort.sendDataLine("\u0018");
		long timeout =  System.currentTimeMillis() + 5000;
		//Look for a response, but only for so long (timeout)
		while (timeout > System.currentTimeMillis() && !grblPort.isNewAvail()){
		
		}		
		while (grblPort.isNewAvail()){
			//We got something!
			String response = grblPort.readNextLine();
			System.out.print("Here is the response: (");
			System.out.print(response);
			System.out.println(")");
			if (response.contains("Grbl")){
				//We got the right thing!
				isCOMReady = true;
				System.out.println("Found the Grbl");
				disconnect();
				grblPort.addNewLineListener(this);
				return true;
			}
		}
		}
		grblPort.addNewLineListener(this);
		disconnect();
		return false;		
	}
	
	public boolean isReturnGood(){
		long timeout =  System.currentTimeMillis() + 100000;
		//Look for a response, but only for so long (timeout)
		while (timeout > System.currentTimeMillis() && !grblPort.isNewAvail()){
		
		}
		
		if (grblPort.isNewAvail()){
			//We got something!
			String response = grblPort.readNextLine();
			if (response.equals("ok")){
				//We got the right thing!
				return true;
			}
			// TODO: Need to add error checking functionality here
		}
		return false;
	}

	/*
	class SendRecieve extends Thread {
        public SendRecieve() {
            
        }
        
@Override
        public void run() {
        	while (grblPort.isConnected()){
        		if (!sendLines.isEmpty()){
        			//We need to send the data, but only if we are ready
        			//First we send an CR+LF to verify that the board is ready by replying with "ok"
        			//Then send the lines, checking that after each one, we get an "ok" statement
        			if (checkForGrbl()){
        				while(!sendLines.isEmpty()){
        					int i = 0;
        					//We will retry up to 5 times.
            				while (i < 5 && !sendNextLine()){
            					i++;
            				}
            				
        				}
        			}
        			
        		}
    		}
           
        }
        public void sendGCode(GCode _code){    		
    		grblPort.sendDataLine(_code.toString());
    	}
        
    }
*/

	public void homeGrbl(){
		grblPort.removeAllListeners();
		grblPort.addNewLineListener(this);
		System.out.println("I made it to the wait");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("I made it past the wait");
		sendLines.add("$H");
		sendLines.add(new GCode('F', 3000));
		sendLines.add(new GCode('G', 92, new GCodeParam('X',0),new GCodeParam('Y',0),new GCodeParam('Z',0)));
		sendLines.add(new GCode('G', 1, new GCodeParam('X',0),new GCodeParam('Y',70),new GCodeParam('Z',70)));
		sendLines.add(new GCode('G', 92, new GCodeParam('X',0),new GCodeParam('Y',0),new GCodeParam('Z',0)));
		
	}
	public boolean isIdle(){
		if (machineState != MachineState.IDLE){
			return false;
		}
		return true;
	}
	public void getStatus(){
		System.out.println("Is More Status Allowed: " + allowMoreStatus);
		if (allowMoreStatus){
			grblPort.sendDataLine("?");
			allowMoreStatus = false;
		}
		
	}
	
	@Override
	public void newLineRecieved() {	//This gets called each time a new response is received from the GRBl	
		String response = grblPort.readNextLine();
		System.out.println(response);
		if (response.equals("ok")){
			//We got the right thing!
			
		}
		if (response.startsWith("<")&& response.endsWith(">")){
			allowMoreStatus = true;
			//We got a status response
			response = response.replaceAll("<", "");
			response = response.replaceAll(">", "");
			String data[] = response.split(",");
			switch (data[0].toUpperCase()) {
			case "IDLE":
				System.out.println("Running");
				machineState = MachineState.IDLE;
				break;
			case "RUN":
				System.out.println("Running");
				machineState = MachineState.RUNNING;
				return;
				//break;
			case "QUEUE":
				System.out.println("Running");
				machineState = MachineState.QUEUE;
				return;
			default:
				return;
			}
			
		}
		if (!sendLines.isEmpty()){//If there is more stuff in the buffer, keep on sending it.
    		grblPort.sendDataLine(sendLines.get(0));
    		sendLines.remove(0);
    	}
		else{
			isCOMReady = true;// We got to the end of the buffer list, make sure that we allow for the cascade to restart
		}
		// TODO: Need to add error checking functionality here
		
	}
	@Override
	public void itemAdded() {
		if (isCOMReady){
			isCOMReady = false; //An Item was added to an empty list, i.e. the cascade has stopped
			System.out.println("Time to send something: " + sendLines.get(0));
			grblPort.sendDataLine(sendLines.get(0));
			sendLines.remove(0);
		}
	}
	
}
