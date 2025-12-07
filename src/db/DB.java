package db;

import java.sql.*;

public class DB {
    private static final String URL  =
            "jdbc:mysql://localhost:3306/csc380Project?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "password";

    private DB() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}


