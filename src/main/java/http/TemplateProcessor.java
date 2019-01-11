package http;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateProcessor {

    private final String fileName;
    private List<String> fileContentLines; // lines with line separators

    public TemplateProcessor(String fileName) {
        this.fileName = fileName;
    }

    public String replace(Map<String, String> variableAssignments) {
        if (fileContentLines == null) {
            try {
                loadFileContent();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return fileContentLines.stream().map(
                line -> line.contains("%")
                        ? line = variableAssignments.keySet().stream().reduce(line, (parsedLine, key) -> parsedLine.replaceAll(key, variableAssignments.get(key)))
                        : line
        ).collect(Collectors.joining());
    }

    private void loadFileContent() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // #lines() can throw UncheckedIOException
            fileContentLines = reader.lines().map(line -> line + System.lineSeparator()).collect(Collectors.toList());
        }
    }

}