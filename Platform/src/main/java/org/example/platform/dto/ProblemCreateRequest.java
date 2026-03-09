package org.example.platform.dto;

import java.util.List;
import java.util.Set;

// Запрос на создание задачи
public record ProblemCreateRequest(
        String title,
        String statement,
        String inputFormat,
        String outputFormat,
        Integer timeLimitMillis,
        Integer memoryLimitMb,
        Set<String> tags, // Передаем просто массив строк: ["dp", "math"]
        List<TestCaseDto> testCases
) {}