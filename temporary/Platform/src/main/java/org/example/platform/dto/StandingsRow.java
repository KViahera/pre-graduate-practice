package org.example.platform.dto;

import java.util.List;

public record StandingsRow(
        int rank,
        String username,
        int problemsSolved,
        int totalPenalty,
        List<StandingsProblemResult> problemResults
) {}
