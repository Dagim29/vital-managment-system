import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;

@WebServlet("/ViewUser")
public class ViewUser extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public ViewUser() {
        super();
    }
    
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Session Security Headers
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);
        
        // Session Validation
        HttpSession session = request.getSession(true);
        if (session.getAttribute("uname") == null || 
            !session.getAttribute("utype").equals("Admin")) {
            response.sendRedirect("main");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>View Users</title>");
            out.println("<link rel='stylesheet' href='styles/main.css'>");
            out.println("<style>");
            out.println(".container { padding: 20px; max-width: 1200px; margin: 0 auto; }");
            out.println(".data-table { width: 100%; border-collapse: collapse; margin: 20px 0; box-shadow: 0 1px 3px rgba(0,0,0,0.2); }");
            out.println(".data-table th, .data-table td { padding: 12px; text-align: left; border: 1px solid #ddd; }");
            out.println(".data-table th { background: #f5f5f5; color: #333; font-weight: bold; }");
            out.println(".data-table tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println(".data-table tr:hover { background: #f5f5f5; }");
            out.println(".action-buttons { margin: 20px 0; }");
            out.println(".button { display: inline-block; padding: 8px 16px; background: #4CAF50; color: white; text-decoration: none; border-radius: 4px; }");
            out.println(".button.small { padding: 6px 12px; font-size: 0.9em; margin: 0 5px; }");
            out.println(".button:hover { background: #45a049; }");
            out.println(".status-active { color: #4CAF50; font-weight: bold; }");
            out.println(".status-inactive { color: #f44336; font-weight: bold; }");
            out.println(".success-message { padding: 15px; background: #dff0d8; border: 1px solid #d6e9c6; color: #3c763d; margin: 10px 0; border-radius: 4px; }");
            out.println(".error-message { padding: 15px; background: #f2dede; border: 1px solid #ebccd1; color: #a94442; margin: 10px 0; border-radius: 4px; }");
            out.println(".welcome-bar { background: #f8f9fa; padding: 15px; margin-bottom: 20px; border-radius: 4px; display: flex; justify-content: space-between; align-items: center; }");
            out.println(".welcome-bar h2 { margin: 0; color: #333; }");
            out.println(".welcome-text { color: #666; }");
            out.println(".logout-btn { padding: 5px 10px; background: #dc3545; color: white; border: none; border-radius: 3px; cursor: pointer; }");
            out.println(".logout-btn:hover { background: #c82333; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            out.println("<div class='container'>");
            
            // Welcome bar with user info
            out.println("<div class='welcome-bar'>");
            out.println("<h2>User Management</h2>");
            out.println("<div class='welcome-text'>Welcome, " + session.getAttribute("uname") + 
                       " <a href='Logout'><button class='logout-btn'>Logout</button></a></div>");
            out.println("</div>");
            
            // Display messages
            String message = request.getParameter("message");
            String error = request.getParameter("error");
            if (message != null) {
                out.println("<div class='success-message'>" + message + "</div>");
            }
            if (error != null) {
                out.println("<div class='error-message'>" + error + "</div>");
            }
            
            // Add New User button
            out.println("<div class='action-buttons'>");
            out.println("<a href='RegisterUser' class='button'>Add New User</a>");
            out.println("</div>");
            
            // Database connection parameters
            String driverName = "com.mysql.cj.jdbc.Driver";
            String dbUrl = "jdbc:mysql://localhost:3306/vms_db";
            String dbUsername = "root";
            String dbPassword = "root";
            
            try {
                Class.forName(driverName);
                conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM users ORDER BY username");
                
                // Users Table
                out.println("<table class='data-table'>");
                out.println("<thead>");
                out.println("<tr>");
                out.println("<th>Username</th>");
                out.println("<th>Role</th>");
                out.println("<th>Status</th>");
                out.println("<th>Actions</th>");
                out.println("</tr>");
                out.println("</thead>");
                out.println("<tbody>");
                
                while (rs.next()) {
                    String currentUsername = rs.getString("username");
                    String currentRole = rs.getString("role");
                    String status = rs.getString("status");
                    
                    out.println("<tr>");
                    out.println("<td>" + currentUsername + "</td>");
                    out.println("<td>" + currentRole + "</td>");
                    out.println("<td class='status-" + status.toLowerCase() + "'>" + status + "</td>");
                    out.println("<td>");
                    
                    // Don't show edit/deactivate buttons for the current logged-in admin
                    if (!currentUsername.equals(session.getAttribute("uname"))) {
                        out.println("<a href='UpdateUser?username=" + currentUsername + 
                            "' class='button small'>Edit</a>");
                        
                        // Only show activate/deactivate for non-admin users
                        if (!"Admin".equals(currentRole)) {
                            String toggleStatus = "Active".equals(status) ? "Deactivate" : "Activate";
                            String buttonColor = "Active".equals(status) ? 
                                "background: #dc3545;" : "background: #28a745;";
                            
                            out.println("<a href='ToggleUserStatus?username=" + currentUsername + 
                                "' class='button small' style='" + buttonColor + "'>" + 
                                toggleStatus + "</a>");
                        }
                    } else {
                        out.println("<em>Current User</em>");
                    }
                    
                    out.println("</td>");
                    out.println("</tr>");
                }
                
                out.println("</tbody>");
                out.println("</table>");
                
            } catch (Exception e) {
                out.println("<div class='error-message'>Error loading users: " + e.getMessage() + "</div>");
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    out.println("<div class='error-message'>Error closing database connections: " + 
                        e.getMessage() + "</div>");
                }
            }
            
            out.println("</div>"); // End container
            
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace();
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