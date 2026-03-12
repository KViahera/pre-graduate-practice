package org.example.platform.dto;

public record ProblemForContestResponse(
        Long id,
        String problemIndex,
        String title,
        Integer maxScore
) {}