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

@WebServlet("/login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String studentId = request.getParameter("student_id");
        String password = request.getParameter("password");

        // Validate login
        if (validateLogin(studentId, password)) {
            // Store student ID in session
            request.getSession().setAttribute("studentId", studentId);

            // Redirect to student.html
            response.sendRedirect("student.html");
        } else {
            // Display signup form
            response.sendRedirect("signup.html");
        }
    }

    private boolean validateLogin(String studentId, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/book", "root", "Bhoo@4402")) {
            String query = "SELECT * FROM student WHERE student_id = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, studentId);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // If a row is found, login is successful
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


