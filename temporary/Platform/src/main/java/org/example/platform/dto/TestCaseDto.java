package org.example.platform.dto;

public record TestCaseDto(
        String inputData,
        String expectedOutput,
        Boolean isSample
) {}
