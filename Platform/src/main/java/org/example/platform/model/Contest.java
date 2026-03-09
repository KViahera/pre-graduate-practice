package org.example.platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contests")
@Getter @Setter @NoArgsConstructor
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Например: "Codeforces Round 900 (Div. 2)"

    @Column(columnDefinition = "TEXT")
    private String description; // Описание, правила, спонсоры

    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startTime; // Запланированное время начала

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes; // Длительность в минутах (обычно 120 или 150)

    // Кто создал контест
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Связь с задачами в этом контесте (через промежуточную таблицу)
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestProblem> contestProblems = new ArrayList<>();

    // Связь с зарегистрированными участниками
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestRegistration> registrations = new ArrayList<>();

    // Удобный метод, чтобы на лету определять, идет ли сейчас контест
    @Transient // Это поле не сохраняется в БД, оно вычисляется в памяти
    public boolean isRunning() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime endTime = startTime.plusMinutes(durationMinutes);
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
}