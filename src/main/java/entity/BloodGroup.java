package entity;

import java.util.Objects;

/**
 *
 * @author Shariar (Shawn) Emami
 */
public enum BloodGroup {
    A("A"), B("B"), AB("AB"), O("O");
    
    private final String symbol;
    
    private BloodGroup(String value) {
        this.symbol = value;
    }
    
      public String getSymbol() {
        return symbol;
    }

    public static BloodGroup getBloodGroup(String symbol) {
        Objects.requireNonNull( symbol, "BloodGroup cannot have null symbol" );
        BloodGroup[] values = values();
        for( BloodGroup value: values ) {
            if(value.getSymbol().equalsIgnoreCase(symbol)){
                return value;
            }
        }
        BloodGroup bloodGroup = BloodGroup.valueOf(symbol);
        if(bloodGroup != null) {
            return bloodGroup;
        }
        throw new IllegalArgumentException( "Symbol=\"" + symbol + "\" does not exists in BloodGroup" );
    }

}
