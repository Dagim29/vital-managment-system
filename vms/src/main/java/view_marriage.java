import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/view_marriage")
public class view_marriage extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public view_marriage() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);
        
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
            out.println("<title>View Marriage Records</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<style>");
            out.println(".search-container { margin: 20px auto; max-width: 600px; padding: 20px; }");
            out.println(".table-container { margin: 20px auto; padding: 20px; overflow-x: auto; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background-color: #cc6600; color: white; }");
            out.println("tr:hover { background-color: #f5f5f5; }");
            out.println(".search-box { width: 100%; padding: 10px; margin-bottom: 10px; }");
            out.println(".photo-cell { width: 100px; }");
            out.println(".photo-cell img { width: 100px; height: 100px; object-fit: cover; border-radius: 4px; }");
            out.println(".photo-container { display: flex; gap: 10px; }");
            out.println(".photo-box { text-align: center; }");
            out.println(".photo-label { font-size: 0.8em; color: #666; margin-top: 4px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            out.println("<div class='search-container'>");
            out.println("<h2>Search Marriage Records</h2>");
            out.println("<form method='post' action='view_marriage'>");
            out.println("<input type='text' name='searchTerm' class='search-box' " +
                       "placeholder='Enter Marriage ID, Names, or Certificate Number'>");
            out.println("<input type='submit' name='search' value='Search Records'>");
            out.println("</form>");
            out.println("</div>");

            displayMarriageRecords(request, out);

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayMarriageRecords(HttpServletRequest request, PrintWriter out) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String searchTerm = request.getParameter("searchTerm");
            String sql;
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                sql = "SELECT * FROM marriages WHERE marriageId LIKE ? OR brideName LIKE ? " +
                      "OR groomName LIKE ? OR marriageCertificateNumber LIKE ? " +
                      "ORDER BY dateOfMarriage DESC";
                pstmt = conn.prepareStatement(sql);
                String searchPattern = "%" + searchTerm.trim() + "%";
                for(int i = 1; i <= 4; i++) {
                    pstmt.setString(i, searchPattern);
                }
            } else {
                sql = "SELECT * FROM marriages ORDER BY dateOfMarriage DESC";
                pstmt = conn.prepareStatement(sql);
            }
            
            rs = pstmt.executeQuery();
            
            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Photos</th>");
            out.println("<th>Marriage ID</th>");
            out.println("<th>Bride & Groom</th>");
            out.println("<th>Date</th>");
            out.println("<th>Place</th>");
            out.println("<th>Witnesses</th>");
            out.println("<th>Certificate No.</th>");
            out.println("<th>Actions</th>");
            out.println("</tr>");

            while (rs.next()) {
                out.println("<tr>");
                
                // Photos cell
                out.println("<td class='photo-cell'>");
                out.println("<div class='photo-container'>");
                
                // Bride photo
                out.println("<div class='photo-box'>");
                String bridePhoto = rs.getString("bridePhoto");
                if(bridePhoto != null && !bridePhoto.isEmpty()) {
                    out.println("<img src='" + bridePhoto + "' alt='Bride'>");
                } else {
                    out.println("<div class='no-photo'>No Photo</div>");
                }
                out.println("<div class='photo-label'>Bride</div>");
                out.println("</div>");
                
                // Groom photo
                out.println("<div class='photo-box'>");
                String groomPhoto = rs.getString("groomPhoto");
                if(groomPhoto != null && !groomPhoto.isEmpty()) {
                    out.println("<img src='" + groomPhoto + "' alt='Groom'>");
                } else {
                    out.println("<div class='no-photo'>No Photo</div>");
                }
                out.println("<div class='photo-label'>Groom</div>");
                out.println("</div>");
                
                out.println("</div>");
                out.println("</td>");
                
                out.println("<td>" + rs.getString("marriageId") + "</td>");
                out.println("<td>Bride: " + rs.getString("brideName") + "<br>Groom: " + 
                          rs.getString("groomName") + "</td>");
                out.println("<td>" + rs.getString("dateOfMarriage") + "</td>");
                out.println("<td>" + rs.getString("placeOfMarriage") + "</td>");
                out.println("<td>1. " + rs.getString("witness1Name") + "<br>2. " + 
                          rs.getString("witness2Name") + "</td>");
                out.println("<td>" + rs.getString("marriageCertificateNumber") + "</td>");
                out.println("<td><a href='update_marriage?searchMarriageId=" + 
                          rs.getString("marriageId") + "' class='action-btn'>Update</a></td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
            e.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
            } catch(SQLException e) {
                e.printStackTrace();
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