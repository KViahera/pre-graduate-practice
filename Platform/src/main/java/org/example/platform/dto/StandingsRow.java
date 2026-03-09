package org.example.platform.dto;

import java.util.List;

// Строка участника в таблице
public record StandingsRow(
        int rank,              // Текущее место
        String username,       // Логин
        int problemsSolved,    // Итого решено задач
        int totalPenalty,      // Итоговое штрафное время
        List<StandingsProblemResult> problemResults // Результаты по каждой задаче
) {}
