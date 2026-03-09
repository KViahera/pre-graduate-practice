package org.example.platform.dto;

import java.util.Set;

// Ответ для списка задач (облегченная версия без длинного текста условий)
public record ProblemSummaryResponse(
        Long id,
        String title,
        Integer difficulty,
        String authorUsername,
        Set<String> tags
) {}