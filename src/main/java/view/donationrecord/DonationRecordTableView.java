package view.donationrecord;

import entity.DonationRecord;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.DonationRecordLogic;
import logic.LogicFactory;

/**
 * DonationRecordTable shows all data from donation_record table in simplebloodbank database.
 * @author Feiqiong Deng
 */
@WebServlet(name = "DonationRecordTable", urlPatterns = {"/DonationRecordTable"})
public class DonationRecordTableView extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>DonationRecordTableViewNormal</title>");            
            out.println("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" "
                    + "integrity=\"sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6\" crossorigin=\"anonymous\">");
            out.println("</head>");
            out.println("<body>");
            out.println( "<div class=\"container\">" );
            out.println( "<div class=\"row\" style=\"text-align: center; margin-top: 30px; margin-bottom:20px;\"><h5><b>Donation Records</b></h5></div>" );
            out.println( "<table class=\"table table-bordered\" style=\"text-align: center; margin-left: auto; margin-right: auto;\" border=\"1\">" );
            
            DonationRecordLogic logic = LogicFactory.getFor( "DonationRecord" );
            out.println( "<tr>" );
            logic.getColumnNames().forEach( c -> out.printf( "<th class=\"table-primary\">%s</th>", c ) );

            out.println( "</tr>" );
            logic.getAll().forEach( e -> out.printf( "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                    logic.extractDataAsList( e ).toArray() ) );

            out.println( "</table>" );
            out.println("</div>");
            out.printf( "<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap( request.getParameterMap() ) );
             out.println("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js\" "
                    + "integrity=\"sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf\" crossorigin=\"anonymous\"></script>");
            out.println( "</body>" );
            out.println( "</html>" );
        }              
    }
    
    
    /**
     * Shows key-value pair in Map to a specific format like key="" and value="".
     *
     * @param m Map<String, String[]>
     * @return String of Map key value pair
     */
    private String toStringMap( Map<String, String[]> m ) {
        StringBuilder builder = new StringBuilder();
        for( String k: m.keySet() ) {
            builder.append( "Key=" ).append( k )
                    .append( ", " )
                    .append( "Value/s=" ).append( Arrays.toString( m.get( k ) ) )
                    .append( System.lineSeparator() );
        }
        return builder.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        DonationRecordLogic logic = LogicFactory.getFor( "DonationRecord" );
        DonationRecord donationRecord = logic.updateEntity(request.getParameterMap());
        logic.update(donationRecord);
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Donation Record View Normal";
    }
    
    private static final boolean DEBUG = true;

    /**
     * Log a message.
     *
     * @param msg String of message
     */
    public void log( String msg ) {
        if(DEBUG){
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
    public void log(String msg, Throwable t) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }
    
}
