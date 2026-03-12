package org.example.backend.repository;

import org.example.backend.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    List<Problem> findTop10ByTitleContainingIgnoreCase(String title);
}