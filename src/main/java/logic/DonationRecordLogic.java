package logic;

import common.ValidationException;
import dal.DonationRecordDAL;
import entity.DonationRecord;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;


/**
 *
 * @author Feiqiong Deng
 */
public class DonationRecordLogic extends GenericLogic<DonationRecord, DonationRecordDAL>{
    
    public static final String PERSON_ID = "person_id";
    public static final String DONATION_ID = "donation_id";
    public static final String TESTED = "tested";
    public static final String ADMINISTRATOR = "administrator";
    public static final String HOSPITAL = "hospital";
    public static final String CREATED = "created";
    public static final String ID = "id";
    
    DonationRecordLogic(){
        super( new DonationRecordDAL() );
    }

      @Override
    public List<DonationRecord> getAll() {
        return get(() -> dal().findAll());
    }
    
     @Override
    public DonationRecord getWithId(int id) {
        return get(() -> dal().findById( id ));
    }
    
    public List<DonationRecord> getDonationRecordWithTested(boolean tested){
        return get(() -> dal().findByTested( tested ));
    }

    public List<DonationRecord> getDonationRecordWithAdministrator(String administrator){
        return get(() -> dal().findByAdministrator( administrator ));
    }
    
    public List<DonationRecord> getDonationRecordWithHospital(String username){
        return get(() -> dal().findByHospital( username ));
    }
    
    public List<DonationRecord> getDonationRecordsWithCreated(Date created){
        return get(() -> dal().findByCreated( created ));    
    }
    
    public List<DonationRecord> getDonationRecordsWithPerson(int personId){
        return get(() -> dal().findByPerson( personId )); 
    }
    
    public List<DonationRecord> getDonationRecordsWithDonation(int donationId){
         return get(() -> dal().findByDonation( donationId ));
    }
    
     @Override
    public DonationRecord createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        DonationRecord entity = new DonationRecord();
        
          //ID is generated, so if it exists add it to the entity object
          //otherwise it does not matter as mysql will create an if for it.
          //the only time that we will have id is for update behaviour.
        if(parameterMap.containsKey(ID)) {
          try{
              entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
          } catch(java.lang.NumberFormatException ex) {
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
          
        String tested = parameterMap.get(TESTED)[0];
        String administrator = parameterMap.get(ADMINISTRATOR)[0];
        String hospital = parameterMap.get(HOSPITAL)[0];
        String created = parameterMap.get(CREATED)[0];
        created = created.replaceAll("T", " ");
        
        //validate the data
        validator.accept(administrator, 100);
        validator.accept(hospital, 100);

        //set values on the entity
        entity.setTested(Boolean.parseBoolean(tested));
        entity.setAdministrator(administrator);
        entity.setHospital(hospital);
        entity.setCreated(convertStringToDate(created));
        
        return entity;
    }
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("Record ID", "Tested", "Administrator", "Hospital", "Created", "Donation ID", "Person ID");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, TESTED, ADMINISTRATOR, HOSPITAL, CREATED, DONATION_ID, PERSON_ID);
    }

    @Override
    public List<?> extractDataAsList(DonationRecord e) {
        return Arrays.asList( e.getId(), e.getTested(), e.getAdministrator(), e.getHospital(), convertDateToString(e.getCreated()),
                e.getBloodDonation()== null ? "null" : e.getBloodDonation().getId(), e.getPerson()== null ? "null" :e.getPerson().getId());
    }
    
    @Override
    public List<DonationRecord> search( String search ) {
        return get( () -> dal().findContaining( search ) );
    }

}
