package org.example.platform.dto;
import org.example.platform.model.enums.ProgrammingLanguage;

public record SubmissionRequest(
        Long problemId,
        Long contestId, // Опционально, если решаем внутри контеста
        ProgrammingLanguage language,
        String sourceCode
) {}
