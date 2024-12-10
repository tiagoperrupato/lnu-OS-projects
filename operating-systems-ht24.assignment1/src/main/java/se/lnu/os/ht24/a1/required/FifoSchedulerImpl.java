package se.lnu.os.ht24.a1.required;

import java.util.ArrayDeque;
import java.util.List;

import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public class FifoSchedulerImpl extends AbstractScheduler {

	private final Reporter reporter;
	private long startingTime;

	private ArrayDeque<ProcessInformation> processQueue;
	private ArrayDeque<ProcessInformation> reporterQueue;
	private Thread cpuExecutionThread;
	private Thread reporterThread;
	private volatile boolean isStopped = false;
	

	private FifoSchedulerImpl(Reporter r) {
		this.reporter = r;
		this.startingTime= System.currentTimeMillis();
	}

	public static Scheduler createInstance(Reporter reporter) {
		Scheduler s = (new FifoSchedulerImpl(reporter)).initialize();
		return s;
	}

	@Override
	public synchronized List<ProcessInformation> getProcessesReport() {
		return reporter.getProcessesReport();
	}

	private Scheduler initialize() {
		// TODO You have to write this method to initialize your Scheduler:
		// For instance, create the CPUthread, the ReporterManager thread, the necessary
		// queues lists/sets, etc.
		
		// Create queues for processes  and reporter
		processQueue = new ArrayDeque<ProcessInformation>();
		reporterQueue = new ArrayDeque<ProcessInformation>();

		// Create and start the CPU thread
		CPUThread cpuThread = new CPUThread(processQueue, reporterQueue);
		Thread cpuExecutionThread = new Thread(cpuThread);
		cpuExecutionThread.start();

		// Create and start the ReporterManager thread
		ReporterManager reporterManager = new ReporterManager(reporter, reporterQueue);
		Thread reporterThread = new Thread(reporterManager);
		reporterThread.start();

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
		
		ProcessInformation process = ProcessInformation.createProcessInformation();
		process.setProcessName(processName);
		process.setCpuBurstDuration(cpuBurstDuration);
		process.setArrivalTime(System.currentTimeMillis());
		addProcessToQueue(process);
	}

	private void addProcessToQueue(ProcessInformation process) {
		synchronized(processQueue) {
			processQueue.add(process);
        	processQueue.notify(); // Notify the CPU thread that a new process is available
		}
	}

	@Override
	public void stop() {
		// TODO You have to write this method for a clean stop of your Scheduler
		// For instance, finish all the remaining processes that need CPU, do not accept
		// any other, do the joins for the created threads, etc.

		synchronized (processQueue) {
			isStopped = true; // Flag to indicate stopping
			processQueue.notifyAll(); // Wake up any waiting threads
		}

		// Wait for threads to complete
		try {
			if (cpuExecutionThread != null) {
				cpuExecutionThread.join();
			}
			if (reporterThread != null) {
				reporterThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
