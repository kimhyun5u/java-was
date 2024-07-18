package codesquad.db.csv.utils;

import java.util.ArrayList;
import java.util.List;

public class Table {
    String name;
    List<Column> columns = new ArrayList<>();

    public Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }
}
