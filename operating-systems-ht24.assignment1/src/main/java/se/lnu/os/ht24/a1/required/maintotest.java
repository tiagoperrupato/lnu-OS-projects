package se.lnu.os.ht24.a1.required;

import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;
import se.lnu.os.ht24.a1.provided.data.SchedulerType;
import se.lnu.os.ht24.a1.provided.impl.ReporterIOImpl;
import se.lnu.os.ht24.a1.provided.impl.SchedulerFactoryImpl;

public class maintotest {

    public static void main(String[] args) {
        System.out.println("===== Testing FIFO Scheduler =====");
        testScheduler(SchedulerType.FIFO);

        System.out.println("\n===== Testing SJF Scheduler =====");
        testScheduler(SchedulerType.SJF);
    }

    private static void testScheduler(SchedulerType schedulerType) {
        Reporter reporter = ReporterIOImpl.create(3000); 
        Scheduler scheduler = SchedulerFactoryImpl.createScheduler(schedulerType, reporter);

        scheduler.newProcess("P1", 8.0); 
        scheduler.newProcess("P2", 4.0); 
        scheduler.newProcess("P3", 9.0); 
        scheduler.newProcess("P4", 5.0); 

        try {
            Thread.sleep(30000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduler.stop();

        System.out.println("===== Completed Processes =====");
        System.out.println(scheduler.getProcessesReport().size());
        for (ProcessInformation process : scheduler.getProcessesReport()) {
            System.out.println(process);
        }
    }
}
