package org.example.platform.dto;

import java.util.List;

public record JudgeTask(
        Long submissionId,
        Long problemId,
        String language,
        String sourceCode,
        Integer timeLimitMillis,
        Integer memoryLimitMb,
        List<TestCaseDto> testCases
) {}