import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/update_death")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1 MB
    maxFileSize = 1024 * 1024 * 10,     // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class update_death extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "upload_files";
       
    public update_death() {
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
            out.println("<title>Update Death Record</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<style>");
            out.println(".current-photo { max-width: 200px; margin: 10px 0; }");
            out.println(".current-photo img { max-width: 100%; height: auto; border-radius: 4px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            // Search form
            out.println("<div class='form-container'>");
            out.println("<h2>Search Death Record</h2>");
            out.println("<form method='post' action='update_death'>");
            out.println("<div class='form-group'>");
            out.println("<label for='searchDeathId'>Enter Death ID:</label>");
            out.println("<input type='text' name='searchDeathId' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<input type='submit' name='search' value='Search'>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div>");

            if (request.getParameter("search") != null) {
                String searchDeathId = request.getParameter("searchDeathId");
                displayUpdateForm(out, searchDeathId);
            } else if (request.getParameter("update") != null) {
                updateDeathRecord(request, response, out);
            }

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayUpdateForm(PrintWriter out, String deathId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql = "SELECT * FROM deaths WHERE deathId = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, deathId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                out.println("<div class='form-container'>");
                out.println("<h2>Update Death Record</h2>");
                out.println("<form method='post' action='update_death' enctype='multipart/form-data'>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='deathId'>Death ID:</label>");
                out.println("<input type='text' name='deathId' value='" + rs.getString("deathId") + "' readonly>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='deceasedName'>Deceased Name:</label>");
                out.println("<input type='text' name='deceasedName' value='" + rs.getString("deceasedName") + "' required>");
                out.println("</div>");
                
                // Display current photo if exists
                String currentPhoto = rs.getString("deceasedPhoto");
                if (currentPhoto != null && !currentPhoto.isEmpty()) {
                    out.println("<div class='form-group'>");
                    out.println("<label>Current Photo:</label>");
                    out.println("<div class='current-photo'>");
                    out.println("<img src='" + currentPhoto + "' alt='Current Photo'>");
                    out.println("</div>");
                    out.println("</div>");
                }
                
                out.println("<div class='form-group'>");
                out.println("<label for='deceasedPhoto'>Update Photo (optional):</label>");
                out.println("<input type='file' name='deceasedPhoto' accept='image/*'>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='dateOfDeath'>Date of Death:</label>");
                out.println("<input type='date' name='dateOfDeath' value='" + rs.getString("dateOfDeath") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='placeOfDeath'>Place of Death:</label>");
                out.println("<input type='text' name='placeOfDeath' value='" + rs.getString("placeOfDeath") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='causeOfDeath'>Cause of Death:</label>");
                out.println("<textarea name='causeOfDeath' required rows='3'>" + rs.getString("causeOfDeath") + "</textarea>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='deathCertificateNumber'>Death Certificate Number:</label>");
                out.println("<input type='text' name='deathCertificateNumber' value='" + 
                    rs.getString("deathCertificateNumber") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<input type='submit' name='update' value='Update Record'>");
                out.println("</div>");
                
                out.println("</form>");
                out.println("</div>");
            } else {
                out.println("<div class='error-message'>No record found for Death ID: " + deathId + "</div>");
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
        }
    }

    private void updateDeathRecord(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws ServletException, IOException {
        try {
            String deathId = request.getParameter("deathId");
            String deceasedName = request.getParameter("deceasedName");
            String dateOfDeath = request.getParameter("dateOfDeath");
            String placeOfDeath = request.getParameter("placeOfDeath");
            String causeOfDeath = request.getParameter("causeOfDeath");
            String deathCertificateNumber = request.getParameter("deathCertificateNumber");
            
            // Handle file upload
            Part photoPart = request.getPart("deceasedPhoto");
            String photoPath = null;
            
            // Save photo if updated
            if (photoPart != null && photoPart.getSize() > 0) {
                String fileName = deathId + "_" + getSubmittedFileName(photoPart);
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                
                photoPath = UPLOAD_DIR + File.separator + fileName;
                String fullPath = uploadPath + File.separator + fileName;
                photoPart.write(fullPath);
            }
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql;
            PreparedStatement pstmt;
            
            if (photoPath != null) {
                sql = "UPDATE deaths SET deceasedName=?, dateOfDeath=?, placeOfDeath=?, " +
                      "causeOfDeath=?, deathCertificateNumber=?, deceasedPhoto=? WHERE deathId=?";
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, deceasedName);
                pstmt.setString(2, dateOfDeath);
                pstmt.setString(3, placeOfDeath);
                pstmt.setString(4, causeOfDeath);
                pstmt.setString(5, deathCertificateNumber);
                pstmt.setString(6, photoPath);
                pstmt.setString(7, deathId);
            } else {
                sql = "UPDATE deaths SET deceasedName=?, dateOfDeath=?, placeOfDeath=?, " +
                      "causeOfDeath=?, deathCertificateNumber=? WHERE deathId=?";
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, deceasedName);
                pstmt.setString(2, dateOfDeath);
                pstmt.setString(3, placeOfDeath);
                pstmt.setString(4, causeOfDeath);
                pstmt.setString(5, deathCertificateNumber);
                pstmt.setString(6, deathId);
            }
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                out.println("<div class='success-message'>Death record updated successfully!</div>");
            } else {
                out.println("<div class='error-message'>Failed to update record.</div>");
            }
            
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
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