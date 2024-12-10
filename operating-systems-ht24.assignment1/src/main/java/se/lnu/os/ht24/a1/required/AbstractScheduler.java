package se.lnu.os.ht24.a1.required;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public abstract class AbstractScheduler implements Scheduler {

    protected Reporter reporter;
    protected long startingTime;
    protected Queue<ProcessInformation> processQueue;
    protected ArrayDeque<ProcessInformation> reporterQueue;
    protected Thread cpuExecutionThread;
    protected Thread reporterManager;
    protected volatile boolean isStopped = false;

    protected AbstractScheduler(Reporter reporter) {
        this.reporter = reporter;
        this.startingTime = System.currentTimeMillis(); 
		// Initialize the queues
		this.processQueue = null; // Each approach for scheduler implements different Queue
        this.reporterQueue = new ArrayDeque<ProcessInformation>();
    }

    @Override
    public synchronized List<ProcessInformation> getProcessesReport() {
        return reporter.getProcessesReport();
    }

    protected void initialize() {
       

        // Create the CPU thread
		cpuExecutionThread = new Thread(() -> {
			// execute code if the thread is running or if there is process in the Queue to be released
            while (!isStopped || !processQueue.isEmpty()) {
                ProcessInformation process;
                synchronized (processQueue) {
                    while (processQueue.isEmpty() && !isStopped) {
                        try {
                            processQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    process = processQueue.poll();
                }
                if (process != null) {
                    process.setCpuScheduledTime((System.currentTimeMillis() - startingTime) / 1000.0);

                try {
                    Thread.sleep((long) (process.getCpuBurstDuration() * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                process.setEndTime((System.currentTimeMillis() - startingTime) / 1000.0);
                
                synchronized(reporterQueue) {
                    reporterQueue.add(process);
                    reporterQueue.notify(); // Notify the CPU thread that a new process is available
                }
                }
            }
        });

		// Create Thread for the Reporter Manager
		reporterManager = new Thread(() -> {
			// execute code if the thread is running or if there is process in the Queue to be released
            while (!isStopped || !reporterQueue.isEmpty()) {
                ProcessInformation process;
                synchronized (reporterQueue) {
                    while (reporterQueue.isEmpty() && !isStopped) {
                        try {
                            reporterQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    process = reporterQueue.poll();
                }
                if (process != null) {
					synchronized(reporter) {
						reporter.addProcessReport(process);
						reporter.notify();
					}
                }
            }
        });

		// start Threads
		cpuExecutionThread.start();
        reporterManager.start();
    }

    @Override
    public void newProcess(String processName, double cpuBurstDuration) {
        // Create the ProcessInformation object
        ProcessInformation process = ProcessInformation.createProcessInformation();
        process.setProcessName(processName);
        process.setCpuBurstDuration(cpuBurstDuration);
        process.setArrivalTime((System.currentTimeMillis() - startingTime) / 1000.0);

        // Delegate to the specific scheduler's queue handling
        addProcessToQueue(process);
    }

    protected abstract void addProcessToQueue(ProcessInformation process); // function to be implemented by each type of scheaduler

    @Override
    public void stop() {
        isStopped = true; // Flag to indicate stopping

		// Wake up waiting threads
        synchronized (processQueue) {
            processQueue.notifyAll(); 
        }

        synchronized (reporterQueue) {
            reporterQueue.notifyAll();
        }

        // Wait for threads to complete
        try {
            if (cpuExecutionThread != null) {
                cpuExecutionThread.join();
            }
            if (reporterManager != null) {
                reporterManager.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}