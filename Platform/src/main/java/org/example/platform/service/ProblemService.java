package org.example.platform.service;

import org.example.platform.dto.ProblemCreateRequest;
import org.example.platform.dto.ProblemSummaryResponse;
import org.example.platform.model.Problem;
import org.example.platform.model.Tag;
import org.example.platform.model.TestCase;
import org.example.platform.model.User;
import org.example.platform.repository.ProblemRepository;
import org.example.platform.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Long createProblem(ProblemCreateRequest request) {
        Problem problem = new Problem();
        problem.setTitle(request.title());
        problem.setStatement(request.statement());
        problem.setInputFormat(request.inputFormat());
        problem.setOutputFormat(request.outputFormat());
        problem.setTimeLimitMillis(request.timeLimitMillis());
        problem.setMemoryLimitMb(request.memoryLimitMb());

        User currentAuthor = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        problem.setAuthor(currentAuthor);

        if (request.tags() != null) {
            for (String tagName : request.tags()) {
                String cleanName = tagName.toLowerCase().trim();
                Tag tag = tagRepository.findByName(cleanName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(cleanName);
                            return tagRepository.save(newTag);
                        });
                problem.getTags().add(tag);
            }
        }

        if (request.testCases() != null) {
            for (var tcDto : request.testCases()) {
                TestCase tc = new TestCase();
                tc.setInputData(tcDto.inputData());
                tc.setExpectedOutput(tcDto.expectedOutput());
                tc.setIsSample(tcDto.isSample());
                tc.setProblem(problem);
                problem.getTestCases().add(tc);
            }
        }

        Problem savedProblem = problemRepository.save(problem);
        return savedProblem.getId();
    }

    @Transactional(readOnly = true)
    public List<ProblemSummaryResponse> getAllPublicProblems() {
        return problemRepository.findAllByIsPublicTrue().stream()
                .map(p -> new ProblemSummaryResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getDifficulty(),
                        p.getAuthor().getUsername(),
                        p.getTags().stream().map(Tag::getName).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }
}