package org.example.platform.dto;

import java.util.List;

// Весь ответ
public record StandingsResponse(
        Long contestId,
        String contestTitle,
        List<StandingsRow> rows
) {}