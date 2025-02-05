import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/register_birth")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class register_birth extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "upload_files";
    
    public register_birth() {
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
            out.println("<title>Register Birth</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            if (request.getParameter("submit") != null) {
                // Get form data
                String birthId = request.getParameter("birthId");
                String childName = request.getParameter("childName");
                String dateOfBirth = request.getParameter("dateOfBirth");
                String parent1Name = request.getParameter("parent1Name");
                String parent2Name = request.getParameter("parent2Name");
                String gender = request.getParameter("gender");
                String birthCertificateNumber = request.getParameter("birthCertificateNumber");
                
                // Handle file upload
                Part filePart = request.getPart("childPhoto");
                String fileName = "";
                String photoPath = "";
                
                if (filePart != null && filePart.getSize() > 0) {
                    fileName = birthId + "_" + getSubmittedFileName(filePart);
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    photoPath = UPLOAD_DIR + File.separator + fileName;
                    String fullPath = uploadPath + File.separator + fileName;
                    
                    // Save the file
                    filePart.write(fullPath);
                }
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/vms_db", "root", "root");
                    
                    String sql = "INSERT INTO births (birthId, childName, dateOfBirth, parent1Name, " +
                                "parent2Name, gender, birthCertificateNumber, childPhoto) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, birthId);
                    pstmt.setString(2, childName);
                    pstmt.setString(3, dateOfBirth);
                    pstmt.setString(4, parent1Name);
                    pstmt.setString(5, parent2Name);
                    pstmt.setString(6, gender);
                    pstmt.setString(7, birthCertificateNumber);
                    pstmt.setString(8, photoPath);  // Save the photo path
                    
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        out.println("<div class='success-message'>Birth record registered successfully!</div>");
                        
                        // Display the registered information temporarily
                        out.println("<div class='form-container'>");
                        out.println("<h3>Registered Information:</h3>");                            
                        out.println("<div class='form-group'>");
                        out.println("<li>Birth ID: " + birthId + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li>Child's Name: " + childName + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li>Date of Birth: " + dateOfBirth + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li>Father's Name: " + parent1Name + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li>Mother's Name: " + parent2Name + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li>Gender: " + gender + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li>Birth Certificate Number: " + birthCertificateNumber + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li>Child's Photo: <img src='" + photoPath + "' alt='Child Photo' width='100' height='100'></li>");
                        out.println("</ul>");
                        out.println("</div>");
                      
                        
                        // Close button to reload the page
                        out.println("<div class='form-group'>");
                        out.println("<button onclick=\"window.location.href='register_birth'\">Close</button>");
                        out.println("</div>");
                        out.println("</div>");
                    }
                    
                    pstmt.close();
                    conn.close();
                    
                } catch (Exception e) {
                    out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
                }
            }
            
            // Display the registration form (this will be visible again after reloading the page)
            out.println("<div class='form-container'>");
            out.println("<h2>Register Birth</h2>");
            out.println("<form method='post' action='register_birth' enctype='multipart/form-data'>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='birthId'>Birth ID:</label>");
            out.println("<input type='text' name='birthId' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='childName'>Child's Name:</label>");
            out.println("<input type='text' name='childName' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='dateOfBirth'>Date of Birth:</label>");
            out.println("<input type='date' name='dateOfBirth' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='parent1Name'>Father's Name:</label>");
            out.println("<input type='text' name='parent1Name' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='parent2Name'>Mother's Name:</label>");
            out.println("<input type='text' name='parent2Name' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='gender'>Gender:</label>");
            out.println("<select name='gender' required>");
            out.println("<option value=''>Select Gender</option>");
            out.println("<option value='Male'>Male</option>");
            out.println("<option value='Female'>Female</option>");
            out.println("<option value='Other'>Other</option>");
            out.println("</select>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='childPhoto'>Child's Photo:</label>");
            out.println("<input type='file' name='childPhoto' accept='image/*' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='birthCertificateNumber'>Birth Certificate Number:</label>");
            out.println("<input type='text' name='birthCertificateNumber' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<input type='submit' name='submit' value='Register Birth'>");
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
        } finally {
            out.close();
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
