package se.lnu.os.ht24.a1.required;

import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

import java.util.PriorityQueue;

public class SjfSchedulerImpl extends AbstractScheduler {

    private SjfSchedulerImpl(Reporter reporter) {
        super(reporter); // Pass the reporter to the AbstractScheduler constructor
		processQueue = new PriorityQueue<ProcessInformation>(
            (p1, p2) -> Double.compare(p1.getCpuBurstDuration(), p2.getCpuBurstDuration())
        ); // PriorityQueue for SJF logic
        initialize();    // Call the initialization logic
    }

    public static Scheduler createInstance(Reporter reporter) {
        return new SjfSchedulerImpl(reporter);
    }

    @Override
    protected void addProcessToQueue(ProcessInformation process) {
        synchronized (processQueue) {
            processQueue.add(process); // Insert into priority queue
            processQueue.notify();     // Notify the CPU thread
        }
    }
}