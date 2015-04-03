package processcontrol;
/*
 * This Enumerator is used for determining which step of an operation the process is at
 * The stage is set/determined by ProcessExecution
 * Other Classes can utilize this information to inform users, request action, and trigger other events
 * 
 * The sequence of events should follow this line of execution
 * 	-First initialize and configure the pump
 * 	-Home all axes of the machine
 * 	-Push the stretch bar to the correct position
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
public enum ProcessStage {
	PRESTART, 
	INITIALIZE_PUMP, 
	HOMING, 
	ALIGNING_STRETCH_BAR,
	READY_TO_START_PUMP, PURGING_PUMP,
	READY_TO_EXTRUDE, PRE_EXT_WIPE, EXTRUDING, EXTRUSION_COMPLETE,
	CLEAN_PUMP, POLYMERIZING,
	READY_TO_STRETCH, STRETCHING, STRETCH_COMPLETE,
	OPERATION_COMPLETE, READY_TO_HOME, READY_TO_ALIGN_STRETCH, MANUALPOLYMERIZING
}
