package org.example.platform.dto;

import java.util.List;

public record JudgeTask(
        Long submissionId,       // Чтобы воркер знал, чей статус потом обновлять
        Long problemId,          // Чтобы воркер мог запросить тесты (или достать из кэша)
        String language,         // "CPP", "JAVA", "PYTHON"
        String sourceCode,       // Сам код
        Integer timeLimitMillis, // Лимит времени (напр. 1000)
        Integer memoryLimitMb,   // Лимит памяти (напр. 256)
        List<TestCaseDto> testCases
) {}