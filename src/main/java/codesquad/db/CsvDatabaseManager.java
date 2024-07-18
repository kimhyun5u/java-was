package codesquad.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CsvDatabaseManager {
    // jdbc 를 통해 csv 파일에 접근하는 방법
//    private static final String DB_URL = "jdbc:h2:mem:~/test;";
    private static final String DB_URL = "jdbc:csv:~/jdbc_csv;";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";
    private static final String CSV_FILE_PATH = "~/jdbc_csv";

    private CsvDatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("codesquad.db.csv.CsvDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL);
//            List<String[]> csvData = readCsvFile(CSV_FILE_PATH);
//            if (csvData.isEmpty()) {
//                return null;
//            }
//
//            String[] headers = csvData.get(0);
//            createTable(connection, "csv_data", headers);
//            insertData(connection, "csv_data", headers, csvData.subList(1, csvData.size()));

            System.out.println("Data inserted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    private static List<String[]> readCsvFile(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // CSV 파일이 쉼표로 구분되어 있다고 가정
                String[] values = line.split(",");
                data.add(values);
            }
        }
        return data;
    }

    private static void createTable(Connection conn, String tableName, String[] columns) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i].replaceAll("\\s", "_")).append(" VARCHAR(255)");
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(")");

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
        }
    }

    private static void insertData(Connection conn, String tableName, String[] columns, List<String[]> data) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i].replaceAll("\\s", "_"));
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            sql.append("?");
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (String[] row : data) {
                for (int i = 0; i < row.length; i++) {
                    pstmt.setString(i + 1, row[i]);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
}
