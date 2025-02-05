import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Footer")
public class Footer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Footer HTML
        out.println("<link rel='stylesheet' type='text/css' href='styles/hf.css'>");
        out.println("<footer class='footer-container'>");
        out.println("<div class='footer-content'>");
        
        // Quick Links
        out.println("<div class='footer-section'>");
        out.println("<h3>Quick Links</h3>");
        out.println("<ul>");
        out.println("<li><a href='main'>Home</a></li>");
        out.println("<li><a href='#'>About Us</a></li>");
        out.println("<li><a href='contactus'>Contact</a></li>");
        out.println("<li><a href='#'>Help</a></li>");
        out.println("</ul>");
        out.println("</div>");
        
        // Contact Information
        out.println("<div class='footer-section'>");
        out.println("<h3>Contact Us</h3>");
        out.println("<ul>");
        out.println("<li>Email: vms@gmail.com</li>");
        out.println("<li>Phone: +251 95 393-7969 </li>");
        out.println("<li>Address: Ethiopia,Debremarkos</li>");
        out.println("</ul>");
        out.println("</div>");
        
        // Footer Bottom
        out.println("<div class='footer-bottom'>");
        out.println("<p>&copy; " + currentYear + " Vital Management System. All rights reserved.</p>");
        out.println("</div>");
        
        out.println("</div>");
        out.println("</footer>");
    }
}