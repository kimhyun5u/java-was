package codesquad.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static final Map<String, Map<Object, Object>> db = new ConcurrentHashMap<>();

    static {
        db.put("users", new ConcurrentHashMap<>());
        db.put("sessions", new ConcurrentHashMap<>());
    }

    private Database() {
    }

    public static void add(String dbname, Object key, Object value) {
        db.compute(dbname, (k, v) -> {
            if (v == null) {
                v = new ConcurrentHashMap<>();
            }
            v.put(key, value);
            return v;
        });
    }

    public static Optional<Object> get(String dbname, Object key) {
        return Optional.ofNullable(db.getOrDefault(dbname, new ConcurrentHashMap<>()).get(key));
    }

    public static void remove(String dbname, Object key) {
        db.computeIfPresent(dbname, (k, v) -> {
            v.remove(key);
            return v;
        });
    }

    public static List<Object> getList(String dbname) {
        return new ArrayList<>(db.getOrDefault(dbname, new ConcurrentHashMap<>()).values());
    }
}
