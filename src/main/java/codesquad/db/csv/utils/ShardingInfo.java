package codesquad.db.csv.utils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ShardingInfo {
    private final static Map<String, AtomicLong> shardingMap = new ConcurrentHashMap<>();
    private final static String filePath = System.getProperty("user.home") + "/jdbc_csv/sharding_info.csv";;
    private final static Long SHARDING_SIZE = 1000L;

    private ShardingInfo() {}

    public static void loadShardingInfo() {
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 파일 읽어서 shardingMap 에 저장
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                shardingMap.put(split[0], new AtomicLong(Long.parseLong(split[1])));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateShardingInfo(String table) {
        shardingMap.putIfAbsent(table, new AtomicLong(-1L));
        Long id = shardingMap.get(table).incrementAndGet();

        // 파일에 저장
        // 파일을 모두 읽고 이미 존재하는 테이블이면 해당 라인을 수정하고, 없으면 추가
        File file = new File(filePath);
        File tempFile = new File(filePath + ".tmp");

        boolean flag = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                if (split[0].equals(table)) {
                    bw.write(table + "," + id);
                    bw.newLine();
                    flag = true;
                } else {
                    bw.write(line);
                    bw.newLine();
                }
            }

            if (!flag) {
                bw.write(table + "," + id);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        file.delete();
        tempFile.renameTo(file);
    }

    public static long getShardingId(String table) {
        return shardingMap.getOrDefault(table, new AtomicLong(0L)).get();
    }

    public static long getShardingSize() {
        return SHARDING_SIZE;
    }
}
