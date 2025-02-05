import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/vms_officer_home")
public class vms_officer_home extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public vms_officer_home() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //For session management
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-store");
        response.setHeader("Expires","0");
        response.setDateHeader("Expires",-1);
        HttpSession session=request.getSession(true);
        if (session.getAttribute("uname")==null||session.getAttribute("utype")==null)
        {
            response.sendRedirect("main");
            return;
        }
        if(!session.getAttribute("utype").equals("VMS Officer")) {
            response.sendRedirect("main"); //redirect to Home page
        }
        String user_name=null,user_type=null;
        session=request.getSession();
        if(session!=null) {
            user_name=(String)session.getAttribute("uname");
            user_type=(String)session.getAttribute("utype");
        }

        PrintWriter out = response.getWriter();
        try {
            out.print("<!DOCTYPE html>");
            out.print("<html lang='en'>");
            out.print("<head>");
            out.print("<meta charset='UTF-8'>");
            out.print("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.print("<title>VMS Officer Dashboard</title>");
            out.print("<link rel='stylesheet' href='styles/vm.css'>");
            out.print("</head>");
            out.print("<body>");
         // Include Header
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);

            out.print("<div class='dashboard-container'>");
            
            // Welcome bar
            out.print("<div class='welcome-bar'>");
            out.print("<h2>VMS Officer Dashboard</h2>");
            out.print("<div class='welcome-text'>Welcome " + user_name + 
                    " <a href='Logout'><button class='logout-btn'>Logout</button></a></div>");
            out.print("</div>");

            // Cards grid
            out.print("<div class='cards-grid'>");
            
            // Birth Card
            out.print("<div class='card' style='background-image: url(\"upload_files/birth.jpg\")'>");
            out.print("<div class='card-content'>");
            out.print("<div class='card-header'>");
            out.print("<h3>Birth Records</h3>");
            out.print("<div style='text-align: center;'>");
            out.print("<a href='#' class='main-btn'>Birth</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("<div class='card-options'>");
            out.print("<a href='register_birth'>Register Birth</a>");
            out.print("<a href='update_birth'>Update Birth Record</a>");
            out.print("<a href='view_birth'>View Birth Records</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("</div>");

            // Marriage Card
            out.print("<div class='card' style='background-image: url(\"upload_files/marriage.jpg\")'>");
            out.print("<div class='card-content'>");
            out.print("<div class='card-header'>");
            out.print("<h3>Marriage Records</h3>");
            out.print("<div style='text-align: center;'>");
            out.print("<a href='#' class='main-btn'>Marriage</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("<div class='card-options'>");
            out.print("<a href='register_marriage'>Register Marriage</a>");
            out.print("<a href='update_marriage'>Update Marriage Record</a>");
            out.print("<a href='view_marriage'>View Marriage Records</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("</div>");

            // Divorce Card
            out.print("<div class='card' style='background-image: url(\"upload_files/divorce.jpg\")'>");
            out.print("<div class='card-content'>");
            out.print("<div class='card-header'>");
            out.print("<h3>Divorce Records</h3>");
            out.print("<div style='text-align: center;'>");
            out.print("<a href='#' class='main-btn'>Divorce</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("<div class='card-options'>");
            out.print("<a href='register_divorce'>Register Divorce</a>");
            out.print("<a href='update_divorce'>Update Divorce Record</a>");
            out.print("<a href='view_divorce'>View Divorce Records</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("</div>");

            // Death Card
            out.print("<div class='card' style='background-image: url(\"upload_files/death.jpg\")'>");
            out.print("<div class='card-content'>");
            out.print("<div class='card-header'>");
            out.print("<h3>Death Records</h3>");
            out.print("<div style='text-align: center;'>");
            out.print("<a href='#' class='main-btn'>Death</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("<div class='card-options'>");
            out.print("<a href='register_death'>Register Death</a>");
            out.print("<a href='update_death'>Update Death Record</a>");
            out.print("<a href='view_death'>View Death Records</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("</div>");
            
         // Certificate Generation Card
            out.print("<div class='card' style='background-image: url(\"upload_files/certificate.jpg\")'>");
            out.print("<div class='card-content'>");
            out.print("<div class='card-header'>");
            out.print("<a href='#' class='main-btn'>Generate Certificates</a>");
            out.print("<div class='card-description'>Generate official certificates for vital records</div>");
            out.print("</div>");
            out.print("<div class='card-options'>");
            out.print("<a href='generate_birth_certificate'>Birth Certificate</a>");
            out.print("<a href='generate_marriage_certificate'>Marriage Certificate</a>");
            out.print("<a href='generate_divorce_certificate'>Divorce Certificate</a>");
            out.print("<a href='generate_death_certificate'>Death Certificate</a>");
            out.print("</div>");
            out.print("</div>");
            out.print("</div>");

            out.print("</div>"); // End cards-grid
            out.print("</div>"); // End dashboard-container
         // Include Footer
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            out.print("</body></html>");
        } catch(Exception e) {
            out.print(e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}