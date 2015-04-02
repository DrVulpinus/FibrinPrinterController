package processcontrol;

public interface ProcessStageListener {
	public void stageCompleted(ProcessStage completeStage);
	public void stageStarted(ProcessStage newStage);
	public void waitingForExternal();
	
}
