import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/generate_marriage_certificate")
public class generate_marriage_certificate extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public generate_marriage_certificate() {
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
            out.println("<title>Generate Marriage Certificate</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/certificate_styles.css'>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            // Search Form
            out.println("<div class='search-form-container'>");
            out.println("<h2>Generate Marriage Certificate</h2>");
            out.println("<form class='search-form' method='post' action='generate_marriage_certificate'>");
            out.println("<input type='text' name='marriageId' placeholder='Enter Marriage ID' required>");
            out.println("<button type='submit' name='generate'>Generate Certificate</button>");
            out.println("</form>");
            out.println("</div>");
            
            if (request.getParameter("generate") != null) {
                String marriageId = request.getParameter("marriageId");
                generateCertificate(marriageId, out);
            }
            
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
    
    private void generateCertificate(String marriageId, PrintWriter out) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql = "SELECT * FROM marriages WHERE marriageId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, marriageId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                out.println("<div class='certificate-container'>");
                out.println("<div class='certificate' data-type='marriage'>");
                
                // Certificate Header
                out.println("<div class='certificate-header'>");
                out.println("<div class='header-content'>");
                out.println("<div class='govt-logo'>");
                out.println("<img src='upload_files/logo.png' alt='Government Logo'>");
                out.println("</div>");
                out.println("<div class='certificate-title'>");
                out.println("<div class='govt-name'>Federal Government of Ethiopia</div>");
                out.println("<div class='cert-type'>Certificate of Marriage</div>");
                out.println("<div class='cert-number'>Registration No: " + rs.getString("marriageCertificateNumber") + "</div>");
                out.println("</div>");
                out.println("</div>");
                out.println("</div>");
                
                // Photos Section
                out.println("<div class='photo-section'>");
                
                // Groom's Photo
                String groomPhoto = rs.getString("groomPhoto");
                if (groomPhoto != null && !groomPhoto.isEmpty()) {
                    out.println("<div class='photo-container'>");
                    out.println("<div class='photo-frame'>");
                    out.println("<img src='" + groomPhoto + "' alt='Groom Photo'>");
                    out.println("</div>");
                    out.println("<div class='photo-label'>Groom</div>");
                    out.println("</div>");
                }
                
                // Bride's Photo
                String bridePhoto = rs.getString("bridePhoto");
                if (bridePhoto != null && !bridePhoto.isEmpty()) {
                    out.println("<div class='photo-container'>");
                    out.println("<div class='photo-frame'>");
                    out.println("<img src='" + bridePhoto + "' alt='Bride Photo'>");
                    out.println("</div>");
                    out.println("<div class='photo-label'>Bride</div>");
                    out.println("</div>");
                }
                
                out.println("</div>"); // End photo-section
                
                // Details Section
                out.println("<div class='details-section'>");
                out.println("<div class='details-grid'>");
                
                // Marriage Details
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Marriage ID:</span>");
                out.println(rs.getString("marriageId"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Date of Marriage:</span>");
                out.println(rs.getString("dateOfMarriage"));
                out.println("</div>");
                
                // Groom Details
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Groom's Name:</span>");
                out.println(rs.getString("groomName"));
                out.println("</div>");
                
                // Bride Details
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Bride's Name:</span>");
                out.println(rs.getString("brideName"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Place of Marriage:</span>");
                out.println(rs.getString("placeOfMarriage"));
                out.println("</div>");
                
                // Witness Details
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Witness 1:</span>");
                out.println(rs.getString("witness1Name"));
                out.println("</div>");
                
                out.println("<div class='detail-item'>");
                out.println("<span class='detail-label'>Witness 2:</span>");
                out.println(rs.getString("witness2Name"));
                out.println("</div>");
                
                out.println("</div>"); // End details-grid
                out.println("</div>"); // End details-section
                
                // Certificate Footer
                out.println("<div class='certificate-footer'>");
                out.println("<div class='verification-text'>");
                out.println("This is to certify that the above marriage has been registered under the Marriage Registration Act.");
                out.println("</div>");
                
                out.println("<div class='signatures'>");
                out.println("<div class='signature-box'>");
                out.println("<div class='sign-line'></div>");
                out.println("<div class='sign-name'>Marriage Registrar</div>");
                out.println("<div class='sign-title'>Marriage Registration Authority</div>");
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
                out.println("<div class='error-message'>No record found for Marriage ID: " + marriageId + "</div>");
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