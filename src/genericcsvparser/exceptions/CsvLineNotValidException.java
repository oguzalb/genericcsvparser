package genericcsvparser.exceptions;


public class CsvLineNotValidException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -6539088726827385482L;
    String uniqueDescriber;
    
    
    public String getUniqueDescriber() {
        return uniqueDescriber;
    }

    
    public void setUniqueDescriber(String uniqueDescriber) {
        this.uniqueDescriber = uniqueDescriber;
    }

    public CsvLineNotValidException(String uniqueDescriber) {
        this.uniqueDescriber = uniqueDescriber;
    }
}
