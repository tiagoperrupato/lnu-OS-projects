package se.lnu.os.ht24.a1.required;

import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

import java.util.ArrayDeque;

public class FifoSchedulerImpl extends AbstractScheduler {

    private FifoSchedulerImpl(Reporter reporter) {
        super(reporter); // Pass the reporter to the AbstractScheduler constructor
		processQueue = new ArrayDeque<ProcessInformation>(); // create FIFO queue for process
        initialize();    // Call the shared initialization logic
    }

    public static Scheduler createInstance(Reporter reporter) {
        return new FifoSchedulerImpl(reporter);
    }

    @Override
    protected void addProcessToQueue(ProcessInformation process) {
        synchronized (processQueue) {
            processQueue.add(process); // Add process to the end of the queue (FIFO)
            processQueue.notify();         // Notify the CPU thread
        }
    }
}