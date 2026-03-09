// Создай в пакете dto обоих проектов
package org.example.worker.dto; // или org.example.worker.dto для воркера

public record JudgeResult(
        Long submissionId,
        String verdict,       // "ACCEPTED", "WRONG_ANSWER", "COMPILATION_ERROR" и т.д.
        String details,       // Текст ошибки или "Все тесты пройдены"
        Long executionTimeMs, // Затраченное время
        Integer testsPassed   // Сколько тестов пройдено успешно
) {}