import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/view_divorce")
public class view_divorce extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public view_divorce() {
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
            out.println("<title>View Divorce Records</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<style>");
            out.println(".record-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; padding: 20px; }");
            out.println(".record-card { background: #fff; border-radius: 8px; padding: 15px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            out.println(".photos-container { display: flex; justify-content: space-between; margin-bottom: 15px; }");
            out.println(".photo-box { width: 48%; text-align: center; }");
            out.println(".photo-box img { width: 100%; height: 150px; object-fit: cover; border-radius: 4px; margin-bottom: 5px; }");
            out.println(".photo-label { font-size: 0.9em; color: #666; }");
            out.println(".record-details { margin: 10px 0; }");
            out.println(".record-details p { margin: 5px 0; color: #666; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            out.println("<div class='container'>");
            out.println("<h2>Divorce Records</h2>");
            
            // Search form
            out.println("<div class='search-container'>");
            out.println("<form method='post' action='view_divorce'>");
            out.println("<input type='text' name='searchTerm' class='search-box' placeholder='Enter Divorce ID, Names, or Legal Documents'>");
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
                    sql = "SELECT * FROM divorces WHERE divorceId LIKE ? OR party1Name LIKE ? " +
                          "OR party2Name LIKE ? OR legalDocuments LIKE ? ORDER BY dateOfDivorce DESC";
                    pstmt = conn.prepareStatement(sql);
                    String searchPattern = "%" + searchTerm + "%";
                    for(int i = 1; i <= 4; i++) {
                        pstmt.setString(i, searchPattern);
                    }
                } else {
                    sql = "SELECT * FROM divorces ORDER BY dateOfDivorce DESC";
                    pstmt = conn.prepareStatement(sql);
                }
                
                rs = pstmt.executeQuery();
                
                out.println("<div class='record-grid'>");
                
                while(rs.next()) {
                    out.println("<div class='record-card'>");
                    
                    // Photos section
                    out.println("<div class='photos-container'>");
                    
                    // Party 1 photo
                    out.println("<div class='photo-box'>");
                    String party1Photo = rs.getString("party1Photo");
                    if(party1Photo != null && !party1Photo.isEmpty()) {
                        out.println("<img src='" + party1Photo + "' alt='Party 1 Photo'>");
                    } else {
                        out.println("<div class='no-photo'>No Photo</div>");
                    }
                    out.println("<div class='photo-label'>Party 1</div>");
                    out.println("</div>");
                    
                    // Party 2 photo
                    out.println("<div class='photo-box'>");
                    String party2Photo = rs.getString("party2Photo");
                    if(party2Photo != null && !party2Photo.isEmpty()) {
                        out.println("<img src='" + party2Photo + "' alt='Party 2 Photo'>");
                    } else {
                        out.println("<div class='no-photo'>No Photo</div>");
                    }
                    out.println("<div class='photo-label'>Party 2</div>");
                    out.println("</div>");
                    
                    out.println("</div>");
                    
                    // Divorce details
                    out.println("<div class='record-details'>");
                    out.println("<p><strong>Divorce ID:</strong> " + rs.getString("divorceId") + "</p>");
                    out.println("<p><strong>Party 1:</strong> " + rs.getString("party1Name") + "</p>");
                    out.println("<p><strong>Party 2:</strong> " + rs.getString("party2Name") + "</p>");
                    out.println("<p><strong>Date of Divorce:</strong> " + rs.getString("dateOfDivorce") + "</p>");
                    out.println("<p><strong>Legal Documents:</strong> " + rs.getString("legalDocuments") + "</p>");
                    out.println("<p><a href='update_divorce?searchDivorceId=" + rs.getString("divorceId") + 
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
            throw new ServletException("Error in view_divorce servlet", e);
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