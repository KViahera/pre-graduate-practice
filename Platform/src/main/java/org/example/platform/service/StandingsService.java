package org.example.platform.service;

import lombok.RequiredArgsConstructor;
import org.example.platform.dto.StandingsProblemResult;
import org.example.platform.dto.StandingsResponse;
import org.example.platform.dto.StandingsRow;
import org.example.platform.model.Contest;
import org.example.platform.model.ContestProblem;
import org.example.platform.model.Submission;
import org.example.platform.model.enums.Verdict;
import org.example.platform.repository.ContestProblemRepository;
import org.example.platform.repository.ContestRepository;
import org.example.platform.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandingsService {

    private final ContestRepository contestRepository;
    private final SubmissionRepository submissionRepository;
    private final ContestProblemRepository contestProblemRepository;

    @Transactional(readOnly = true)
    public StandingsResponse getStandings(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("Контест не найден"));

        List<ContestProblem> contestProblems = contestProblemRepository.findByContestIdOrderByProblemIndexAsc(contestId);

        List<Submission> submissions = submissionRepository.findByContestIdOrderBySubmittedAtAsc(contestId);

        Map<String, UserStandingsState> stateMap = new HashMap<>();

        for (Submission sub : submissions) {
            String username = sub.getUser().getUsername();
            stateMap.putIfAbsent(username, new UserStandingsState(username));
            UserStandingsState userState = stateMap.get(username);

            String pIndex = contestProblems.stream()
                    .filter(cp -> cp.getProblem().getId().equals(sub.getProblem().getId()))
                    .findFirst()
                    .map(ContestProblem::getProblemIndex)
                    .orElse("Unknown");

            long minutesFromStart = Math.max(0, Duration.between(contest.getStartTime(), sub.getSubmittedAt()).toMinutes());

            userState.processSubmission(pIndex, sub.getVerdict(), (int) minutesFromStart);
        }

        List<StandingsRow> rows = stateMap.values().stream()
                .map(state -> state.toDto(contestProblems))
                .sorted(Comparator
                        .comparingInt(StandingsRow::problemsSolved).reversed()
                        .thenComparingInt(StandingsRow::totalPenalty)
                )
                .collect(Collectors.toList());

        List<StandingsRow> rankedRows = new ArrayList<>();
        int currentRank = 1;
        for (int i = 0; i < rows.size(); i++) {
            StandingsRow row = rows.get(i);
            if (i > 0 &&
                    row.problemsSolved() == rows.get(i-1).problemsSolved() &&
                    row.totalPenalty() == rows.get(i-1).totalPenalty()) {

                rankedRows.add(new StandingsRow(currentRank, row.username(), row.problemsSolved(), row.totalPenalty(), row.problemResults()));
            } else {
                currentRank = i + 1;
                rankedRows.add(new StandingsRow(currentRank, row.username(), row.problemsSolved(), row.totalPenalty(), row.problemResults()));
            }
        }

        return new StandingsResponse(contest.getId(), contest.getTitle(), rankedRows);
    }

    private static class UserStandingsState {
        String username;
        int totalSolved = 0;
        int totalPenalty = 0;
        Map<String, ProblemState> problems = new HashMap<>();

        UserStandingsState(String username) {
            this.username = username;
        }

        void processSubmission(String problemIndex, Verdict verdict, int timeMinutes) {
            problems.putIfAbsent(problemIndex, new ProblemState());
            ProblemState pState = problems.get(problemIndex);

            if (pState.isAccepted) return;

            if (verdict == Verdict.COMPILATION_ERROR) return;

            if (verdict == Verdict.ACCEPTED) {
                pState.isAccepted = true;
                pState.penalty = timeMinutes + (pState.attempts * 20);

                this.totalSolved++;
                this.totalPenalty += pState.penalty;
            } else {
                pState.attempts++;
            }
        }

        StandingsRow toDto(List<ContestProblem> contestProblems) {
            List<StandingsProblemResult> results = new ArrayList<>();

            for (ContestProblem cp : contestProblems) {
                ProblemState pState = problems.getOrDefault(cp.getProblemIndex(), new ProblemState());
                results.add(new StandingsProblemResult(cp.getProblemIndex(), pState.isAccepted, pState.attempts, pState.penalty));
            }
            return new StandingsRow(0, username, totalSolved, totalPenalty, results);
        }
    }

    private static class ProblemState {
        boolean isAccepted = false;
        int attempts = 0;
        int penalty = 0;
    }
}