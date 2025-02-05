import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Admin_home")
public class Admin_home extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public Admin_home() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);
        
        // Session validation
        HttpSession session = request.getSession(true);
        if (session.getAttribute("uname") == null || session.getAttribute("utype") == null) {
            response.sendRedirect("main");
            return;
        }
        if (!session.getAttribute("utype").equals("Admin")) {
            response.sendRedirect("main");
            return;
        }

        String user_name = (String)session.getAttribute("uname");
        String user_type = (String)session.getAttribute("utype");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Admin Dashboard</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<style>");
            out.println(".dashboard-container { padding: 20px; }");
            out.println(".stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 20px 0; }");
            out.println(".stat-card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);text-decoration:none; }");
            out.println(".stat-card h3 { margin: 0 0 10px 0; color: #cc6600;text-decoration:none; }");
            out.println(".stat-value { font-size: 24px; font-weight: bold; }");
            out.println(".admin-actions { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 20px 0; }");
            out.println(".action-button { background: #cc6600; color: white; padding: 15px; text-align: center; border-radius: 8px; text-decoration: none; }");
            out.println(".action-button:hover { background: #b35900; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            // Include Header
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            out.println("<div class='dashboard-container'>");
            
            // Welcome message
            out.println("<div class='welcome-bar'>");
            out.println("<h2>Admin Dashboard</h2>");
            out.println("<p>Welcome, " + user_name + " | <a href='Logout'>Logout</a></p>");
            out.println("</div>");
            
            // Statistics Section
            out.println("<div class='stats-grid'>");
            displayStatistics(out);
            out.println("</div>");
            
            out.println("<div class='manage-users-section'>");
            out.println("<h4>Manage Users</h4>");
            out.println("<div class='user-management-links'>");
            out.println("<a href='RegisterUser' class='action-button'>Register New User</a>");
            out.println("<a href='UpdateUser' class='action-button'>Update User</a>");
            out.println("<a href='ViewUser' class='action-button'>View Users</a>");
            out.println("</div>");
            out.println("</div>");

            
            // Recent Activity Section
            out.println("<h3>Recent System Activity</h3>");
            displayRecentActivity(out);
            
            out.println("</div>"); // End dashboard-container
            out.println("<style>");
         // ... existing styles ...

         out.println(".manage-users-section { background: #f5f5f5; padding: 20px; border-radius: 8px; }");
         out.println(".manage-users-section h4 { color: #333; margin: 0 0 15px 0; }");
         out.println(".user-management-links { display: grid; grid-template-columns: 1fr; gap: 10px; }");
         out.println(".user-management-links .action-button { background: #004d99; margin: 5px 0; }");
         out.println(".user-management-links .action-button:hover { background: #003366; }");
         out.println("</style>");
            
            // Include Footer
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    private void displayStatistics(PrintWriter out) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            // Birth Records Count
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM births");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            out.println("<div class='stat-card'>");
            out.println("<a href='view_birth'><h3>Birth Records</h3></a>");
            out.println("<div class='stat-value'>" + rs.getInt(1) + "</div>");
            out.println("</div>");
            
            // Marriage Records Count
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM marriages");
            rs = pstmt.executeQuery();
            rs.next();
            out.println("<div class='stat-card'>");
            out.println("<a href='view_marriage'><h3>Marriage Records</h3></a>");
            out.println("<div class='stat-value'>" + rs.getInt(1) + "</div>");
            out.println("</div>");
            
            // Divorce Records Count
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM divorces");
            rs = pstmt.executeQuery();
            rs.next();
            out.println("<div class='stat-card'>");
            out.println("<a href='view_divorce'><h3>Divorce Records</h3></a>");
            out.println("<div class='stat-value'>" + rs.getInt(1) + "</div>");
            out.println("</div>");
            
            // Death Records Count
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM deaths");
            rs = pstmt.executeQuery();
            rs.next();
            out.println("<div class='stat-card'>");
            out.println("<a href='view_death'><h3>Death Records</h3></a>");
            out.println("<div class='stat-value'>" + rs.getInt(1) + "</div>");
            out.println("</div>");
            
            // Active Users Count
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE status='Active'");
            rs = pstmt.executeQuery();
            rs.next();
            out.println("<div class='stat-card'>");
            out.println("<a href='ViewUser'><h3>Active Users</h3></a>");
            out.println("<div class='stat-value'>" + rs.getInt(1) + "</div>");
            out.println("</div>");
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error loading statistics: " + e.getMessage() + "</div>");
        }
    }

    private void displayRecentActivity(PrintWriter out) {
        out.println("<div class='table-container'>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>Date/Time</th>");
        out.println("<th>User</th>");
        out.println("<th>Action</th>");
        out.println("<th>Details</th>");
        out.println("</tr>");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql = "SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 10";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("timestamp") + "</td>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("action") + "</td>");
                out.println("<td>" + rs.getString("details") + "</td>");
                out.println("</tr>");
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            out.println("<tr><td colspan='4'>Error loading activity logs: " + e.getMessage() + "</td></tr>");
        }
        
        out.println("</table>");
        out.println("</div>");
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