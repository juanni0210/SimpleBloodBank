package dal;

import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Juan Ni
 * Finished on March 29, 2021
 */
public class BloodDonationDAL extends GenericDAL<BloodDonation> {

    public BloodDonationDAL() {
        super( BloodDonation.class );
    }

    @Override
    public List<BloodDonation> findAll() {
        //first argument is a name given to a named query defined in appropriate entity
        //second argument is map used for parameter substitution.
        //parameters are names starting with : in named queries, :[name]
        return findResults("BloodDonation.findAll", null);
    }

    @Override
    public BloodDonation findById(int donationId) {
        Map<String, Object> map = new HashMap<>();
        map.put("donationId", donationId);
        //first argument is a name given to a named query defined in appropriate entity
        //second argument is map used for parameter substitution.
        //parameters are names starting with : in named queries, :[name]
        //in this case the parameter is named "id" and value for it is put in map
        return findResult("BloodDonation.findByDonationId", map);
    }
    
    public List<BloodDonation> findByMilliliters(int milliliters){
        Map<String, Object> map = new HashMap<>();
        map.put("milliliters", milliliters);
        return findResults("BloodDonation.findByMilliliters", map);
    }
    
    public List<BloodDonation> findByBloodGroup(BloodGroup bloodGroup) {
        Map<String, Object> map = new HashMap<>();
        map.put("bloodGroup", bloodGroup);
        return findResults("BloodDonation.findByBloodGroup", map);
    }
    
    public List<BloodDonation> findByRhd(RhesusFactor rhd){
        Map<String, Object> map = new HashMap<>();
        map.put("rhd", rhd);
        return findResults("BloodDonation.findByRhd", map);
    }
    
    public List<BloodDonation> findByCreated(Date created){
        Map<String, Object> map = new HashMap<>();
        map.put("created", created);
        return findResults("BloodDonation.findByCreated", map);
    }
    
    public List<BloodDonation> findByBloodBank(int bloodBank){
        Map<String, Object> map = new HashMap<>();
        map.put("bloodBankId", bloodBank);
        return findResults("BloodDonation.findByBloodBank", map);
    }
    
    // bonus
    public List<BloodDonation> findContaining( String search ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "search", search );
        return findResults( "BloodDonation.findContaining", map );
    }
}
