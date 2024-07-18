package codesquad.db.csv.utils;


public class Column {
    String name;
    String type;
    boolean isPrimaryKey;
    boolean isNotNull;

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
