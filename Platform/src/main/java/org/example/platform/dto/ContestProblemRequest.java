package org.example.platform.dto;

import java.time.ZonedDateTime;

public record ContestProblemRequest(
        Long problemId,
        String problemIndex, // "A", "B", "C"
        Integer maxScore
) {}
