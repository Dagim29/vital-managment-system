import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/generate_birth_certificate")
public class generate_birth_certificate extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public generate_birth_certificate() {
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
            out.println("<title>Generate Birth Certificate</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/certificate_styles.css'>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            // Search Form
            out.println("<div class='search-form-container'>");
            out.println("<h2>Generate Birth Certificate</h2>");
            out.println("<form class='search-form' method='post' action='generate_birth_certificate'>");
            out.println("<input type='text' name='birthId' placeholder='Enter Birth ID' required>");
            out.println("<button type='submit' name='generate'>Generate Certificate</button>");
            out.println("</form>");
            out.println("</div>");
            
            // Handle certificate generation
            if (request.getParameter("generate") != null) {
                String birthId = request.getParameter("birthId");
                generateCertificate(birthId, out);
            }
            
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
    
    private void generateCertificate(String birthId, PrintWriter out) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql = "SELECT * FROM births WHERE birthId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, birthId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                out.println("<div class='certificate-container'>");
                out.println("<div class='certificate' data-type='birth'>");
                
                // Certificate Header
                out.println("<div class='certificate-header'>");
                out.println("<div class='header-content'>");
                out.println("<div class='govt-logo'>");
                out.println("<img src='upload_files/logo.png' alt='Government Logo'>");
                out.println("</div>");
                out.println("<div class='certificate-title'>");
                out.println("<div class='govt-name'>Federal Government of Ethiopia</div>");
                out.println("<div class='cert-type'>Certificate of Birth</div>");
                out.println("<div class='cert-number'>Registration No: " + rs.getString("birthCertificateNumber") + "</div>");
                out.println("</div>");
                out.println("</div>");
                out.println("</div>");
                
                // Photo Section
                String photoPath = rs.getString("childPhoto");
                if (photoPath != null && !photoPath.isEmpty()) {
                    out.println("<div class='photo-section'>");
                    out.println("<div class='photo-container'>");
                    out.println("<div class='photo-frame'>");
                    out.println("<img src='" + photoPath + "' alt='Child Photo'>");
                    out.println("</div>");
                    out.println("<div class='photo-label'>Child's Photograph</div>");
                    out.println("</div>");
                    out.println("</div>");
                }
                
                // Details Section
                out.println("<div class='details-section'>");
                out.println("<div class='details-grid'>");
                
                // Child Details
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Name of Child:</span>");
                out.println(rs.getString("childName"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Gender:</span>");
                out.println(rs.getString("gender"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Date of Birth:</span>");
                out.println(rs.getString("dateOfBirth"));
                out.println("</div>");
                
               
                // Parent Details
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Father's Name:</span>");
                out.println(rs.getString("parent1Name"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Mother's Name:</span>");
                out.println(rs.getString("parent2Name"));
                out.println("</div>");
                
                //out.println("<div class='detail-item'>");
                //out.println("<span class='detail-label'>Address:</span>");
                //out.println(rs.getString("address"));
                //out.println("</div>");
                
                out.println("</div>"); // End details-grid
                out.println("</div>"); // End details-section
                
                // Certificate Footer
                out.println("<div class='certificate-footer'>");
                out.println("<div class='verification-text'>");
                out.println("This is to certify that the above information is correct according to the records of birth registered with us.");
                out.println("</div>");
                
                out.println("<div class='signatures'>");
                out.println("<div class='signature-box'>");
                out.println("<div class='sign-line'></div>");
                out.println("<div class='sign-name'>Registrar</div>");
                out.println("<div class='sign-title'>Birth & Death Registration</div>");
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
                out.println("<div class='error-message'>No record found for Birth ID: " + birthId + "</div>");
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