package org.example.backend.repository;

import org.example.backend.entity.Contest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Integer> {

    Page<Contest> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}