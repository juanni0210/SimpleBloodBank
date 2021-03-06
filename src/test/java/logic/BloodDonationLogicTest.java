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
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Juan Ni
 * finished on March 28, 2021
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
        //get all the blood donations from the DB
        List<BloodDonation> list = logic.getAll();
        //store the size of list, this way we know how many blood donations exits in DB
        int originalSize = list.size();

        //make sure blood donation was created successfully
        assertNotNull(expectedEntity);
        //delete the new blood donation
        logic.delete(expectedEntity);

        //get all blood donations again
        list = logic.getAll();
        //the new size of blood donations must be one less
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
            assertEquals(expectedEntity.getBloodGroup().getSymbol(), bloodDonation.getBloodGroup().getSymbol());
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
        sampleMap.put(BloodDonationLogic.MILLILITERS, new String[]{ "1111"});
        sampleMap.put(BloodDonationLogic.BLOOD_GROUP, new String[]{ "A"});
        sampleMap.put(BloodDonationLogic.RHESUS_FACTOR, new String[]{ "-"});
        sampleMap.put(BloodDonationLogic.CREATED, new String[]{ "2021-04-02 12:00:00"});

        
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
        
        int bankID = 1;
        
        BloodDonation returnedBloodDonation = logic.createEntity(sampleMap);
        BloodBank bloodBank = bloodBankLogic.getWithId(bankID);
        if(bloodBank != null) {
            returnedBloodDonation.setBloodBank(bloodBank);
            logic.add(returnedBloodDonation);
        } 
        List<BloodDonation> returnedBloodDonations = logic.getBloodDonationsWithBloodBank(bankID);
        returnedBloodDonation = returnedBloodDonations.get(returnedBloodDonations.size()-1);

        assertEquals(bankID, returnedBloodDonation.getBloodBank().getId());
        assertEquals(sampleMap.get(BloodDonationLogic.MILLILITERS )[ 0 ], Integer.toString(returnedBloodDonation.getMilliliters()));
        assertEquals(sampleMap.get(BloodDonationLogic.BLOOD_GROUP)[ 0 ], returnedBloodDonation.getBloodGroup().getSymbol());
        assertEquals(sampleMap.get(BloodDonationLogic.RHESUS_FACTOR)[ 0 ], returnedBloodDonation.getRhd().getSymbol());
        assertEquals(sampleMap.get(BloodDonationLogic.CREATED)[ 0 ], logic.convertDateToString(returnedBloodDonation.getCreated()));

        logic.delete(returnedBloodDonation);
    }
    
    @Test
    final void testCreateEntityBankNotExist() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodDonationLogic.BANK_ID, new String[]{ "1000000"});
        sampleMap.put(BloodDonationLogic.MILLILITERS, new String[]{ "1111"});
        sampleMap.put(BloodDonationLogic.BLOOD_GROUP, new String[]{ "A"});
        sampleMap.put(BloodDonationLogic.RHESUS_FACTOR, new String[]{ "-"});
        sampleMap.put(BloodDonationLogic.CREATED, new String[]{ "2021-04-02 12:00:00"});

        
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
        
        int bankID = Integer.parseInt(sampleMap.get(BloodDonationLogic.BANK_ID)[0]);
        
        BloodDonation returnedBloodDonation = logic.createEntity(sampleMap);
        BloodBank bloodBank = bloodBankLogic.getWithId(bankID);
        returnedBloodDonation.setBloodBank(bloodBank);
        logic.add(returnedBloodDonation);
        assertThrows(NullPointerException.class, () -> returnedBloodDonation.getBloodBank().getId());
        logic.delete(returnedBloodDonation);
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodDonationLogic.ID, new String[]{Integer.toString(expectedEntity.getId())} );
        //sampleMap.put(BloodDonationLogic.BANK_ID, new String[]{Integer.toString(expectedEntity.getBloodBank().getId())} );
        sampleMap.put(BloodDonationLogic.MILLILITERS, new String[]{Integer.toString(expectedEntity.getMilliliters())});
        sampleMap.put(BloodDonationLogic.BLOOD_GROUP, new String[]{expectedEntity.getBloodGroup().getSymbol()});
        sampleMap.put(BloodDonationLogic.RHESUS_FACTOR, new String[]{expectedEntity.getRhd().getSymbol()});
        sampleMap.put(BloodDonationLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});

        BloodDonation returnedBloodDonation = logic.createEntity(sampleMap);
        returnedBloodDonation.setBloodBank(expectedEntity.getBloodBank());
        
        assertBloodDonationEquals(expectedEntity, returnedBloodDonation);
    }
    
       @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put( BloodDonationLogic.ID, new String[]{ Integer.toString(expectedEntity.getId())});
            map.put( BloodDonationLogic.BANK_ID, new String[]{Integer.toString(expectedEntity.getBloodBank().getId())});
            map.put( BloodDonationLogic.MILLILITERS, new String[]{Integer.toString(expectedEntity.getMilliliters())});
            map.put( BloodDonationLogic.BLOOD_GROUP, new String[]{expectedEntity.getBloodGroup().getSymbol()} );
            map.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{expectedEntity.getRhd().getSymbol()} );
            map.put( BloodDonationLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(BloodDonationLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap) );
        sampleMap.replace(BloodDonationLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap) );
        
        fillMap.accept( sampleMap );
        //can be null
        sampleMap.replace( BloodDonationLogic.BANK_ID, new String[]{} );
        BloodDonation bloodDonation = logic.createEntity( sampleMap );
        assertThrows( IndexOutOfBoundsException.class, () -> bloodDonation.setBloodBank(new BloodBank(Integer.parseInt(sampleMap.get(BloodDonationLogic.BANK_ID)[0]))));
        
        
        fillMap.accept(sampleMap);
        sampleMap.replace(BloodDonationLogic.MILLILITERS, null );
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BloodDonationLogic.MILLILITERS, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
        
        fillMap.accept(sampleMap);
        sampleMap.replace(BloodDonationLogic.BLOOD_GROUP, null );
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BloodDonationLogic.BLOOD_GROUP, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(BloodDonationLogic.RHESUS_FACTOR, null );
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BloodDonationLogic.RHESUS_FACTOR, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap));

        fillMap.accept( sampleMap);
        sampleMap.replace(BloodDonationLogic.CREATED, null );
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BloodDonationLogic.CREATED, new String[]{} );
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }
    
    
    @Test
    final void testCreateEntityWithEmptyBankID() {
        
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodDonationLogic.BANK_ID, new String[]{ ""});
        sampleMap.put(BloodDonationLogic.MILLILITERS, new String[]{ "500"});
        sampleMap.put(BloodDonationLogic.BLOOD_GROUP, new String[]{ "AB"});
        sampleMap.put(BloodDonationLogic.RHESUS_FACTOR, new String[]{ "+"});
        sampleMap.put(BloodDonationLogic.CREATED, new String[]{ "2021-04-03 12:12:12"});

        //idealy every test should be in its own method
        BloodDonation returnBloodDonation = logic.createEntity(sampleMap);
        assertEquals( sampleMap.get( BloodDonationLogic.MILLILITERS )[ 0 ], String.valueOf(returnBloodDonation.getMilliliters()));
        assertEquals( sampleMap.get( BloodDonationLogic.BLOOD_GROUP )[ 0 ], returnBloodDonation.getBloodGroup().getSymbol());
        assertEquals( sampleMap.get( BloodDonationLogic.RHESUS_FACTOR )[ 0 ], returnBloodDonation.getRhd().getSymbol());
        assertEquals( sampleMap.get( BloodDonationLogic.CREATED )[ 0 ], logic.convertDateToString(returnBloodDonation.getCreated()));


    
    }
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList("Donation ID", "Bank ID", "Milliliters", "Blood Group", "RHD", "Date"), list);
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
        assertEquals(logic.convertDateToString(expectedEntity.getCreated()), list.get(5));
    }
}
