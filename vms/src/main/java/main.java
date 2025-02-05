import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/main")
public class main extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public main() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            out.print("<!DOCTYPE html>");
            out.print("<html lang='en'>");
            out.print("<head>");
            out.print("<meta charset='UTF-8'>");
            out.print("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.print("<title>Vital Management System</title>");
            out.print("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.print("</head>");
            out.print("<body>");

            // Include Header
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);

            String wrongusername = request.getParameter("wrongusername");
            String wrongpassword = request.getParameter("wrongpassword");
            String logout = request.getParameter("logout");

            out.print("<div class='login-container'>");
            out.print("<div class='login-form'>");
            out.print("<h2>Vital Management System</h2>");
            out.print("<h3>Please log in to continue</h3>");
            out.print("<form action='ValidateUser' method='post'>");
            out.print("<label for='username'>Username</label>");
            out.print("<input type='text' id='username' name='username' placeholder='Enter your username' required>");
            out.print("<label for='password'>Password</label>");
            out.print("<input type='password' id='password' name='password' placeholder='Enter your password' required>");
            out.print("<button type='submit'>Login</button>");

            if (wrongusername != null) {
                out.print("<p class='error'>Wrong username, try again, or your status may be blocked by the system admin</p>");
            }
            if (wrongpassword != null) {
                out.print("<p class='error'>Wrong password, try again</p>");
            }
            if (logout != null) {
                out.print("<p class='info'>You have logged out</p>");
            }

            out.print("</form>");
            out.print("</div>");
            out.print("</div>");

            // Include Footer
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);

            out.print("</body></html>");
        } catch (Exception e) {
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        service(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        service(request, response);
    }
}
