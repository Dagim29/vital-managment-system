import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/register_divorce")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1 MB
    maxFileSize = 1024 * 1024 * 10,     // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class register_divorce extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "upload_files";
       
    public register_divorce() {
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
            out.println("<title>Register Divorce</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            if (request.getParameter("submit") != null) {
                // Handle form submission
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
                
                // Save party1's photo
                if (party1PhotoPart != null && party1PhotoPart.getSize() > 0) {
                    String fileName = divorceId + "_party1_" + getSubmittedFileName(party1PhotoPart);
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    party1PhotoPath = UPLOAD_DIR + File.separator + fileName;
                    String fullPath = uploadPath + File.separator + fileName;
                    party1PhotoPart.write(fullPath);
                }
                
                // Save party2's photo
                if (party2PhotoPart != null && party2PhotoPart.getSize() > 0) {
                    String fileName = divorceId + "_party2_" + getSubmittedFileName(party2PhotoPart);
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    
                    party2PhotoPath = UPLOAD_DIR + File.separator + fileName;
                    String fullPath = uploadPath + File.separator + fileName;
                    party2PhotoPart.write(fullPath);
                }
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/vms_db", "root", "root");
                    
                    String sql = "INSERT INTO divorces (divorceId, party1Name, party2Name, " +
                                "dateOfDivorce, legalDocuments, party1Photo, party2Photo) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, divorceId);
                    pstmt.setString(2, party1Name);
                    pstmt.setString(3, party2Name);
                    pstmt.setString(4, dateOfDivorce);
                    pstmt.setString(5, legalDocuments);
                    pstmt.setString(6, party1PhotoPath);
                    pstmt.setString(7, party2PhotoPath);
                    
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        out.println("<div class='success-message'>Divorce record registered successfully!</div>");

                        // Display the registered information after form submission
                        out.println("<div class='form-container'>");
                        out.println("<h3>Registered Divorce Information:</h3>");
                        out.println("<ul>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Divorce ID:</strong> " + divorceId + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Party 1 Name:</strong> " + party1Name + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Party 2 Name:</strong> " + party2Name + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Date of Divorce:</strong> " + dateOfDivorce + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Legal Documents Reference:</strong> " + legalDocuments + "</li>");
                        out.println("</div>");
                        out.println("<div class='form-group'>");
                        out.println("<li><strong>Party 1 Photo:</strong> <img src='" + party1PhotoPath + "' alt='Party 1 Photo' width='100' height='100'></li>");
                        out.println("<li><strong>Party 2 Photo:</strong> <img src='" + party2PhotoPath + "' alt='Party 2 Photo' width='100' height='100'></li>");
                        out.println("</ul>");
                       

                        // Close button to reload the page and go back to the form
                        out.println("<div class='form-group'>");
                        out.println("<button onclick=\"window.location.href='register_divorce'\">Close</button>");
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
            out.println("<h2>Register Divorce</h2>");
            out.println("<form method='post' action='register_divorce' enctype='multipart/form-data'>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='divorceId'>Divorce ID:</label>");
            out.println("<input type='text' name='divorceId' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='party1Name'>Party 1 Name:</label>");
            out.println("<input type='text' name='party1Name' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='party1Photo'>Party 1 Photo:</label>");
            out.println("<input type='file' name='party1Photo' accept='image/*' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='party2Name'>Party 2 Name:</label>");
            out.println("<input type='text' name='party2Name' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='party2Photo'>Party 2 Photo:</label>");
            out.println("<input type='file' name='party2Photo' accept='image/*' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='dateOfDivorce'>Date of Divorce:</label>");
            out.println("<input type='date' name='dateOfDivorce' required>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<label for='legalDocuments'>Legal Documents Reference:</label>");
            out.println("<input type='text' name='legalDocuments' required>");
            out.println("<small>Enter reference numbers of court orders/legal documents</small>");
            out.println("</div>");
            
            out.println("<div class='form-group'>");
            out.println("<input type='submit' name='submit' value='Register Divorce'>");
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