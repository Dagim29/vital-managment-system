import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;

// For Encryption
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

@WebServlet("/ValidateUser")
public class ValidateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);

        PrintWriter out = response.getWriter();
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            // Removed submit parameter check since form submission is enough
            String status = "Active";
            String driverName = "com.mysql.cj.jdbc.Driver";
            String dbUrl = "jdbc:mysql://localhost:3306/vms_db";
            String dbUsername = "root";
            String dbPassword = "root";

            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                Class.forName(driverName);
                conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                String sql = "SELECT * FROM users WHERE username=? AND status=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, status);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    response.sendRedirect("main?wrongusername=true");
                    return; // Added return to prevent further execution
                }

                String utype = rs.getString("role");
                String uname = rs.getString("username");
                String pword = rs.getString("password");

                // Decrypt the password
                int KEY_LENGTH = 256;
                int ITERATION_COUNT = 65536;
                String secretKey = "12_win";
                String salt = "_gm+";

                byte[] encryptedValue = Base64.getDecoder().decode(pword);
                byte[] iv2 = new byte[16];
                System.arraycopy(encryptedValue, 0, iv2, 0, iv2.length);
                IvParameterSpec ivspec2 = new IvParameterSpec(iv2);

                SecretKeyFactory factory2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec2 = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
                SecretKey tmp2 = factory2.generateSecret(spec2);
                SecretKeySpec secretKeySpec2 = new SecretKeySpec(tmp2.getEncoded(), "AES");

                Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher2.init(Cipher.DECRYPT_MODE, secretKeySpec2, ivspec2);

                byte[] cipherText2 = new byte[encryptedValue.length - 16];
                System.arraycopy(encryptedValue, 16, cipherText2, 0, cipherText2.length);

                byte[] decryptedText2 = cipher2.doFinal(cipherText2);
                String decryptedValue = new String(decryptedText2, "UTF-8");

                if (!password.equals(decryptedValue)) {
                    response.sendRedirect("main?wrongpassword=true");
                    return; // Added return to prevent further execution
                }

                // If password matches, create session and redirect
                HttpSession session = request.getSession();
                session.setAttribute("utype", utype);
                session.setAttribute("uname", uname);

                // Debug print
                System.out.println("User type: " + utype);
                System.out.println("Username: " + uname);

                if ("VMS Officer".equals(utype)) {
                    response.sendRedirect("vms_officer_home");
                } else if ("Admin".equals(utype)) {
                    response.sendRedirect("Admin_home");
                } else {
                    response.sendRedirect("main?norole=true");
                }

            } catch (Exception e) {
                e.printStackTrace();
                out.println("Error: " + e.getMessage());
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        service(request, response);
    }
}