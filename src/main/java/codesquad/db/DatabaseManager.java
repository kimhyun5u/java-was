package codesquad.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test;";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
}
