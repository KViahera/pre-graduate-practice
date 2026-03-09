package org.example.platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.platform.model.enums.ProgrammingLanguage;
import org.example.platform.model.enums.Verdict;

import java.time.ZonedDateTime;

@Entity
@Table(name = "submissions")
@Getter @Setter @NoArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Кто отправил код
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // По какой задаче
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    // Если посылка отправлена в рамках контеста (может быть null, если решали в архиве)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgrammingLanguage language;

    @Column(name = "source_code", columnDefinition = "TEXT", nullable = false)
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Verdict verdict = Verdict.PENDING;

    // Затраченные ресурсы (заполняются после проверки)
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "memory_used_kb")
    private Integer memoryUsedKb;

    // Логи компилятора (полезно при COMPILATION_ERROR)
    @Column(name = "compiler_logs", columnDefinition = "TEXT")
    private String compilerLogs;

    @Column(name = "submitted_at", updatable = false)
    private ZonedDateTime submittedAt = ZonedDateTime.now();
}