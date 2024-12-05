package se.lnu.os.ht24.a1.required.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.lnu.os.ht24.a1.TestUtils;
import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.data.SchedulerType;
import se.lnu.os.ht24.a1.provided.impl.ReporterIOImpl;
import se.lnu.os.ht24.a1.provided.impl.SchedulerFactoryImpl;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

class FifoSchedulerTestDefault {

	Scheduler scheduler;
	Reporter reporter;
	ArrayList<ProcessInformation> toCheck;
	long startingTime;

	@BeforeEach
	public void createNewFifoScheduler() {
		reporter = ReporterIOImpl.create(3000);
		scheduler = SchedulerFactoryImpl.createScheduler(SchedulerType.FIFO,reporter);
		toCheck = new ArrayList<ProcessInformation>();
		startingTime= System.currentTimeMillis();
	}

	@Test
	void oneProcess() {

		System.out.println("======Starting a scheduler of one process=====");
		
		createRequestAndEntry("Process1", 1.0);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(1, reporter.getProcessesReport().size(),
				"scheduler report did not contain the expected number of elements.");
		assertEquals(1, toCheck.size(), "list of elements to check did not contain the expected number of elements.");
		assertTrue(TestUtils.checkEqual(reporter.getProcessesReport(), toCheck));

	}

	private ProcessInformation createRequestAndEntry(String processName, double cpuBurstDuration) {
		ProcessInformation v = ProcessInformation.createProcessInformation()
				.setArrivalTime((System.currentTimeMillis() - startingTime) / 1000.0).setProcessName(processName)
				.setEndTime((System.currentTimeMillis() - startingTime) / 1000.0 + cpuBurstDuration)
				.setCpuBurstDuration(cpuBurstDuration)
				.setCpuScheduledTime((System.currentTimeMillis() - startingTime) / 1000.0);

		scheduler.newProcess(processName, cpuBurstDuration);

		toCheck.add(v);
		return v;
	}

}
