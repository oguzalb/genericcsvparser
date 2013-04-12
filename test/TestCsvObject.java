import genericcsvparser.GenericCsvFile;
import genericcsvparser.GenericCsvParser;

import org.apache.commons.csv.CSVStrategy;
import org.junit.Test;

import testcsv.Person;



public class TestCsvObject {

    @Test
    public void testCsv() {
        CSVStrategy strategy = new CSVStrategy(';', '|', '|');
        GenericCsvParser csvParser = new GenericCsvParser(strategy);
        GenericCsvFile<Person> list = csvParser.extractFromFile("test.csv", Person.class);
        if (list != null)
            for (Person person : list.getLinesLinked()) {
                System.out.println(person);
            };
    }

}
