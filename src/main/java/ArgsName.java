import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArgsName {
    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("Key " + key + " does not exist");
        }
        return values.get(key);
    }

    private void parse(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("-") || !arg.contains("=")) {
                throw new IllegalArgumentException("Wrong argument format");
            }
            String[] pair = arg.replaceFirst("-", "").split("=", 2);
            if (Objects.equals(pair[0], "") || Objects.equals(pair[1], "")) {
                throw new IllegalArgumentException("Key or value is null");
            }
            values.put(pair[0], pair[1]);
        }
    }

    public static ArgsName of(String[] args) {
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }
}
