package se.lnu.os.ht24.a1.required;

import java.util.*;

import se.lnu.os.ht24.a1.provided.*;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;
import se.lnu.os.ht24.a1.provided.impl.ReporterIOImpl;

public class ReporterManager extends Thread {
    private final ReporterIOImpl reporter = new ReporterIOImpl();
    private final Queue<ProcessInformation> reporterQueue = new ArrayDeque<>();
    private final Object reporterLock = new Object();

    public ReporterManager(Reporter reporter2, Deque<ProcessInformation> processQueue) {
        //TODO Auto-generated constructor stub
    }

    public void addProcessToReport(ProcessInformation process) {
        synchronized (reporterLock) {
            reporterQueue.add(process);
            reporterLock.notify(); // Notify the reporter thread
        }
    }

    @Override
    public void run() {
        while (true) {
            ProcessInformation process;
            synchronized (reporterLock) {
                while (reporterQueue.isEmpty()) {
                    try {
                        reporterLock.wait(); // Wait for new reports
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                process = reporterQueue.poll();
            }

            if (process != null) {
                reporter.addProcessReport(process);
            }
        }
    }
}
