package src.Tools;

import java.util.HashMap;
import java.util.Map;

public class QuerySplitter {
    public static Map<String, String> splitQuery(String query) {
        Map<String, String> query_pairs = new HashMap<>();
        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            query_pairs.put(keyValue[0], keyValue[1]);
        }

        return query_pairs;
    }
}
