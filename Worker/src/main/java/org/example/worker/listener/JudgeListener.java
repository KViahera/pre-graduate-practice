package org.example.worker.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.worker.config.RabbitMQConfiguration;
import org.example.worker.dto.JudgeResult;
import org.example.worker.dto.JudgeTask;
import org.example.worker.service.JavaExecutorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgeListener {

    private final JavaExecutorService javaExecutor;
    private final RabbitTemplate rabbitTemplate; // Внедряем инструмент отправки

    @RabbitListener(queues = "judge_queue")
    public void processTask(JudgeTask task) {
        log.info("Проверяем посылку ID: {}", task.submissionId());

        JudgeResult resultDto;

        if ("JAVA".equalsIgnoreCase(task.language())) {
            var result = javaExecutor.execute(task.sourceCode(), task.timeLimitMillis(), task.testCases());

            // Формируем DTO с результатом
            resultDto = new JudgeResult(
                    task.submissionId(),
                    result.status(),
                    result.details(),
                    result.executionTimeMs(),
                    result.testsPassed()
            );
        } else {
            resultDto = new JudgeResult(task.submissionId(), "SYSTEM_ERROR", "Неподдерживаемый язык", 0L, 0);
        }

        // ОТПРАВЛЯЕМ РЕЗУЛЬТАТ ОБРАТНО В БЭКЕНД
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE_NAME,
                RabbitMQConfiguration.RESULT_ROUTING_KEY,
                resultDto
        );
        log.info("Результат посылки {} отправлен обратно!", task.submissionId());
    }
}