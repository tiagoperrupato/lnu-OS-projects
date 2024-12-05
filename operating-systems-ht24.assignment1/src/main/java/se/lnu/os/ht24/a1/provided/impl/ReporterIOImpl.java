package se.lnu.os.ht24.a1.provided.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.lnu.os.ht24.a1.provided.Reporter;
import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public class ReporterIOImpl implements Reporter {

	private List<ProcessInformation> recordBook;
	private int sleepTime;

	private ReporterIOImpl(int d) {

		recordBook = new ArrayList<ProcessInformation>();
		sleepTime = d;
	}

	public synchronized List<ProcessInformation> getProcessesReport() {
		List<ProcessInformation> copy = new ArrayList<ProcessInformation>(recordBook);
		Collections.copy(copy, recordBook);
		return copy;
	}

	public void addProcessReport(ProcessInformation v) {
		int position = recordBook.size();
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		recordBook.add(position, v);
	}

	public static Reporter create(int d) {
		return new ReporterIOImpl(d);
	}

}
