package org.example.platform.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.platform.config.RabbitMQConfiguration;
import org.example.platform.dto.JudgeResult;
import org.example.platform.model.Submission;
import org.example.platform.model.enums.Verdict;
import org.example.platform.repository.SubmissionRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionResultListener {

    private final SubmissionRepository submissionRepository;

    @RabbitListener(queues = RabbitMQConfiguration.RESULT_QUEUE)
    @Transactional
    public void receiveJudgeResult(JudgeResult result) {
        log.info("Получен вердикт для посылки ID {}: {}", result.submissionId(), result.verdict());

        Submission submission = submissionRepository.findById(result.submissionId())
                .orElseThrow(() -> new IllegalArgumentException("Посылка не найдена в БД"));

        // Преобразуем строку в наш Enum (внимательно следим, чтобы имена совпадали!)
        try {
            submission.setVerdict(Verdict.valueOf(result.verdict()));
        } catch (IllegalArgumentException e) {
            log.error("Неизвестный вердикт: {}", result.verdict());
            submission.setVerdict(Verdict.RUNTIME_ERROR);
        }

        submission.setExecutionTimeMs(result.executionTimeMs().intValue());

        // Если была ошибка компиляции или неверный ответ, сохраняем детали, чтобы юзер понял, где ошибся
        if (!"ACCEPTED".equals(result.verdict())) {
            submission.setCompilerLogs(result.details());
        } else {
            // Можно очистить логи, если до этого там что-то было
            submission.setCompilerLogs(null);
        }

        // Сохраняем обновленный статус!
        submissionRepository.save(submission);
    }
}