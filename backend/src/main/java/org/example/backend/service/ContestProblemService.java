package org.example.backend.service;

import org.example.backend.entity.Contest;
import org.example.backend.entity.ContestProblem;
import org.example.backend.entity.Problem;
import org.example.backend.repository.ContestProblemRepository;
import org.example.backend.repository.ContestRepository;
import org.example.backend.repository.ProblemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContestProblemService {

    private final ContestProblemRepository contestProblemRepository;
    private final ContestRepository contestRepository;
    private final ProblemRepository problemRepository;

    public ContestProblemService(ContestProblemRepository contestProblemRepository, ContestRepository contestRepository, ProblemRepository problemRepository) {
        this.contestProblemRepository = contestProblemRepository;
        this.contestRepository = contestRepository;
        this.problemRepository = problemRepository;
    }

    @Transactional
    public void linkProblemToContest(Integer contestId, Integer problemId) {
        Contest contest = contestRepository.findById(contestId)
            .orElseThrow(() -> new RuntimeException("Contest not found"));
        Problem problem = problemRepository.findById(problemId)
            .orElseThrow(() -> new RuntimeException("Problem not found"));

        if (contestProblemRepository.existsByContestIdAndProblemId(contestId, problemId)) {
            throw new RuntimeException("Problem is already in this contest");
        }

        Integer maxIndex = contestProblemRepository.findMaxIndexByContestId(contestId);
        int nextIndex = (maxIndex == null) ? 0 : maxIndex + 1;

        if (nextIndex > 25) {
            throw new RuntimeException("Maximum of 26 problems (A-Z) allowed per contest");
        }

        ContestProblem cp = new ContestProblem();
        cp.setContest(contest);
        cp.setProblem(problem);
        cp.setProblemIndex(nextIndex);

        contestProblemRepository.save(cp);
    }

    @Transactional
    public void unlinkProblem(Integer contestId, Integer problemId) {
        ContestProblem toDelete = contestProblemRepository.findByContestIdAndProblemId(contestId, problemId)
            .orElseThrow(() -> new RuntimeException("Link not found"));

        contestProblemRepository.delete(toDelete);

        List<ContestProblem> remaining = contestProblemRepository.findByContestIdOrderByProblemIndexAsc(contestId);

        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setProblemIndex(i);
        }

        contestProblemRepository.saveAll(remaining);
    }

    @Transactional
    public void reorderProblems(Integer contestId, List<Integer> orderedProblemIds) {
        List<ContestProblem> currentLinks = contestProblemRepository.findByContestId(contestId);

        for (int i = 0; i < orderedProblemIds.size(); i++) {
            Integer currentProblemId = orderedProblemIds.get(i);
            int newIndex = i;

            currentLinks.stream()
                .filter(cp -> cp.getProblem().getId().equals(currentProblemId))
                .findFirst()
                .ifPresent(cp -> cp.setProblemIndex(newIndex));
        }

        contestProblemRepository.saveAll(currentLinks);
    }
}