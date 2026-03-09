package org.example.worker.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.worker.dto.JudgeTask;
import org.example.worker.service.JavaExecutorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgeListener {

    private final JavaExecutorService javaExecutor;

    @RabbitListener(queues = "judge_queue")
    public void processTask(JudgeTask task) {
        log.info("Воркер получил задачу ID: {}. Язык: {}", task.submissionId(), task.language());

        if ("JAVA".equalsIgnoreCase(task.language())) {
            var result = javaExecutor.execute(task.sourceCode(), task.timeLimitMillis());

            log.info("Статус: {}", result.status());
            log.info("Время выполнения: {} ms", result.executionTimeMs());
            log.info("Вывод программы/Ошибки:\n{}", result.outputOrError());

            // TODO: Отправить результат обратно в главный бэкенд (REST-запросом или через другую очередь)
        } else {
            log.warn("Язык {} пока не поддерживается воркером!", task.language());
        }
    }
}