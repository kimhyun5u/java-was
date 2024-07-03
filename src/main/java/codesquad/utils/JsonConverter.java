package codesquad.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class JsonConverter {

    private JsonConverter() {
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        }
        if (obj instanceof Collection) {
            return collectionToJson((Collection<?>) obj);
        }
        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj);
        }
        return objectToJson(obj);
    }

    private static String objectToJson(Object obj) {
        StringBuilder json = new StringBuilder("{");
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;
        for (Field field : fields) {
            field.setAccessible(true);
            if (!first) {
                json.append(",");
            }
            try {
                json.append("\"").append(field.getName()).append("\":");
                json.append(toJson(field.get(obj)));
                first = false;
            } catch (IllegalAccessException e) {
                // 필드에 접근할 수 없는 경우 무시
            }
        }
        json.append("}");
        return json.toString();
    }

    private static String collectionToJson(Collection<?> collection) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                json.append(",");
            }
            json.append(toJson(item));
            first = false;
        }
        json.append("]");
        return json.toString();
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append(toJson(entry.getKey())).append(":");
            json.append(toJson(entry.getValue()));
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    private static String escapeString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
