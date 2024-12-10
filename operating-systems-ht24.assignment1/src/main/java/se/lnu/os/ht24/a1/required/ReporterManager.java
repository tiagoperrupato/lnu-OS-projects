package se.lnu.os.ht24.a1.required;

import java.util.*;

import se.lnu.os.ht24.a1.provided.*;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public class ReporterManager extends Thread {
    private final Reporter reporter;
    private final ArrayDeque<ProcessInformation> reporterQueue;

    public ReporterManager(Reporter reporter, ArrayDeque<ProcessInformation> reporterQueue) {
        //TODO Auto-generated constructor stub
        this.reporter = reporter;
        this.reporterQueue = reporterQueue;
    }

    @Override
    public void run() {
        while (true) {
            ProcessInformation process;
            synchronized (reporterQueue) {
                while (reporterQueue.isEmpty()) {
                    try {
                        reporterQueue.wait(); // Wait for new reports
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                process = reporterQueue.poll();
            }

            if (process != null) {
                synchronized (reporter) {
                    reporter.addProcessReport(process);
                    reporter.notify();
                }
            }
        }
    }
}
