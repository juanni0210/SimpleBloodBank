package logic;

import common.ValidationException;
import dal.BloodDonationDAL;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;
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
public class BloodDonationLogic extends GenericLogic<BloodDonation, BloodDonationDAL> {
    public static final String BANK_ID = "bank_id";
    public static final String MILLILITERS = "milliliters";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String RHESUS_FACTOR = "rhesus_factor";
    public static final String CREATED = "created";
    public static final String ID = "id";

    public BloodDonationLogic() {
        super(new BloodDonationDAL());
    }
    
    @Override
    public List<BloodDonation> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public BloodDonation getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public List<BloodDonation> getBloodDonationWithMilliliters(int milliliters) {
        return get(() -> dal().findByMilliliters(milliliters));
    }
    
    public List<BloodDonation> getBloodDonationWithBloodGroup(BloodGroup bloodGroup) {
        return get(() -> dal().findByBloodGroup(bloodGroup));
    }
    
    public List<BloodDonation> getBloodDonationWithCreated(Date created) {
        return get(() -> dal().findByCreated(created));
    }
    
    public List<BloodDonation> getBloodDonationsWithRhd(RhesusFactor rhd) {
        return get(() -> dal().findByRhd(rhd));
    }
    
    public List<BloodDonation> getBloodDonationsWithBloodBank(int bankId) {
        return get(() -> dal().findByBloodBank(bankId));
    }
    
    @Override
    public BloodDonation createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        BloodDonation entityBloodDonation = new BloodDonation();
        
        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if(parameterMap.containsKey(ID)) {
            try{
                entityBloodDonation.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch(java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        
        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer<Integer> validator = (value, length) -> {
                String error = "";
                if(value == null){
                    error = "value cannot be null: " + value;
                }
                throw new ValidationException(error);

        };
        
        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        String milliliters = parameterMap.get(MILLILITERS)[0];
        String bloodGroup = parameterMap.get(BLOOD_GROUP)[0];
        String rhd = parameterMap.get(RHESUS_FACTOR)[0];
        String created = parameterMap.get(CREATED)[0];
        created = created.replaceAll("T", " ");
        //validate the data
        entityBloodDonation.setMilliliters(Integer.parseInt(milliliters));
        entityBloodDonation.setBloodGroup(BloodGroup.getBloodGroup(bloodGroup));
        entityBloodDonation.setRhd(RhesusFactor.getRhesusFactor(rhd));
        entityBloodDonation.setCreated(convertStringToDate(created));
        
        return entityBloodDonation;
    }
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("Donation ID", "Bank ID", "Milliliters", "Blood Group", "RHD", "Date Created");
    }

    @Override
    public List<String> getColumnCodes() {
         return Arrays.asList(ID, BANK_ID, MILLILITERS, BLOOD_GROUP, RHESUS_FACTOR, CREATED);
    }

    @Override
    public List<?> extractDataAsList(BloodDonation e) {
        return Arrays.asList(e.getId(), e.getBloodBank().getId(), e.getMilliliters(), e.getBloodGroup(), e.getRhd(), e.getCreated());
    }
   
}
