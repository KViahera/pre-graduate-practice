package org.example.platform.dto;

import java.time.ZonedDateTime;

public record ContestResponse(
        Long id,
        String title,
        ZonedDateTime startTime,
        Integer durationMinutes,
        String status
) {}
