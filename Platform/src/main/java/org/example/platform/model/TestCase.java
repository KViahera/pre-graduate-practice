package org.example.platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "test_cases")
@Getter @Setter @NoArgsConstructor
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "input_data", columnDefinition = "TEXT", nullable = false)
    private String inputData;

    @Column(name = "expected_output", columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;

    // Является ли тест примером (будет ли он отображаться в условии задачи)
    @Column(name = "is_sample", nullable = false)
    private Boolean isSample = false;

    // Вес теста в баллах (если мы захотим делать частичную оценку, как на IOI)
    @Column(name = "score_weight")
    private Integer scoreWeight = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
}