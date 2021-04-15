package view.blooddonation;

import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
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
import logic.LogicFactory;

/**
 * Insert a new row to blood_donation table in the simplebloodbank database using JSP.
 * @author Juan Ni
 * Created on April 4, 2021
 * Finished on April 9, 2021
 */
@WebServlet( name = "CreateBloodDonationJSP", urlPatterns = { "/CreateBloodDonationJSP" } )
public class CreateBloodDonationJSP extends HttpServlet{
    String errorMessage = null;
    
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
    private void createInputBloodDonationForm( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute( "path", path );
        req.setAttribute( "title", path.substring( 1 ) );
        
        BloodDonationLogic logic = LogicFactory.getFor("BloodDonation");
        req.setAttribute( "bloodDonationColumnNames", logic.getColumnNames().subList(1, logic.getColumnNames().size()));
        req.setAttribute( "bloodDonationColumnCodes", logic.getColumnCodes().subList(1, logic.getColumnCodes().size()));
         req.setAttribute("bloodGroupList", Arrays.asList(BloodGroup.values()));
        req.setAttribute("rhdList", Arrays.asList(RhesusFactor.values()));
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
        List<String> bloodBankIDs = new ArrayList<>();
        bloodBankIDs.add("");
        for (BloodBank bb : bloodBankLogic.getAll()) {
            bloodBankIDs.add(bb.getId().toString());
        }
        req.setAttribute( "bloodBankIDs", bloodBankIDs);
        req.setAttribute( "request", toStringMap( req.getParameterMap() ) );
        
        if (errorMessage != null && !errorMessage.isEmpty()) {
            req.setAttribute("errorMessage", errorMessage);
        }
        //clear the error message if when reload the page
        errorMessage = "";
        
        req.getRequestDispatcher( "/jsp/CreateRecord-BloodDonation.jsp" ).forward( req, resp );
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
        createInputBloodDonationForm( req, resp );
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
        BloodDonationLogic bloodDonationLogic = LogicFactory.getFor("BloodDonation");
        
        try {
            BloodDonation bloodDonation = bloodDonationLogic.createEntity(req.getParameterMap());
            if (req.getParameter(BloodDonationLogic.BANK_ID).isEmpty()) {
                bloodDonationLogic.add(bloodDonation);
            } else {
                BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
                //because user can only select the bank that already exist from drop down menu
                //so we do not need to test whether bank is existing or not
                int userInputBankID = Integer.parseInt(req.getParameter(BloodDonationLogic.BANK_ID));
                BloodBank bloodBank = bloodBankLogic.getWithId(userInputBankID);
                bloodDonation.setBloodBank(bloodBank);
                bloodDonationLogic.add(bloodDonation);
            }
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }

        if (req.getParameter("add") != null) {
            //if add button is pressed return the same page
            createInputBloodDonationForm(req, resp);
        } else if (req.getParameter("view") != null) {
            //if view button is pressed redirect to the appropriate table
            resp.sendRedirect("BloodDonationTableJSP");
        }

    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create Blood Donation Reccord using JSP";
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
