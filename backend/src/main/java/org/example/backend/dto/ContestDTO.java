package org.example.backend.dto;

import java.time.OffsetDateTime;

public record ContestDTO(
    Integer id,
    String name,
    OffsetDateTime startTime,
    Integer duration
) {}