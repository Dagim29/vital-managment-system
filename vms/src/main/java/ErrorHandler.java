import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/ErrorHandler")
public class ErrorHandler extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    private void processError(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Error Page</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("</head>");
            out.println("<body>");
            
            // Include Header
            RequestDispatcher headerDispatcher = request.getRequestDispatcher("Header");
            headerDispatcher.include(request, response);

            out.println("<div class='error-container'>");
            out.println("<div class='error-content'>");
            
            // Get the error message
            String error = (String) request.getAttribute("error");
            if (error != null) {
                out.println("<h2>Error Details</h2>");
                out.println("<p class='error'>" + error + "</p>");
            } else {
                out.println("<h2>Unknown Error Occurred</h2>");
                out.println("<p class='error'>Please try again later.</p>");
            }

            out.println("<div class='error-actions'>");
            out.println("<a href='UpdateUser' class='button'>Return to Update User</a>");
            out.println("<a href='main' class='button'>Go to Home</a>");
            out.println("</div>");
            
            out.println("</div>");
            out.println("</div>");

            // Include Footer
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
        }
    }
}