package testcsv;

import genericcsvparser.GenericCsvObject;
import genericcsvparser.annotations.types.Column;
import genericcsvparser.annotations.types.RegExControl;
import genericcsvparser.annotations.types.UTFBomControl;
import genericcsvparser.annotations.types.UniqueDescriber;

@UTFBomControl
public class Person extends GenericCsvObject {

    @Column(index = 0, name = "NAME")
    String name;
    
    @UniqueDescriber
    @Column(index = 1, name = "NUMBER")
    Integer number;
    
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
