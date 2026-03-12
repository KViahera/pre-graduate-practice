package org.example.backend.utility;

import org.example.backend.dto.ProblemDTO;
import org.example.backend.dto.TestCaseDTO;
import org.example.backend.entity.Problem;
import org.example.backend.entity.TestCase;

import java.util.List;
import java.util.stream.Collectors;

public class ProblemMapper {

    public static ProblemDTO mapProblemToDto(Problem problem) {
        List<TestCaseDTO> sampleDTOs = problem.getTestCases().stream()
            .filter(TestCase::getIsSample)
            .map(tc -> new TestCaseDTO(
                tc.getId(),
                tc.getInputData(),
                tc.getOutputData(),
                true
            ))
            .collect(Collectors.toList());

        return new ProblemDTO(
            problem.getId(),
            problem.getTitle(),
            problem.getStatement(),
            problem.getInputFormat(),
            problem.getOutputFormat(),
            problem.getTimeLimitMilliseconds(),
            problem.getMemoryLimitMegabytes(),
            problem.getCreatedAt(),
            sampleDTOs
        );
    }
}