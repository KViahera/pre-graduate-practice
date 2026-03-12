package org.example.backend.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ProblemDTO(
    Integer id,
    String title,
    String statement,
    String inputFormat,
    String outputFormat,
    Integer timeLimitMilliseconds,
    Integer memoryLimitMegabytes,
    OffsetDateTime createdAt,
    List<TestCaseDTO> testCases
) {}