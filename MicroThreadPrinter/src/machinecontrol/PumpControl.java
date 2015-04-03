package machinecontrol;

import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import javax.swing.JOptionPane;

import application.SettingsManager;

public class PumpControl {
	private IOPortControl pumpPort;
	private SettingsManager prefs;
	public PumpControl(SettingsManager _prefs, String _port){
		pumpPort = new IOPortControl(_port, 9600);
		prefs = _prefs;
	}
	public PumpControl(){
		for (String port : IOPortControl.getPorts()) {
			pumpPort = new IOPortControl(port, 9600);
			System.out.print("Checking for pump on ");
			System.out.print(port);
			System.out.println(" ...");
			if (verifySettings()){
				if( checkForPump()){
					System.out.println("Found pump!");
					return;
				}
			}
		}
		pumpPort = null;
		if (pumpPort == null){
			try {
				throw new Exception("There is no Pump Attached!");
			} catch (Exception e) {
				
				JOptionPane.showMessageDialog(null,e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				
			}
			
		}
		
	}
	public boolean verifySettings(){
		if (!connect()){
			return false;
		}
		if (!disconnect()){
			return false;
		}
		return true;
	}
	public String getPort(){
		return pumpPort.getPort();
	}
	public void configurePump(){
		connect();
		pumpPort.sendDataLine("dia " + prefs.getSyringeDia());
		pumpPort.sendDataLine("mode I");
		pumpPort.sendDataLine("ratei " + "00500" + " " + prefs.getSyringeUnits());
	}
	public boolean connect(){
		return pumpPort.connectPort();
	}
	public boolean disconnect(){
		return pumpPort.disconnectPort();
	}
	public boolean checkForPump(){
		connect();
		if (pumpPort.isConnected()){
		pumpPort.sendDataLine("prom?");
		long timeout =  System.currentTimeMillis() + 5000;
		//Look for a response, but only for so long (timeout)
		while (timeout > System.currentTimeMillis() && !pumpPort.isNewAvail()){
		
		}
		
		if (pumpPort.isNewAvail()){
			//We got something!
			String response = pumpPort.readNextLine();
			
			if (response.contains("210")){
				//We something which appears to be from this pump
				disconnect();
				return true;
			}
			
		}
		}
		disconnect();
		return false;		
	}
	public void startPump(){
		pumpPort.sendDataLine("run");
	}
	public void stopPump(){
		pumpPort.sendDataLine("stop");
	}
}
