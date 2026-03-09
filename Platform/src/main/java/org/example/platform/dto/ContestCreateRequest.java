package org.example.platform.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record ContestCreateRequest(
        String title,
        String description,
        ZonedDateTime startTime,
        Integer durationMinutes,
        List<ContestProblemRequest> problems
) {}
