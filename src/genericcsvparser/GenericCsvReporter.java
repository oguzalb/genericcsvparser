package genericcsvparser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

public class GenericCsvReporter {

    Hashtable<String, String> reports         = new Hashtable<String, String>();
    LinkedList<String>        reportsSequence = new LinkedList<String>();

    public void setReportLine(String uniqueDescriber, String report) {
        if (!reports.contains(uniqueDescriber)) {
            reports.put(uniqueDescriber, report + "\n");
            reportsSequence.add(uniqueDescriber);
        } else
            reports.put(uniqueDescriber, report + "\n");
    }

    public String extractReport() {
        StringBuilder reportText = new StringBuilder();
        for (String uniqueDescriber : reportsSequence) {
            reportText.append(uniqueDescriber + " " + reports.get(uniqueDescriber));
        }
        return reportText.toString();
    }

    public void extractReportToFile(String fileFullPath) throws IOException {
        File file = new File(fileFullPath);
        FileWriter fWriter = new FileWriter(file);
        fWriter.write(this.extractReport());
        fWriter.close();
    }

    public boolean isReportLineSet(final String uniqueDescriber) {
        return reports.contains(uniqueDescriber);
    }
}
