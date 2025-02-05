import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/update_birth")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
public class update_birth extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "upload_files";
       
    public update_birth() {
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
            out.println("<title>Update Birth Record</title>");
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
            out.println("<h2>Search Birth Record</h2>");
            out.println("<form method='post' action='update_birth'>");
            out.println("<div class='form-group'>");
            out.println("<label for='searchBirthId'>Enter Birth ID:</label>");
            out.println("<input type='text' name='searchBirthId' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<input type='submit' name='search' value='Search'>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div>");

            if (request.getParameter("search") != null) {
                String searchBirthId = request.getParameter("searchBirthId");
                displayUpdateForm(out, searchBirthId);
            } else if (request.getParameter("update") != null) {
                updateBirthRecord(request, response, out);
            }

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayUpdateForm(PrintWriter out, String birthId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql = "SELECT * FROM births WHERE birthId = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, birthId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                out.println("<div class='form-container'>");
                out.println("<h2>Update Birth Record</h2>");
                out.println("<form method='post' action='update_birth' enctype='multipart/form-data'>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='birthId'>Birth ID:</label>");
                out.println("<input type='text' name='birthId' value='" + rs.getString("birthId") + "' readonly>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='childName'>Child's Name:</label>");
                out.println("<input type='text' name='childName' value='" + rs.getString("childName") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='dateOfBirth'>Date of Birth:</label>");
                out.println("<input type='date' name='dateOfBirth' value='" + rs.getString("dateOfBirth") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='parent1Name'>Parent 1 Name:</label>");
                out.println("<input type='text' name='parent1Name' value='" + rs.getString("parent1Name") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='parent2Name'>Parent 2 Name:</label>");
                out.println("<input type='text' name='parent2Name' value='" + rs.getString("parent2Name") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='gender'>Gender:</label>");
                out.println("<select name='gender' required>");
                String currentGender = rs.getString("gender");
                out.println("<option value='Male'" + ("Male".equals(currentGender) ? " selected" : "") + ">Male</option>");
                out.println("<option value='Female'" + ("Female".equals(currentGender) ? " selected" : "") + ">Female</option>");
                out.println("<option value='Other'" + ("Other".equals(currentGender) ? " selected" : "") + ">Other</option>");
                out.println("</select>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='birthCertificateNumber'>Birth Certificate Number:</label>");
                out.println("<input type='text' name='birthCertificateNumber' value='" + 
                    rs.getString("birthCertificateNumber") + "' required>");
                out.println("</div>");
                
                // Display current photo if exists
                String currentPhoto = rs.getString("childPhoto");
                if (currentPhoto != null && !currentPhoto.isEmpty()) {
                    out.println("<div class='form-group'>");
                    out.println("<label>Current Photo:</label>");
                    out.println("<div class='current-photo'>");
                    out.println("<img src='" + currentPhoto + "' alt='Current Child Photo'>");
                    out.println("</div>");
                    out.println("</div>");
                }
                
                out.println("<div class='form-group'>");
                out.println("<label for='childPhoto'>Update Photo (optional):</label>");
                out.println("<input type='file' name='childPhoto' accept='image/*'>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<input type='submit' name='update' value='Update Record'>");
                out.println("</div>");
                
                out.println("</form>");
                out.println("</div>");
            } else {
                out.println("<div class='error-message'>No record found for Birth ID: " + birthId + "</div>");
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
        }
    }

    private void updateBirthRecord(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws ServletException, IOException {
        try {
            String birthId = request.getParameter("birthId");
            String childName = request.getParameter("childName");
            String dateOfBirth = request.getParameter("dateOfBirth");
            String parent1Name = request.getParameter("parent1Name");
            String parent2Name = request.getParameter("parent2Name");
            String gender = request.getParameter("gender");
            String birthCertificateNumber = request.getParameter("birthCertificateNumber");
            
            // Handle file upload
            Part filePart = request.getPart("childPhoto");
            String photoPath = null;
            
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = birthId + "_" + getSubmittedFileName(filePart);
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                
                photoPath = UPLOAD_DIR + File.separator + fileName;
                String fullPath = uploadPath + File.separator + fileName;
                
                // Save the file
                filePart.write(fullPath);
            }
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql;
            PreparedStatement pstmt;
            
            if (photoPath != null) {
                sql = "UPDATE births SET childName=?, dateOfBirth=?, parent1Name=?, " +
                      "parent2Name=?, gender=?, birthCertificateNumber=?, childPhoto=? WHERE birthId=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, childName);
                pstmt.setString(2, dateOfBirth);
                pstmt.setString(3, parent1Name);
                pstmt.setString(4, parent2Name);
                pstmt.setString(5, gender);
                pstmt.setString(6, birthCertificateNumber);
                pstmt.setString(7, photoPath);
                pstmt.setString(8, birthId);
            } else {
                sql = "UPDATE births SET childName=?, dateOfBirth=?, parent1Name=?, " +
                      "parent2Name=?, gender=?, birthCertificateNumber=? WHERE birthId=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, childName);
                pstmt.setString(2, dateOfBirth);
                pstmt.setString(3, parent1Name);
                pstmt.setString(4, parent2Name);
                pstmt.setString(5, gender);
                pstmt.setString(6, birthCertificateNumber);
                pstmt.setString(7, birthId);
            }
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                out.println("<div class='success-message'>Birth record updated successfully!</div>");
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