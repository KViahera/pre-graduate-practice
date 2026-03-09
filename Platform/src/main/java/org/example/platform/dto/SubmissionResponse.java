package org.example.platform.dto;
import org.example.platform.model.enums.ProgrammingLanguage;
import org.example.platform.model.enums.Verdict;

public record SubmissionResponse(
        Long id,
        Long problemId,
        String username,
        ProgrammingLanguage language,
        Verdict verdict,
        Integer executionTimeMs,
        Integer memoryUsedKb
) {}