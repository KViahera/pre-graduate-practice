package org.example.backend.dto;

public record ContestProblemDTO(
    ProblemDTO problem,
    Integer index
) {}