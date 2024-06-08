package com.idiot.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/bookList")
public class BookListServlet extends HttpServlet {
    private static final String bookDataQuery = "SELECT book_id, BOOK_NAME, AUTHOR_NAME, BOOK_TYPE, QUANTITY FROM BOOKDATA";
    private static final String bookIssueQuery = "SELECT book_id, STATUS FROM BOOKISSUE WHERE book_id = ?";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/book", "root", "Bhoo@4402");
             PreparedStatement bookDataPS = con.prepareStatement(bookDataQuery);
             PreparedStatement bookIssuePS = con.prepareStatement(bookIssueQuery)) {

            ResultSet bookDataRS = bookDataPS.executeQuery();

            pw.println("<!DOCTYPE html>");
            pw.println("<html>");
            pw.println("<head>");
            pw.println("<meta charset='ISO-8859-1'>");
            pw.println("<title>Book List</title>");
            pw.println("<style>");
            pw.println("body {");
            pw.println("    margin: 0;");
            pw.println("    padding: 0;");
            pw.println("    height: 100vh;");
            pw.println("    width: 100vw;");
            pw.println("    display: flex;");
            pw.println("    justify-content: center; /* Center horizontally */");
            pw.println("    align-items: center; /* Center vertically */");
            pw.println("    background-color: white;");
            pw.println("}");
            pw.println(".container-fluid.card {");
            pw.println("    width: 50%;"); // Adjusted to accommodate larger content
            pw.println("    background-color: white;");
            pw.println("    padding: 20px;");
            pw.println("    box-shadow: 0 0 10px rgba(0,0,0,0.5);");
            pw.println("    border-radius: 10px;");
            pw.println("    display: flex;");
            pw.println("    flex-direction: column;");
            pw.println("    position: absolute;");
            pw.println("    top: 50%;");
            pw.println("    left: 50%;");
            pw.println("    transform: translate(-50%, -50%);");
            pw.println("}");
            pw.println("h2.bg-danger.text-white.text-center {");
            pw.println("    background-color: #0000FF;");
            pw.println("    color: white;");
            pw.println("    text-align: center;");
            pw.println("    padding: 10px;");
            pw.println("    border-radius: 5px;");
            pw.println("    width: 100%;");
            pw.println("    box-sizing: border-box;");
            pw.println("}");
            pw.println(".table {");
            pw.println("    width: 100%;");
            pw.println("    border-collapse: collapse;");
            pw.println("}");
            pw.println(".table th, .table td {");
            pw.println("    text-align: center;");
            pw.println("    padding: 10px;");
            pw.println("    border: 1px solid #ddd;");
            pw.println("}");
            pw.println(".table th {");
            pw.println("    background-color: #0000FF;");
            pw.println("    color: white;");
            pw.println("}");
            pw.println(".table input[type='submit'],");
            pw.println(".table input[type='reset'] {");
            pw.println("    width: calc(50% - 10px);");
            pw.println("    background-color: #0000FF;");
            pw.println("    color: white;");
            pw.println("    border: none;");
            pw.println("    padding: 10px;");
            pw.println("    margin: 5px;");
            pw.println("    cursor: pointer;");
            pw.println("    border-radius: 5px;");
            pw.println("}");
            pw.println(".table input[type='submit']:hover,");
            pw.println(".table input[type='reset']:hover {");
            pw.println("    background-color: #0000cc;");
            pw.println("}");
            pw.println("</style>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<div class='container-fluid card'>");
            pw.println("<h2 class='bg-danger text-white text-center'>Book List</h2>");

            pw.println("<table class='table'>");
            pw.println("<tr>");
            pw.println("<th>Book Id</th>");
            pw.println("<th>Book Name</th>");
            pw.println("<th>Author Name</th>");
            pw.println("<th>Book Type</th>");
            pw.println("<th>Quantity</th>");
            pw.println("<th>Status</th>");
            pw.println("</tr>");

            while (bookDataRS.next()) {
                int bookId = bookDataRS.getInt(1);

                // Retrieve the book issue status
                bookIssuePS.setInt(1, bookId);
                ResultSet bookIssueRS = bookIssuePS.executeQuery();

                String status = "Not Issued";
                if (bookIssueRS.next()) {
                    status = bookIssueRS.getString("STATUS");
                }

                // Update the quantity based on the status
                int updatedQuantity = bookDataRS.getInt(5);
                if ("Issued".equals(status)) {
                    updatedQuantity -= 1;
                } else if ("Returned".equals(status)) {
                    updatedQuantity += 1;
                }

                pw.println("<tr>");
                pw.println("<td>" + bookDataRS.getInt(1) + "</td>");
                pw.println("<td>" + bookDataRS.getString(2) + "</td>");
                pw.println("<td>" + bookDataRS.getString(3) + "</td>");
                pw.println("<td>" + bookDataRS.getString(4) + "</td>");
                pw.println("<td>" + updatedQuantity + "</td>");
                pw.println("<td>" + status + "</td>");
                pw.println("</tr>");

                // Update the quantity in the BOOKDATA table
                updateBookQuantity(con, bookId, updatedQuantity);
            }

            pw.println("</table>");
            
            // Add the Home button
            pw.println("<form action='homeStart.html' method='get' style='text-align: center; margin-top: 20px;'>");
            pw.println("<input type='submit' value='Home' style='background-color: #0000FF; color: white; border: none; padding: 10px 20px; cursor: pointer; border-radius: 5px;' />");
            pw.println("</form>");

            pw.println("</div>");
            pw.println("</body>");
            pw.println("</html>");
        } catch (SQLException se) {
            se.printStackTrace();
            pw.println("<h1>" + se.getMessage() + "</h1>");
        } catch (Exception e) {
            e.printStackTrace();
            pw.println("<h1>" + e.getMessage() + "</h1>");
        }
    }

    private void updateBookQuantity(Connection con, int bookId, int updatedQuantity) throws SQLException {
        String updateQuantityQuery = "UPDATE BOOKDATA SET QUANTITY = ? WHERE book_id = ?";
        try (PreparedStatement updateQuantityPS = con.prepareStatement(updateQuantityQuery)) {
            updateQuantityPS.setInt(1, updatedQuantity);
            updateQuantityPS.setInt(2, bookId);
            updateQuantityPS.executeUpdate();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }
}