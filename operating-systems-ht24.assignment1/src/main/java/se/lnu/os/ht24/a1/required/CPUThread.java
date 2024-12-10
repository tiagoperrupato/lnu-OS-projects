package se.lnu.os.ht24.a1.required;

import java.util.ArrayDeque;

import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public class CPUThread extends Thread {
    private final ArrayDeque<ProcessInformation> processQueue;
    private final ArrayDeque<ProcessInformation> reporterQueue;
    private long startingTime;

    public CPUThread(ArrayDeque<ProcessInformation> processQueue, ArrayDeque<ProcessInformation> reporterQueue, long startingTime) {
        this.processQueue = processQueue;
        this.reporterQueue = reporterQueue;
        this.startingTime = startingTime;
    }

    @Override
    public void run() {
        while (true) {
            ProcessInformation process;
            synchronized (processQueue) {
                while (processQueue.isEmpty()) {
                    try {
                        processQueue.wait(); // Wait for new processes
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
    }
}