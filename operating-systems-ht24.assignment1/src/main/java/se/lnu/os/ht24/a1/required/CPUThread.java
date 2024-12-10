package se.lnu.os.ht24.a1.required;

import java.util.ArrayDeque;

import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public class CPUThread extends Thread {
    private final ArrayDeque<ProcessInformation> processQueue;
    private final ArrayDeque<ProcessInformation> reporterQueue;

    public CPUThread(ArrayDeque<ProcessInformation> processQueue, ArrayDeque<ProcessInformation> reporterQueue) {
        this.processQueue = processQueue;
        this.reporterQueue = reporterQueue;
    }

    @Override
    public void run() {
        while (true) {
            ProcessInformation process;
            synchronized (processQueue) {
                while (processQueue.isEmpty()) {
                    try {
                        wait(); // Wait for new processes
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                process = processQueue.poll();
            }

            if (process != null) {
                double startTime = System.currentTimeMillis();
                process.setCpuScheduledTime(startTime);

                try {
                    Thread.sleep((long) (process.getCpuBurstDuration() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                double endTime = System.currentTimeMillis();
                process.setEndTime(endTime);
                
                synchronized(reporterQueue) {
                    reporterQueue.add(process);
                    notify(); // Notify the CPU thread that a new process is available
                }
            }
        }
    }
}