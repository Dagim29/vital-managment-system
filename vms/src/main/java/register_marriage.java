import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/register_marriage")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1 MB
    maxFileSize = 1024 * 1024 * 10,     // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class register_marriage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "upload_files";
       
    public register_marriage() {
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
            out.println("<title>Register Marriage</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            if (request.getParameter("submit") != null) {
                // Handle form submission
                String marriageId = request.getParameter("marriageId");
                String brideName = request.getParameter("brideName");
                String groomName = request.getParameter("groomName");
                String dateOfMarriage = request.getParameter("dateOfMarriage");
                String placeOfMarriage = request.getParameter("placeOfMarriage");
                String witness1Name = request.getParameter("witness1Name");
                String witness2Name = request.getParameter("witness2Name");
                String marriageCertificateNumber = request.getParameter("marriageCertificateNumber");
                
                // Handle file uploads
                Part bridePhotoPart = request.getPart("bridePhoto");
                Part groomPhotoPart = request.getPart("groomPhoto");
                
                String bridePhotoPath = null;
                String groomPhotoPath = null;
                
                // Save bride's photo
                if (bridePhotoPart != null && bridePhotoPart.getSize() > 0) {
                    String fileName = marriageId + "_bride_" + getSubmittedFileName(bridePhotoPart);
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    bridePhotoPath = UPLOAD_DIR + File.separator + fileName;
                    String fullPath = uploadPath + File.separator + fileName;
                    bridePhotoPart.write(fullPath);
                }
                
                // Save groom's photo
                if (groomPhotoPart != null && groomPhotoPart.getSize() > 0) {
                    String fileName = marriageId + "_groom_" + getSubmittedFileName(groomPhotoPart);
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    groomPhotoPath = UPLOAD_DIR + File.separator + fileName;
                    String fullPath = uploadPath + File.separator + fileName;
                    groomPhotoPart.write(fullPath);
                }
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/vms_db", "root", "root");
                    
                    String sql = "INSERT INTO marriages (marriageId, brideName, groomName, dateOfMarriage, " +
                                "placeOfMarriage, witness1Name, witness2Name, marriageCertificateNumber, " +
                                "bridePhoto, groomPhoto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, marriageId);
                    pstmt.setString(2, brideName);
                    pstmt.setString(3, groomName);
                    pstmt.setString(4, dateOfMarriage);
                    pstmt.setString(5, placeOfMarriage);
                    pstmt.setString(6, witness1Name);
                    pstmt.setString(7, witness2Name);
                    pstmt.setString(8, marriageCertificateNumber);
                    pstmt.setString(9, bridePhotoPath);
                    pstmt.setString(10, groomPhotoPath);
                    
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        out.println("<div class='success-message'>Marriage record registered successfully!</div>");
                        out.println("<div class='form-container'>");
                        out.println("<h3>Registered Marriage Information:</h3>");
                        out.println("<ul>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Marriage ID:</strong> " + marriageId + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Bride's Name:</strong> " + brideName + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Groom's Name:</strong> " + groomName + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Date of Marriage:</strong> " + dateOfMarriage + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Place of Marriage:</strong> " + placeOfMarriage + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Witness 1 Name:</strong> " + witness1Name + "</li>");
                        out.println("<li><strong>Witness 2 Name:</strong> " + witness2Name + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Marriage Certificate Number:</strong> " + marriageCertificateNumber + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Bride's Photo:</strong> <img src='" + bridePhotoPath + "' alt='Bride Photo' width='100' height='100'></li>");
                        out.println("<li><strong>Groom's Photo:</strong> <img src='" + groomPhotoPath + "' alt='Groom Photo' width='100' height='100'></li>");
                        out.println("</ul>");
                       
                        
                        // Close button to reload the page and go back to the form
                        out.println("<div class='form-group'>");
                        out.println("<button onclick=\"window.location.href='register_marriage'\">Close</button>");
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
            out.println("<h2>Register Marriage</h2>");
            out.println("<form method='post' action='register_marriage' enctype='multipart/form-data'>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='marriageId'>Marriage ID:</label>");
            out.println("<input type='text' name='marriageId' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='brideName'>Bride's Name:</label>");
            out.println("<input type='text' name='brideName' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='bridePhoto'>Bride's Photo:</label>");
            out.println("<input type='file' name='bridePhoto' accept='image/*' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='groomName'>Groom's Name:</label>");
            out.println("<input type='text' name='groomName' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='groomPhoto'>Groom's Photo:</label>");
            out.println("<input type='file' name='groomPhoto' accept='image/*' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='dateOfMarriage'>Date of Marriage:</label>");
            out.println("<input type='date' name='dateOfMarriage' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='placeOfMarriage'>Place of Marriage:</label>");
            out.println("<input type='text' name='placeOfMarriage' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='witness1Name'>Witness 1 Name:</label>");
            out.println("<input type='text' name='witness1Name' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='witness2Name'>Witness 2 Name:</label>");
            out.println("<input type='text' name='witness2Name' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='marriageCertificateNumber'>Marriage Certificate Number:</label>");
            out.println("<input type='text' name='marriageCertificateNumber' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<input type='submit' name='submit' value='Register Marriage'>");
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