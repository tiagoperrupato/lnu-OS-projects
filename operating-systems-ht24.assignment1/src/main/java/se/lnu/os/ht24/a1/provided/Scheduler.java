package se.lnu.os.ht24.a1.provided;

public interface Scheduler extends ReportProvider{

	void newProcess(String processName, double cpuBurstDuration);
	
	void stop();	
}
