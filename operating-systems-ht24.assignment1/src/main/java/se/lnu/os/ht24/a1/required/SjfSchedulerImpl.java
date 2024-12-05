package se.lnu.os.ht24.a1.required;

import java.util.List;

import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public class SjfSchedulerImpl extends AbstractScheduler {

	private final Reporter reporter;
	private long startingTime;

	private SjfSchedulerImpl(Reporter r) {
		this.reporter = r;
		startingTime= System.currentTimeMillis();
	}

	public static Scheduler createInstance(Reporter reporter) {
		Scheduler s = (new SjfSchedulerImpl(reporter)).initialize();
		return s;
	}

	@Override
	public List<ProcessInformation> getProcessesReport() {
		return reporter.getProcessesReport();
	}

	private Scheduler initialize() {
		// TODO You have to write this method to initialize your Scheduler:
		// For instance, create the CPUthread, the ReporterManager thread, the necessary
		// queues lists/sets, etc.

		return this;
	}

	/**
	 * Handles a new process to schedule from the user. When the user invokes it,
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
