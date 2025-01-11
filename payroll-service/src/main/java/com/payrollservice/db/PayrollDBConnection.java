package com.payrollservice.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.sql.Driver;

public class PayrollDBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/payroll_service";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root"; // Change this to your MySQL password

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            // Register the driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully!");

            // List all registered drivers
            listRegisteredDrivers();

            // Establish connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connection established successfully!");

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return connection;
    }

    private static void listRegisteredDrivers() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        System.out.println("List of registered JDBC drivers:");
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            System.out.println("  " + driver.getClass().getName());
        }
    }
}
