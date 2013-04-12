package genericcsvparser;

import java.lang.reflect.Constructor;
import java.util.HashSet;

import genericcsvparser.annotations.types.Column;

/**
 * 
 *
 * @param <T>
 * Creates new GenericCsvObjects
 */
public class GenericCsvObjectFactory<T extends GenericCsvObject> {

    Class<T> type;

    public GenericCsvObjectFactory(Class<T> type) {
        this.type = type;
    }

    public T createCsvObject() {
        T csvobject = null;
        try {
            Class<?>[] param = {};
            Object[] initargs = {};
            Constructor<T> cons = type.getConstructor(param);
            csvobject = cons.newInstance(initargs);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create Csv object; constructor defined private?", e);
        }

        return csvobject;
    }

    public HashSet<Column> getColNamePlacePairs() {
        try {
            return (HashSet<Column>) type.newInstance().getColPairs();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
