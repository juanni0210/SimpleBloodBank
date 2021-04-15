package logic;

import common.ValidationException;
import dal.BloodBankDAL;
import entity.BloodBank;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author shado
 */
public class BloodBankLogic extends GenericLogic<BloodBank, BloodBankDAL>{
    
    public static final String OWNER_ID = "owner_id";
    public static final String PRIVATELY_OWNED = "privately_owned";
    public static final String ESTABLISHED = "established";
    public static final String NAME = "name";
    public static final String EMPLOYEE_COUNT = "employee_count";
    public static final String ID = "id";

    BloodBankLogic() {
        super(new BloodBankDAL());
    }
    
    @Override
    public List<BloodBank> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public BloodBank getWithId(int id) {
        return get( () -> dal().findById( id ) );
    }
    
    public BloodBank getBloodBankWithName(String name) {
        return get( () -> dal().findByName(name) );
    }
    
    public List<BloodBank> getBloodBankWithPrivatelyOwned(boolean privatelyOwned) {
        return get( () -> dal().findByPrivatelyOwned(privatelyOwned) );
    }
    
    public List<BloodBank> getBloodBankWithEstablished(Date established) {
        return get( () -> dal().findByEstablished(established) );
    }
    
    public BloodBank getBloodBanksWithOwner(int ownerId) {
        return get( () -> dal().findByOwner(ownerId) );
    }
    
    public List<BloodBank> getBloodBanksWithEmplyeeCount(int count) {
        return get( () -> dal().findByEmplyeeCount(count) );
    }
    
    @Override
    public BloodBank createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        BloodBank entity = new BloodBank();
        
        if( parameterMap.containsKey( ID ) ){
            try {
                entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }
        
        ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty.";
                }
                if( value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };
        
        String name = parameterMap.get(NAME)[0];
        String privatelyOwned = parameterMap.get(PRIVATELY_OWNED)[0];
        String established = parameterMap.get(ESTABLISHED)[0];
        String employeeCount = parameterMap.get(EMPLOYEE_COUNT)[0];
        established = established.replaceAll("T", " ");
        
        validator.accept(name, 100);
        
        entity.setName(name);
        entity.setPrivatelyOwned(Boolean.parseBoolean(privatelyOwned));
        entity.setEstablished(convertStringToDate(established));
        entity.setEmplyeeCount(Integer.parseInt(employeeCount));
        
        return entity;
    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "Bank ID", "Owner ID", "Name", "Privately Owned", "Established Date", "Employee Count" );
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, OWNER_ID, NAME, PRIVATELY_OWNED, ESTABLISHED, EMPLOYEE_COUNT );
    }

    @Override
    public List<?> extractDataAsList(BloodBank e) {
        return Arrays.asList( e.getId(), e.getOwner() == null ? "null" : e.getOwner().getId(), e.getName(), e.getPrivatelyOwned(), convertDateToString(e.getEstablished()), e.getEmplyeeCount() );
    }
    
    @Override
    public List<BloodBank> search( String search ) {
        return get( () -> dal().findContaining( search ) );
    }
    
}
