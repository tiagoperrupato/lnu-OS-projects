package se.lnu.os.ht24.a1.provided;

import se.lnu.os.ht24.a1.provided.data.ProcessInformation;

public interface Reporter extends ReportProvider{

	
	void addProcessReport(ProcessInformation v);
	
	
	
}
