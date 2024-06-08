package com.idiot.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final String query = "INSERT INTO BOOKDATA(BOOK_NAME,AUTHOR_NAME,BOOK_TYPE,QUANTITY) VALUES(?,?,?,?)";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //get PrintWriter
        PrintWriter pw = res.getWriter();
        //set content type
        res.setContentType("text/html");
        //GET THE book info
        String bookName = req.getParameter("bookName");
        String authorName = req.getParameter("authorName");
        String bookType = req.getParameter("bookType");
        Integer quantity = Integer.parseInt(req.getParameter("quantity"));
        //LOAD jdbc driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }
        //generate the connection
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/book","root","Bhoo@4402"); PreparedStatement ps = con.prepareStatement(query);) {
            ps.setString(1, bookName);
            ps.setString(2, authorName);
            ps.setString(3, bookType);
            ps.setInt(4, quantity);
            int count = ps.executeUpdate();
            if (count == 1) {
            	req.getRequestDispatcher("homeStart.html").include(req, res);
        		pw.println("<div class='container'>");
        		req.getRequestDispatcher("bookList").include(req, res);
            } else {
            	req.getRequestDispatcher("homeStart.html").include(req, res);
                pw.println("<h2>Record not Registered Sucessfully");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            pw.println("<h1>" + se.getMessage() + "</h2>");
        } catch (Exception e) {
            e.printStackTrace();
            pw.println("<h1>" + e.getMessage() + "</h2>");
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }
}

