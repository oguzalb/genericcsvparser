package genericcsvparser;

import genericcsvparser.annotations.types.Column;
import genericcsvparser.annotations.types.RegExControl;
import genericcsvparser.annotations.types.UTFBomControl;
import genericcsvparser.annotations.types.UniqueDescriber;
import genericcsvparser.exceptions.ColumnDoesNotMatchPatternException;
import genericcsvparser.exceptions.RegexControlNotSuccessfulException;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.type.UnknownTypeException;

import org.apache.log4j.Logger;

/**
 * 
 * GenericCsvObject which should be extended to use the GenericCsvParser
 * utility
 * An example can be reached at test codes
 */
public abstract class GenericCsvObject {
    private static Logger logger = Logger.getLogger(GenericCsvParser.class);
    private static Field uniqueDescriberField;
    private boolean      valid;
    private static HashSet<String> csvColumnNames = new HashSet<String>();
    private static Hashtable<String, Integer> csvColumnIndexes = new Hashtable<String, Integer>();
    private static Hashtable<String, Pattern> regexControls = new Hashtable<String, Pattern>();
    private static boolean utfBomControl;
    
    public static boolean isUtfBomControl() {
        return utfBomControl;
    }

    public boolean isValid() {
        return valid;
    }

    public static boolean checkCols(String[][] csvdata) {
        Hashtable<String,Integer> colsTable = new Hashtable<String,Integer>();

        for (int i=0;i<csvdata[0].length;i++) {
            logger.debug(csvdata[0][i]);
            colsTable.put(csvdata[0][i],i);
        }
        for (String columnName : csvColumnNames) {
            // TODO Exception
            if (logger.isDebugEnabled())
                logger.debug("Comparing column " + columnName);
            if (columnName != null && colsTable.containsKey(columnName)) {
                Integer columnIndex = csvColumnIndexes.get(columnName);
                if (!colsTable.get(columnName).equals(columnIndex)) {
                    logger.error("Index of column ("+colsTable.get(columnName)+") dont match with "+columnIndex);
                    return false;
                }
            } else {
                logger.error("column is not in the csv file " + columnName);
                return false;
            }
        }
        return true;
    }
    
    public void prepareAnnotations() {
        Field[] fieldArray = this.getClass().getDeclaredFields();
        for (Field field : fieldArray) {

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                csvColumnNames.add(column.name());
                csvColumnIndexes.put(column.name(), new Integer(column.index()));
                String columnName = field.getName();
                RegExControl regExControlAnn = field.getAnnotation(RegExControl.class);
                if (regExControlAnn != null) {
                    Pattern pattern = Pattern.compile(regExControlAnn.pattern());
                    regexControls.put(columnName, pattern);
                    System.out.println(columnName);
                }
                UTFBomControl utfBomControlAnn = field.getAnnotation(UTFBomControl.class);
                if (utfBomControlAnn != null) {
                    utfBomControl = true;
                }
            }
        }
    }
    /**
     * Parses the line, extracts the columns and maps to the GenericCsvObject fields
     * If RegEx found for a field, the column is checked for a match
     * @param line
     * @throws ColumnDoesNotMatchPatternException
     */
    public void parseLine(String[] line) {
        Field[] fieldArray = this.getClass().getDeclaredFields();
        for (Field field : fieldArray) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String columnString = line[column.index()];
                String columnName = field.getName();
                try {
                    if (regexControls.containsKey(columnName)) {
                        Pattern pattern = regexControls.get(columnName);
                        System.out.println(columnName);
                        Matcher matcher = pattern.matcher(columnString);
                        if (!matcher.matches())
                            throw new RegexControlNotSuccessfulException();
                    }
                    field.setAccessible(true);
                    if (field.getType() == Integer.class) {
                        field.set(this, Integer.parseInt(columnString));
                    } else if (field.getType() == String.class) {
                        field.set(this, columnString);
                    } else if (field.getType() == Double.class) {
                        field.set(this, Double.parseDouble(columnString));
                    } else if (field.getType() == Float.class) {
                        field.set(this, Float.parseFloat(columnString));
                    } else if (field.getType() == Long.class) {
                        field.set(this, Long.parseLong(columnString));
                    } else
                        throw new UnknownTypeException(null, "Type unknown");
                } catch (NumberFormatException e) {
                    throw e;
                } catch (IllegalArgumentException e) {
                    throw e;
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (RegexControlNotSuccessfulException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        this.cascadeOperations();
        this.valid = true;
    }

    /**
     * unique describer which the extractor uses to index the object
     * @return
     */
    public String getUniqueDescriber() {
        if (uniqueDescriberField == null) {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields)
                if (field.isAnnotationPresent(UniqueDescriber.class)) {
                    uniqueDescriberField = field;
                    break;
                }
            // TODO Optimization
            if (uniqueDescriberField == null)
                return null;
        }
        String describer = null;
        try {
            uniqueDescriberField.setAccessible(true);
            describer = uniqueDescriberField.get(this).toString();
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass()).error("Exception at genericCsvObject", e);
        } catch (IllegalAccessException e) {
            Logger.getLogger(this.getClass()).error("Exception at genericCsvObject", e);
        }
        return describer;
    }

    public abstract void cascadeOperations();
}
