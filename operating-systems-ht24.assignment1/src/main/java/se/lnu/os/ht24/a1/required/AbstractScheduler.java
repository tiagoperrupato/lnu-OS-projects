package se.lnu.os.ht24.a1.required;

import java.util.ArrayDeque;
import java.util.List;

import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public abstract class AbstractScheduler implements Scheduler {

    protected Reporter reporter;
    protected long startingTime;
    protected final ArrayDeque<ProcessInformation> processQueue;
    protected final ArrayDeque<ProcessInformation> reporterQueue;
    protected Thread cpuExecutionThread;
    protected Thread reporterManager;
    protected volatile boolean isStopped = false;

    protected AbstractScheduler(Reporter reporter) {
        this.reporter = reporter;
        this.startingTime = System.currentTimeMillis(); // Initialize the queues
        this.processQueue = new ArrayDeque<>();
        this.reporterQueue = new ArrayDeque<>();
    }

    @Override
    public synchronized List<ProcessInformation> getProcessesReport() {
        return reporter.getProcessesReport();
    }

    protected void initialize() {
       

        // Create and start the CPU thread
		cpuExecutionThread = new Thread(() -> {
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
                    reporterQueue.notifyAll(); // Notify the CPU thread that a new process is available
                }
                }
            }
        });

		reporterManager = new Thread(() -> {
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
					}
                }
            }
        });

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

    protected abstract void addProcessToQueue(ProcessInformation process);

    @Override
    public void stop() {
        isStopped = true; // Flag to indicate stopping

        synchronized (processQueue) {
            processQueue.notifyAll(); // Wake up waiting threads
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