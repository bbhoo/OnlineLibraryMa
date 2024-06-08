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

@WebServlet("/search")
public class SearchBook extends HttpServlet {
    private static final String query = "SELECT * FROM BOOKDATA WHERE BOOK_TYPE=?";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // get PrintWriter
        PrintWriter pw = res.getWriter();
        // set content type
        res.setContentType("text/html");

        // Get the book type parameter from the request
        String bookType = req.getParameter("bookType");

        // Check if bookType is null or empty
        if (bookType == null || bookType.trim().isEmpty()) {
            pw.println("<h2>Please provide a valid book type for the search.</h2>");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }

        // generate the connection
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/book", "root", "Bhoo@4402");
             PreparedStatement ps = con.prepareStatement(query);) {

            // Set the book type parameter in the prepared statement
            ps.setString(1, bookType);

            ResultSet rs = ps.executeQuery();
            req.getRequestDispatcher("student.html").include(req, res);
            
            pw.println("<!DOCTYPE html>");
            pw.println("<html>");
            pw.println("<head>");
            pw.println("<meta charset='ISO-8859-1'>");
            pw.println("<title>Search Book</title>");
            pw.println("<style>");
            pw.println("body {");
            pw.println("    margin: 0;");
            pw.println("    padding: 0;");
            pw.println("    height: 100vh;");
            pw.println("    width: 100vw;");
            pw.println("    display: flex;");
            pw.println("    justify-content: center;");
            pw.println("    align-items: center;");
            pw.println("    background-color: white;");
            pw.println("}");
            pw.println(".container {");
            pw.println("    width: 70%;");
            pw.println("    background-color: white;");
            pw.println("    padding: 20px;");
            pw.println("    box-shadow: 0 0 10px rgba(0,0,0,0.5);");
            pw.println("    border-radius: 10px;");
            pw.println("}");
            pw.println("h2 {");
            pw.println("    background-color: #0000FF;");
            pw.println("    color: white;");
            pw.println("    text-align: center;");
            pw.println("    padding: 10px;");
            pw.println("    border-radius: 5px;");
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
            pw.println("</style>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<div class='container'>");
            pw.println("<h2>Search Results</h2>");
            pw.println("<table class='table'>");
            pw.println("<tr>");
            pw.println("<th>Book Id</th>");
            pw.println("<th>Book Name</th>");
            pw.println("<th>Author Name</th>");
            pw.println("<th>Book Type</th>");
            pw.println("</tr>");

            while (rs.next()) {
                pw.println("<tr>");
                pw.println("<td>" + rs.getInt(1) + "</td>");
                pw.println("<td>" + rs.getString(2) + "</td>");
                pw.println("<td>" + rs.getString(3) + "</td>");
                pw.println("<td>" + rs.getString(4) + "</td>");
                pw.println("</tr>");
            }

            pw.println("</table>");
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }
}
