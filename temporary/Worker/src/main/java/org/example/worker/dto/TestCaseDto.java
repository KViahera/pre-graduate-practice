package org.example.worker.dto;

// DTO для передачи тестов
public record TestCaseDto(
        String inputData,
        String expectedOutput,
        Boolean isSample
) {}
