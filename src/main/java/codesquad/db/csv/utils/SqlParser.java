package codesquad.db.csv.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {

    public static Table parseCreateTable(String sql) {
        // 기본적인 CREATE TABLE 구문 파싱
        Pattern pattern = Pattern.compile("CREATE\\s+TABLE\\s+(\\w+)\\s*\\((.*?)\\)\\s*;", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            String columnDefinitions = matcher.group(2);

            Table table = new Table(tableName);

            // 컬럼 정의 파싱
            String[] columnDefs = columnDefinitions.split(",");
            for (String columnDef : columnDefs) {
                columnDef = columnDef.trim();
                String[] parts = columnDef.split("\\s+");
                if (parts.length >= 2) {
                    Column column = new Column(parts[0], parts[1]);

                    // PRIMARY KEY와 NOT NULL 체크
                    for (int i = 2; i < parts.length; i++) {
                        if (parts[i].equalsIgnoreCase("PRIMARY") && i + 1 < parts.length && parts[i + 1].equalsIgnoreCase("KEY")) {
                            column.setPrimaryKey(true);
                            i++; // "KEY" 건너뛰기
                        } else if (parts[i].equalsIgnoreCase("NOT") && i + 1 < parts.length && parts[i + 1].equalsIgnoreCase("NULL")) {
                            column.setNotNull(true);
                            i++; // "NULL" 건너뛰기
                        }
                    }

                    table.columns.add(column);
                }
            }

            return table;
        }

        throw new IllegalArgumentException("Invalid CREATE TABLE statement");
    }


    public static Table parseInsertTable(String sql) {
        // 기본적인 INSERT INTO 구문 파싱
        Pattern pattern = Pattern.compile("INSERT\\s+INTO\\s+(\\w+)\\s*\\((.*?)\\)\\s*VALUES\\s*\\(([^)]*)\\)\\s*;?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            String columnNames = matcher.group(2);
            String values = matcher.group(3);

            Table table = new Table(tableName);

            // 컬럼명 파싱
            String[] columnNamesArray = columnNames.split(",");
            for (String columnName : columnNamesArray) {
                table.columns.add(new Column(columnName.trim(), null));
            }

            // 값 파싱
            String[] valuesArray = values.split(",");
            for (int i = 0; i < valuesArray.length; i++) {
                Column column = table.columns.get(i);
                column.setType("string"); // 값이 숫자인지 문자열인지 판단할 수 없으므로 일단 문자열로 설정
                column.setValue(valuesArray[i].trim());
            }

            return table;
        }
        return null;
    }

    public static Table parseSelectTable(String sql) {
        // 기본적인 SELECT 구문 파싱
        Pattern pattern = Pattern.compile("SELECT\\s+(\\*|\\w+(?:\\s*,\\s*\\w+)*)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+?))?\\s*;?\\s*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
//            String columnNames = matcher.group(1);
            String tableName = matcher.group(2);

            Table table = new Table(tableName);

            // 컬럼명 파싱
//            String[] columnNamesArray = columnNames.split(",");
//            for (String columnName : columnNamesArray) {
//                table.columns.add(new Column(columnName.trim(), null));
//            }
            String where = matcher.group(3);
            if (where != null) {
                String[] whereArray = where.split("=");
                Column column = new Column(whereArray[0].trim(), null);
                column.setValue(whereArray[1].trim());
                table.columns.add(column);
            }


            return table;
        }
        return null;
    }

    public static Table parDeleteTable(String sql) {
        Pattern pattern = Pattern.compile("DELETE\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+?))?\\s*;?\\s*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            Table table = new Table(tableName);
            String where = matcher.group(2);
            if (where != null) {
                String[] whereArray = where.split("=");
                Column column = new Column(whereArray[0].trim(), null);
                column.setValue(whereArray[1].trim());
                table.columns.add(column);
            }
            return table;
        }

        return null;
    }
}
