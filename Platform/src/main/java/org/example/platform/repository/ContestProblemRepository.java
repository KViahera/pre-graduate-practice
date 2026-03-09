package org.example.platform.repository;

import org.example.platform.model.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Long> {
    // Получаем задачи контеста, отсортированные по индексу (A, B, C...)
    List<ContestProblem> findByContestIdOrderByProblemIndexAsc(Long contestId);
}