package view;

import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.DonationRecord;
import entity.Person;
import entity.RhesusFactor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BloodBankLogic;
import logic.BloodDonationLogic;
import logic.DonationRecordLogic;
import logic.LogicFactory;
import logic.PersonLogic;

/**
 * Insert a new row to person, blood_donation and donation_record table in the simplebloodbank database using JSP.
 * @author Juan Ni
 * Finished on April 9 2021
 */

@WebServlet( name = "DonateBloodFrom", urlPatterns = { "/DonateBloodFrom" } )
public class BloodDonateForm extends HttpServlet{
    private String errorMessage = null;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param req servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processRequest( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute( "path", path );
        req.setAttribute( "title", path.substring( 1 ) );
        
        PersonLogic personLogic = LogicFactory.getFor( "Person" );
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
        req.setAttribute( "personColumnNames", personLogic.getColumnNames().subList(1, personLogic.getColumnNames().size()));
        req.setAttribute( "personColumnCodes", personLogic.getColumnCodes().subList(1, personLogic.getColumnCodes().size()));
        List<String> bloodColumnNames = new ArrayList<>(List.of("Blood Group", "RHD", "Amount", "Tested"));
        List<String> bloodColumnCodes = new ArrayList<>(List.of(BloodDonationLogic.BLOOD_GROUP, BloodDonationLogic.RHESUS_FACTOR, BloodDonationLogic.MILLILITERS, DonationRecordLogic.TESTED));
        req.setAttribute("bloodColumnNames", bloodColumnNames);
        req.setAttribute("bloodColumnCodes", bloodColumnCodes);
        req.setAttribute("bloodGroupList", Arrays.asList(BloodGroup.values()));
        req.setAttribute("positiveNegativeList", Arrays.asList(RhesusFactor.values()));
        List<String> adminColumnNames = new ArrayList<>(List.of("Hospital", "Administrator", "Date", "Blood Bank"));
        List<String> adminColumnCodes = new ArrayList<>(List.of(DonationRecordLogic.HOSPITAL, DonationRecordLogic.ADMINISTRATOR, DonationRecordLogic.CREATED, BloodBankLogic.NAME));
        List<String> bloodBankNames = new ArrayList<>();
        for (BloodBank bb : bloodBankLogic.getAll()) {
            bloodBankNames.add(bb.getName());
        }
        req.setAttribute("adminColumnNames", adminColumnNames);
        req.setAttribute("adminColumnCodes", adminColumnCodes);
        req.setAttribute("bloodBankNames", bloodBankNames);
        req.setAttribute( "request", toStringMap(req.getParameterMap()) );
        
        if (errorMessage != null && !errorMessage.isEmpty()) {
            req.setAttribute("errorMessage", errorMessage);
        }
        //clear the error message if when reload the page
        errorMessage = "";

        req.getRequestDispatcher( "/jsp/CreateRecords-BloodDonateForm.jsp" ).forward( req, resp );
    }
    
    /**
     * Shows key-value pair in Map to a specific format like key="" and value="".
     *
     * @param m Map<String, String[]>
     * @return String of Map key value pair
     */
    private String toStringMap( Map<String, String[]> m ) {
        StringBuilder builder = new StringBuilder();
        m.keySet().forEach( ( k ) -> {
            builder.append( "Key=" ).append( k )
                    .append( ", " )
                    .append( "Value/s=" ).append( Arrays.toString( m.get( k ) ) )
                    .append( System.lineSeparator() );
        } );
        return builder.toString();
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * get method is called first when requesting a URL. since this servlet will
     * create a host this method simple delivers the html code. creation will be
     * done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "GET" );
        processRequest( req, resp );
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user
     * submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log( "POST" );
        
        PersonLogic personLogic = LogicFactory.getFor("Person");
        BloodDonationLogic bloodDonationLogic = LogicFactory.getFor("BloodDonation");
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
        DonationRecordLogic donationRecordLogic = LogicFactory.getFor("DonationRecord");

        try {
            //create person entity
            Person person = personLogic.createEntity(req.getParameterMap());
            personLogic.add(person);
            
            //create blood donation entity, because user can only select the bank that already exist from drop down menu
            //so we do not need to test whether bank is existing or not
            BloodDonation bloodDonation = bloodDonationLogic.createEntity(req.getParameterMap());
            BloodBank bloodBank = bloodBankLogic.getBloodBankWithName(req.getParameter(BloodBankLogic.NAME));
            bloodDonation.setBloodBank(bloodBank);
            bloodDonationLogic.add(bloodDonation);
            
            //create Donation Record entity
            //we are using the person and blood donation that just created to get person ID and blood donation ID
            //so same, do not need to check they'are null or not
            DonationRecord donationRecord = donationRecordLogic.createEntity(req.getParameterMap());
            donationRecord.setPerson(person);
            donationRecord.setBloodDonation(bloodDonation);
            donationRecordLogic.add(donationRecord);
            
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
        
  
        
        if( req.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            processRequest(req, resp );
        } 
    }
    
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create Person Record, Blood Donation Reccord and Donation Record using JSP";
    }

    private static final boolean DEBUG = true;

    /**
     * Log a message.
     *
     * @param msg String of message
     */
    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    /**
     * Log a message.
     *
     * @param msg String of message
     * @param t Throwable type t
     */
    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }

}
