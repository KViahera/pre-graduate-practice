package org.example.platform.service;

import lombok.RequiredArgsConstructor;
import org.example.platform.dto.*;
import org.example.platform.model.*;
import org.example.platform.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;
    private final ProblemRepository problemRepository;
    private final ContestProblemRepository contestProblemRepository;
    private final ContestRegistrationRepository registrationRepository;

    // Вспомогательный метод для получения текущего пользователя
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public Long createContest(ContestCreateRequest request) {
        Contest contest = new Contest();
        contest.setTitle(request.title());
        contest.setDescription(request.description());
        contest.setStartTime(request.startTime());
        contest.setDurationMinutes(request.durationMinutes());
        contest.setAuthor(getCurrentUser());

        Contest savedContest = contestRepository.save(contest);

        // Привязываем задачи к контесту
        for (ContestProblemRequest cpReq : request.problems()) {
            Problem problem = problemRepository.findById(cpReq.problemId())
                    .orElseThrow(() -> new IllegalArgumentException("Задача не найдена: " + cpReq.problemId()));

            ContestProblem cp = new ContestProblem();
            cp.setContest(savedContest);
            cp.setProblem(problem);
            cp.setProblemIndex(cpReq.problemIndex());
            cp.setMaxScore(cpReq.maxScore());

            contestProblemRepository.save(cp);
        }

        return savedContest.getId();
    }

    @Transactional
    public void registerForContest(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("Контест не найден"));

        User currentUser = getCurrentUser();

        if (registrationRepository.existsByContestAndUser(contest, currentUser)) {
            throw new IllegalStateException("Вы уже зарегистрированы на этот контест");
        }

        ContestRegistration registration = new ContestRegistration();
        registration.setContest(contest);
        registration.setUser(currentUser);
        registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<ProblemForContestResponse> getContestProblems(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("Контест не найден"));

        User currentUser = getCurrentUser();
        ZonedDateTime now = ZonedDateTime.now();

        // Проверяем, начался ли контест
        if (now.isBefore(contest.getStartTime())) {
            // Если не начался, доступ имеет ТОЛЬКО автор контеста
            if (!contest.getAuthor().getId().equals(currentUser.getId())) {
                throw new IllegalStateException("Контест еще не начался! Доступ к задачам закрыт.");
            }
        } else {
            // Если контест начался или завершился, проверяем регистрацию участника
            // (В реальной системе после окончания контеста задачи часто открывают для всех,
            // но во время контеста - только для зарегистрированных)
            if (contest.isRunning() && !registrationRepository.existsByContestAndUser(contest, currentUser)) {
                // Исключение: автор может смотреть всегда
                if (!contest.getAuthor().getId().equals(currentUser.getId())) {
                    throw new IllegalStateException("Вы не зарегистрированы на этот контест.");
                }
            }
        }

        // Если все проверки пройдены, отдаем отсортированный список задач
        return contestProblemRepository.findByContestIdOrderByProblemIndexAsc(contestId)
                .stream()
                .map(cp -> new ProblemForContestResponse(
                        cp.getProblem().getId(),
                        cp.getProblemIndex(),
                        cp.getProblem().getTitle(),
                        cp.getMaxScore()
                ))
                .collect(Collectors.toList());
    }
}