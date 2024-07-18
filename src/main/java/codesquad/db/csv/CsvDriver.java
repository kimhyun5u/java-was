package codesquad.db.csv;

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
        return url.startsWith("jdbc:csv:");
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