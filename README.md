genericcsvparser
================

A wrapper for apache csv parser which uses annotations and object class definitions to make parsing easier

An example for the main structure of the program which will fetch the csv object from csv file  

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

Object to be fetched from the csv file.  
CSV file should have column names which are described below with the annotations.  

    package testcsv;
    
    import genericcsvparser.GenericCsvObject;
    import genericcsvparser.annotations.types.Column;
    import genericcsvparser.annotations.types.RegExControl;
    import genericcsvparser.annotations.types.UTFBomControl;
    import genericcsvparser.annotations.types.UniqueDescriber;
    
    // First three characters of the file should be checked
    // To be sure if the file is an UTF file
    @UTFBomControl
    public class Person extends GenericCsvObject {
        // 0 th column is a string field,
        // Check if NAME is written for that column in the csv file
        @Column(index = 0, name = "NAME")
        String name;
        
        // This field is the key value, you can use it as the index
        // for hashing
        @UniqueDescriber
        @Column(index = 1, name = "NUMBER")
        Integer number;
        
        // Control if this field is appropriate for this regex pattern
        @RegExControl(pattern="[A-Z0-9]{8}")
        @Column(index = 2, name = "SERIAL")
        String serial;
        
        @Override
        public void cascadeOperations() {
            // TODO Auto-generated method stub
            
        }
    
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Person [name=");
            builder.append(name);
            builder.append(", number=");
            builder.append(number);
            builder.append(", serial=");
            builder.append(serial);
            builder.append("]");
            return builder.toString();
        }
    
    }
