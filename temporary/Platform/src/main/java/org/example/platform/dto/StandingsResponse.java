package org.example.platform.dto;

import java.util.List;

public record StandingsResponse(
        Long contestId,
        String contestTitle,
        List<StandingsRow> rows
) {}