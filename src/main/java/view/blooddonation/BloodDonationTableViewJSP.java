package view.blooddonation;

import entity.BloodBank;
import entity.BloodDonation;
import entity.DonationRecord;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BloodDonationLogic;
import logic.DonationRecordLogic;
import logic.LogicFactory;

/**
 * BloodDonationTable shows all data from blood_donation table in simplebloodbank database using JSP.
 * @author Juan Ni
 * Created on April 1, 2021
 * Finished on April 2, 2021
 */

@WebServlet( name = "BloodDonationTableJSP", urlPatterns = { "/BloodDonationTableJSP" } )
public class BloodDonationTableViewJSP extends HttpServlet{
    String errorMessage = null;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void fillTableData( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute( "entities", extractTableData( req ) );
        req.setAttribute( "request", toStringMap( req.getParameterMap() ) );
        req.setAttribute( "path", path );
        req.setAttribute( "title", path.substring( 1 ) );
        if (errorMessage != null && !errorMessage.isEmpty()) {
            req.setAttribute("errorMessage", errorMessage);
        }
        //clear the error message if when reload the page
        errorMessage = "";
        req.getRequestDispatcher( "/jsp/ShowTable-BloodDonation.jsp" ).forward( req, resp );
    }
    
    /**
     * Get table data from database and put into an string array.
     *
     * @param req servlet request
     * @return A String list containing entity details
     */
    private List<?> extractTableData( HttpServletRequest req ) {
        String search = req.getParameter( "searchText" );
        if (search != null) {
            if(search.equalsIgnoreCase("Positive")) {
                search = "+";
            } else if (search.equalsIgnoreCase("Negative")) {
                search = "-";
            }
        }
        
        BloodDonationLogic logic = LogicFactory.getFor( "BloodDonation" );
        req.setAttribute( "columnName", logic.getColumnNames() );
        req.setAttribute( "columnCode", logic.getColumnCodes() );
        List<BloodDonation> list;
        if( search != null ){
            list = logic.search( search );
        } else {
            list = logic.getAll();
        }
        if( list == null || list.isEmpty() ){
            return Collections.emptyList();
        }
        return appendDataToNewList( list, logic::extractDataAsList );
    }
    
    /**
     * Convert an entity to a string array.
     *
     * @param list List <T>
     * @param toArray Function<T, List<?>>
     * @return A list containing entity
     */     
    private <T> List<?> appendDataToNewList( List<T> list, Function<T, List<?>> toArray ) {
        List<List<?>> newlist = new ArrayList<>( list.size() );
        list.forEach( i -> newlist.add( toArray.apply( i ) ) );
        return newlist;
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
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "POST" );
        BloodDonationLogic logic = LogicFactory.getFor( "BloodDonation" );
        if( req.getParameter( "edit" ) != null ){
            BloodDonation bloodDonation = logic.updateEntity( req.getParameterMap() );
            int bankId = Integer.parseInt(req.getParameter(BloodDonationLogic.BANK_ID));
            bloodDonation.setBloodBank(new BloodBank(bankId));
            logic.update( bloodDonation );
        } else if( req.getParameter( "delete" ) != null ){
            String[] ids = req.getParameterMap().get( "deleteMark" );
            for( String id: ids ) {
                //check whether the blood donation that user wants to delete exists in the donation record table or not.
                //if yes, cannnot delete it. if no, shows an error message
                DonationRecordLogic donationRecordLogic = LogicFactory.getFor("DonationRecord");
                List<DonationRecord> donationRecordList = donationRecordLogic.getDonationRecordsWithDonation(Integer.parseInt(id));
                if (donationRecordList.size() == 0) {
                    logic.delete( logic.getWithId( Integer.valueOf( id ) ) );
                } else {
                    errorMessage = "Blood Donation " + id + " exists in the Donation Record Table. Fail to delete.";
                }
            }
        }
        fillTableData( req, resp );
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "GET" );
        fillTableData( req, resp );
    }
    
    /**
     * Handles the HTTP <code>PUT</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "PUT" );
        doPost( req, resp );
    }
    
     /**
     * Handles the HTTP <code>DELETE</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "DELETE" );
        doPost( req, resp );
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Blood Donation Table using JSP";
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
