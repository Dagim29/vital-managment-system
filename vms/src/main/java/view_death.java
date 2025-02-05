import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/view_death")
public class view_death extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public view_death() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Expires", "0");
            response.setDateHeader("Expires", -1);
            
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("uname") == null || 
                session.getAttribute("utype") == null || 
                !session.getAttribute("utype").equals("VMS Officer")) {
                response.sendRedirect("main");
                return;
            }
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>View Death Records</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<style>");
            out.println(".record-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; padding: 20px; }");
            out.println(".record-card { background: #fff; border-radius: 8px; padding: 15px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            out.println(".photo-container { margin-bottom: 15px; text-align: center; }");
            out.println(".photo-container img { width: 200px; height: 200px; object-fit: cover; border-radius: 4px; }");
            out.println(".record-details { margin: 10px 0; }");
            out.println(".record-details p { margin: 5px 0; color: #666; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            out.println("<div class='container'>");
            out.println("<h2>Death Records</h2>");
            
            // Search form
            out.println("<div class='search-container'>");
            out.println("<form method='post' action='view_death'>");
            out.println("<input type='text' name='searchTerm' class='search-box' placeholder='Enter Death ID, Name, or Certificate Number'>");
            out.println("<input type='submit' value='Search'>");
            out.println("</form>");
            out.println("</div>");
            
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vms_db", "root", "root");
                
                String sql;
                String searchTerm = request.getParameter("searchTerm");
                
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    sql = "SELECT * FROM deaths WHERE deathId LIKE ? OR deceasedName LIKE ? " +
                          "OR deathCertificateNumber LIKE ? ORDER BY dateOfDeath DESC";
                    pstmt = conn.prepareStatement(sql);
                    String searchPattern = "%" + searchTerm + "%";
                    for(int i = 1; i <= 3; i++) {
                        pstmt.setString(i, searchPattern);
                    }
                } else {
                    sql = "SELECT * FROM deaths ORDER BY dateOfDeath DESC";
                    pstmt = conn.prepareStatement(sql);
                }
                
                rs = pstmt.executeQuery();
                
                out.println("<div class='record-grid'>");
                
                while(rs.next()) {
                    out.println("<div class='record-card'>");
                    
                    // Photo section
                    out.println("<div class='photo-container'>");
                    String photoPath = rs.getString("deceasedPhoto");
                    if(photoPath != null && !photoPath.isEmpty()) {
                        out.println("<img src='" + photoPath + "' alt='Deceased Photo'>");
                    } else {
                        out.println("<div class='no-photo'>No Photo Available</div>");
                    }
                    out.println("</div>");
                    
                    // Death details
                    out.println("<div class='record-details'>");
                    out.println("<p><strong>Death ID:</strong> " + rs.getString("deathId") + "</p>");
                    out.println("<p><strong>Name:</strong> " + rs.getString("deceasedName") + "</p>");
                    out.println("<p><strong>Date of Death:</strong> " + rs.getString("dateOfDeath") + "</p>");
                    out.println("<p><strong>Place:</strong> " + rs.getString("placeOfDeath") + "</p>");
                    out.println("<p><strong>Cause:</strong> " + rs.getString("causeOfDeath") + "</p>");
                    out.println("<p><strong>Certificate Number:</strong> " + rs.getString("deathCertificateNumber") + "</p>");
                    out.println("<p><a href='update_death?searchDeathId=" + rs.getString("deathId") + 
                              "' class='action-btn'>Update Record</a></p>");
                    out.println("</div>");
                    
                    out.println("</div>");
                }
                
                out.println("</div>");
                
            } catch(Exception e) {
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
            
            out.println("</div>");
            
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch(Exception e) {
            throw new ServletException("Error in view_death servlet", e);
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