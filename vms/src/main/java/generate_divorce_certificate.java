import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/generate_divorce_certificate")
public class generate_divorce_certificate extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public generate_divorce_certificate() {
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
            out.println("<title>Generate Divorce Certificate</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/certificate_styles.css'>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            // Search Form
            out.println("<div class='search-form-container'>");
            out.println("<h2>Generate Divorce Certificate</h2>");
            out.println("<form class='search-form' method='post' action='generate_divorce_certificate'>");
            out.println("<input type='text' name='divorceId' placeholder='Enter Divorce ID' required>");
            out.println("<button type='submit' name='generate'>Generate Certificate</button>");
            out.println("</form>");
            out.println("</div>");
            
            if (request.getParameter("generate") != null) {
                String divorceId = request.getParameter("divorceId");
                generateCertificate(divorceId, out);
            }
            
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
    
    private void generateCertificate(String divorceId, PrintWriter out) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql = "SELECT * FROM divorces WHERE divorceId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, divorceId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                out.println("<div class='certificate-container'>");
                out.println("<div class='certificate' data-type='divorce'>");
                
                // Certificate Header
                out.println("<div class='certificate-header'>");
                out.println("<div class='header-content'>");
                out.println("<div class='govt-logo'>");
                out.println("<img src='upload_files/logo.png' alt='Government Logo'>");
                out.println("</div>");
                out.println("<div class='certificate-title'>");
                out.println("<div class='govt-name'>Federal Government of Ethiopia</div>");
                out.println("<div class='cert-type'>Certificate of Divorce</div>");
                out.println("<div class='cert-number'>Legal Document No: " + rs.getString("legalDocuments") + "</div>");
                out.println("</div>");
                out.println("</div>");
                out.println("</div>");
                
                // Photos Section
                out.println("<div class='photo-section'>");
                
                // Party 1 Photo
                String party1Photo = rs.getString("party1Photo");
                if (party1Photo != null && !party1Photo.isEmpty()) {
                    out.println("<div class='photo-container'>");
                    out.println("<div class='photo-frame'>");
                    out.println("<img src='" + party1Photo + "' alt='Party 1 Photo'>");
                    out.println("</div>");
                    out.println("<div class='photo-label'>Party 1</div>");
                    out.println("</div>");
                }
                
                // Party 2 Photo
                String party2Photo = rs.getString("party2Photo");
                if (party2Photo != null && !party2Photo.isEmpty()) {
                    out.println("<div class='photo-container'>");
                    out.println("<div class='photo-frame'>");
                    out.println("<img src='" + party2Photo + "' alt='Party 2 Photo'>");
                    out.println("</div>");
                    out.println("<div class='photo-label'>Party 2</div>");
                    out.println("</div>");
                }
                
                out.println("</div>"); // End photo-section
                
                // Details Section
                out.println("<div class='details-section'>");
                out.println("<div class='details-grid'>");
                
                // Divorce Details
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Divorce ID:</span>");
                out.println(rs.getString("divorceId"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Date of Divorce:</span>");
                out.println(rs.getString("dateOfDivorce"));
                out.println("</div>");
                
                // Party Details
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Party 1 Name:</span>");
                out.println(rs.getString("party1Name"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Party 2 Name:</span>");
                out.println(rs.getString("party2Name"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Legal Document Reference:</span>");
                out.println(rs.getString("legalDocuments"));
                out.println("</div>");
                
                out.println("</div>"); // End details-grid
                out.println("</div>"); // End details-section
                
                // Certificate Footer
                out.println("<div class='certificate-footer'>");
                out.println("<div class='verification-text'>");
                out.println("This is to certify that the marriage between the above-mentioned parties has been legally dissolved as per the divorce decree.");
                out.println("</div>");
                
                out.println("<div class='signatures'>");
                out.println("<div class='signature-box'>");
                out.println("<div class='sign-line'></div>");
                out.println("<div class='sign-name'>Family Court Judge</div>");
                out.println("<div class='sign-title'>Court of Civil Jurisdiction</div>");
                out.println("</div>");
                out.println("</div>");
                
                // Official Seal
                out.println("<div class='official-seal'></div>");
                
                out.println("</div>"); // End certificate-footer
                
                out.println("</div>"); // End certificate
                
                // Print Button
                out.println("<div class='print-button'>");
                out.println("<button onclick='window.print()'>Print Certificate</button>");
                out.println("</div>");
                
                out.println("</div>"); // End certificate-container
                
            } else {
                out.println("<div class='error-message'>No record found for Divorce ID: " + divorceId + "</div>");
            }
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error generating certificate: " + e.getMessage() + "</div>");
        } finally {
            try {
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
            } catch(SQLException e) {
                out.println("<div class='error-message'>Error closing database connections: " + e.getMessage() + "</div>");
            }
        }
    }
}