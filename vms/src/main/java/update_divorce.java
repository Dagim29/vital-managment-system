import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/update_divorce")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1 MB
    maxFileSize = 1024 * 1024 * 10,     // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class update_divorce extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "upload_files";
       
    public update_divorce() {
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
            out.println("<title>Update Divorce Record</title>");
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
            out.println("<h2>Search Divorce Record</h2>");
            out.println("<form method='post' action='update_divorce'>");
            out.println("<div class='form-group'>");
            out.println("<label for='searchDivorceId'>Enter Divorce ID:</label>");
            out.println("<input type='text' name='searchDivorceId' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<input type='submit' name='search' value='Search'>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div>");

            if (request.getParameter("search") != null) {
                String searchDivorceId = request.getParameter("searchDivorceId");
                displayUpdateForm(out, searchDivorceId);
            } else if (request.getParameter("update") != null) {
                updateDivorceRecord(request, response, out);
            }

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayUpdateForm(PrintWriter out, String divorceId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql = "SELECT * FROM divorces WHERE divorceId = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, divorceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                out.println("<div class='form-container'>");
                out.println("<h2>Update Divorce Record</h2>");
                out.println("<form method='post' action='update_divorce' enctype='multipart/form-data'>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='divorceId'>Divorce ID:</label>");
                out.println("<input type='text' name='divorceId' value='" + rs.getString("divorceId") + "' readonly>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='party1Name'>Party 1 Name:</label>");
                out.println("<input type='text' name='party1Name' value='" + rs.getString("party1Name") + "' required>");
                out.println("</div>");
                
                // Display current party1's photo if exists
                String party1Photo = rs.getString("party1Photo");
                if (party1Photo != null && !party1Photo.isEmpty()) {
                    out.println("<div class='form-group'>");
                    out.println("<label>Current Party 1 Photo:</label>");
                    out.println("<div class='photo-box'>");
                    out.println("<img src='" + party1Photo + "' alt='Current Party 1 Photo'>");
                    out.println("</div>");
                    out.println("</div>");
                }
                
                out.println("<div class='form-group'>");
                out.println("<label for='party1Photo'>Update Party 1 Photo (optional):</label>");
                out.println("<input type='file' name='party1Photo' accept='image/*'>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='party2Name'>Party 2 Name:</label>");
                out.println("<input type='text' name='party2Name' value='" + rs.getString("party2Name") + "' required>");
                out.println("</div>");
                
                // Display current party2's photo if exists
                String party2Photo = rs.getString("party2Photo");
                if (party2Photo != null && !party2Photo.isEmpty()) {
                    out.println("<div class='form-group'>");
                    out.println("<label>Current Party 2 Photo:</label>");
                    out.println("<div class='photo-box'>");
                    out.println("<img src='" + party2Photo + "' alt='Current Party 2 Photo'>");
                    out.println("</div>");
                    out.println("</div>");
                }
                
                out.println("<div class='form-group'>");
                out.println("<label for='party2Photo'>Update Party 2 Photo (optional):</label>");
                out.println("<input type='file' name='party2Photo' accept='image/*'>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='dateOfDivorce'>Date of Divorce:</label>");
                out.println("<input type='date' name='dateOfDivorce' value='" + rs.getString("dateOfDivorce") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label for='legalDocuments'>Legal Documents:</label>");
                out.println("<input type='text' name='legalDocuments' value='" + rs.getString("legalDocuments") + "' required>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<input type='submit' name='update' value='Update Record'>");
                out.println("</div>");
                
                out.println("</form>");
                out.println("</div>");
            } else {
                out.println("<div class='error-message'>No record found for Divorce ID: " + divorceId + "</div>");
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            out.println("<div class='error-message'>Error: " + e.getMessage() + "</div>");
        }
    }

    private void updateDivorceRecord(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws ServletException, IOException {
        try {
            String divorceId = request.getParameter("divorceId");
            String party1Name = request.getParameter("party1Name");
            String party2Name = request.getParameter("party2Name");
            String dateOfDivorce = request.getParameter("dateOfDivorce");
            String legalDocuments = request.getParameter("legalDocuments");
            
            // Handle file uploads
            Part party1PhotoPart = request.getPart("party1Photo");
            Part party2PhotoPart = request.getPart("party2Photo");
            
            String party1PhotoPath = null;
            String party2PhotoPath = null;
            
            // Save party1's photo if updated
            if (party1PhotoPart != null && party1PhotoPart.getSize() > 0) {
                String fileName = divorceId + "_party1_" + getSubmittedFileName(party1PhotoPart);
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                
                party1PhotoPath = UPLOAD_DIR + File.separator + fileName;
                String fullPath = uploadPath + File.separator + fileName;
                party1PhotoPart.write(fullPath);
            }
            
            // Save party2's photo if updated
            if (party2PhotoPart != null && party2PhotoPart.getSize() > 0) {
                String fileName = divorceId + "_party2_" + getSubmittedFileName(party2PhotoPart);
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                
                party2PhotoPath = UPLOAD_DIR + File.separator + fileName;
                String fullPath = uploadPath + File.separator + fileName;
                party2PhotoPart.write(fullPath);
            }
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/vms_db", "root", "root");
            
            String sql;
            PreparedStatement pstmt;
            
            if (party1PhotoPath != null || party2PhotoPath != null) {
                sql = "UPDATE divorces SET party1Name=?, party2Name=?, dateOfDivorce=?, " +
                      "legalDocuments=?";
                
                if (party1PhotoPath != null) {
                    sql += ", party1Photo=?";
                }
                if (party2PhotoPath != null) {
                    sql += ", party2Photo=?";
                }
                sql += " WHERE divorceId=?";
                
                pstmt = conn.prepareStatement(sql);
                int paramIndex = 1;
                pstmt.setString(paramIndex++, party1Name);
                pstmt.setString(paramIndex++, party2Name);
                pstmt.setString(paramIndex++, dateOfDivorce);
                pstmt.setString(paramIndex++, legalDocuments);
                
                if (party1PhotoPath != null) {
                    pstmt.setString(paramIndex++, party1PhotoPath);
                }
                if (party2PhotoPath != null) {
                    pstmt.setString(paramIndex++, party2PhotoPath);
                }
                pstmt.setString(paramIndex, divorceId);
            } else {
                sql = "UPDATE divorces SET party1Name=?, party2Name=?, dateOfDivorce=?, " +
                      "legalDocuments=? WHERE divorceId=?";
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, party1Name);
                pstmt.setString(2, party2Name);
                pstmt.setString(3, dateOfDivorce);
                pstmt.setString(4, legalDocuments);
                pstmt.setString(5, divorceId);
            }
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                out.println("<div class='success-message'>Divorce record updated successfully!</div>");
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