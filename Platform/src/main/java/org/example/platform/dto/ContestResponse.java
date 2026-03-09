package org.example.platform.dto;

import java.time.ZonedDateTime;

// --- Для ответов клиенту ---
public record ContestResponse(
        Long id,
        String title,
        ZonedDateTime startTime,
        Integer durationMinutes,
        String status // "UPCOMING", "RUNNING", "FINISHED"
) {}
