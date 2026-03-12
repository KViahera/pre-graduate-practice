package org.example.backend.repository;

import org.example.backend.entity.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestProblemRepository extends JpaRepository<ContestProblem, Integer> {

    boolean existsByContestIdAndProblemId(Integer contestId, Integer problemId);

    List<ContestProblem> findByContestIdOrderByProblemIndexAsc(Integer contestId);

    List<ContestProblem> findByContestId(Integer contestId);

    @Query("SELECT MAX(cp.problemIndex) FROM ContestProblem cp WHERE cp.contest.id = :contestId")
    Integer findMaxIndexByContestId(@Param("contestId") Integer contestId);

    Optional<ContestProblem> findByContestIdAndProblemId(Integer contestId, Integer problemId);
}