package se.lnu.os.ht24.a1.required;

import java.util.ArrayDeque;
import java.util.List;

import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public abstract class AbstractScheduler implements Scheduler {
    protected Reporter reporter;
    protected long startingTime;
    protected ArrayDeque<ProcessInformation> processQueue;
    protected ArrayDeque<ProcessInformation> reporterQueue;
    protected Thread cpuExecutionThread;
    protected Thread reporterThread;
    protected volatile boolean isStopped = false;

    protected AbstractScheduler(Reporter reporter) {
        this.reporter = reporter;
        this.startingTime = System.currentTimeMillis();
    }

    @Override
    public synchronized List<ProcessInformation> getProcessesReport() {
        return reporter.getProcessesReport();
    }

    protected void initialize() {
        // Initialize the queues
        processQueue = new ArrayDeque<>();
        reporterQueue = new ArrayDeque<>();

        // Create and start the CPU thread
        CPUThread cpuThread = new CPUThread(processQueue, reporterQueue, startingTime);
        cpuExecutionThread = new Thread(cpuThread);
        cpuExecutionThread.start();

        // Create and start the ReporterManager thread
        ReporterManager reporterManager = new ReporterManager(reporter, reporterQueue);
        reporterThread = new Thread(reporterManager);
        reporterThread.start();
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
            if (reporterThread != null) {
                reporterThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}