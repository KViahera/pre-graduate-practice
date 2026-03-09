// Создай в пакете dto обоих проектов
package org.example.platform.dto; // или org.example.worker.dto для воркера

public record JudgeResult(
        Long submissionId,
        String verdict,
        String details,
        Long executionTimeMs,
        Integer testsPassed
) {}