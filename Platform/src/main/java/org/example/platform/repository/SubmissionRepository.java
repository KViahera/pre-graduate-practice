package org.example.platform.repository;

import org.example.platform.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    // В будущем тут можно добавить поиск посылок конкретного пользователя
    List<Submission> findByContestIdOrderBySubmittedAtAsc(Long contestId);
}