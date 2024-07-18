package codesquad.db.csv;

import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class CsvDriver implements Driver {
    static {
        try {
            DriverManager.registerDriver(new CsvDriver());
        } catch (SQLException e) {
            throw new RuntimeException("Can't register driver!", e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        return new CsvConnection(url, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url.startsWith("jdbc:csv:")) {
            String path = url.split("jdbc:csv:")[1];
            if (path.startsWith("~/")) {
                path = System.getProperty("user.home") + "/" + path.split("~/")[1];
            }

            // 마지막 ; 확인 후 제거
            if (path.endsWith(";")) {
                path = path.substring(0, path.length() - 1);
            }

            // 디렉토리가 존재하는지 확인
            File dir = new File(path);
            if (!dir.exists()) {
                boolean success = dir.mkdirs();
                if (!success) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
