package org.example.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.example.worker.dto.TestCaseDto;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JavaExecutorService {

    // Добавили список тестов в аргументы
    public ExecutionResult execute(String sourceCode, int timeLimitMillis, List<TestCaseDto> testCases) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("judge_");
            File sourceFile = new File(tempDir.toFile(), "Main.java");
            Files.writeString(sourceFile.toPath(), sourceCode);

            // 1. КОМПИЛЯЦИЯ (один раз)
            ProcessBuilder compilePb = new ProcessBuilder("javac", "Main.java");
            compilePb.directory(tempDir.toFile());
            Process compileProcess = compilePb.start();

            if (!compileProcess.waitFor(10, TimeUnit.SECONDS) || compileProcess.exitValue() != 0) {
                String error = new String(compileProcess.getErrorStream().readAllBytes());
                return new ExecutionResult("COMPILATION_ERROR", error, 0, 0); // 0 тестов пройдено
            }

            long totalTime = 0;

            // 2. ПРОГОН ТЕСТОВ (цикл)
            for (int i = 0; i < testCases.size(); i++) {
                TestCaseDto tc = testCases.get(i);

                ProcessBuilder runPb = new ProcessBuilder("java", "Main");
                runPb.directory(tempDir.toFile());
                Process runProcess = runPb.start();

                long startTime = System.currentTimeMillis();

                // ПЕРЕДАЕМ INPUT В ПРОГРАММУ
                // Используем try-with-resources, чтобы поток ввода точно закрылся,
                // иначе сканер в Java-программе будет ждать данных вечно!
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()))) {
                    writer.write(tc.inputData());
                    writer.flush();
                }

                // ЖДЕМ ЛИМИТ ВРЕМЕНИ
                boolean finishedInTime = runProcess.waitFor(timeLimitMillis, TimeUnit.MILLISECONDS);
                long executionTime = System.currentTimeMillis() - startTime;
                totalTime += executionTime;

                if (!finishedInTime) {
                    runProcess.destroyForcibly();
                    return new ExecutionResult("TIME_LIMIT_EXCEEDED", "На тесте " + (i + 1), totalTime, i);
                }

                if (runProcess.exitValue() != 0) {
                    String error = new String(runProcess.getErrorStream().readAllBytes());
                    return new ExecutionResult("RUNTIME_ERROR", error, totalTime, i);
                }

                // СЧИТЫВАЕМ И СРАВНИВАЕМ OUTPUT
                // trim() удаляет лишние пробелы и переносы строк в начале и в конце
                String actualOutput = new String(runProcess.getInputStream().readAllBytes()).trim();
                String expectedOutput = tc.expectedOutput().trim();

                if (!actualOutput.equals(expectedOutput)) {
                    String diff = String.format("Ожидалось: [%s], Получено: [%s]", expectedOutput, actualOutput);
                    return new ExecutionResult("WRONG_ANSWER", diff, totalTime, i);
                }
            }

            // Если цикл завершился и не выпал return, значит все тесты пройдены!
            return new ExecutionResult("ACCEPTED", "Все тесты пройдены!", totalTime, testCases.size());

        } catch (Exception e) {
            log.error("Системная ошибка", e);
            return new ExecutionResult("SYSTEM_ERROR", e.getMessage(), 0, 0);
        } finally {
            if (tempDir != null) deleteDirectory(tempDir.toFile());
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    // Обновили record, чтобы возвращать еще и количество пройденных тестов
    public record ExecutionResult(String status, String details, long executionTimeMs, int testsPassed) {}
}