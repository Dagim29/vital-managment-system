import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;
import java.text.SimpleDateFormat;

@WebServlet("/notice")
public class Notice extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public Notice() {
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
            out.println("<title>Notices - VMS</title>");
            out.println("<link rel='stylesheet' href='styles/main.css'>");
            out.println("<style>");
            out.println(".notice-container { max-width: 1000px; margin: 40px auto; padding: 20px; }");
            out.println(".notice-card { background: #fff; padding: 20px; margin-bottom: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            out.println(".notice-date { color: #666; font-size: 0.9em; margin-bottom: 10px; }");
            out.println(".notice-title { font-size: 1.2em; font-weight: bold; margin-bottom: 10px; color: #333; }");
            out.println(".notice-content { line-height: 1.6; color: #444; }");
            out.println(".notice-priority-high { border-left: 4px solid #dc3545; }");
            out.println(".notice-priority-medium { border-left: 4px solid #ffc107; }");
            out.println(".notice-priority-low { border-left: 4px solid #28a745; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            // Include Header
            RequestDispatcher rdHeader = request.getRequestDispatcher("Header");
            rdHeader.include(request, response);
            
            out.println("<div class='notice-container'>");
            out.println("<h2>System Notices</h2>");
            
            // Database connection
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            
            try {
                String driverName = "com.mysql.cj.jdbc.Driver";
                String dbUrl = "jdbc:mysql://localhost:3306/vms_db";
                String dbUsername = "root";
                String dbPassword = "root";
                
                Class.forName(driverName);
                conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                stmt = conn.createStatement();
                
                // Query to get notices ordered by date (most recent first)
                String sql = "SELECT * FROM notices WHERE active = 1 ORDER BY notice_date DESC";
                rs = stmt.executeQuery(sql);
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                
                while (rs.next()) {
                    String priority = rs.getString("priority");
                    String priorityClass = "notice-priority-" + priority.toLowerCase();
                    
                    out.println("<div class='notice-card " + priorityClass + "'>");
                    out.println("<div class='notice-date'>" + 
                        dateFormat.format(rs.getDate("notice_date")) + "</div>");
                    out.println("<div class='notice-title'>" + rs.getString("title") + "</div>");
                    out.println("<div class='notice-content'>" + rs.getString("content") + "</div>");
                    out.println("</div>");
                }
                
            } catch (Exception e) {
                out.println("<div class='error-message'>Error loading notices: " + 
                    e.getMessage() + "</div>");
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            out.println("</div>"); // End notice-container
            
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