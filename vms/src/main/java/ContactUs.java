import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/contact_us")
public class ContactUs extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public ContactUs() {
        super();
    }
    
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Contact Us - VMS</title>");
            out.println("<link rel='stylesheet' href='styles/main.css'>");
            out.println("<style>");
            out.println(".contact-container { max-width: 800px; margin: 40px auto; padding: 20px; }");
            out.println(".contact-card { background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            out.println(".contact-info { margin: 20px 0; }");
            out.println(".contact-item { margin: 15px 0; display: flex; align-items: center; }");
            out.println(".contact-label { font-weight: bold; width: 120px; }");
            out.println(".contact-value { flex: 1; }");
            out.println(".map-container { margin-top: 30px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            // Include Header
            RequestDispatcher rdHeader = request.getRequestDispatcher("Header");
            rdHeader.include(request, response);
            
            out.println("<div class='contact-container'>");
            out.println("<div class='contact-card'>");
            out.println("<h2>Contact Information</h2>");
            
            out.println("<div class='contact-info'>");
            out.println("<div class='contact-item'>");
            out.println("<span class='contact-label'>Address:</span>");
            out.println("<span class='contact-value'>Debremarkos,Ethiopia</span>");
            out.println("</div>");
            
            out.println("<div class='contact-item'>");
            out.println("<span class='contact-label'>Phone:</span>");
            out.println("<span class='contact-value'>+251 94 748-2468  </span>");
            out.println("</div>");
            
            out.println("<div class='contact-item'>");
            out.println("<span class='contact-label'>Email:</span>");
            out.println("<span class='contact-value'>vms@gmail.com</span>");
            out.println("</div>");
            
            out.println("<div class='contact-item'>");
            out.println("<span class='contact-label'>Hours:</span>");
            out.println("<span class='contact-value'>Monday - Friday: 9:00 AM - 5:00 PM</span>");
            out.println("</div>");
            
            out.println("<div class='contact-item'>");
            out.println("<span class='contact-label'>Emergency:</span>");
            out.println("<span class='contact-value'>24/7 Support: +251 95 393-7969 </span>");
            out.println("</div>");
            out.println("</div>");
            
            out.println("</div>"); // End contact-card
            out.println("</div>"); // End contact-container
            
            // Include Footer
            RequestDispatcher rdFooter = request.getRequestDispatcher("Footer");
            rdFooter.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } finally {
            out.close();
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        service(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        service(request, response);
    }
}