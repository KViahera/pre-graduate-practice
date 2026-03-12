package org.example.backend.dto;

public record TestCaseDTO(
    Integer id,
    String inputData,
    String outputData,
    Boolean isSample
) {}