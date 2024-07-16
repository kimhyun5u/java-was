package codesquad.db;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Database.class);
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test;";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";
    private static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
        }
    }

    private Database() {
    }

    public static PreparedStatement getPreparedStatement(String sql) {
        try {
            return conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Fail to get prepared statement", e);
        }
    }
}
