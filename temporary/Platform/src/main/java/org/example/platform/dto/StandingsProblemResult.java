package org.example.platform.dto;

public record StandingsProblemResult(
        String problemIndex,
        boolean isAccepted,
        int attempts,
        int penaltyTime
) {}
