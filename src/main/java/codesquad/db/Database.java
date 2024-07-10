package codesquad.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    private static final Map<String, Map<Object, Object>> db = new ConcurrentHashMap<>(); // 지워질 코드>
    private static final Map<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
    static {
        db.put("users", new ConcurrentHashMap<>());
        db.put("sessions", new ConcurrentHashMap<>());
    }

    private Database() {
    }

    public static void add(String dbname, Object key, Object value) {
        locks.computeIfAbsent(dbname, k -> new ReentrantReadWriteLock()).writeLock().lock();
        try {
            db.computeIfAbsent(dbname, k -> new ConcurrentHashMap<>()).put(key, value);
        } finally {
            locks.get(dbname).writeLock().unlock();
        }
    }

    public static Optional<Object> get(String dbname, Object key) {
        if (db.containsKey(dbname) && db.get(dbname).containsKey(key)) {
            return Optional.ofNullable(db.get(dbname).get(key));
        }
        return Optional.empty();
    }

    public static void remove(String dbname, Object key) {
        db.get(dbname).remove(key);
    }

    public static List<Object> getList(String dbname) {
        return new ArrayList<>(db.getOrDefault(dbname, new ConcurrentHashMap<>()).values());
    }
}
