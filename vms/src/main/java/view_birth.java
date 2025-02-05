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

@WebServlet("/view_birth")
public class view_birth extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public view_birth() {
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
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>View Birth Records</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<style>");
            out.println(".search-container { margin: 20px auto; max-width: 600px; padding: 20px; }");
            out.println(".table-container { margin: 20px auto; padding: 20px; overflow-x: auto; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background-color: #cc6600; color: white; }");
            out.println("tr:hover { background-color: #f5f5f5; }");
            out.println(".search-box { width: 100%; padding: 10px; margin-bottom: 10px; }");
            out.println(".photo-cell img { max-width: 100px; height: auto; border-radius: 4px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            // Include Header
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            // Search form
            out.println("<div class='search-container'>");
            out.println("<h2>Search Birth Records</h2>");
            out.println("<form method='post' action='view_birth'>");
            out.println("<input type='text' name='searchTerm' class='search-box' placeholder='Enter Birth ID, Child Name, or Certificate Number'>");
            out.println("<input type='submit' name='search' value='Search Records'>");
            out.println("</form>");
            out.println("</div>");

            // Display records
            displayBirthRecords(request, out);

            // Include Footer
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayBirthRecords(HttpServletRequest request, PrintWriter out) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql;
            String searchTerm = request.getParameter("searchTerm");
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                sql = "SELECT * FROM births WHERE birthId LIKE ? OR childName LIKE ? OR birthCertificateNumber LIKE ? ORDER BY dateOfBirth DESC";
                pstmt = conn.prepareStatement(sql);
                String searchPattern = "%" + searchTerm + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
            } else {
                sql = "SELECT * FROM births ORDER BY dateOfBirth DESC";
                pstmt = conn.prepareStatement(sql);
            }
            
            rs = pstmt.executeQuery();
            
            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Photo</th>");
            out.println("<th>Birth ID</th>");
            out.println("<th>Child Name</th>");
            out.println("<th>Date of Birth</th>");
            out.println("<th>Gender</th>");
            out.println("<th>FatherName</th>");
            out.println("<th>MotherName</th>");
            out.println("<th>Certificate Number</th>");
            out.println("<th>Actions</th>");
            out.println("</tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                
                // Photo cell
                out.println("<td class='photo-cell'>");
                String photoPath = rs.getString("childPhoto");
                if (photoPath != null && !photoPath.isEmpty()) {
                    out.println("<img src='" + photoPath + "' alt='Child Photo'>");
                } else {
                    out.println("No photo");
                }
                out.println("</td>");
                
                out.println("<td>" + rs.getString("birthId") + "</td>");
                out.println("<td>" + rs.getString("childName") + "</td>");
                out.println("<td>" + rs.getString("dateOfBirth") + "</td>");
                out.println("<td>" + rs.getString("gender") + "</td>");
                out.println("<td>" + rs.getString("parent1Name") + "</td>");
                out.println("<td>" + rs.getString("parent2Name") + "</td>");
                out.println("<td>" + rs.getString("birthCertificateNumber") + "</td>");
                out.println("<td><a href='update_birth?searchMarriageId=" + 
                        rs.getString("birthId") + "' class='action-btn'>Update</a></td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                out.println("<div class='error-message'>Error closing resources: " + e.getMessage() + "</div>");
            }
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