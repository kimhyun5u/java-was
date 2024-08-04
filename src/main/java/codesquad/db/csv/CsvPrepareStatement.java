package codesquad.db.csv;

import codesquad.db.csv.utils.Column;
import codesquad.db.csv.utils.ShardingInfo;
import codesquad.db.csv.utils.SqlParser;
import codesquad.db.csv.utils.Table;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class CsvPrepareStatement implements PreparedStatement {
    private Connection conn;
    private String sql;
    private Table table;
    private List<Map<String, Object>> resultSet;

    public CsvPrepareStatement(Connection conn, String sql) {
        this.conn = conn;
        this.sql = sql;

        if (sql.toUpperCase().startsWith("INSERT")) {
            table = SqlParser.parseInsertTable(sql);
        } else if (sql.toUpperCase().startsWith("SELECT")) {
            table = SqlParser.parseSelectTable(sql);
        } else if (sql.toUpperCase().startsWith("DELETE")) {
            table = SqlParser.parDeleteTable(sql);
        } else {
            throw new RuntimeException("Not Support SQL");
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        return 0;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {

    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {

    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        table.getColumns().get(parameterIndex - 1).setValue(String.valueOf(x));
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        table.getColumns().get(parameterIndex - 1).setValue(String.valueOf(x));
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        table.getColumns().get(parameterIndex - 1).setValue(String.valueOf(x));
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        table.getColumns().get(parameterIndex - 1).setValue(String.valueOf(x));
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        table.getColumns().get(parameterIndex - 1).setValue(String.valueOf(x));
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        table.getColumns().get(parameterIndex - 1).setValue(String.valueOf(x));
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {

    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        table.getColumns().get(parameterIndex - 1).setValue(String.valueOf(x));
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {

    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {

    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void clearParameters() throws SQLException {

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {

    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {

    }

    @Override
    public boolean execute() throws SQLException {
        String filePath = System.getProperty("user.home") + "/jdbc_csv/" + table.getName() + ".csv";

        File file = new File(filePath);

        // 파일 존재 검증
        if (!file.exists()) {
            throw new SQLException("Table not found");
        }

        if (sql.toUpperCase().startsWith("INSERT")) {
            Long shardingId = ShardingInfo.getShardingId(table.getName());
            Long shardingNum = shardingId / ShardingInfo.getShardingSize();
            // 파일 쓰기
            // ID 정책을 항상 +1 이고 메모리에 마지막 ID를 저장해두고 사용하면
            // 삽입 비용을 절약할 수 있다.

            // 마지막 샤딩 ID에 맞는 CSV 파일을 읽어서 삽입할 데이터를 정렬된 순서로 삽입
            File shardingFile = new File(System.getProperty("user.home") + "/jdbc_csv/" + table.getName() + (shardingId < ShardingInfo.getShardingSize() ? "" : "_" + shardingNum) + ".csv");

            if (!shardingFile.exists()) {
                try {
                    shardingFile.createNewFile();
                    // file 에서 헤더를 읽고 넣어주기
                    try (var writer = new BufferedWriter(new FileWriter(shardingFile))) {
                        try (var reader = new BufferedReader(new FileReader(file))) {
                            writer.write(reader.readLine() + System.lineSeparator());
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // 모두 읽은 다음에 정렬
            String header;
            PriorityQueue<String> lines = new PriorityQueue<>();
            try (var reader = new BufferedReader(new FileReader(shardingFile))) {
                String line;
                // 헤더 읽기
                header = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    lines.add(line + System.lineSeparator());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            // 삽입할 데이터 생성
            String insertValue = table.getColumns().stream().map(Column::getValue).collect(Collectors.joining(",")) + System.lineSeparator();
            lines.add(insertValue);


            // 삽입할 데이터를 정렬된 순서로 파일에 쓰기
            try (var writer = new BufferedWriter(new FileWriter(shardingFile))) {
                writer.write(header + System.lineSeparator());
                while (!lines.isEmpty()) {
                    writer.write(lines.poll());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 삽입한 데이터의 ID를 ShardingInfo 에 저장
            ShardingInfo.updateShardingInfo(table.getName());

        } else if (sql.toUpperCase().startsWith("SELECT")) {
            // id 값으로 찾을 때 sharding size 로 나누어서 파일을 읽어서 찾는다.
            if ("id".equals(table.getColumns().get(0).getName())) {
                // sharding size 로 나눈 몫으로 파일 읽기
                Long targetId = Long.parseLong(table.getColumns().get(0).getValue());
                Long shardingNum = Long.parseLong(table.getColumns().get(0).getValue()) / ShardingInfo.getShardingSize();
                File shardingFile = new File(System.getProperty("user.home") + "/jdbc_csv/" + table.getName() + (targetId < ShardingInfo.getShardingSize() ? "" : "_" + shardingNum) + ".csv");
                List<Map<String, Object>> resultSet = new ArrayList<>();

                if (!shardingFile.exists()) {
                    this.resultSet = resultSet;
                    return true;
                }

                try (var reader = new BufferedReader(new FileReader(shardingFile))) {
                    String line;
                    line = reader.readLine();
                    String[] headers = line.split(",");

                    while ((line = reader.readLine()) != null) {
                        Map<String, Object> result = new HashMap<>();
                        String[] values = line.split(",");
                        if (!values[0].equals(targetId.toString())) {
                            continue;
                        }
                        for (int i = 0; i < headers.length; i++) {
                            result.put(headers[i], values[i]);
                        }
                        resultSet.add(result);
                        break;
                    }
                    this.resultSet = resultSet;

                    return true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // id 값이 아닐 때 전체 파일을 읽어서 찾는다.
            Long shardingId = ShardingInfo.getShardingId(table.getName());
            Long maxShardingNum = ShardingInfo.getShardingId(table.getName()) / ShardingInfo.getShardingSize() + 1;
            List<Map<String, Object>> resultSet = new ArrayList<>();
            for (Long shardingNum = 0L; shardingNum < maxShardingNum; shardingNum++) {
                File shardingFile = new File(System.getProperty("user.home") + "/jdbc_csv/" + table.getName() + (shardingId < ShardingInfo.getShardingSize() ? "" : "_" + shardingNum) + ".csv");
                try (var reader = new BufferedReader(new FileReader(shardingFile))) {
                    String line;
                    line = reader.readLine();
                    String[] headers = line.split(",");

                    if (table.getColumns().isEmpty()) {
                        while ((line = reader.readLine()) != null) {
                            Map<String, Object> result = new HashMap<>();
                            String[] values = line.split(",");
                            for (int i = 0; i < headers.length; i++) {
                                result.put(headers[i], values[i]);
                            }
                            resultSet.add(result);
                        }
                        continue;
                    }

                    Column column = table.getColumns().get(0);// param

                    // id 컬럼의 인덱스 찾기
                    int idIndex = Arrays.asList(headers).indexOf(column.getName());

                    while ((line = reader.readLine()) != null) {
                        String value = line.split(",")[idIndex];
                        if (value.equals(column.getValue())) {
                            break;
                        }
                    }

                    Map<String, Object> result = new HashMap<>();
                    if (line != null) {
                        String[] values = line.split(",");
                        for (int i = 0; i < headers.length; i++) {
                            result.put(headers[i], values[i]);
                        }
                        resultSet.add(result);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.resultSet = resultSet;

            return true;
        } else if (sql.toUpperCase().startsWith("UPDATE")) {

        } else if (sql.toUpperCase().startsWith("DELETE")) {
            try (var reader = new BufferedReader(new FileReader(file))) {
                String line;
                line = reader.readLine();
                String[] headers = line.split(",");

                Column column = table.getColumns().get(0);// param

                // id 컬럼의 인덱스 찾기
                int idIndex = Arrays.asList(headers).indexOf(column.getName());

                List<String> lines = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String value = line.split(",")[idIndex];
                    if (!value.equals(column.getValue())) {
                        lines.add(line);
                    }
                }

                try (var writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(Arrays.stream(headers).collect(Collectors.joining(",")) + System.lineSeparator());
                    for (String l : lines) {
                        writer.write(l + System.lineSeparator());
                    }
                } catch (IOException e) {
                    throw new SQLException(e);
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public void addBatch() throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {

    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {

    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {

    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {

    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {

    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {

    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {

    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return 0;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return new CsvResultSet(resultSet);
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
