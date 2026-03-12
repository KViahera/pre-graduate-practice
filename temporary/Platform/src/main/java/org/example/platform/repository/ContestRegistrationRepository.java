package org.example.platform.repository;

import org.example.platform.model.Contest;
import org.example.platform.model.ContestRegistration;
import org.example.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRegistrationRepository extends JpaRepository<ContestRegistration, Long> {
    boolean existsByContestAndUser(Contest contest, User user);
}
