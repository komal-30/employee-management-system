package employee.management.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/employee_management?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";  // Change to your MySQL username
    private static final String PASSWORD = "Komal@325740";  // Change to your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

