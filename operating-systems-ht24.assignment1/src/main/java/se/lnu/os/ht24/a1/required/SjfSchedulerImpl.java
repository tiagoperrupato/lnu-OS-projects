package se.lnu.os.ht24.a1.required;

import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

import java.util.ArrayList;
import java.util.Collections;

public class SjfSchedulerImpl extends AbstractScheduler {

    private SjfSchedulerImpl(Reporter reporter) {
        super(reporter); // Pass the reporter to the AbstractScheduler constructor
        initialize();    // Call the shared initialization logic
    }

    public static Scheduler createInstance(Reporter reporter) {
        return new SjfSchedulerImpl(reporter);
    }

    @Override
    protected void addProcessToQueue(ProcessInformation process) {
        synchronized (processQueue) {
            processQueue.add(process); // Add the new process
            // Sort the queue by CPU burst duration (SJF logic)
            ArrayList<ProcessInformation> tempList = new ArrayList<>(processQueue);
            tempList.sort((p1, p2) -> Double.compare(p1.getCpuBurstDuration(), p2.getCpuBurstDuration()));
            processQueue.clear();
            processQueue.addAll(tempList);
            processQueue.notify(); // Notify the CPU thread
        }
    }
}