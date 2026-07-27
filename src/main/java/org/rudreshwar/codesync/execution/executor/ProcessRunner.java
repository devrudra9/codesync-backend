package org.rudreshwar.codesync.execution.executor;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ProcessRunner {

    private static final long TIMEOUT_SECONDS = 5;

    public ProcessResult run(List<String> command, String input, Path workingDirectory) throws Exception {

        long start = System.currentTimeMillis();

        ProcessBuilder builder = new ProcessBuilder(command);

        builder.directory(workingDirectory.toFile());

        builder.redirectErrorStream(false);

        Process process = builder.start();

        if (input != null && !input.isBlank()) {
            try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(input);
                writer.flush();
            }
        }

        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            return ProcessResult.builder()
                    .timedOut(true)
                    .exitCode(-1)
                    .executionTimeMs(System.currentTimeMillis() - start)
                    .output("")
                    .error("Execution timed out.")
                    .build();

        }

        String output = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))
                        .lines()
                        .reduce("", (a, b) -> a + b + "\n");

        String error = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))
                        .lines()
                        .reduce("", (a, b) -> a + b + "\n");

        return ProcessResult.builder()
                .output(output)
                .error(error)
                .exitCode(process.exitValue())
                .executionTimeMs(System.currentTimeMillis() - start)
                .timedOut(false)
                .build();

    }

}