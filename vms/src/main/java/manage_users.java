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

@WebServlet("/manage_users")
public class manage_users extends HttpServlet {
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
            out.println("<title>User Management</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<style>");
            out.println(".user-grid { display: grid; gap: 20px; padding: 20px; }");
            out.println(".user-actions { display: flex; gap: 10px; }");
            out.println(".status-active { color: green; }");
            out.println(".status-blocked { color: red; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);

            out.println("<div class='container'>");
            out.println("<h2>User Management</h2>");

            // Handle user actions
            String action = request.getParameter("action");
            if (action != null) {
                handleUserAction(request, response, out);
            }

            // Add New User Form
            out.println("<div class='form-container'>");
            out.println("<h3>Add New User</h3>");
            out.println("<form method='post' action='manage_users'>");
            out.println("<input type='hidden' name='action' value='add'>");
            out.println("<div class='form-group'>");
            out.println("<label>Username:</label>");
            out.println("<input type='text' name='username' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label>Password:</label>");
            out.println("<input type='password' name='password' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label>Role:</label>");
            out.println("<select name='role' required>");
            out.println("<option value='VMS Officer'>VMS Officer</option>");
            out.println("<option value='Admin'>Admin</option>");
            out.println("</select>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<input type='submit' value='Add User'>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div>");

            // Display Users
            displayUsers(out);

            out.println("</div>");

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);

            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayUsers(PrintWriter out) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY username");

            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<tr><th>Username</th><th>Role</th><th>Status</th><th>Actions</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("role") + "</td>");
                out.println("<td class='status-" + rs.getString("status").toLowerCase() + "'>" + 
                           rs.getString("status") + "</td>");
                out.println("<td class='user-actions'>");
                
                // Toggle status button
                String newStatus = rs.getString("status").equals("Active") ? "Block" : "Activate";
                out.println("<form method='post' action='manage_users' style='display:inline;'>");
                out.println("<input type='hidden' name='action' value='toggle_status'>");
                out.println("<input type='hidden' name='username' value='" + rs.getString("username") + "'>");
                out.println("<input type='submit' value='" + newStatus + "'>");
                out.println("</form>");

                // Reset password button
                out.println("<form method='post' action='manage_users' style='display:inline;'>");
                out.println("<input type='hidden' name='action' value='reset_password'>");
                out.println("<input type='hidden' name='username' value='" + rs.getString("username") + "'>");
                out.println("<input type='submit' value='Reset Password'>");
                out.println("</form>");
                
                out.println("</td></tr>");
            }
            out.println("</table>");
            out.println("</div>");

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            out.println("Error displaying users: " + e.getMessage());
        }
    }

    private void handleUserAction(HttpServletRequest request, HttpServletResponse response, 
                                PrintWriter out) {
        String action = request.getParameter("action");
        String username = request.getParameter("username");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");

            switch (action) {
                case "add":
                    String password = request.getParameter("password");
                    String role = request.getParameter("role");
                    addUser(conn, username, password, role, out);
                    break;
                
                case "toggle_status":
                    toggleUserStatus(conn, username, out);
                    break;
                
                case "reset_password":
                    resetPassword(conn, username, out);
                    break;
            }

            conn.close();
        } catch (Exception e) {
            out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
        }
    }

    private void addUser(Connection conn, String username, String password, String role, 
                        PrintWriter out) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, 'Active')";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password); // In production, hash the password
        pstmt.setString(3, role);
        pstmt.executeUpdate();
        out.println("<div class='success-message'>User added successfully</div>");
    }

    private void toggleUserStatus(Connection conn, String username, PrintWriter out) 
            throws SQLException {
        String sql = "UPDATE users SET status = CASE WHEN status = 'Active' THEN 'Blocked' " +
                    "ELSE 'Active' END WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.executeUpdate();
        out.println("<div class='success-message'>User status updated</div>");
    }

    private void resetPassword(Connection conn, String username, PrintWriter out) 
            throws SQLException {
        String defaultPassword = "password123"; // In production, generate a random password
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, defaultPassword);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        out.println("<div class='success-message'>Password reset to: " + defaultPassword + "</div>");
    }
}