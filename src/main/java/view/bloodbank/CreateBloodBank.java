package view.bloodbank;

import entity.BloodBank;
import entity.Person;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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
 * Insert a new row to blood_bank table in the simplebloodbank database.
 * @author Cinthialesli Dzouatekeu
 */
@WebServlet(name = "CreateBloodBank", urlPatterns = {"/CreateBloodBank"})
public class CreateBloodBank extends HttpServlet {
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<title>Create Blood Bank</title>");
            out.println("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" "
                    + "integrity=\"sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6\" crossorigin=\"anonymous\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
            out.println( "<div class=\"row\" style=\"text-align: center; margin-top: 30px; margin-bottom:20px;\"><h5><b>Create Person</b></h5></div>" );
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">Owner ID:</label>");
            out.printf( "<input class=\"form-control\" type=\"text\" name=\"%s\" value=\"\">", BloodBankLogic.OWNER_ID);
            out.println("</div>");
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">Bank Name:</label>");
            out.printf( "<input class=\"form-control\" type=\"text\" name=\"%s\" value=\"\">", BloodBankLogic.NAME);
            out.println("</div>");
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">Privately Owned:</label>");
            out.printf("<select class=\"form-select\" name=\"%s\">", BloodBankLogic.PRIVATELY_OWNED);
            out.println("<option value=\"1\">");
            out.println("True");
            out.println("</option>");
            out.println("<option value=\"0\">");
            out.println("False");
            out.println("</option>");
            out.println("</select>");
            out.println("</div>");
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">Established:</label>");
            out.printf( "<input class=\"form-control\" type=\"datetime-local\" step =\"1\" name=\"%s\" value=\"\">", BloodBankLogic.ESTABLISHED);
            out.println("</div>");
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">EmployeeCount:</label>");
            out.printf("<input class=\"form-control\" type=\"text\" name=\"%s\" value=\"\">", BloodBankLogic.EMPLOYEE_COUNT);
            out.println("</div>");
            
            out.println("<div class=\"mb-3\" style=\"margin-top: 30px; text-align: center;\">");
            out.println("<input type=\"submit\" class=\"btn btn-primary\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" class=\"btn btn-primary\" name=\"add\" value=\"Add\">");
            out.println("</div>");
            out.println("</form>");

            if (errorMessage != null && !errorMessage.isEmpty()) {
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");

            }
            // clear the error message
            errorMessage = "";
            out.println("<pre style=\"text-align: center;\">");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js\" "
                    + "integrity=\"sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf\" crossorigin=\"anonymous\"></script>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Shows key-value pair in Map to a specific format like key="" and value="".
     *
     * @param m Map<String, String[]>
     * @return String of Map key value pair
     */
    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log("GET");
        processRequest(req, resp);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("POST");
        BloodBankLogic logic = LogicFactory.getFor("BloodBank");
        PersonLogic personLogic = LogicFactory.getFor("Person");
        
        String bankName = req.getParameter(BloodBankLogic.NAME);
        
        if( logic.getBloodBankWithName(bankName) == null ){
            try {
                BloodBank bloodBank = logic.createEntity(req.getParameterMap());
                
                if(req.getParameter(BloodBankLogic.OWNER_ID).isEmpty()){
                    logic.add(bloodBank);
                }else{
                    int ownerID = Integer.parseInt(req.getParameter(BloodBankLogic.OWNER_ID));
                    BloodBank bloodBankExist = logic.getBloodBanksWithOwner(ownerID);
                    if(bloodBankExist == null) {
                        Person person= personLogic.getWithId(ownerID);
                        if(person != null){
                            bloodBank.setOwner(person);
                            logic.add(bloodBank);  
                       }else{
                          errorMessage = "Person: \"" + ownerID + "\" does not exist in person table.";
                       }
                    } else {
                        errorMessage = "Person: \"" + ownerID + "\" already owns a blood bank.";;
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
            processRequest(req, resp);
        } else if (req.getParameter("view") != null) {
            resp.sendRedirect("BloodBankTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a BloodBank Entity";
    }

    private static final boolean DEBUG = true;

    /**
     * Log a message.
     *
     * @param msg String of message
     */
    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    /**
     * Log a message.
     *
     * @param msg String of message
     * @param t Throwable type t
     */
    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
