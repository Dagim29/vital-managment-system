import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

@WebServlet("/RegisterUser")
public class RegisterUser extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String submit = request.getParameter("submit");

        out.print("<html><head>");
        out.print("<title>Registration User</title>");
        out.print("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
        out.print("</head><body>");
        
        // Include Header
        RequestDispatcher rd = request.getRequestDispatcher("Header");
        rd.include(request, response);
        
        out.print("<div class='update-container'>");
        out.print("<h1 style='color:#cc6600'>User Registration Page</h1>");

        if (submit == null) {
            out.print("<form action='RegisterUser' method='post'>");
            out.print("User Name: <input type='text' name='username' required /><br>");
            out.print("Password: <input type='password' name='password' min='6' required /><br>");
            out.print("Role: <select name='role' required>");
            out.print("<option value='Select Role' selected>Select Role</option>");
            out.print("<option value='Admin'>Admin</option>");
            out.print("<option value='VMS Officer'>VMS Officer</option>");
            out.print("</select><br>");
            out.print("<input type='submit' name='submit' value='Register' class='btn-register' /><input type='reset' value='Clear' class='btn-clear'>");
            out.print("</form>");
            //out.print("</div>");
        } else {
            String encrypted_password = null;
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String role = request.getParameter("role");

            try {
                int KEY_LENGTH = 256;
                int ITERATION_COUNT = 65536;
                String secretKey = "12_win";
                String salt = "_gm+";

                SecureRandom secureRandom = new SecureRandom();
                byte[] iv = new byte[16];
                secureRandom.nextBytes(iv);
                IvParameterSpec ivspec = new IvParameterSpec(iv);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
                SecretKey tmp = factory.generateSecret(spec);
                SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
                byte[] cipherText = cipher.doFinal(password.getBytes("UTF-8"));
                byte[] encryptedData = new byte[iv.length + cipherText.length];
                System.arraycopy(iv, 0, encryptedData, 0, iv.length);
                System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
                encrypted_password = Base64.getEncoder().encodeToString(encryptedData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String driverName = "com.mysql.cj.jdbc.Driver";
            String dbUrl = "jdbc:mysql://localhost:3306/vms_db";
            String dbusername = "root";
            String dbpassword = "root";
            Connection conn = null;
            PreparedStatement ps = null;

            try {
                Class.forName(driverName);
                conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword);
                String sql = "INSERT INTO users (username,password,role,created_at,updated_at,status) VALUES (?, ?, ?, ?, ?, ? )";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, encrypted_password);
                ps.setString(3, role);
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
                ps.setTimestamp(5, new Timestamp(new Date().getTime()));
                ps.setString(6, "Active");
                int i = ps.executeUpdate();
                
                if (i > 0) {
                	 out.print("<div class='form-container'>");
                    out.println("<h3>The following user record has been inserted into the users table:</h3>");
                    out.println("<div class='form-group'>");
                    out.print("User Name: " + username + "<br>");
                    out.print("</div>");
                    out.println("<div class='form-group'>");
                    out.print("Role: " + role + "<br>");
                    out.print("</div>");
                    out.println("<div class='action-buttons'>");
                    out.println("<button onclick=\"window.location.href='RegisterUser'\">Close</button>");
                    out.println("</div>");
                    out.print("</div>");
                    out.print("</div>");
                } else {
                    out.println("<div class='error'>Failed to insert the data</div>");
                }
                RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
                footerDispatcher.include(request, response);
                
                out.println("</div></body></html>");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                out.println("<div class='error'>" + e.getMessage() + "</div>");
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        out.close(); // Ensure PrintWriter is closed
    }
}
