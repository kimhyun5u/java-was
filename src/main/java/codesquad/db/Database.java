package codesquad.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static final Map<String, Map<Object, Object>> db = new ConcurrentHashMap<>(); // 지워질 코드>

    static {
        db.put("users", new ConcurrentHashMap<>());
        db.put("sessions", new ConcurrentHashMap<>());
    }

    private Database() {
    }

    public static void add(String dbname, Object key, Object value) {
        db.get(dbname).putIfAbsent(key, value);
    }

    public static Optional<Object> get(String dbname, Object key) {
        return Optional.ofNullable(db.get(dbname).get(key));
    }
}
