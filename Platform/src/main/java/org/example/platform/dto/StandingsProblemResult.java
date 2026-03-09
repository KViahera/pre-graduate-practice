package org.example.platform.dto;

// Результат по одной конкретной задаче (Ячейка в таблице)
public record StandingsProblemResult(
        String problemIndex,   // "A", "B", "C"
        boolean isAccepted,    // Решена ли задача
        int attempts,          // Количество неверных попыток до AC
        int penaltyTime        // Штрафное время конкретно за эту задачу
) {}
