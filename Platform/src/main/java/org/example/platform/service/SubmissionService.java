package org.example.platform.service;

import lombok.RequiredArgsConstructor;
import org.example.platform.config.RabbitMQConfiguration;
import org.example.platform.dto.JudgeTask;
import org.example.platform.dto.SubmissionRequest;
import org.example.platform.dto.SubmissionResponse;
import org.example.platform.dto.TestCaseDto;
import org.example.platform.model.Problem;
import org.example.platform.model.Submission;
import org.example.platform.model.User;
import org.example.platform.model.enums.Verdict;
import org.example.platform.repository.ProblemRepository;
import org.example.platform.repository.SubmissionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;

    private final RabbitTemplate rabbitTemplate;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public Long submitCode(SubmissionRequest request) {
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));

        Submission submission = new Submission();
        submission.setUser(getCurrentUser());
        submission.setProblem(problem);
        submission.setLanguage(request.language());
        submission.setSourceCode(request.sourceCode());
        submission.setVerdict(Verdict.PENDING); // Ставим статус ожидания

        // Сохраняем в БД, чтобы получить сгенерированный ID
        Submission savedSubmission = submissionRepository.save(submission);

        List<TestCaseDto> testCases = problem.getTestCases().stream()
                .map(tc -> new TestCaseDto(tc.getInputData(), tc.getExpectedOutput(), false))
                .toList();

        // Формируем задачу для воркера
        JudgeTask task = new JudgeTask(
                savedSubmission.getId(),
                problem.getId(),
                savedSubmission.getLanguage().name(),
                savedSubmission.getSourceCode(),
                problem.getTimeLimitMillis(),
                problem.getMemoryLimitMb(),
                testCases // <-- ПЕРЕДАЕМ ТЕСТЫ СЮДА
        );

        // ОТПРАВЛЯЕМ В ОЧЕРЕДЬ!
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE_NAME,
                RabbitMQConfiguration.ROUTING_KEY,
                task
        );

        return savedSubmission.getId();
    }

    // Имитация тестирующей системы (в будущем этот код уедет в отдельный микросервис-воркер)
    private void simulateTesting(Submission submission) {
        // Допустим, проверка прошла успешно
        submission.setVerdict(Verdict.ACCEPTED);
        submission.setExecutionTimeMs(45); // код отработал за 45 мс
        submission.setMemoryUsedKb(1024);  // съел 1 МБ памяти

        // В реальной жизни здесь RabbitMQ прислал бы ответ, и мы бы обновили статус
        submissionRepository.save(submission);
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionStatus(Long id) {
        Submission s = submissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Посылка не найдена"));

        return new SubmissionResponse(
                s.getId(),
                s.getProblem().getId(),
                s.getUser().getUsername(),
                s.getLanguage(),
                s.getVerdict(),
                s.getExecutionTimeMs(),
                s.getMemoryUsedKb()
        );
    }
}