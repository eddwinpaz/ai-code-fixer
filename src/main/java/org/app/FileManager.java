package org.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
    private final Path repoPath;

    public FileManager(String repoPath) {
        this.repoPath = Paths.get(repoPath);
    }

    public String readFile(String filePath) throws IOException {
        System.out.println(STR."File path: \{filePath}");
        Path file = repoPath.resolve(filePath);
        return Files.readString(file);
    }

    public void writeFile(String filePath, String content) throws IOException {
        System.out.println(STR."File path: \{filePath}");
        System.out.println(STR."File content: \{content}");
        Path file = repoPath.resolve(filePath);
        Files.writeString(file, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public String readLines(String filePath, int startLine, int endLine) throws IOException {
        Path file = repoPath.resolve(filePath);
        List<String> lines = Files.readAllLines(file);
        return lines.subList(startLine - 1, endLine).stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public void modifyFile(String filePath, int startLine, int endLine, String newContent) throws IOException {
        Path file = repoPath.resolve(filePath);
        List<String> lines = Files.readAllLines(file);
        lines.subList(startLine - 1, endLine).clear();
        lines.addAll(startLine - 1, Arrays.asList(newContent.split(System.lineSeparator())));
        Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
