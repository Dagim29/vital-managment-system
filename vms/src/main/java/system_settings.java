import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/system_settings")
public class system_settings extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);
        
        HttpSession session = request.getSession(true);
        if (session.getAttribute("uname") == null || 
            !session.getAttribute("utype").equals("Admin")) {
            response.sendRedirect("main");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>System Settings</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("</head>");
            out.println("<body>");

            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);

            out.println("<div class='container'>");
            out.println("<h2>System Settings</h2>");

            // Handle settings update
            if (request.getParameter("action") != null && 
                request.getParameter("action").equals("update")) {
                updateSettings(request, out);
            }

            // Display settings form
            displaySettingsForm(out);

            out.println("</div>");

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);

            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displaySettingsForm(PrintWriter out) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM system_settings");

            out.println("<div class='form-container'>");
            out.println("<form method='post' action='system_settings'>");
            out.println("<input type='hidden' name='action' value='update'>");

            while (rs.next()) {
                out.println("<div class='form-group'>");
                out.println("<label>" + rs.getString("setting_name") + ":</label>");
                out.println("<input type='text' name='setting_" + rs.getString("setting_id") + 
                           "' value='" + rs.getString("setting_value") + "'>");
                out.println("</div>");
            }

            out.println("<div class='form-group'>");
            out.println("<input type='submit' value='Save Settings'>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div>");

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            out.println("Error loading settings: " + e.getMessage());
        }
    }

    private void updateSettings(HttpServletRequest request, PrintWriter out) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT setting_id FROM system_settings");

            while (rs.next()) {
                String settingId = rs.getString("setting_id");
                String newValue = request.getParameter("setting_" + settingId);
                
                if (newValue != null) {
                    PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE system_settings SET setting_value = ? WHERE setting_id = ?");
                    pstmt.setString(1, newValue);
                    pstmt.setString(2, settingId);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }

            out.println("<div class='success-message'>Settings updated successfully</div>");

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            out.println("<div class='error-message'>Error updating settings: " + 
                       e.getMessage() + "</div>");
        }
    }
}