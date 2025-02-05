import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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

@WebServlet("/UpdateUser")
public class UpdateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            String search = request.getParameter("search");
            String update = request.getParameter("update");
            String uname = request.getParameter("username");
            String pword = request.getParameter("password");
            String utype = request.getParameter("usertype");
            String ustatus = request.getParameter("userstatus");

            out.print("<html><head>");
            out.print("<title>Update User Information</title>");
            out.print("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.print("</head><body>");
            
            // Include Header
            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);
            
            out.print("<div class='update-container'>");
            out.print("<h1>User Information Update Form</h1>");
            out.print("<form action='UpdateUser' method='POST'>");
            out.print("Search By User Name: <input type='text' name='username' required />");
            out.print("<br>");
            out.print("<input type='submit' name='search' value='Search' />");
            out.print("<input type='reset' value='Reset' />");
            out.print("</form>");

            String driverName = "com.mysql.cj.jdbc.Driver";
            String dbUrl = "jdbc:mysql://localhost:3306/vms_db";
            String dbusername = "root";
            String dbpassword = "root";
            Connection conn = null;
            Statement stmt = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                Class.forName(driverName);
                conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword);
                stmt = conn.createStatement();

                if (search != null && update == null) {
                    String encrypted_password = null, decrypted_password = null;
                    String sql = "SELECT * FROM users WHERE username='" + uname + "'";
                    rs = stmt.executeQuery(sql);

                    if (!rs.next()) {
                        out.print("<font color='red'>There is no user information registered with username " + uname + "</font>");
                    } else {
                        String user_name = null, user_type = null, user_status = null;
                        user_name = rs.getString("username");
                        encrypted_password = rs.getString("password");
                        user_type = rs.getString("role");
                        user_status = rs.getString("status");

                        // Decrypt the password
                        int KEY_LENGTH = 256;
                        int ITERATION_COUNT = 65536;
                        String secretKey = "12_win";
                        String salt = "_gm+";

                        byte[] encryptedvalue = Base64.getDecoder().decode(encrypted_password);
                        byte[] iv2 = new byte[16];
                        System.arraycopy(encryptedvalue, 0, iv2, 0, iv2.length);
                        IvParameterSpec ivspec2 = new IvParameterSpec(iv2);
                        SecretKeyFactory factory2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                        KeySpec spec2 = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
                        SecretKey tmp2 = factory2.generateSecret(spec2);
                        SecretKeySpec secretKeySpec2 = new SecretKeySpec(tmp2.getEncoded(), "AES");
                        Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
                        cipher2.init(Cipher.DECRYPT_MODE, secretKeySpec2, ivspec2);
                        byte[] cipherText2 = new byte[encryptedvalue.length - 16];
                        System.arraycopy(encryptedvalue, 16, cipherText2, 0, cipherText2.length);
                        byte[] decryptedText2 = cipher2.doFinal(cipherText2);
                        decrypted_password = new String(decryptedText2, "UTF-8");

                        out.print("<form action='UpdateUser' method='post'>");
                        out.print("User Name: <input type='text' name='username' value='" + user_name + "' readonly /><br>");
                        out.print("Password: <input type='text' name='password' value='" + decrypted_password + "' required /><br>");
                        out.print("User Type: <select name='usertype' required >");
                        out.print("<option value='Admin'" + ("Admin".equals(user_type) ? " selected" : "") + ">Admin</option>");
                        out.print("<option value='vms Officer'" + ("VMS Officer".equals(user_type) ? " selected" : "") + ">VMS Officer</option>");
                        out.print("</select><br>");
                        out.print("User Status: <select name='userstatus' required >");
                        out.print("<option value='Active'" + ("Active".equals(user_status) ? " selected" : "") + ">Active</option>");
                        out.print("<option value='Blocked'" + ("Blocked".equals(user_status) ? " selected" : "") + ">Blocked</option>");
                        out.print("</select><br>");
                        out.print("<input type='submit' name='update' value='Update' /><input type='reset' value='Clear'>");
                        out.print("</form>");
                    }
                } else if (search == null && update != null) {
                    String encrypted_password = null;
                    int KEY_LENGTH = 256;
                    int ITERATION_COUNT = 65536;
                    String secretKey = "12_win";
                    String salt = "_gm+";

                    // Encrypt the password
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
                    byte[] cipherText = cipher.doFinal(pword.getBytes("UTF-8"));
                    byte[] encryptedData = new byte[iv.length + cipherText.length];
                    System.arraycopy(iv, 0, encryptedData, 0, iv.length);
                    System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
                    encrypted_password = Base64.getEncoder().encodeToString(encryptedData);

                    String updatesql = "UPDATE users SET password=?, role=?, status=? WHERE username=?";
                    ps = conn.prepareStatement(updatesql);
                    ps.setString(1, encrypted_password);
                    ps.setString(2, utype);
                    ps.setString(3, ustatus);
                    ps.setString(4, uname);
                    int updated = ps.executeUpdate();

                    if (updated > 0) {
                        out.println("<font color='green'>Updated successfully</font>");
                        out.print("<form action='UpdateUser' method='post'>");
                        out.print("User Name: <input type='text' name='username' value='" + uname + "' readonly /><br>");
                        out.print("Password: <input type='text' name='password' value='" + pword + "' readonly /><br>");
                        out.print("User Type: <input type='text' name='usertype' value='" + utype + "' readonly /><br>");
                        out.print("Status: <input type='text' name='userstatus' value='" + ustatus + "' readonly /><br>");
                        out.print("<input type='submit' value='Close' />");
                        out.print("</form>");
                    } else {
                        out.println("<font color='red'>Failed to update</font>");
                    }
                }
            } catch (Exception e) {
                out.print(e.getMessage());
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
         // Include Footer
            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);
            out.print("</div></body></html>");
        } catch (ServletException e) {
            // Handle servlet specific exceptions
            request.setAttribute("error", "Servlet Error: " + e.getMessage());
            RequestDispatcher errorDispatcher = request.getRequestDispatcher("ErrorHandler");
            errorDispatcher.forward(request, response);
        } catch (IOException e) {
            // Handle IO exceptions
            request.setAttribute("error", "IO Error: " + e.getMessage());
            RequestDispatcher errorDispatcher = request.getRequestDispatcher("ErrorHandler");
            errorDispatcher.forward(request, response);
        } catch (Exception e) {
            // Handle all other exceptions
            request.setAttribute("error", "System Error: " + e.getMessage());
            RequestDispatcher errorDispatcher = request.getRequestDispatcher("ErrorHandler");
            errorDispatcher.forward(request, response);
         
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}