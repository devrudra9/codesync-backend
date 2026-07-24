package org.rudreshwar.codesync.execution.executor;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileManager {

    public Path createWorkspace() throws IOException {
        return Files.createTempDirectory("codesync-");
    }

    public void deleteWorkspace(Path workspace) throws IOException {
        Files.walk(workspace)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                    }
                });
    }

}