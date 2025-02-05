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

@WebServlet("/view_activity_logs")
public class view_activity_logs extends HttpServlet {
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
            out.println("<title>Activity Logs</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("</head>");
            out.println("<body>");

            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);

            out.println("<div class='container'>");
            out.println("<h2>System Activity Logs</h2>");

            // Search form
            out.println("<div class='search-container'>");
            out.println("<form method='get'>");
            out.println("<div class='form-group'>");
            out.println("<label>Date Range:</label>");
            out.println("<input type='date' name='start_date'>");
            out.println("<input type='date' name='end_date'>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label>User:</label>");
            out.println("<input type='text' name='username'>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<input type='submit' value='Search'>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div>");

            // Display logs
            displayLogs(request, out);

            out.println("</div>");

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);

            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayLogs(HttpServletRequest request, PrintWriter out) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");

            StringBuilder sql = new StringBuilder(
                "SELECT * FROM activity_logs WHERE 1=1");
            
            // Add filters
            String startDate = request.getParameter("start_date");
            String endDate = request.getParameter("end_date");
            String username = request.getParameter("username");

            if (startDate != null && !startDate.isEmpty()) {
                sql.append(" AND DATE(timestamp) >= '").append(startDate).append("'");
            }
            if (endDate != null && !endDate.isEmpty()) {
                sql.append(" AND DATE(timestamp) <= '").append(endDate).append("'");
            }
            if (username != null && !username.isEmpty()) {
                sql.append(" AND username LIKE '%").append(username).append("%'");
            }

            sql.append(" ORDER BY timestamp DESC");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());

            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Timestamp</th>");
            out.println("<th>Username</th>");
            out.println("<th>Action</th>");
            out.println("<th>Details</th>");
            out.println("<th>IP Address</th>");
            out.println("</tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getTimestamp("timestamp") + "</td>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("action") + "</td>");
                out.println("<td>" + rs.getString("details") + "</td>");
                out.println("<td>" + rs.getString("ip_address") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</div>");

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            out.println("Error displaying logs: " + e.getMessage());
        }
    }
}