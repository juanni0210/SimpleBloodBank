package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.DonationRecord;
import entity.Person;
import entity.RhesusFactor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import static logic.DonationRecordLogic.ADMINISTRATOR;
import static logic.DonationRecordLogic.CREATED;
import static logic.DonationRecordLogic.HOSPITAL;
import static logic.DonationRecordLogic.TESTED;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 *
 * @author Feiqiong Deng
 */
public class DonationRecordTest {
    
    private DonationRecordLogic logic;
    private DonationRecord expectedEntity;
    
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

        logic = LogicFactory.getFor( "DonationRecord" );
        
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //check if the depdendecy exists on DB already
        //em.find takes two arguments, the class type of return result and the primery key.
        Person person = em.find( Person.class, 1 );
        BloodDonation bloodDonation = em.find( BloodDonation.class, 1 );
        //if result is null create the entity and persist it
        if( person == null){
            //cearet object
            person = new Person();
            person.setFirstName("Bella");
            person.setLastName("Smiths");
            person.setPhone("123456789");
            person.setAddress("123 JUnit5 Street");
            person.setBirth(logic.convertStringToDate( "1111-11-11 11:11:11" ));
            //persist the dependency first
            em.persist( person );
        
        }
        
        if(bloodDonation == null){
            bloodDonation = new BloodDonation();
            bloodDonation.setMilliliters(100);
            bloodDonation.setBloodGroup(BloodGroup.B);
            bloodDonation.setRhd(RhesusFactor.Positive);
            bloodDonation.setCreated(logic.convertStringToDate( "1111-11-11 11:11:11" ));
             //persist the dependency first
            em.persist( bloodDonation );
        }

        //create the desired entity
        DonationRecord entity = new DonationRecord();
        entity.setTested(true);
        entity.setAdministrator("Anna");
        entity.setHospital("Sunshine Hospital");
        entity.setCreated( logic.convertStringToDate( "1111-11-11 11:11:11" ) );
        //add dependency to the desired entity
        entity.setPerson(person);
        entity.setBloodDonation(bloodDonation);

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
        List<DonationRecord> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull( expectedEntity );
        //delete the new account
        logic.delete( expectedEntity );

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }

 /**
     * helper method for testing all DonationRecord fields
     *
     * @param expected
     * @param actual
     */
    private void assertDonationRecordEquals( DonationRecord expected, DonationRecord actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getPerson().getId(), actual.getPerson().getId());
        assertEquals( expected.getBloodDonation().getId(), actual.getBloodDonation().getId());
        assertEquals( expected.getTested(), actual.getTested());
        assertEquals( expected.getAdministrator(), actual.getAdministrator());
        assertEquals( expected.getHospital(), actual.getHospital());
        assertEquals( expected.getCreated(), actual.getCreated());
    }
    
    @Test
    final void testGetWithId() {
        //using the id of test record get another record from logic
        DonationRecord returnedRecord = logic.getWithId( expectedEntity.getId() );
        //the two records expected and returnedRecord must be the same
        assertDonationRecordEquals( expectedEntity, returnedRecord );
    }

    @Test
    final void testGetDonationRecordWithTested(){
        int foundFull = 0;     
        List<DonationRecord> returnedRecords = logic.getDonationRecordWithTested(expectedEntity.getTested());

        for( DonationRecord record: returnedRecords ) {
            assertEquals( expectedEntity.getTested(), record.getTested() );
            if( record.getId().equals( expectedEntity.getId() ) ){
                assertDonationRecordEquals( expectedEntity, record );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testGetDonationRecordWithAdministrator(){
        int foundFull = 0;      
        List<DonationRecord> returnedRecords = logic.getDonationRecordWithAdministrator(expectedEntity.getAdministrator());

        for( DonationRecord record: returnedRecords ) {
            assertEquals( expectedEntity.getAdministrator(), record.getAdministrator() );
            if( record.getId().equals( expectedEntity.getId() ) ){
                assertDonationRecordEquals( expectedEntity, record );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testGetDonationRecordWithHospital(){
        int foundFull = 0;      
        List<DonationRecord> returnedRecords = logic.getDonationRecordWithHospital(expectedEntity.getHospital());

        for( DonationRecord record: returnedRecords ) {
            assertEquals( expectedEntity.getHospital(), record.getHospital() );
            if( record.getId().equals( expectedEntity.getId() ) ){
                assertDonationRecordEquals( expectedEntity, record );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testGetDonationRecordsWithCreated(){
        int foundFull = 0;      
        List<DonationRecord> returnedRecords = logic.getDonationRecordsWithCreated(expectedEntity.getCreated());

        for( DonationRecord record: returnedRecords ) {
            assertEquals( expectedEntity.getCreated(), record.getCreated() );
            if( record.getId().equals( expectedEntity.getId() ) ){
                assertDonationRecordEquals( expectedEntity, record );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testGetDonationRecordsWithPerson(){
        int foundFull = 0;      
        List<DonationRecord> returnedRecords = logic.getDonationRecordsWithPerson(expectedEntity.getPerson().getId());

        for( DonationRecord record: returnedRecords ) {
            assertEquals( expectedEntity.getPerson().getId(), record.getPerson().getId() );
            if( record.getId().equals( expectedEntity.getId()) ){
                assertDonationRecordEquals( expectedEntity, record );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testGetDonationRecordsWithDonation(){
        int foundFull = 0;      
        List<DonationRecord> returnedRecords = logic.getDonationRecordsWithDonation(expectedEntity.getBloodDonation().getId());

        for( DonationRecord record: returnedRecords ) {
            assertEquals( expectedEntity.getBloodDonation().getId(), record.getBloodDonation().getId() );
            if( record.getId().equals( expectedEntity.getId()) ){
                assertDonationRecordEquals( expectedEntity, record );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testCreateEntity(){
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( DonationRecordLogic.TESTED, new String[]{ Boolean.toString(expectedEntity.getTested()) } );
        sampleMap.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ expectedEntity.getAdministrator() } );
        sampleMap.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital()} );
        sampleMap.put( DonationRecordLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});

        DonationRecord returnedRecord = logic.createEntity( sampleMap );
        returnedRecord.setPerson(expectedEntity.getPerson());
        returnedRecord.setBloodDonation(expectedEntity.getBloodDonation());
        
        assertDonationRecordEquals(expectedEntity, returnedRecord);
    }
    
    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(DonationRecordLogic.PERSON_ID, new String[]{ "2"} );
        sampleMap.put(DonationRecordLogic.DONATION_ID, new String[]{ "1"} );
        sampleMap.put(DonationRecordLogic.TESTED, new String[]{ "true"});
        sampleMap.put(DonationRecordLogic.ADMINISTRATOR, new String[]{ "Sam"});
        sampleMap.put(DonationRecordLogic.HOSPITAL, new String[]{ "GoodHealth Hospital"});
        sampleMap.put(DonationRecordLogic.CREATED, new String[]{ "2021-04-07 12:00:00"});
        
        PersonLogic personLogic = LogicFactory.getFor("Person");
        BloodDonationLogic bloodDonationLogic = LogicFactory.getFor("BloodDonation");
        
        int personId = Integer.parseInt(sampleMap.get(DonationRecordLogic.PERSON_ID)[0]);
        int donationId = Integer.parseInt(sampleMap.get(DonationRecordLogic.DONATION_ID)[0]);
        
        DonationRecord returnedRecord = logic.createEntity( sampleMap );
        
         if(personLogic.getWithId(personId) != null && bloodDonationLogic.getWithId(donationId) != null) {
            returnedRecord.setPerson(new Person(personId));
            returnedRecord.setBloodDonation(new BloodDonation(donationId));
            logic.add(returnedRecord);
        } 
        
        returnedRecord = logic.getWithId(returnedRecord.getId());

        assertEquals(sampleMap.get(DonationRecordLogic.ADMINISTRATOR)[ 0 ], returnedRecord.getAdministrator());
        assertEquals(sampleMap.get(DonationRecordLogic.DONATION_ID )[ 0 ], Integer.toString(returnedRecord.getBloodDonation().getId()));
        assertEquals(sampleMap.get(DonationRecordLogic.HOSPITAL)[ 0 ], returnedRecord.getHospital());
        assertEquals(sampleMap.get(DonationRecordLogic.PERSON_ID)[ 0 ], Integer.toString(returnedRecord.getPerson().getId()));
        assertEquals(sampleMap.get(DonationRecordLogic.CREATED)[ 0 ], logic.convertDateToString(returnedRecord.getCreated()));
        assertEquals(sampleMap.get(DonationRecordLogic.TESTED)[ 0 ], Boolean.toString(returnedRecord.getTested()));

        logic.delete(returnedRecord);
    }
    
     @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString(expectedEntity.getId())});
            map.put( DonationRecordLogic.PERSON_ID, new String[]{Integer.toString(expectedEntity.getPerson().getId())});
            map.put( DonationRecordLogic.DONATION_ID, new String[]{Integer.toString(expectedEntity.getBloodDonation().getId())});
            map.put( DonationRecordLogic.ADMINISTRATOR, new String[]{expectedEntity.getAdministrator()} );
            map.put( DonationRecordLogic.HOSPITAL, new String[]{expectedEntity.getHospital()} );
            map.put( DonationRecordLogic.TESTED, new String[]{Boolean.toString(expectedEntity.getTested())} );
            map.put( DonationRecordLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap) );
        sampleMap.replace(DonationRecordLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap) );
        
        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.PERSON_ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap).setPerson(new Person(Integer.parseInt(sampleMap.get(DonationRecordLogic.PERSON_ID)[0]))));
        sampleMap.replace(DonationRecordLogic.PERSON_ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap).setPerson(new Person(Integer.parseInt(sampleMap.get(DonationRecordLogic.PERSON_ID)[0]))));

        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.DONATION_ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap).setBloodDonation(new BloodDonation(Integer.parseInt(sampleMap.get(DonationRecordLogic.DONATION_ID)[0]))));
        sampleMap.replace(DonationRecordLogic.DONATION_ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap).setBloodDonation(new BloodDonation(Integer.parseInt(sampleMap.get(DonationRecordLogic.DONATION_ID)[0]))));
        
        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.ADMINISTRATOR, null );
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(DonationRecordLogic.ADMINISTRATOR, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
        
        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.HOSPITAL, null );
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(DonationRecordLogic.HOSPITAL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.TESTED, null );
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(DonationRecordLogic.TESTED, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap));

        fillMap.accept( sampleMap);
        sampleMap.replace(DonationRecordLogic.CREATED, null );
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(DonationRecordLogic.CREATED, new String[]{} );
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }
    
    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString(expectedEntity.getId())});
            map.put( DonationRecordLogic.PERSON_ID, new String[]{Integer.toString(expectedEntity.getPerson().getId())});
            map.put( DonationRecordLogic.DONATION_ID, new String[]{Integer.toString(expectedEntity.getBloodDonation().getId())});
            map.put( DonationRecordLogic.ADMINISTRATOR, new String[]{expectedEntity.getAdministrator()} );
            map.put( DonationRecordLogic.HOSPITAL, new String[]{expectedEntity.getHospital()} );
            map.put( DonationRecordLogic.TESTED, new String[]{Boolean.toString(expectedEntity.getTested())} );
            map.put( DonationRecordLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});
        };

        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{ "12a" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ADMINISTRATOR, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.ADMINISTRATOR, new String[]{ generateString.apply( 101 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.HOSPITAL, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.HOSPITAL, new String[]{ generateString.apply( 101 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

    }  
    
    @Test
    final void testGetColumnNames(){
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList("Record ID", "Tested", "Administrator", "Hospital", "Created", "Donation ID", "Person ID"), list);
    }
    
    @Test
    final void testGetColumnCodes(){
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( DonationRecordLogic.ID, DonationRecordLogic.TESTED, DonationRecordLogic.ADMINISTRATOR, 
                DonationRecordLogic.HOSPITAL, DonationRecordLogic.CREATED, DonationRecordLogic.DONATION_ID, DonationRecordLogic.PERSON_ID), list );
    }
    
    @Test
    final void testExtractDataAsList(){
        List<?> list = logic.extractDataAsList(expectedEntity);

        assertEquals(expectedEntity.getId(), list.get(0));
        assertEquals(expectedEntity.getTested(), list.get(1));
        assertEquals(expectedEntity.getAdministrator(), list.get(2));
        assertEquals(expectedEntity.getHospital(), list.get(3));
        assertEquals(logic.convertDateToString(expectedEntity.getCreated()), list.get(4));
        assertEquals(expectedEntity.getBloodDonation().getId(), list.get(5));
        assertEquals(expectedEntity.getPerson().getId(), list.get(6));

    }
    
}
