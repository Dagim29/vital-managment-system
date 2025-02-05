import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/register_death")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1 MB
    maxFileSize = 1024 * 1024 * 10,     // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class register_death extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "upload_files";
       
    public register_death() {
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
            out.println("<title>Register Death</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            if (request.getParameter("submit") != null) {
                // Handle form submission
                String deathId = request.getParameter("deathId");
                String deceasedName = request.getParameter("deceasedName");
                String dateOfDeath = request.getParameter("dateOfDeath");
                String placeOfDeath = request.getParameter("placeOfDeath");
                String causeOfDeath = request.getParameter("causeOfDeath");
                String deathCertificateNumber = request.getParameter("deathCertificateNumber");
                
                // Handle file upload
                Part photoPart = request.getPart("deceasedPhoto");
                String photoPath = null;
                
                // Save deceased's photo
                if (photoPart != null && photoPart.getSize() > 0) {
                    String fileName = deathId + "_" + getSubmittedFileName(photoPart);
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    photoPath = UPLOAD_DIR + File.separator + fileName;
                    String fullPath = uploadPath + File.separator + fileName;
                    photoPart.write(fullPath);
                }
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/vms_db", "root", "root");
                    
                    String sql = "INSERT INTO deaths (deathId, deceasedName, dateOfDeath, " +
                                "placeOfDeath, causeOfDeath, deathCertificateNumber, deceasedPhoto) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, deathId);
                    pstmt.setString(2, deceasedName);
                    pstmt.setString(3, dateOfDeath);
                    pstmt.setString(4, placeOfDeath);
                    pstmt.setString(5, causeOfDeath);
                    pstmt.setString(6, deathCertificateNumber);
                    pstmt.setString(7, photoPath);
                    
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        out.println("<div class='success-message'>Death record registered successfully!</div>");
                     // Display the registered death information temporarily
                        out.println("<div class='form-container'>");
                        out.println("<h3>Registered Death Information:</h3>");
                        out.println("<ul>");  // Using <ul> for a list style display

                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Death ID:</strong> " + deathId + "</li>");
                        out.println("</div>");

                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Deceased Name:</strong> " + deceasedName + "</li>");
                        out.println("</div>");

                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Date of Death:</strong> " + dateOfDeath + "</li>");
                        out.println("</div>");

                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Place of Death:</strong> " + placeOfDeath + "</li>");
                        out.println("</div>");

                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Cause of Death:</strong> " + causeOfDeath + "</li>");
                        out.println("</div>");

                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Death Certificate Number:</strong> " + deathCertificateNumber + "</li>");
                        out.println("</div>");

                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Deceased Photo:</strong> <img src='" + photoPath + "' alt='Deceased Photo' width='100' height='100'></li>");
                        out.println("</ul>");
                        
                        // Close button to reload the page and show the registration form again
                        out.println("<div class='form-group'>");
                        out.println("<button onclick=\"window.location.href='register_death'\">Close</button>");
                        out.println("</div>");
                        out.println("</div>");
                        out.println("</div>");


                    
                    }
                    
                    pstmt.close();
                    conn.close();
                    
                } catch (Exception e) {
                    out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
                }
            }
            
            // Display the registration form
            out.println("<div class='form-container'>");
            out.println("<h2>Register Death</h2>");
            out.println("<form method='post' action='register_death' enctype='multipart/form-data'>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='deathId'>Death ID:</label>");
            out.println("<input type='text' name='deathId' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='deceasedName'>Deceased Name:</label>");
            out.println("<input type='text' name='deceasedName' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='deceasedPhoto'>Deceased Photo:</label>");
            out.println("<input type='file' name='deceasedPhoto' accept='image/*' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='dateOfDeath'>Date of Death:</label>");
            out.println("<input type='date' name='dateOfDeath' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='placeOfDeath'>Place of Death:</label>");
            out.println("<input type='text' name='placeOfDeath' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='causeOfDeath'>Cause of Death:</label>");
            out.println("<textarea name='causeOfDeath' required rows='3'></textarea>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='deathCertificateNumber'>Death Certificate Number:</label>");
            out.println("<input type='text' name='deathCertificateNumber' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<input type='submit' name='submit' value='Register Death'>");
            out.println("</div>");
            
            out.println("</form>");
            out.println("</div>");
            out.println("<div class='form-group' style='text-align: center; margin-top: 30px;'>");
            out.println("<button onclick=\"window.location.href='vms_officer_home'\" style='"
                        + "background-color: #007BFF; "
                        + "color: white; "
                        + "border: none; "
                        + "padding: 8px 16px; "
                        + "font-size: 14px; "
                        + "font-family: Arial, sans-serif; "
                        + "cursor: pointer; "
                        + "border-radius: 4px; "
                        + "transition: background-color 0.3s ease, transform 0.2s; "
                        + "text-align: center; "
                        + "display: inline-block; "
                        + "text-decoration: none;' "
                        + "onmouseover=\"this.style.backgroundColor='#0056b3'; this.style.transform='scale(1.05)'\" "
                        + "onmouseout=\"this.style.backgroundColor='#007BFF'; this.style.transform='scale(1)'\">"
                        + "Back</button>");
            out.println("</div>");

            
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
    
    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
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