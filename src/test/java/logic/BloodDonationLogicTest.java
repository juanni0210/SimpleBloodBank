package logic;

import common.EMFactory;
import common.TomcatStartUp;
import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author shado
 */
public class BloodDonationLogicTest {
    
    private BloodDonationLogic logic;
    private BloodDonation expectedEntity;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/SimpleBloodBank", "common.ServletListener", "simplebloodbank-PU-test" );
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {

        logic = LogicFactory.getFor( "BloodDonation" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //check if the depdendecy exists on DB already
        //em.find takes two arguments, the class type of return result and the primery key.
        BloodBank bb = em.find( BloodBank.class, 1 );
        //if result is null create the entity and persist it
        if( bb == null ){
            //cearet object
            bb = new BloodBank();
            bb.setName( "JUNIT" );
            bb.setPrivatelyOwned( true );
            bb.setEstablished( logic.convertStringToDate( "1111-11-11 11:11:11" ) );
            bb.setEmplyeeCount( 111 );
            //persist the dependency first
            em.persist( bb );
        }

        //create the desired entity
        BloodDonation entity = new BloodDonation();
        entity.setMilliliters( 100 );
        entity.setBloodGroup( BloodGroup.AB );
        entity.setRhd( RhesusFactor.Negative );
        entity.setCreated( logic.convertStringToDate( "1111-11-11 11:11:11" ) );
        //add dependency to the desired entity
        entity.setBloodBank( bb );

        //add desired entity to hibernate, entity is now managed.
        //we use merge instead of add so we can get the managed entity.
        expectedEntity = em.merge( entity );
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if( expectedEntity != null ){
            logic.delete(expectedEntity );
        }
    }

    @Test
    final void testGetAll() {
        //get all the accounts from the DB
        List<BloodDonation> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull(expectedEntity);
        //delete the new account
        logic.delete(expectedEntity);

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }
    
    /**
     * helper method for testing all BloodDonation fields
     *
     * @param expected
     * @param actual
     */
    private void assertBloodDonationEquals( BloodDonation expected, BloodDonation actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getBloodBank().getId(), actual.getBloodBank().getId());
        assertEquals( expected.getMilliliters(), actual.getMilliliters());
        assertEquals( expected.getBloodGroup().getSymbol(), actual.getBloodGroup().getSymbol());
        assertEquals( expected.getRhd().getSymbol(), actual.getRhd().getSymbol());
        assertEquals( expected.getCreated(), actual.getCreated());
    }
    
     @Test
    final void testGetWithId() {
        //using the id of test blood donation get another blood donation from logic
        BloodDonation returnedBloodDonation = logic.getWithId(expectedEntity.getId());

        //the two blood donations must be the same
        assertBloodDonationEquals(expectedEntity, returnedBloodDonation);
    }
    
    @Test
    final void testGetBloodDonationWithMilliliters() {
        int foundFull = 0;
        List<BloodDonation> returnedBloodDonations = logic.getBloodDonationWithMilliliters(expectedEntity.getMilliliters());
        for (BloodDonation bloodDonation : returnedBloodDonations) {
            assertEquals(expectedEntity.getMilliliters(), bloodDonation.getMilliliters());
            if (bloodDonation.getId().equals(expectedEntity.getId())) {
                assertBloodDonationEquals(expectedEntity, bloodDonation);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetBloodDonationWithBloodGroup() {
        int foundFull = 0;
        List<BloodDonation> returnedBloodDonations = logic.getBloodDonationWithBloodGroup(expectedEntity.getBloodGroup());
        for (BloodDonation bloodDonation : returnedBloodDonations) {
            assertEquals(expectedEntity.getBloodGroup(), bloodDonation.getBloodGroup());
            if (bloodDonation.getId().equals(expectedEntity.getId())) {
                assertBloodDonationEquals(expectedEntity, bloodDonation);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetBloodDonationWithCreated() {
        int foundFull = 0;
        List<BloodDonation> returnedBloodDonations = logic.getBloodDonationWithCreated(expectedEntity.getCreated());
        for (BloodDonation bloodDonation : returnedBloodDonations) {
            assertEquals(expectedEntity.getCreated(), bloodDonation.getCreated());
            if (bloodDonation.getId().equals(expectedEntity.getId())) {
                assertBloodDonationEquals(expectedEntity, bloodDonation);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetBloodDonationsWithRhd() {
        int foundFull = 0;
        List<BloodDonation> returnedBloodDonations = logic.getBloodDonationsWithRhd(expectedEntity.getRhd());
        for (BloodDonation bloodDonation : returnedBloodDonations) {
            assertEquals(expectedEntity.getRhd(), bloodDonation.getRhd());
            if (bloodDonation.getId().equals(expectedEntity.getId())) {
                assertBloodDonationEquals(expectedEntity, bloodDonation);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetBloodDonationsWithBloodBank() {
        int foundFull = 0;
        List<BloodDonation> returnedBloodDonations = logic.getBloodDonationsWithBloodBank(expectedEntity.getBloodBank().getId());
        for (BloodDonation bloodDonation : returnedBloodDonations) {
            assertEquals(expectedEntity.getBloodBank().getId(), bloodDonation.getBloodBank().getId());
            if (bloodDonation.getId().equals(expectedEntity.getId())) {
                assertBloodDonationEquals(expectedEntity, bloodDonation);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodDonationLogic.BANK_ID, new String[]{ "1"} );
        sampleMap.put(BloodDonationLogic.MILLILITERS, new String[]{ "1111"});
        sampleMap.put(BloodDonationLogic.BLOOD_GROUP, new String[]{ "A"});
        sampleMap.put(BloodDonationLogic.RHESUS_FACTOR, new String[]{ "Negative"});
        sampleMap.put(BloodDonationLogic.CREATED, new String[]{ "2021-04-02 12:00:00"});

        
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
        
        int bankID = Integer.parseInt(sampleMap.get(BloodDonationLogic.BANK_ID)[0]);
        
        BloodDonation returnedBloodDonation = logic.createEntity(sampleMap);
        if(bloodBankLogic.getWithId(bankID) != null) {
            returnedBloodDonation.setBloodBank(new BloodBank(bankID));
            logic.add(returnedBloodDonation);
        } 
        returnedBloodDonation = logic.getWithId(returnedBloodDonation.getId());

        assertEquals(sampleMap.get(BloodDonationLogic.BANK_ID)[ 0 ], Integer.toString(returnedBloodDonation.getBloodBank().getId()));
        assertEquals(sampleMap.get(BloodDonationLogic.MILLILITERS )[ 0 ], Integer.toString(returnedBloodDonation.getMilliliters()));
        assertEquals(sampleMap.get(BloodDonationLogic.BLOOD_GROUP)[ 0 ], returnedBloodDonation.getBloodGroup().getSymbol());
        assertEquals(sampleMap.get(BloodDonationLogic.RHESUS_FACTOR)[ 0 ], returnedBloodDonation.getRhd().getSymbol());
        assertEquals(sampleMap.get(BloodDonationLogic.CREATED)[ 0 ], logic.convertDateToString(returnedBloodDonation.getCreated()));

        logic.delete(returnedBloodDonation);
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodDonationLogic.ID, new String[]{Integer.toString(expectedEntity.getId())} );
        sampleMap.put(BloodDonationLogic.BANK_ID, new String[]{Integer.toString(expectedEntity.getBloodBank().getId())} );
        sampleMap.put(BloodDonationLogic.MILLILITERS, new String[]{Integer.toString(expectedEntity.getMilliliters())});
        sampleMap.put(BloodDonationLogic.BLOOD_GROUP, new String[]{expectedEntity.getBloodGroup().getSymbol()});
        sampleMap.put(BloodDonationLogic.RHESUS_FACTOR, new String[]{expectedEntity.getRhd().getSymbol()});
        sampleMap.put(BloodDonationLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});

        BloodDonation returnedBloodDonation = logic.createEntity(sampleMap);
        
        assertBloodDonationEquals(expectedEntity, returnedBloodDonation);
    }
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList("Donation ID", "Bank ID", "Milliliters", "Blood Group", "RHD", "Date Created"), list);
    }
    
    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(BloodDonationLogic.ID, BloodDonationLogic.BANK_ID, BloodDonationLogic.MILLILITERS, BloodDonationLogic.BLOOD_GROUP, BloodDonationLogic.RHESUS_FACTOR, BloodDonationLogic.CREATED), list);
    }
    
    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedEntity);
        assertEquals(expectedEntity.getId(), list.get(0));
        assertEquals(expectedEntity.getBloodBank().getId(), list.get(1));
        assertEquals(expectedEntity.getMilliliters(), list.get(2));
        assertEquals(expectedEntity.getBloodGroup(), list.get(3));
        assertEquals(expectedEntity.getRhd(), list.get(4));
        assertEquals(expectedEntity.getCreated(), list.get(5));
    }
}
