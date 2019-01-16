package suchmaschine.http;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateProcessor {

    private final String fileName;

    public TemplateProcessor(String fileName) {
        this.fileName = fileName;
    }

    public String replace(Map<String, String> variableAssignments) {
        List<String> fileContentLines;
        try {
            // reading file doesn't need to be synchronized, only read access and other stuff will be handled by the OS
            // since every call opens a new FileReader based on the fileName attribute, which is also read-only
            fileContentLines = loadFileContent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return fileContentLines.stream().map(
                line -> line.contains("%")
                        ? line = variableAssignments.keySet().stream().reduce(line, (parsedLine, key) -> parsedLine.replaceAll(key, variableAssignments.get(key)))
                        : line
        ).collect(Collectors.joining());
    }

    private List<String> loadFileContent() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // #lines() can throw UncheckedIOException
            return reader.lines().map(line -> line + System.lineSeparator()).collect(Collectors.toList());
        }
    }

}