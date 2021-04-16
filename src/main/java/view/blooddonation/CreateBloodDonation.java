package view.blooddonation;

import entity.BloodBank;
import entity.BloodDonation;
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
import logic.BloodDonationLogic;
import logic.LogicFactory;

/**
 * Insert a new row to blood_donation table in the simplebloodbank database.
 * @author Juan Ni 
 * Finished on March 29, 2021
 */
@WebServlet(name = "CreateBloodDonation", urlPatterns = {"/CreateBloodDonation"})
public class CreateBloodDonation extends HttpServlet {
    private String errorMessage = "";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Account</title>");
            out.println("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" "
                    + "integrity=\"sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6\" crossorigin=\"anonymous\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
            out.println( "<div class=\"row\" style=\"text-align: center; margin-top: 30px; margin-bottom:20px;\"><h5><b>Create Blood Donation</b></h5></div>" );
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">Bank ID:</label>");
            //instead of typing the name of column manualy use the static vraiable in logic
            //use the same name as column id of the table. will use this name to get date
            //from parameter map.
            out.printf("<input class=\"form-control\" type=\"text\" name=\"%s\" value=\"\">", BloodDonationLogic.BANK_ID);
            out.println("</div>");
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">Milliliterse:</label>");
            out.printf( "<input class=\"form-control\" type=\"text\" name=\"%s\" value=\"\">", BloodDonationLogic.MILLILITERS);
            out.println("</div>");
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">Blood Group:</label>");
            out.printf("<select class=\"form-select\" name=\"%s\">", BloodDonationLogic.BLOOD_GROUP);
            out.println("<option value=\"A\">");
            out.println("A");
            out.println("</option>");
            out.println("<option value=\"B\">");
            out.println("B");
            out.println("</option>");
            out.println("<option value=\"AB\">");
            out.println("AB");
            out.println("</option>");
            out.println("<option value=\"O\">");
            out.println("O");
            out.println("</option>");
            out.println("</select>");
            out.println("</div>");
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">RHD:</label>");
            out.printf("<select class=\"form-select\" name=\"%s\">", BloodDonationLogic.RHESUS_FACTOR);
            out.println("<option value=\"Positive\">");
            out.println("Positive");
            out.println("</option>");
            out.println("<option value=\"Negative\">");
            out.println("Negative");
            out.println("</option>");
            out.println("</select>");
            out.println("</div>");
            
            out.println("<div class=\"mb-3\">");
            out.println("<label class=\"form-label\">Date:</label>");
            out.printf("<input class=\"form-control\" type=\"datetime-local\" name=\"%s\" value=\"\" step=\"1\">", BloodDonationLogic.CREATED);
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
            //clear the error message if when reload the page
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        BloodDonationLogic bloodDonationLogic = LogicFactory.getFor("BloodDonation");
        BloodBankLogic bloodBankLogic = LogicFactory.getFor("BloodBank");

        try {
            BloodDonation bloodDonation = bloodDonationLogic.createEntity(request.getParameterMap());
            if (request.getParameter(BloodDonationLogic.BANK_ID).isEmpty()) {
                bloodDonationLogic.add(bloodDonation);
            } else {
                int userInputBankID = Integer.parseInt(request.getParameter(BloodDonationLogic.BANK_ID));
                BloodBank bloodBank = bloodBankLogic.getWithId(userInputBankID);
                if (bloodBank != null) {
                    bloodDonation.setBloodBank(bloodBank);
                    bloodDonationLogic.add(bloodDonation);
                } else {
                    errorMessage = "Bank ID: " + userInputBankID + " does not exist in the BloodBank Table.";
                }
            }
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }

        if (request.getParameter("add") != null) {
            //if add button is pressed return the same page
            processRequest(request, response);
        } else if (request.getParameter("view") != null) {
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect("BloodDonationTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Blood Donation Entity";
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
