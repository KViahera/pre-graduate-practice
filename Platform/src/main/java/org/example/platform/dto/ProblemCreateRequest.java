package org.example.platform.dto;

import java.util.List;
import java.util.Set;

public record ProblemCreateRequest(
        String title,
        String statement,
        String inputFormat,
        String outputFormat,
        Integer timeLimitMillis,
        Integer memoryLimitMb,
        Set<String> tags,
        List<TestCaseDto> testCases
) {}