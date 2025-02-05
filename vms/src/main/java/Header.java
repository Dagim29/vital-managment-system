import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/Header")
public class Header extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public Header() {
        super();
    }
    
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        StringBuilder html = new StringBuilder();
        html.append("<link rel='stylesheet' type='text/css' href='styles/hf.css'>");
        
        html.append("<header class='header'>");
        html.append("<div class='header-content'>");
        
        // Title in center
        html.append("<div class='title-container'>");
        html.append("<h1 class='title'>Vital Management System</h1>");
        html.append("</div>");
        
        // Navigation links on right
        html.append("<nav class='nav-container'>");
        html.append("<a href='contact_us' class='nav-link'>Contact Us</a>");
        html.append("<a href='notice' class='nav-link'>Notice</a>");
        html.append("</nav>");
        
        html.append("</div>");
        html.append("</header>");
        
        out.println(html.toString());
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