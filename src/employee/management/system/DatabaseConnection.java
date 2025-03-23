package employee.management.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/employee_management?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";  //Change to username here
    private static final String PASSWORD = "password";  // Change to your MySQL password here

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

