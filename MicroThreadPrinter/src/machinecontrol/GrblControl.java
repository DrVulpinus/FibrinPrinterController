package machinecontrol;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JOptionPane;

public class GrblControl implements COMLineRecieved, ArrayAddListener{
	private MachineState machineState;
	private COMOutLines sendLines = new COMOutLines(); //New Commands are put here, each time a new command is added it sends it, and will continue sending commands until there are none left
	
	private IOPortControl grblPort;
	private Hashtable<Integer,Float> grblSettings = new Hashtable<Integer,Float>();
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
		return sendLines.isEmpty();
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
	/**
	 * This method is actually pretty dangerous.  It should only be called from within the checkForGrbl.
	 * It asks for Grbl's settings, reads them, and loads them into the local ArrayList.
	 * To do this it simply sends the command, waits 2.5 seconds, then looks for a reply.
	 * If no reply is given, it simply returns, if a reply is given, it loads those settings into the Arraylist
	 * @return
	 */
	private void getGrblSettings(){
		grblPort.sendDataLine("$$");//Send the command to get the settings
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Grbl returns a bunch of lines, with each line containing a different setting and value
		//The format of each line follows the following format (the items in quotes would be replaced with an actual value):
		//$"setting key" = "setting value" ("setting description")
		//Below is an example of this format:
		//$0=10 (step pulse, usec)
		while(grblPort.isNewAvail()){
			String rawReturn = grblPort.readNextLine();
			if (rawReturn.toCharArray()[0] == '$'){ //Make sure each line we are getting is actually a line with a setting
				//Process the string to obtain the key and the value
				rawReturn = rawReturn.substring(1, rawReturn.length()-1);
				String settingKeyString = rawReturn.split("=")[0];
				String rawValueString = rawReturn.split("=")[1];
				String valueString = rawValueString.split(" ")[0];
				grblSettings.put(Integer.getInteger(settingKeyString),Float.valueOf(valueString));
			}
		}
	}
	public float getGrblSettingValue(int _settingKey){
		return grblSettings.get(_settingKey);
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
			if (response.contains("Grbl")){
				getGrblSettings();
				System.out.println("Found the Grbl");
				disconnect();
				
				return true;
			}
		}
		}
		
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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendLines.add("$H");
		sendLines.add(new GCode('F', 3000));
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
		if (isBufferEmpty()){
			sendLines.addForImmediateSend("?");
		}
		else{
			machineState = MachineState.RUNNING;
		}
			
		
	}
	
	@Override
	public void newLineRecieved() {	//This gets called each time a new response is received from the GRBl	
		String response = grblPort.readNextLine();
		response.trim();
		System.out.println("Recieve: " + response);
		//System.out.println("HAHAHAH");
		if (response.contains("ok") && grblPort.ok == false){			
			grblPort.ok = true;		
			System.out.println("Got ACK");
			if (!sendLines.isEmpty()){//If there is more stuff in the buffer, keep on sending it.
				sendLines.remove(0);
				grblPort.sendDataLine(sendLines.get(0));   		
	    	}
		}
		if (response.contains("error")){
			System.out.println("ERROR...ERROR...ERROR...ERROR...ERROR...ERROR...ERROR...ERROR...ERROR");
			grblPort.sendDataLine(sendLines.get(0));  
		}
		if (response.contains("<")&&response.contains(">")){
			if (!sendLines.isEmpty()){//If there is more stuff in the buffer, keep on sending it.
			//	grblPort.sendDataLine(sendLines.remove(0));   		
	    	}
			response = response.replaceAll("<", "");
			response = response.replaceAll(">", "");
			String data[] = response.split(",");
			for (String string : data) {
				string = string.toUpperCase();
			}
			System.out.println("************************************* "+data[0]);
			switch (data[0].toUpperCase()) {
			case "IDLE":
				machineState = MachineState.IDLE;
				break;
			case "RUN":
				machineState = MachineState.RUNNING;
				return;
				//break;
			case "QUEUE":
				machineState = MachineState.QUEUE;
				return;
			default:
				return;
			}
			
		}
		
		
		
		// TODO: Need to add error checking functionality here
		
	}
	@Override
	public void itemAdded() {
		/*
		 * If there is only one item in the list, we can safely assume
		 * that the recursive COM loop has stopped, and we should now
		 * trigger it to restart by send one line manually
		 */
		if (sendLines.size() == 1){
			grblPort.addNewLineListener(this);
			grblPort.sendDataLine(sendLines.get(0));		
		}
		
	}
	@Override
	public void preAddNew() {
		// TODO Auto-generated method stub
		if (sendLines.isEmpty()){
			//grblPort.removeAllListeners(); 
		}
	}
	
}
