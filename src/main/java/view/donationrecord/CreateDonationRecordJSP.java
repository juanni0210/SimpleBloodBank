package view.donationrecord;

import entity.BloodDonation;
import entity.DonationRecord;
import entity.Person;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BloodDonationLogic;
import logic.DonationRecordLogic;
import logic.LogicFactory;
import logic.PersonLogic;

/**
 * Insert a new row to donation_record table in the simplebloodbank database using JSP.
 * @author Feiqiong Deng
 */
@WebServlet(name = "CreateDonationRecordJSP", urlPatterns = {"/CreateDonationRecordJSP"})
public class CreateDonationRecordJSP extends HttpServlet {
    public String errorMessage = null;

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
    private void fillTableData( HttpServletRequest req, HttpServletResponse resp )
           throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute( "request", toStringMap( req.getParameterMap() ) );
        req.setAttribute( "path", path );
        req.setAttribute( "title", path.substring( 1 ) );
         if (errorMessage != null && !errorMessage.isEmpty()) {
            req.setAttribute("error", errorMessage);
        }
        //clear the error message if when reload the page
        errorMessage = "";
              
        PersonLogic personLogic = LogicFactory.getFor("Person");
        List<String> personIDs = new ArrayList<>();
        personIDs.add("");
        for (Person person : personLogic.getAll()) {
            personIDs.add(person.getId().toString());
        }
        req.setAttribute( "personIDList", personIDs);
        
        BloodDonationLogic bloodDonationLogic = LogicFactory.getFor("BloodDonation");
        List<String> bloodDonationIDs = new ArrayList<>();
        bloodDonationIDs.add("");
        for (BloodDonation bloodDonation : bloodDonationLogic.getAll()) {
            bloodDonationIDs.add(bloodDonation.getId().toString());
        }
        req.setAttribute( "bloodDonationList", bloodDonationIDs);
        req.getRequestDispatcher( "/jsp/CreateRecord-DonationRecord.jsp" ).forward( req, resp );
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
        
        DonationRecordLogic donationRecordLogic = LogicFactory.getFor("DonationRecord");
        BloodDonationLogic bloodDonationLogic = LogicFactory.getFor("BloodDonation");
        PersonLogic personLogic = LogicFactory.getFor("Person");

        if(req.getParameter("person_id").isEmpty() && req.getParameter("donation_id").isEmpty()){
            try{
                DonationRecord newRecord = donationRecordLogic.createEntity( req.getParameterMap() );
                donationRecordLogic.add(newRecord);
            }catch( Exception ex ) {
                errorMessage = ex.getMessage();
            }           
        }
        
        if(req.getParameter("person_id").isEmpty() && !req.getParameter("donation_id").isEmpty()){
            int inputDonationID = Integer.parseInt(req.getParameter("donation_id"));
            BloodDonation bloodDonation = bloodDonationLogic.getWithId(inputDonationID);
            if(bloodDonation != null){
                try{
                   DonationRecord newRecord = donationRecordLogic.createEntity( req.getParameterMap() );
                   newRecord.setBloodDonation(bloodDonation);
                   donationRecordLogic.add(newRecord);
                }catch( Exception ex ) {
                errorMessage = ex.getMessage();
                }           
            } else {
               errorMessage =  "Donation ID: \"" + inputDonationID + "\" does not exist in the Blood Donation Table";
            }
        }
        
        if(!req.getParameter("person_id").isEmpty() && req.getParameter("donation_id").isEmpty()){
            int inputPersonID = Integer.parseInt(req.getParameter("person_id"));
            Person person = personLogic.getWithId(inputPersonID);
            if(person != null){
                try{
                   DonationRecord newRecord = donationRecordLogic.createEntity( req.getParameterMap() );
                   newRecord.setPerson(person);
                   donationRecordLogic.add(newRecord);
                }catch( Exception ex ) {
                errorMessage = ex.getMessage();
                }           
            } else {
               errorMessage =  "Person ID: \"" + inputPersonID + "\" does not exist in the Person Table";
            }
        }
        
        if(!req.getParameter("person_id").isEmpty() && !req.getParameter("donation_id").isEmpty()){
            int inputPersonID = Integer.parseInt(req.getParameter("person_id"));
            Person person = personLogic.getWithId(inputPersonID);
            int inputDonationID = Integer.parseInt(req.getParameter("donation_id"));
            BloodDonation bloodDonation = bloodDonationLogic.getWithId(inputDonationID);
            if(person != null && bloodDonation != null){
                try{
                   DonationRecord newRecord = donationRecordLogic.createEntity( req.getParameterMap() );
                   newRecord.setPerson(person);
                     newRecord.setBloodDonation(bloodDonation);
                   donationRecordLogic.add(newRecord);
                }catch( Exception ex ) {
                errorMessage = ex.getMessage();
                }           
            } else {
               if (person == null && bloodDonation == null){
                errorMessage =  "Neither Person ID nor Dontion ID does not exist";
               } else if(person == null && bloodDonation != null){
                  errorMessage =  "Person ID: \"" + inputPersonID + "\" does not exist in the Person Table";
               } else{
                  errorMessage =  "Donation ID: \"" + inputDonationID + "\" does not exist in the Blood Donation Table";
               }
            }
        }      
       
       if( req.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            fillTableData( req, resp );
            errorMessage = "";
        } else if( req.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            resp.sendRedirect( "DonationRecordTable" );
        } 
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log( "GET" );
        fillTableData( req, resp );
    }
    
    @Override
    protected void doPut( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "PUT" );
        doPost( req, resp );
    }


    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a donation record JSP";
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
