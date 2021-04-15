package view.bloodbank;

import entity.BloodBank;
import entity.Person;
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
import logic.LogicFactory;
import logic.PersonLogic;

/**
 * Insert a new row to blood_bank table in the simplebloodbank database using JSP.
 * @author Juan Ni
 */
@WebServlet( name = "CreateBloodBankJSP", urlPatterns = { "/CreateBloodBankJSP" } )
public class CreateBloodBankJSP extends HttpServlet{
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
        req.setAttribute( "request", toStringMap( req.getParameterMap() ) );
        req.setAttribute( "path", path );
        req.setAttribute( "title", path.substring( 1 ) );
        
        if (errorMessage != null && !errorMessage.isEmpty()) {
            req.setAttribute("errorMessage", errorMessage);
        }
        //clear the error message if when reload the page
        errorMessage = "";
        req.getRequestDispatcher( "/jsp/CreateRecord-BloodBank.jsp" ).forward( req, resp );
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
        
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");
        PersonLogic personLogic = LogicFactory.getFor("Person");
        String bankName = req.getParameter(BloodBankLogic.NAME);
        
        if( bloodBankLogic.getBloodBankWithName(bankName) == null ){
            try {
                BloodBank bloodBank = bloodBankLogic.createEntity(req.getParameterMap());
                
                if(req.getParameter(BloodBankLogic.OWNER_ID).isEmpty()){
                    bloodBankLogic.add(bloodBank);
                }else{
                    int ownerID = Integer.parseInt(req.getParameter(BloodBankLogic.OWNER_ID));
                    BloodBank bloodBankExist = bloodBankLogic.getBloodBanksWithOwner(ownerID);
                    if(bloodBankExist == null) {
                        Person person= personLogic.getWithId(ownerID);
                        if(person != null){
                            bloodBank.setOwner(person);
                            bloodBankLogic.add(bloodBank);  
                       }else{
                          errorMessage = "Person: \"" + ownerID + "\" does not exist in person table.";
                       }
                    } else {
                        errorMessage = "Person: \"" + ownerID + "\" already owns a blood bank.";
                    }
                }
            } catch( Exception ex ) {
                errorMessage = ex.getMessage();
            }
        } else {
           //if duplicate print the error message
           errorMessage = "Bank name: \"" + bankName + "\" already exists";
        }

        if (req.getParameter("add") != null) {
            //if add button is pressed return the same page
            createInputBloodDonationForm(req, resp);
        } else if (req.getParameter("view") != null) {
            //if view button is pressed redirect to the appropriate table
            resp.sendRedirect("BloodBankTableJSP");
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
