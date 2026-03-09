package org.example.platform.dto;

import java.util.Set;

public record ProblemSummaryResponse(
        Long id,
        String title,
        Integer difficulty,
        String authorUsername,
        Set<String> tags
) {}