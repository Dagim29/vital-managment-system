import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ToggleUserStatus")
public class ToggleUserStatus extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public ToggleUserStatus() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
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

        String username = request.getParameter("username");
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // Database connection parameters
            String driverName = "com.mysql.cj.jdbc.Driver";
            String dbUrl = "jdbc:mysql://localhost:3306/vms_db";
            String dbUsername = "root";
            String dbPassword = "root";
            
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            
            // First, get current status
            String checkSql = "SELECT status, role FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String currentStatus = rs.getString("status");
                String role = rs.getString("role");
                
                // Don't allow status change for admin users
                if ("Admin".equals(role)) {
                    response.sendRedirect("ViewUser?error=Cannot modify admin status");
                    return;
                }
                
                // Toggle the status
                String newStatus = "Active".equals(currentStatus) ? "Inactive" : "Active";
                
                // Update the status
                String updateSql = "UPDATE users SET status = ? WHERE username = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, newStatus);
                pstmt.setString(2, username);
                pstmt.executeUpdate();
                
                response.sendRedirect("ViewUser?message=User status updated successfully");
            } else {
                response.sendRedirect("ViewUser?error=User not found");
            }
            
        } catch (Exception e) {
            response.sendRedirect("ViewUser?error=" + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}