import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Searcher {

    private String[] validation(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Need to set 4 arguments: root path; file name, mask, or regex; type of search - name, mask or regex; output file path");
        }
        ArgsName argsName = ArgsName.of(args);
        String[] values = {
                argsName.get("d"),
                argsName.get("n"),
                argsName.get("t"),
                argsName.get("o")
        };
        Path root = Paths.get(values[0]);
        if (!root.toFile().exists()) {
            throw new IllegalArgumentException("Root folder does not exist");
        }
        String searchType = values[2];
        if (!("mask".equals(searchType) || "regex".equals(searchType) || "name".equals(searchType))) {
            throw new IllegalArgumentException("Wrong type of search format: needs to be \"name\", \"mask\" or \"regex\"");
        }
        if ("mask".equals(searchType)) {
            values[1] = values[1].replace("*", "\\\\*.")
                    .replace("?", "\\\\?.") + "$";
        }
        return values;
    }

    private List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    private void save(List<Path> list, String file) {
        try (PrintWriter out = new PrintWriter(
                new BufferedOutputStream(
                        new FileOutputStream(file)
                ))) {
            for (Path path : list) {
                out.println(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Searcher searcher = new Searcher();
        String[] arguments = searcher.validation(args);
        Path root = Paths.get(arguments[0]);
        Predicate<Path> condition;
        if ("mask".equals(arguments[2]) || "regex".equals(arguments[2])) {
            Pattern pattern = Pattern.compile(arguments[1]);
            condition = path -> pattern.matcher(path.toString()).find();
        } else {
            condition = path -> arguments[1].equals(path.toFile().getName());
        }
        List<Path> result = searcher.search(root, condition);
        searcher.save(result, arguments[3]);
        for (Path path : result) {
            System.out.println(path.toString());
        }
    }
}
