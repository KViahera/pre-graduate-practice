package org.example.platform.dto;

// DTO для передачи тестов
public record TestCaseDto(
        String inputData,
        String expectedOutput,
        Boolean isSample
) {}
