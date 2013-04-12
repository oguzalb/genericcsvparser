package genericcsvparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.csv.CSVParser;

import genericcsvparser.exceptions.ColumnDoesNotMatchPatternException;
import genericcsvparser.exceptions.CsvFileNotValidException;
import genericcsvparser.exceptions.CsvLineNotValidException;


/**
 * 
 *
 * @param <T>
 * Parses the csv file, extracts lines as objects which are from user
 * defined classes that extend from GenericCsvObject
 * Example can be reached from the test codes
 */
public class GenericCsvParser {

    public String[][] csvdata;
    
    public CSVStrategy csvStrategy;



    private static Logger logger = Logger.getLogger(GenericCsvParser.class);

    public GenericCsvParser(CSVStrategy csvStrategy) {
        this.csvStrategy = csvStrategy;
    }
    
    public <T extends GenericCsvObject> GenericCsvFile<T> extractFromFile(String filename,
                                                                          Class<T> type) {
        GenericCsvFile<T> file = null;
        FileInputStream fin = null;
        try {
            File csvfile = new File(filename);
            fin = new FileInputStream(csvfile);
            byte[] data;
            if (T.isUtfBomControl() == true) {
                byte[] bom = new byte[3];
                data = new byte[(int) csvfile.length() - 3];
                fin.read(bom);
                fin.read(data, 0, (int) csvfile.length() - 3);
                if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
                    System.out.println("UTF without BOM or ascii file, not appropriate: " + filename);
                    System.out.println(" " + Integer.toString(bom[0] & 0xFF, 16) + " "
                            + Integer.toString(bom[1] & 0xFF, 16) + " "
                            + Integer.toString(bom[2] & 0xFF, 16));
                    throw new CsvFileNotValidException();
                }
            } else {
                data = new byte[(int) csvfile.length()];
                fin.read(data);
            }
            String fileContent = new String(data);
            StringReader sreader = new StringReader(fileContent);
            CSVParser csvparser = new CSVParser(sreader, csvStrategy);
            csvdata = csvparser.getAllValues();
            sreader.close();
            file = new GenericCsvFile<T>(filename, csvdata, type);
            if (!file.parseFile(csvdata)) {
                System.out.println("Column names dont match for " + filename);
                throw new CsvFileNotValidException();
            }
        } catch (IOException e) {
            logger.error("No file named " + filename);
            file = null;
        } catch (ColumnDoesNotMatchPatternException e) {
            logger.error("Columns dont conform for file " + filename);
            file = null;
        } catch (CsvLineNotValidException e) {
            logger.error("line " + e.getUniqueDescriber() + " wrong at "
                    + filename);
            file = null;
        } catch (CsvFileNotValidException e) {
            logger.error("");
        } finally {
                try {
                    if (fin != null)
                        fin.close();
                } catch (IOException e) {
                    logger.error("Could not be closed" + filename);
                }
        }
        return file;
    }
}
