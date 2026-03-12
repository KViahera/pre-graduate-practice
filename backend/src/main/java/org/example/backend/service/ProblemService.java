package org.example.backend.service;

import org.example.backend.dto.ProblemDTO;
import org.example.backend.dto.TestCaseDTO;
import org.example.backend.entity.Problem;
import org.example.backend.entity.TestCase;
import org.example.backend.repository.ProblemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.backend.utility.ProblemMapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Transactional(readOnly = true)
    public List<ProblemDTO> searchProblems(String search) {
        List<Problem> problems = (search != null && !search.isBlank())
            ? problemRepository.findTop10ByTitleContainingIgnoreCase(search)
            : problemRepository.findTop10ByTitleContainingIgnoreCase("");

        return problems.stream()
            .map(ProblemMapper::mapProblemToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void updateProblem(Integer id, ProblemDTO dto) {
        Problem problem = problemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Problem not found"));

        problem.setTitle(dto.title());
        problem.setStatement(dto.statement());
        problem.setInputFormat(dto.inputFormat());
        problem.setOutputFormat(dto.outputFormat());
        problem.setTimeLimitMilliseconds(dto.timeLimitMilliseconds());
        problem.setMemoryLimitMegabytes(dto.memoryLimitMegabytes());

        List<TestCase> existingTests = problem.getTestCases();
        List<TestCaseDTO> incomingSamples = dto.testCases();

        if (incomingSamples == null || incomingSamples.isEmpty()) {
            existingTests.removeIf(TestCase::getIsSample);
        } else {
            Set<Integer> incomingIds = incomingSamples.stream()
                .map(TestCaseDTO::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

            existingTests.removeIf(tc -> tc.getIsSample() && tc.getId() != null && !incomingIds.contains(tc.getId()));

            for (TestCaseDTO incoming : incomingSamples) {
                if (incoming.id() != null) {
                    existingTests.stream()
                        .filter(tc -> incoming.id().equals(tc.getId()))
                        .findFirst()
                        .ifPresent(tc -> {
                            tc.setInputData(incoming.inputData());
                            tc.setOutputData(incoming.outputData());
                        });
                } else {
                    TestCase newSample = new TestCase();

                    newSample.setInputData(incoming.inputData());
                    newSample.setOutputData(incoming.outputData());
                    newSample.setIsSample(true);

                    problem.addTestCase(newSample);
                }
            }
        }

        problemRepository.save(problem);
    }

    @Transactional
    public void deleteProblem(Integer id) {
        if (!problemRepository.existsById(id)) {
            throw new RuntimeException("Problem not found");
        }

        problemRepository.deleteById(id);
    }
}