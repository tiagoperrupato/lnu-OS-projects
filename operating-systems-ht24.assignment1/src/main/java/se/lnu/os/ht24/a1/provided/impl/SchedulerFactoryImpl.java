package se.lnu.os.ht24.a1.provided.impl;

import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.Scheduler;
import se.lnu.os.ht24.a1.provided.data.SchedulerType;
import se.lnu.os.ht24.a1.required.FifoSchedulerImpl;
import se.lnu.os.ht24.a1.required.SjfSchedulerImpl;

public class SchedulerFactoryImpl {

	
	public static Scheduler createScheduler(SchedulerType type, Reporter reporter) {

		Scheduler r = null;
		
		switch (type) {

		case FIFO:
			r = FifoSchedulerImpl.createInstance(reporter);
			
			break;
		case SJF:
			r = SjfSchedulerImpl.createInstance(reporter);
			break;
		default:
			r = FifoSchedulerImpl.createInstance(reporter);
		}
		return r;

	}

}
