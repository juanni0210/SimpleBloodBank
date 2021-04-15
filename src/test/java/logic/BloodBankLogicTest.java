package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.BloodBank;
import entity.Person;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
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
 * created on April 11, 2021
 */
public class BloodBankLogicTest {
    private BloodBankLogic logic;
    private BloodBank expectedEntity;
    
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
        logic = LogicFactory.getFor( "BloodBank" );
        
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
        Person person = em.find( Person.class, 1 );
        //if result is null create the entity and persist it
        if( person == null){
            //cearet object
            person = new Person();
            person.setFirstName("James");
            person.setLastName("Potter");
            person.setPhone("343123456");
            person.setAddress("400 Hogwarts Street");
            person.setBirth(logic.convertStringToDate( "1111-11-11 11:11:11" ));
            //persist the dependency first
            em.persist( person );
        }

        //create the desired entity
        BloodBank entity = new BloodBank();
        entity.setName("TestBank");
        entity.setPrivatelyOwned(true);
        entity.setEstablished(logic.convertStringToDate( "1111-11-11 11:11:11" ));
        entity.setEmplyeeCount(123);
        //add dependency to the desired entity
        entity.setOwner(person);

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
        List<BloodBank> list = logic.getAll();
        int originalSize = list.size();

        assertNotNull(expectedEntity);
        logic.delete(expectedEntity);

        list = logic.getAll();
        assertEquals( originalSize - 1, list.size() );
    }
    
    /**
     * helper method for testing all BloodBank fields
     *
     * @param expected
     * @param actual
     */
    private void assertBloodBankEquals( BloodBank expected, BloodBank actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getOwner().getId(), actual.getOwner().getId());
        assertEquals( expected.getPrivatelyOwned(), actual.getPrivatelyOwned());
        assertEquals( expected.getEstablished(), actual.getEstablished());
        assertEquals( expected.getName(), actual.getName());
        assertEquals( expected.getEmplyeeCount(), actual.getEmplyeeCount());
    }
    
    @Test
    final void testGetWithId() {
        BloodBank returnedBloodBank = logic.getWithId(expectedEntity.getId());
        assertBloodBankEquals(expectedEntity, returnedBloodBank);
    }
    
    @Test
    final void testGetBloodBankWithName() {
        BloodBank returnedBloodBank = logic.getBloodBankWithName(expectedEntity.getName());
        assertBloodBankEquals(expectedEntity, returnedBloodBank);
    }
    
    @Test
    final void testGetBloodBankWithPrivatelyOwned() {
        int foundFull = 0;
        List<BloodBank> returnedBloodBanks = logic.getBloodBankWithPrivatelyOwned(expectedEntity.getPrivatelyOwned());
        for (BloodBank bloodBank : returnedBloodBanks) {
            assertEquals(expectedEntity.getPrivatelyOwned(), bloodBank.getPrivatelyOwned());
            if (bloodBank.getId().equals(expectedEntity.getId())) {
                assertBloodBankEquals(expectedEntity, bloodBank);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetBloodBankWithEstablished() {
        int foundFull = 0;
        List<BloodBank> returnedBloodBanks = logic.getBloodBankWithEstablished(expectedEntity.getEstablished());
        for (BloodBank bloodBank : returnedBloodBanks) {
            assertEquals(expectedEntity.getEstablished(), bloodBank.getEstablished());
            if (bloodBank.getId().equals(expectedEntity.getId())) {
                assertBloodBankEquals(expectedEntity, bloodBank);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetBloodBanksWithOwner() {
        BloodBank returnedBloodBank = logic.getBloodBanksWithOwner(expectedEntity.getOwner().getId());
        assertBloodBankEquals(expectedEntity, returnedBloodBank);
    }
    
    @Test
    final void testGetBloodBanksWithEmplyeeCount() {
        int foundFull = 0;
        List<BloodBank> returnedBloodBanks = logic.getBloodBanksWithEmplyeeCount(expectedEntity.getEmplyeeCount());
        for (BloodBank bloodBank : returnedBloodBanks) {
            assertEquals(expectedEntity.getEmplyeeCount(), bloodBank.getEmplyeeCount());
            if (bloodBank.getId().equals(expectedEntity.getId())) {
                assertBloodBankEquals(expectedEntity, bloodBank);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodBankLogic.OWNER_ID, new String[]{ "2"});
        sampleMap.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{ "false"});
        sampleMap.put(BloodBankLogic.ESTABLISHED, new String[]{ "2021-04-10 12:00:00"});
        sampleMap.put(BloodBankLogic.NAME, new String[]{ "bbbbbank"});
        sampleMap.put(BloodBankLogic.EMPLOYEE_COUNT, new String[]{ "100"});

        int ownerId = Integer.parseInt(sampleMap.get(BloodBankLogic.OWNER_ID)[0]);
        PersonLogic personLogic = LogicFactory.getFor("Person");
        BloodBank returnedBloodBank = logic.createEntity(sampleMap);
        Person person = personLogic.getWithId(ownerId);
       
        returnedBloodBank.setOwner(person);
        logic.add(returnedBloodBank);
        
        returnedBloodBank = logic.getBloodBanksWithOwner(ownerId);

        assertEquals(sampleMap.get(BloodBankLogic.OWNER_ID)[0], returnedBloodBank.getOwner().getId().toString());
        assertEquals(sampleMap.get(BloodBankLogic.PRIVATELY_OWNED)[0], String.valueOf(returnedBloodBank.getPrivatelyOwned()));
        assertEquals(sampleMap.get(BloodBankLogic.ESTABLISHED)[0], logic.convertDateToString(returnedBloodBank.getEstablished()));
        assertEquals(sampleMap.get(BloodBankLogic.NAME)[0], returnedBloodBank.getName());
        assertEquals(sampleMap.get(BloodBankLogic.EMPLOYEE_COUNT)[0], String.valueOf(returnedBloodBank.getEmplyeeCount()));

        logic.delete(returnedBloodBank);
    }
    
    @Test
    final void testCreateEntityPersonNotExist() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodBankLogic.OWNER_ID, new String[]{ "11111111"});
        sampleMap.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{ "false"});
        sampleMap.put(BloodBankLogic.ESTABLISHED, new String[]{ "2021-04-10 12:00:00"});
        sampleMap.put(BloodBankLogic.NAME, new String[]{ "aaaaabank"});
        sampleMap.put(BloodBankLogic.EMPLOYEE_COUNT, new String[]{ "100"});

        int ownerId = Integer.parseInt(sampleMap.get(BloodBankLogic.OWNER_ID)[0]);
        PersonLogic personLogic = LogicFactory.getFor("Person");
        BloodBank returnedBloodBank = logic.createEntity(sampleMap);
        
        Person person = personLogic.getWithId(ownerId);
        returnedBloodBank.setOwner(person);
        logic.add(returnedBloodBank);
        assertThrows(NullPointerException.class, () -> returnedBloodBank.getOwner().getId());
        logic.delete(returnedBloodBank);
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodBankLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
        sampleMap.put(BloodBankLogic.OWNER_ID, new String[]{Integer.toString(expectedEntity.getId())});
        sampleMap.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{String.valueOf(expectedEntity.getPrivatelyOwned())});
        sampleMap.put(BloodBankLogic.ESTABLISHED, new String[]{logic.convertDateToString(expectedEntity.getEstablished())});
        sampleMap.put(BloodBankLogic.NAME, new String[]{expectedEntity.getName()});
        sampleMap.put(BloodBankLogic.EMPLOYEE_COUNT, new String[]{Integer.toString(expectedEntity.getEmplyeeCount())});

        BloodBank returnedBloodBank = logic.createEntity(sampleMap);
        returnedBloodBank.setOwner(expectedEntity.getOwner());
        
        assertBloodBankEquals(expectedEntity, returnedBloodBank);
    }
    
    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            sampleMap.put(BloodBankLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
            sampleMap.put(BloodBankLogic.OWNER_ID, new String[]{Integer.toString(expectedEntity.getOwner().getId())});
            sampleMap.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{String.valueOf(expectedEntity.getPrivatelyOwned())});
            sampleMap.put(BloodBankLogic.ESTABLISHED, new String[]{logic.convertDateToString(expectedEntity.getEstablished())});
            sampleMap.put(BloodBankLogic.NAME, new String[]{expectedEntity.getName()});
            sampleMap.put(BloodBankLogic.EMPLOYEE_COUNT, new String[]{Integer.toString(expectedEntity.getEmplyeeCount())});
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        //can be null
        sampleMap.replace( BloodBankLogic.OWNER_ID, new String[]{} );
        BloodBank bank = logic.createEntity( sampleMap );
        assertThrows( IndexOutOfBoundsException.class, () -> bank.setOwner(new Person(Integer.parseInt(sampleMap.get(BloodBankLogic.OWNER_ID)[0]))) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            sampleMap.put(BloodBankLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
            sampleMap.put(BloodBankLogic.OWNER_ID, new String[]{Integer.toString(expectedEntity.getOwner().getId())});
            sampleMap.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{String.valueOf(expectedEntity.getPrivatelyOwned())});
            sampleMap.put(BloodBankLogic.ESTABLISHED, new String[]{logic.convertDateToString(expectedEntity.getEstablished())});
            sampleMap.put(BloodBankLogic.NAME, new String[]{expectedEntity.getName()});
            sampleMap.put(BloodBankLogic.EMPLOYEE_COUNT, new String[]{Integer.toString(expectedEntity.getEmplyeeCount())});
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
        sampleMap.replace( BloodBankLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{ generateString.apply( 101 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
    }
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "Bank ID", "Owner ID", "Name", "Privately Owned", "Established Date", "Employee Count" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( BloodBankLogic.ID, BloodBankLogic.OWNER_ID, BloodBankLogic.NAME, BloodBankLogic.PRIVATELY_OWNED, BloodBankLogic.ESTABLISHED, BloodBankLogic.EMPLOYEE_COUNT ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getOwner().getId(), list.get( 1 ) );
        assertEquals( expectedEntity.getName(), list.get( 2 ) );
        assertEquals( expectedEntity.getPrivatelyOwned(), list.get( 3 ) );
        assertEquals( logic.convertDateToString(expectedEntity.getEstablished()), list.get( 4 ) );
        assertEquals( expectedEntity.getEmplyeeCount(), list.get( 5 ) );
    }

    
}
