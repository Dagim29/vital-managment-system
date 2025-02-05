import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/backup_database")
public class backup_database extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);
        
        HttpSession session = request.getSession(true);
        if (session.getAttribute("uname") == null || 
            !session.getAttribute("utype").equals("Admin")) {
            response.sendRedirect("main");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Database Backup</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles/main.css'>");
            out.println("</head>");
            out.println("<body>");

            RequestDispatcher rd = request.getRequestDispatcher("Header");
            rd.include(request, response);

            out.println("<div class='container'>");
            out.println("<h2>Database Backup</h2>");

            if (request.getParameter("action") != null && 
                request.getParameter("action").equals("backup")) {
                performBackup(out);
            }

            // Backup form
            out.println("<div class='form-container'>");
            out.println("<form method='post' action='backup_database'>");
            out.println("<input type='hidden' name='action' value='backup'>");
            out.println("<div class='form-group'>");
            out.println("<p>Click the button below to create a backup of the database.</p>");
            out.println("<input type='submit' value='Create Backup'>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div>");

            // Display previous backups
            displayBackupHistory(out);

            out.println("</div>");

            RequestDispatcher footerDispatcher = request.getRequestDispatcher("Footer");
            footerDispatcher.include(request, response);

            out.println("</body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void performBackup(PrintWriter out) {
        try {
            String backupPath = getServletContext().getRealPath("/") + "backups/";
            File backupDir = new File(backupPath);
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String backupFileName = "backup_" + dateFormat.format(new Date()) + ".sql";
            String backupFile = backupPath + backupFileName;

            // MySQL backup command
            String[] command = new String[]{
                "mysqldump",
                "-u", "root",
                "-proot",
                "vms_db",
                "-r",
                backupFile
            };

            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                out.println("<div class='success-message'>Backup created successfully: " + 
                           backupFileName + "</div>");
            } else {
                out.println("<div class='error-message'>Backup failed</div>");
            }
        } catch (Exception e) {
            out.println("<div class='error-message'>Error creating backup: " + 
                       e.getMessage() + "</div>");
        }
    }

    private void displayBackupHistory(PrintWriter out) {
        String backupPath = getServletContext().getRealPath("/") + "backups/";
        File backupDir = new File(backupPath);
        File[] backupFiles = backupDir.listFiles();

        out.println("<div class='table-container'>");
        out.println("<h3>Backup History</h3>");
        out.println("<table>");
        out.println("<tr><th>Backup File</th><th>Size</th><th>Date</th><th>Action</th></tr>");

        if (backupFiles != null) {
            for (File file : backupFiles) {
                if (file.isFile() && file.getName().endsWith(".sql")) {
                    out.println("<tr>");
                    out.println("<td>" + file.getName() + "</td>");
                    out.println("<td>" + (file.length() / 1024) + " KB</td>");
                    out.println("<td>" + new Date(file.lastModified()) + "</td>");
                    out.println("<td><a href='backups/" + file.getName() + 
                               "' download>Download</a></td>");
                    out.println("</tr>");
                }
            }
        }

        out.println("</table>");
        out.println("</div>");
    }
}