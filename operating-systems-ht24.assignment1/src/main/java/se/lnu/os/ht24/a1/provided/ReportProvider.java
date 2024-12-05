package se.lnu.os.ht24.a1.provided;

import java.util.List;

import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public interface ReportProvider {

	List<ProcessInformation> getProcessesReport();
	
}
