package processcontrol;

import java.util.ArrayList;

import javax.swing.JTextField;

import machinecontrol.GCode;
import machinecontrol.GCodeGenerator;
import machinecontrol.GCodeParam;
import machinecontrol.GrblControl;
import machinecontrol.PumpControl;

public class ProcessExecution extends Thread{
	PumpControl pumpCtrl;
	GrblControl grblCtrl;
	GCodeGenerator codeProg;
	private boolean extHold = true;
	PathCreator paths;
	ProcessStage currentStage;
	JTextField timerView;
	JTextField timerExtrudeView;
	JTextField timerStretchView;
	JTextField timerOpTime;
	Timer tmr;
	Timer tmrOp;
	Timer tmrExtrude;
	Timer tmrStretch;
	private ArrayList<ProcessStage> executionPath = new ArrayList<ProcessStage>();
	
	private ArrayList<ProcessStageListener> listeners = new ArrayList<ProcessStageListener>();
	public ProcessExecution(PumpControl _pumpCtrl, GrblControl _grblCtrl, PathCreator _paths, JTextField _timerView, JTextField _timerExtrudeView, JTextField _timerStretchView, JTextField _timerOpTime){
		pumpCtrl = _pumpCtrl;
		grblCtrl = _grblCtrl;
		paths = _paths;
		timerView = _timerView;
		timerOpTime = _timerOpTime;
		timerStretchView = _timerStretchView;
		timerExtrudeView = _timerExtrudeView;
		tmr = new Timer(_timerView);
		tmrOp = new Timer(timerOpTime);
		tmrExtrude = new Timer(timerExtrudeView);
		tmrStretch = new Timer(timerStretchView);
		executionPath.add(ProcessStage.PRESTART);
	}
	
	public void setupRun(ProcessStage... stages){
		for (ProcessStage processStage : stages) {
			executionPath.add(processStage);
		}
		
	}

	
	
	public void setupCompleteRunManual(){
		setupRun(ProcessStage.INITIALIZE_PUMP, 
				ProcessStage.READY_TO_HOME, ProcessStage.HOMING,
				ProcessStage.READY_TO_ALIGN_STRETCH, ProcessStage.ALIGNING_STRETCH_BAR,
				ProcessStage.READY_TO_START_PUMP, ProcessStage.PURGING_PUMP,
				ProcessStage.READY_TO_EXTRUDE, ProcessStage.EXTRUDING,
				ProcessStage.CLEAN_PUMP, ProcessStage.MANUALPOLYMERIZING,
				ProcessStage.READY_TO_STRETCH, ProcessStage.STRETCHING,
				ProcessStage.OPERATION_COMPLETE);
	}
	public void setupCompleteRunSemiAuto(){
		setupRun(ProcessStage.INITIALIZE_PUMP, 
				ProcessStage.READY_TO_HOME, ProcessStage.HOMING,
				ProcessStage.READY_TO_ALIGN_STRETCH, ProcessStage.ALIGNING_STRETCH_BAR,
				ProcessStage.READY_TO_START_PUMP, ProcessStage.PURGING_PUMP,
				ProcessStage.READY_TO_EXTRUDE, ProcessStage.EXTRUDING,
				ProcessStage.CLEAN_PUMP, ProcessStage.POLYMERIZING,
				ProcessStage.READY_TO_STRETCH, ProcessStage.STRETCHING,
				ProcessStage.OPERATION_COMPLETE);
	}
	public void setupCompleteRunFullAuto(){
		setupRun(ProcessStage.INITIALIZE_PUMP, 
				ProcessStage.HOMING,
				ProcessStage.ALIGNING_STRETCH_BAR,
				ProcessStage.PURGING_PUMP,
				ProcessStage.EXTRUDING, 
				ProcessStage.STRETCHING,
				ProcessStage.OPERATION_COMPLETE);
	}
	public void setupExtrudeOnlyAuto(){
		setupRun(ProcessStage.INITIALIZE_PUMP, 
				ProcessStage.HOMING,
				ProcessStage.ALIGNING_STRETCH_BAR,
				ProcessStage.PURGING_PUMP,
				ProcessStage.EXTRUDING,
				ProcessStage.CLEAN_PUMP,
				ProcessStage.OPERATION_COMPLETE);
	}
	public void setupStretchOnlyAuto(){
		setupRun( 
				ProcessStage.HOMING,
				ProcessStage.ALIGNING_STRETCH_BAR,
				ProcessStage.STRETCHING,
				ProcessStage.OPERATION_COMPLETE);
	}
	
	public void addStageListener(ProcessStageListener _listener){
		listeners.add(_listener);
	}
	
	private void stageCompleted(){
		for (ProcessStageListener processStageListener : listeners) {
			processStageListener.stageCompleted(currentStage);
		}
	}
	private void stageStarted(){
		for (ProcessStageListener processStageListener : listeners) {
			processStageListener.stageStarted(currentStage);
		}
	}
	public ProcessStage getCurrentStage(){
		return currentStage;
	}
	
	/**
	 * This method blocks execution until the external hold is cleared
	 */
	private void waitForExt(){
		extHold = true;
		for (ProcessStageListener processStageListener : listeners) {
			processStageListener.waitingForExternal();
			while(extHold){
			
			}
		}
	}
	

	
	/**
	 * If program execution has been paused for an external hold, this cancels the hold and allows for execution to continue
	 */
	public void finishHold(){
		if (extHold){
			extHold = false;
		}
	}
	
	
	/**
	 * This cancels the current operation and forces
	 * everything to be restarted
	 */
	public void cancelOperation(){
		this.interrupt();
	}
	//This Class should provide all of the functionality to combine all of the different elements that have been created/calculated
	//and then use them to coordinate and execute a coherent series of events to perform the operation.
	
	/*
	 * The sequence of events should follow this line of execution
	 * 	-First initialize and configure the pump
	 * 	-Home all axes of the machine
	 * 	-Push the stretch bar to the correct position
	 * 	-Move out nozzle to start position
	 * 	-Start syringe pump
	 * 	-Wait for clearance to continue after purging pump
	 * 	-Wipe nozzle
	 * 	-Move to start position
	 * 	-Run program
	 * 	-Complete Program
	 * 	-Alert Operators
	 * 	-Stop Syringe Pump?
	 * 	-Wait for Poly-Time
	 * 	-Begin Stretch
	 * 	-Complete Stretch
	 * 	-Alert Operators
	 */
	
	
	
	/**
	 * Running start will begin running the currently loaded run configuration as was determined the last time
	 * the application called one of the setup functions.  The machine will begin operations as soon as this method is called
	 */
	@Override
	public void start(){
		super.start();
	}
	
	
	public void doSleep(long _millis){
		try {
			Thread.sleep(_millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The run method here increments through each stage of currently configured run in a state-machine.
	 * In this way it is quite easy to setup different run modes to run through a different set of steps.
	 */
	@Override
	public void run(){
		paths.checkValues();
		for (ProcessStage processStage : executionPath) {
			if (isInterrupted()){
				return;
			}
			currentStage = processStage;
			stageStarted(); //We have now started a new stage
			switch (currentStage) {
			case PRESTART:
				
				break;
			case INITIALIZE_PUMP:
				tmrOp.startTimer();
				if(pumpCtrl != null){
				pumpCtrl.configurePump();
				}
				break;
			case READY_TO_HOME:
				waitForExt();
				break;
			case HOMING:
				grblCtrl.homeGrbl();
				while(grblCtrl.isIdle()){
					doSleep(500);
					grblCtrl.getStatus();
				}
				while(!grblCtrl.isIdle()){
					doSleep(500);
					grblCtrl.getStatus();
				}
				// TODO: Go to 0,0
				break;
			case READY_TO_ALIGN_STRETCH:
				waitForExt();	
				break;
			case ALIGNING_STRETCH_BAR:							
				//Do the necessary things to setup the bar
				for (GCode code : paths.getInitCodes()) {					
					grblCtrl.addNewGCode(code);
				}
				System.out.println("Added All GCodes");
				while(grblCtrl.isIdle()){
					doSleep(500);
					grblCtrl.getStatus();
				}
				while(!grblCtrl.isIdle()){
					doSleep(500);
					grblCtrl.getStatus();
				}
				break;
			case READY_TO_START_PUMP:
				waitForExt();
				grblCtrl.addNewGCode(new GCode('G', 0, new GCodeParam('Y', -3)));
				break;
			case PURGING_PUMP:
				if(pumpCtrl != null){
					pumpCtrl.startPump();
				}
				break;
			case READY_TO_EXTRUDE:
				waitForExt();
				break;
			case PRE_EXT_WIPE:
				//Wipe the nozzle
				break;
			case EXTRUDING:
				tmrExtrude.startTimer();
				System.out.println(paths.getExtCodes().size());
				for (GCode code : paths.getExtCodes()) {
					System.out.println(code.toString());
					grblCtrl.addNewGCode(code);
				}
				System.out.println("Added All GCodes");
				
				while(grblCtrl.isIdle()){
					doSleep(500);
					grblCtrl.getStatus();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while(!grblCtrl.isIdle()){
					doSleep(500);
					grblCtrl.getStatus();
				}
				tmrExtrude.stopTimer();
				break;
			case CLEAN_PUMP:
				if (pumpCtrl != null){
					pumpCtrl.stopPump();
				}
				break;
			case MANUALPOLYMERIZING:
				tmr.startTimer();
				break;
			case POLYMERIZING:
				tmr.startCountDownTimer(paths.getPolyTime());
				while (tmr.getTimeMillis() >= 0){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				tmr.stopTimer();
				break;
			case READY_TO_STRETCH:
				waitForExt();
				tmr.stopTimer();
				break;
			case STRETCHING:
				tmrStretch.startTimer();
				for (GCode code : paths.getStretchCodes()) {
					grblCtrl.addNewGCode(code);
				}
				while(grblCtrl.isIdle()){
					doSleep(500);
					grblCtrl.getStatus();
				}
				while(!grblCtrl.isIdle()){
					doSleep(500);
					grblCtrl.getStatus();
				}
				tmrStretch.stopTimer();
				break;
			case OPERATION_COMPLETE:
				tmrOp.stopTimer();
				grblCtrl.sendHomeAtZero();
				//Shutdown Sequence
				break;
			default:
				break;
			}
			stageCompleted();//Stage execution complete
		}
	}
		
}
