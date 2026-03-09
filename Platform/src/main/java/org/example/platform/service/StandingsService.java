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

        // 1. Получаем структуру задач контеста (A, B, C...)
        List<ContestProblem> contestProblems = contestProblemRepository.findByContestIdOrderByProblemIndexAsc(contestId);

        // 2. Получаем все посылки контеста
        List<Submission> submissions = submissionRepository.findByContestIdOrderBySubmittedAtAsc(contestId);

        // 3. Собираем статистику (Map: Username -> (Map: ProblemIndex -> State))
        Map<String, UserStandingsState> stateMap = new HashMap<>();

        for (Submission sub : submissions) {
            String username = sub.getUser().getUsername();
            stateMap.putIfAbsent(username, new UserStandingsState(username));
            UserStandingsState userState = stateMap.get(username);

            // Находим индекс задачи для этой посылки
            String pIndex = contestProblems.stream()
                    .filter(cp -> cp.getProblem().getId().equals(sub.getProblem().getId()))
                    .findFirst()
                    .map(ContestProblem::getProblemIndex)
                    .orElse("Unknown");

            // Рассчитываем время отправки в минутах от старта контеста
            long minutesFromStart = Math.max(0, Duration.between(contest.getStartTime(), sub.getSubmittedAt()).toMinutes());

            userState.processSubmission(pIndex, sub.getVerdict(), (int) minutesFromStart);
        }

        // 4. Формируем список строк и сортируем по правилам ICPC
        List<StandingsRow> rows = stateMap.values().stream()
                .map(state -> state.toDto(contestProblems))
                .sorted(Comparator
                        .comparingInt(StandingsRow::problemsSolved).reversed() // Сначала больше решенных
                        .thenComparingInt(StandingsRow::totalPenalty)          // Затем меньше штрафа
                )
                .collect(Collectors.toList());

        // 5. Расставляем места (rank)
        List<StandingsRow> rankedRows = new ArrayList<>();
        int currentRank = 1;
        for (int i = 0; i < rows.size(); i++) {
            StandingsRow row = rows.get(i);
            // Если результаты совпадают с предыдущим, ранг одинаковый (например, два первых места)
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

    // --- Вспомогательный класс для накопления состояния конкретного юзера ---
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

            // Если задача уже решена, игнорируем дальнейшие посылки по ней
            if (pState.isAccepted) return;

            // Ошибки компиляции не учитываем в ICPC
            if (verdict == Verdict.COMPILATION_ERROR) return;

            if (verdict == Verdict.ACCEPTED) {
                pState.isAccepted = true;
                pState.penalty = timeMinutes + (pState.attempts * 20); // Время + (20 мин * кол-во ошибок)

                this.totalSolved++;
                this.totalPenalty += pState.penalty;
            } else {
                pState.attempts++;
            }
        }

        StandingsRow toDto(List<ContestProblem> contestProblems) {
            List<StandingsProblemResult> results = new ArrayList<>();
            // Гарантируем, что в DTO будут ячейки для ВСЕХ задач контеста, даже если юзер их не отправлял
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