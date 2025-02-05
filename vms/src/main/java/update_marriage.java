import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/update_marriage")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
public class update_marriage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "upload_files";
    
    public update_marriage() {
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
            out.println("<title>Update Marriage Record</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("<style>");
            out.println(".current-photos { display: flex; justify-content: space-between; margin: 15px 0; }");
            out.println(".photo-box { width: 48%; text-align: center; }");
            out.println(".photo-box img { max-width: 100%; height: auto; border-radius: 4px; }");
            out.println(".photo-label { margin-top: 5px; color: #666; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            // Search form
            out.println("<div class='form-container'>");
            out.println("<h2>Search Marriage Record</h2>");
            out.println("<form method='post' action='update_marriage'>");
            out.println("<div class='form-group'>");
            out.println("<label for='searchMarriageId'>Enter Marriage ID:</label>");
            out.println("<input type='text' name='searchMarriageId' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<input type='submit' name='search' value='Search'>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div>");

            if (request.getParameter("search") != null) {
                String searchMarriageId = request.getParameter("searchMarriageId");
                displayUpdateForm(out, searchMarriageId);
            } else if (request.getParameter("update") != null) {
                updateMarriageRecord(request, response, out);
            }

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayUpdateForm(PrintWriter out, String marriageId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql = "SELECT * FROM marriages WHERE marriageId = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, marriageId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                out.println("<div class='form-container'>");
                out.println("<h2>Update Marriage Record</h2>");
                out.println("<form method='post' action='update_marriage' enctype='multipart/form-data'>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='marriageId'>Marriage ID:</label>");
                out.println("<input type='text' name='marriageId' value='" + rs.getString("marriageId") + "' readonly>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='brideName'>Bride's Name:</label>");
                out.println("<input type='text' name='brideName' value='" + rs.getString("brideName") + "' required>");
                out.println("</div>");
                
                // Display current bride's photo if exists
                String bridePhoto = rs.getString("bridePhoto");
                if (bridePhoto != null && !bridePhoto.isEmpty()) {
                    out.println("<div class='form-group'>");
                    out.println("<label>Current Bride's Photo:</label>");
                    out.println("<div class='photo-box'>");
                    out.println("<img src='" + bridePhoto + "' alt='Current Bride Photo'>");
                    out.println("</div>");
                    out.println("</div>");
                }
                
                out.println("<div class='form-group'>");
                out.println("<label for='bridePhoto'>Update Bride's Photo (optional):</label>");
                out.println("<input type='file' name='bridePhoto' accept='image/*'>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='groomName'>Groom's Name:</label>");
                out.println("<input type='text' name='groomName' value='" + rs.getString("groomName") + "' required>");
                out.println("</div>");
                
                // Display current groom's photo if exists
                String groomPhoto = rs.getString("groomPhoto");
                if (groomPhoto != null && !groomPhoto.isEmpty()) {
                    out.println("<div class='form-group'>");
                    out.println("<label>Current Groom's Photo:</label>");
                    out.println("<div class='photo-box'>");
                    out.println("<img src='" + groomPhoto + "' alt='Current Groom Photo'>");
                    out.println("</div>");
                    out.println("</div>");
                }
                
                out.println("<div class='form-group'>");
                out.println("<label for='groomPhoto'>Update Groom's Photo (optional):</label>");
                out.println("<input type='file' name='groomPhoto' accept='image/*'>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='dateOfMarriage'>Date of Marriage:</label>");
                out.println("<input type='date' name='dateOfMarriage' value='" + rs.getString("dateOfMarriage") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='placeOfMarriage'>Place of Marriage:</label>");
                out.println("<input type='text' name='placeOfMarriage' value='" + rs.getString("placeOfMarriage") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='witness1Name'>Witness 1 Name:</label>");
                out.println("<input type='text' name='witness1Name' value='" + rs.getString("witness1Name") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='witness2Name'>Witness 2 Name:</label>");
                out.println("<input type='text' name='witness2Name' value='" + rs.getString("witness2Name") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='marriageCertificateNumber'>Marriage Certificate Number:</label>");
                out.println("<input type='text' name='marriageCertificateNumber' value='" + 
                    rs.getString("marriageCertificateNumber") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<input type='submit' name='update' value='Update Record'>");
                out.println("</div>");
                
                out.println("</form>");
                out.println("</div>");
            } else {
                out.println("<div class='error-message'>No record found for Marriage ID: " + marriageId + "</div>");
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
        }
    }

    private void updateMarriageRecord(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws ServletException, IOException {
        try {
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
            
            // Save bride's photo if updated
            if (bridePhotoPart != null && bridePhotoPart.getSize() > 0) {
                String fileName = marriageId + "_bride_" + getSubmittedFileName(bridePhotoPart);
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                
                bridePhotoPath = UPLOAD_DIR + File.separator + fileName;
                String fullPath = uploadPath + File.separator + fileName;
                bridePhotoPart.write(fullPath);
            }
            
            // Save groom's photo if updated
            if (groomPhotoPart != null && groomPhotoPart.getSize() > 0) {
                String fileName = marriageId + "_groom_" + getSubmittedFileName(groomPhotoPart);
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                
                groomPhotoPath = UPLOAD_DIR + File.separator + fileName;
                String fullPath = uploadPath + File.separator + fileName;
                groomPhotoPart.write(fullPath);
            }
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql;
            PreparedStatement pstmt;
            
            if (bridePhotoPath != null || groomPhotoPath != null) {
                sql = "UPDATE marriages SET brideName=?, groomName=?, dateOfMarriage=?, " +
                      "placeOfMarriage=?, witness1Name=?, witness2Name=?, marriageCertificateNumber=?";
                
                if (bridePhotoPath != null) {
                    sql += ", bridePhoto=?";
                }
                if (groomPhotoPath != null) {
                    sql += ", groomPhoto=?";
                }
                sql += " WHERE marriageId=?";
                
                pstmt = conn.prepareStatement(sql);
                int paramIndex = 1;
                pstmt.setString(paramIndex++, brideName);
                pstmt.setString(paramIndex++, groomName);
                pstmt.setString(paramIndex++, dateOfMarriage);
                pstmt.setString(paramIndex++, placeOfMarriage);
                pstmt.setString(paramIndex++, witness1Name);
                pstmt.setString(paramIndex++, witness2Name);
                pstmt.setString(paramIndex++, marriageCertificateNumber);
                
                if (bridePhotoPath != null) {
                    pstmt.setString(paramIndex++, bridePhotoPath);
                }
                if (groomPhotoPath != null) {
                    pstmt.setString(paramIndex++, groomPhotoPath);
                }
                pstmt.setString(paramIndex, marriageId);
            } else {
                sql = "UPDATE marriages SET brideName=?, groomName=?, dateOfMarriage=?, " +
                      "placeOfMarriage=?, witness1Name=?, witness2Name=?, marriageCertificateNumber=? " +
                      "WHERE marriageId=?";
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, brideName);
                pstmt.setString(2, groomName);
                pstmt.setString(3, dateOfMarriage);
                pstmt.setString(4, placeOfMarriage);
                pstmt.setString(5, witness1Name);
                pstmt.setString(6, witness2Name);
                pstmt.setString(7, marriageCertificateNumber);
                pstmt.setString(8, marriageId);
            }
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                out.println("<div class='success-message'>Marriage record updated successfully!</div>");
                
            
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