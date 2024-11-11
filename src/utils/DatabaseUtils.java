package utils;

import java.sql.*;

public class DatabaseUtils {
    // 修改连接URL，添加时区和SSL设置
    private static final String URL = "jdbc:mysql://localhost:3306/student_db?useSSL=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "admin" ;

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}