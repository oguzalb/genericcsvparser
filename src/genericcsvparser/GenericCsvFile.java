package genericcsvparser;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import genericcsvparser.annotations.types.Column;
import genericcsvparser.exceptions.ColumnDoesNotMatchPatternException;
import genericcsvparser.exceptions.CsvLineNotValidException;
import genericcsvparser.exceptions.RegexControlNotSuccessfulException;

/**
 * GenericCsvFile class, maps to a csv file which
 * has column names, starts with a BOM and has the UTF-8 format and is
 * seperated with comma
 *
 * @param <GenericCsvObject>
 */
public class GenericCsvFile<T extends GenericCsvObject> {
    Hashtable<String,T> linesTable = new Hashtable<String,T>();
    LinkedList<T> linesLinked = new LinkedList<T>();
    Hashtable<String,String[]> csvDataHashtable = new Hashtable<String,String[]>();
    GenericCsvReporter reporter = new GenericCsvReporter();
    
    public GenericCsvReporter getReporter() {
        return reporter;
    }

    public void refreshLinesTable () {
        if (linesLinked.size() > 0 && linesLinked.get(0).getUniqueDescriber() != null) {
            linesTable = new Hashtable<String,T>();
            for (T record: linesLinked)
                linesTable.put(record.getUniqueDescriber(),record);
        }
    }

    public Hashtable<String, String[]> getCsvDataHashtable() {
        return csvDataHashtable;
    }
    String filename;
    int inputLineCount;
    private Logger logger = Logger.getLogger(this.getClass());
    private Class<T> type;
    public int getParsedCount()
    {
        return linesLinked.size();
    }
    public T getCsvObject(String key) {
        if (linesTable.containsKey(key)) {
            return linesTable.get(key);
        }
        return null;
    }
    
    public GenericCsvFile(String filename, String[][] csvdata, Class<T> type) {
        this.inputLineCount = csvdata.length;
        this.type = type;
    }
    
    public boolean parseFile(String[][] csvdata) throws ColumnDoesNotMatchPatternException, CsvLineNotValidException {
        T csvobject;
        if (!T.checkCols(csvdata))
            return false;
        try {
            T csvobjectPrototype = type.newInstance();
            csvobjectPrototype.prepareAnnotations();
        } catch (InstantiationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for (int i= 1; i<csvdata.length; i++) {
                try {
                    csvobject = type.newInstance();
                    csvdata[i][csvdata[i].length-1] = csvdata[i][csvdata[i].length-1].replace("\r", "");
                    csvdata[i][csvdata[i].length-1] = csvdata[i][csvdata[i].length-1].replace("\n", "");
                    csvobject.parseLine(csvdata[i]);
                    logger.debug(csvobject.toString());
                    if (csvobject.isValid()) {
                        if (csvobject.getUniqueDescriber() != null) {
                            linesTable.put(csvobject.getUniqueDescriber(), csvobject);
                            csvDataHashtable.put(csvobject.getUniqueDescriber(), csvdata[i]);
                        }
                        linesLinked.add(csvobject);
                    } else {
                        if (csvobject.getUniqueDescriber() != null)
                            throw new CsvLineNotValidException(csvobject.getUniqueDescriber());
                        else
                            throw new CsvLineNotValidException("");
                    }
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        }
        logger.debug("Parsed "+linesLinked.size());
        return true;
    }
    
    public Hashtable<String, T> getLinesTable() {
        return linesTable;
    }
    public LinkedList<T> getLinesLinked() {
        return linesLinked;
    }

}
