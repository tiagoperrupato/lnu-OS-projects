package se.lnu.os.ht24.a1.required;

import java.util.ArrayDeque;
import java.util.List;

import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

/*
 * MODIFY THIS CLASS ONLY IF YOU HAVE STUDIED INHERITANCE IN OBJECT-ORIENTED PROGRAMMING
 * 
 * Doing the assignment through an abstract class that gathers the common code 
 * in the FIFO Scheduler and the SJF scheduler is a better programming practice. Therefore, if you know 
 * how to write that "cleaner" code, feel free to fill this class :-) instead of 
 * repeating code in the FifoSchedulerImpl.java and SjfSchedulerImpl.java.
 * 
 * If you do NOT know about inheritance in object-oriented programming, 
 * you can simply ignore this class and fill your code in FifoSchedulerImpl.java and SjfSchedulerImpl.java, 
 * even if it seems that you are repeating yourself in some parts.
 * 
 * There is no penalty for not knowing the inheritance in this course. But people who know it might feel bad 
 * for not using it in this clear case, so we offer the possibility. 
 * 
 */
public abstract class AbstractScheduler implements Scheduler {

	protected Reporter reporter;
	protected long startingTime;

	

	@Override
	public List<ProcessInformation> getProcessesReport() {
		return reporter.getProcessesReport();
	}

	/**
	 * Handles a new process to schedule from the client. When a client invokes it,
	 * a {@link ProcessInformation} object is created to record the process name,
	 * arrival time, and the length of the cpuBurst to schedule.
	 */
	@Override
	public void newProcess(String processName, double cpuBurstDuration) {
		// TODO You have to write this method.
	}

	@Override
	public void stop() {
		// TODO You have to write this method for a clean stop of your Scheduler
		// For instance, finish all the remaining processes that need CPU, do not accept
		// any other, do the joins for the created threads, etc.
	}


}
